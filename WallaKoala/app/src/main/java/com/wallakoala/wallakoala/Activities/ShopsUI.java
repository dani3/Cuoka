package com.wallakoala.wallakoala.Activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.RequestFuture;
import com.squareup.picasso.Picasso;
import com.wallakoala.wallakoala.Adapters.ShopsListAdapter;
import com.wallakoala.wallakoala.Beans.Shop;
import com.wallakoala.wallakoala.Beans.User;
import com.wallakoala.wallakoala.Properties.Properties;
import com.wallakoala.wallakoala.R;
import com.wallakoala.wallakoala.Singletons.VolleySingleton;
import com.wallakoala.wallakoala.Utils.SharedPreferencesManager;
import com.wallakoala.wallakoala.Utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Pantalla que muestra las tiendas del usuario y las disponibles.
 * Created by Daniel Mancebo Aldea on 23/10/2016.
 */

public class ShopsUI extends AppCompatActivity
{
    /* Container Views */
    protected RecyclerView mShopsRecyclerView;
    protected CoordinatorLayout mCoordinatorLayout;

    /* Adapter */
    protected ShopsListAdapter mShopListAdapter;

    /* SharedPreferences */
    protected SharedPreferencesManager mSharedPreferencesManager;

    /* Data */
    protected List<Shop> mAllShopsList;
    protected List<String> mMyShopsList;

    /* User */
    protected User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_shops);

        _initData();
        _initToolbar();
        _retrieveShops();
    }

    /**
     * Metodo que inicializa las distintas ED's y datos.
     */
    private void _initData()
    {
        mSharedPreferencesManager = new SharedPreferencesManager(ShopsUI.this);

        mUser = mSharedPreferencesManager.retreiveUser();

        mMyShopsList = new ArrayList<>();
        mAllShopsList = new ArrayList<>();

        mMyShopsList.addAll(mUser.getShops());
    }

    /**
     * Inicializacion de la Toolbar.
     */
    protected void _initToolbar()
    {
        Toolbar mToolbar = (Toolbar)findViewById(R.id.shops_toolbar);
        TextView mToolbarTextView = (TextView)findViewById(R.id.toolbar_textview);

        mToolbarTextView.setText(getResources().getString(R.string.toolbar_shops));

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        }
    }

    /**
     * Metodo que precarga los logos de las tiendas.
     */
    private void _fetchImages()
    {
        for (Shop shop : mAllShopsList)
        {
            String logoFile = shop.getName() + "-logo.jpg";
            String fixedUrl = Utils.fixUrl(Properties.SERVER_URL + Properties.LOGOS_PATH + logoFile);

            Picasso.with(this)
                   .load(fixedUrl)
                   .fetch();
        }
    }

    /**
     * Metodo que llama al servidor para traer la lista de tiendas.
     */
    private void _retrieveShops()
    {
        new RetrieveShopsFromServer().execute();
    }

    /**
     * Tarea en segundo plano que trae la lista de tiendas del servidor.
     */
    private class RetrieveShopsFromServer extends AsyncTask<String, Void, Void>
    {
        private JSONArray content = null;
        private String error = null;

        @Override
        protected void onPreExecute()
        {
            findViewById(R.id.shops_loading).setVisibility(View.VISIBLE);

            mCoordinatorLayout = (CoordinatorLayout)findViewById(R.id.shops_coordinator);
        }

        @Override
        protected Void doInBackground(String... unused)
        {
            RequestFuture<JSONArray> future = RequestFuture.newFuture();

            final String fixedURL = Utils.fixUrl(Properties.SERVER_URL + ":" + Properties.SERVER_SPRING_PORT
                    + "/shops/" + mUser.getMan());

            Log.d(Properties.TAG, "Conectando con: " + fixedURL + " para traer la lista de tiendas");

            // Creamos una peticion
            final JsonArrayRequest jsonObjReq = new JsonArrayRequest(Request.Method.GET
                                                                , fixedURL
                                                                , null
                                                                , future
                                                                , future);

            // La mandamos a la cola de peticiones
            VolleySingleton.getInstance(ShopsUI.this).addToRequestQueue(jsonObjReq);

            if (isCancelled())
            {
                return null;
            }

            try
            {
                content = future.get(20, TimeUnit.SECONDS);

            } catch (InterruptedException e) {
                error = "Thread interrumpido";
                Log.d(Properties.TAG, error);
            } catch (ExecutionException | TimeoutException e) {
                error = "Timeout exception";
                Log.d(Properties.TAG, error);
            }

            if (isCancelled())
            {
                return null;
            }

            // Si content esta vacio, es que ha fallado la conexion.
            if (content == null)
            {
                error = "Imposible conectar con el servidor";
                Log.d(Properties.TAG, error);

            } else {
                try
                {
                    List<JSONObject> jsonList = new ArrayList<>();

                    for (int j = 0; j < content.length(); j++)
                    {
                        JSONObject js = content.getJSONObject(j);

                        jsonList.add(js);
                    }

                    for (JSONObject jsonObject : jsonList)
                    {
                        String name = jsonObject.getString("name");
                        int numberOfProducts = jsonObject.getInt("products");

                        Shop shop = new Shop(name, false, false, numberOfProducts);

                        mAllShopsList.add(shop);
                    }

                    Collections.sort(mAllShopsList);

                    _fetchImages();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused)
        {
            findViewById(R.id.shops_loading).setVisibility(View.GONE);

            if (error == null)
            {
                _initRecyclerView();

            } else {
                Snackbar.make(mCoordinatorLayout, "Ops, algo ha ido mal", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Reintentar", new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                new RetrieveShopsFromServer().execute();
                            }
                        }).show();
            }
        }
    }

    /**
     * Metodo que inicializa el RecyclerView con las tiendas.
     */
    private void _initRecyclerView()
    {
        mShopsRecyclerView = (RecyclerView)findViewById(R.id.shops_recyclerview);

        mShopsRecyclerView.setVisibility(View.VISIBLE);

        mShopListAdapter = new ShopsListAdapter(this, mAllShopsList, mMyShopsList);
        mShopsRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        mShopsRecyclerView.setAdapter(mShopListAdapter);
        mShopsRecyclerView.setHasFixedSize(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.toolbar_menu_shops, menu);

        return super.onCreateOptionsMenu(menu);
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
        setResult(RESULT_CANCELED);

        super.onBackPressed();
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.left_in_animation, R.anim.left_out_animation);
    }
}
