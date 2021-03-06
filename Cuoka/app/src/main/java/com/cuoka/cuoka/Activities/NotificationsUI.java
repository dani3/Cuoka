package com.cuoka.cuoka.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.cuoka.cuoka.Adapters.NotificationsAdapter;
import com.cuoka.cuoka.Beans.Notification;
import com.cuoka.cuoka.Properties.Properties;
import com.cuoka.cuoka.R;
import com.cuoka.cuoka.Singletons.RestClientSingleton;
import com.cuoka.cuoka.Utils.ExceptionPrinter;
import com.cuoka.cuoka.Utils.JSONParser;
import com.cuoka.cuoka.Views.StaggeredRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pantalla donde se muestran las notificaciones.
 * Created by Daniel Mancebo on 25/11/2016.
 */

public class NotificationsUI extends AppCompatActivity
{
    /* ContainerViews */
    private CoordinatorLayout mCoordinatorLayout;

    /* Adapter */
    private NotificationsAdapter mNotificationListAdapter;

    /* Data */
    private List<Notification> mNotificationList;
    private boolean mShopsChanged;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_notifications);

        _initData();
        _initToolbar();
        _getNotificationsFromServer();
    }

    /**
     * Metodo que inicializa los distintos datos.
     */
    private void _initData()
    {
        mNotificationList = new ArrayList<>();

        mShopsChanged = false;
    }

    /**
     * Inicializacion de la Toolbar.
     */
    private void _initToolbar()
    {
        Toolbar toolbar = (Toolbar)findViewById(R.id.notifications_toolbar);
        TextView toolbarTextView = (TextView)findViewById(R.id.toolbar_textview);

        toolbarTextView.setText(getResources().getString(R.string.toolbar_notifications));

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        }
    }

    /**
     * Metodo que llama al servidor para traer la lista de notificaciones.
     */
    private void _getNotificationsFromServer()
    {
        new RetrieveNotificationsTask().execute();
    }

    /**
     * Tarea en segundo plano que trae la lista de notificaciones.
     */
    private class RetrieveNotificationsTask extends AsyncTask<String, Void, Void>
    {
        private JSONArray content;
        private String error = null;

        @Override
        protected void onPreExecute()
        {
            findViewById(R.id.notifications_loading).setVisibility(View.VISIBLE);
            findViewById(R.id.notifications_nodata).setVisibility(View.GONE);

            mCoordinatorLayout = (CoordinatorLayout)findViewById(R.id.notifications_coordinator);
        }

        @Override
        protected Void doInBackground(String... unused)
        {
            Log.d(Properties.TAG, "[NOTIFICATIONS_UI] Se obtienen las notificaciones");
            content = RestClientSingleton.retrieveNotifications(NotificationsUI.this);

            // Si content esta vacio, es que ha fallado la conexion.
            if (content == null)
            {
                error = "Imposible conectar con el servidor";

            } else {
                try
                {
                    List<JSONObject> jsonList = new ArrayList<>();

                    // Sacamos cada JSON (notificacion).
                    for (int j = 0; j < content.length(); j++)
                    {
                        JSONObject js = content.getJSONObject(j);

                        jsonList.add(js);
                    }

                    // Parseamos los JSON.
                    for (JSONObject jsonObject : jsonList)
                    {
                        mNotificationList.add(JSONParser.convertJSONtoNotification(jsonObject));
                    }

                    Collections.sort(mNotificationList);

                } catch (JSONException e) {
                    ExceptionPrinter.printException("NOTIFICATIONS_UI", e);
                }
            }

            return null;
        }

        @SuppressWarnings("deprecation")
        @Override
        protected void onPostExecute(Void unused)
        {
            findViewById(R.id.notifications_loading).setVisibility(View.GONE);

            if (error == null)
            {
                if (mNotificationList.isEmpty())
                {
                    Log.d(Properties.TAG, "[NOTIFICATION_UI] No hay notificaciones por leer");
                    findViewById(R.id.notifications_nodata).setVisibility(View.VISIBLE);

                } else {
                    _initRecyclerView();
                }

            } else {
                Snackbar snackbar = Snackbar
                        .make(mCoordinatorLayout, getResources().getString(R.string.error_message), Snackbar.LENGTH_INDEFINITE)
                        .setAction("Reintentar", new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                new RetrieveNotificationsTask().execute();
                            }
                        });

                snackbar.getView().setBackgroundColor(getResources().getColor(android.R.color.white));
                snackbar.setActionTextColor(getResources().getColor(R.color.colorAccent));
                ((TextView)snackbar.getView().findViewById(android.support.design.R.id.snackbar_text))
                        .setTextColor(getResources().getColor(R.color.colorText));

                snackbar.show();
            }
        }
    }

    /**
     * Metodo que inicializa el RecyclerView con las tiendas.
     */
    private void _initRecyclerView()
    {
        StaggeredRecyclerView notificationRecyclerView = (StaggeredRecyclerView) findViewById(R.id.notifications_recyclerview);

        mNotificationListAdapter = new NotificationsAdapter(this, mNotificationList);

        notificationRecyclerView.setItemViewCacheSize(Properties.CACHED_SHOPS);
        notificationRecyclerView.setVisibility(View.VISIBLE);
        notificationRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        notificationRecyclerView.setAdapter(mNotificationListAdapter);
        notificationRecyclerView.setHasFixedSize(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            notificationRecyclerView.scheduleLayoutAnimation();
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            onBackPressed();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent();
        intent.putExtra("shops", mShopsChanged);

        if (mNotificationListAdapter != null)
        {
            setResult((mNotificationListAdapter.isEveryNotificationRead()) ? RESULT_OK : RESULT_CANCELED, intent);

        } else {
            setResult(RESULT_CANCELED, intent);
        }

        super.onBackPressed();
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.left_in_animation, R.anim.left_out_animation);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        mShopsChanged = (resultCode == RESULT_OK);
    }
}
