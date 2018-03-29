package com.wallakoala.wallakoala.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.wallakoala.wallakoala.Adapters.StylesListAdapter;
import com.wallakoala.wallakoala.Beans.User;
import com.wallakoala.wallakoala.Properties.Properties;
import com.wallakoala.wallakoala.R;
import com.wallakoala.wallakoala.Singletons.RestClientSingleton;
import com.wallakoala.wallakoala.Utils.SharedPreferencesManager;
import com.wallakoala.wallakoala.Views.StaggeredRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Pantalla donde se eligen los estilos del usuario.
 * Created by Daniel Mancebo Aldea on 09/04/2017.
 */

public class StylesUI extends AppCompatActivity
{
    /* Constants */
    private static final int MALE_NUM_COLUMNS = 3;
    private static final int FEMALE_NUM_COLUMNS = 3;

    /* Adapter */
    private StylesListAdapter mStylesListAdapter;

    /* FAB */
    private FloatingActionButton mAcceptFAB;

    /* Container Views */
    private CoordinatorLayout mCoordinatorLayout;

    /* Data */
    private User mUser;
    private List<String> mStylesList;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_styles);

        _initData();
        _initViews();
        _initToolbar();
        _initRecyclerView();
        _initFloatingButton();
    }

    /**
     * Metodo que inicializa las distintas ED's y datos.
     */
    private void _initData()
    {
        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(StylesUI.this);

        mUser = sharedPreferencesManager.retrieveUser();

        mStylesList = new ArrayList<>();

        mStylesList.addAll(mUser.getStyles());
    }

    /**
     * Metodo que inicializa las views.
     */
    private void _initViews()
    {
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.styles_coordinator);
    }

    /**
     * Inicializacion de la Toolbar.
     */
    private void _initToolbar()
    {
        Toolbar toolbar = (Toolbar)findViewById(R.id.styles_toolbar);
        TextView toolbarTextView = (TextView)findViewById(R.id.toolbar_textview);

        toolbarTextView.setText(getResources().getString(R.string.toolbar_styles));

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
     * Metodo que inicializa el RecyclerView con las tiendas.
     */
    private void _initRecyclerView()
    {
        final StaggeredRecyclerView shopsRecyclerView = (StaggeredRecyclerView) findViewById(R.id.styles_recyclerview);

        mStylesListAdapter = new StylesListAdapter(this, mUser.getMan(), mStylesList);

        shopsRecyclerView.setVisibility(View.VISIBLE);
        shopsRecyclerView.setLayoutManager(new GridLayoutManager(
                this, ((mUser.getMan()) ? MALE_NUM_COLUMNS : FEMALE_NUM_COLUMNS)));
        shopsRecyclerView.setAdapter(mStylesListAdapter);
        shopsRecyclerView.setHasFixedSize(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            shopsRecyclerView.scheduleLayoutAnimation();
        }
    }

    /**
     * Metodo que inicaliza el FAB.
     */
    private void _initFloatingButton()
    {
        mAcceptFAB = (FloatingActionButton) findViewById(R.id.styles_accept);

        mAcceptFAB.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d(Properties.TAG, "[STYLES_UI] Se hace click -> Aceptar");

                new SendStylesTask().execute();
            }
        });

        Animation explode = AnimationUtils.loadAnimation(StylesUI.this, R.anim.explode_animation);
        explode.setStartOffset(375);
        explode.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation)
            {
                mAcceptFAB.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                mAcceptFAB.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        mAcceptFAB.startAnimation(explode);
    }

    /**
     * Tarea en segundo plano que envia la seleccion de estilos al servidor.
     */
    private class SendStylesTask extends AsyncTask<String, Void, Void>
    {
        private ProgressDialog progressDialog;
        private boolean correct;

        @Override
        protected void onPreExecute()
        {
            progressDialog = new ProgressDialog(StylesUI.this, R.style.MyDialogTheme);
            progressDialog.setTitle("");
            progressDialog.setMessage("Realizando cambios...");
            progressDialog.setIndeterminate(true);

            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... unused)
        {
            Log.d(Properties.TAG, "[STYLES_UI] Se envían nos cambios al servidor");
            correct = RestClientSingleton.sendStyles(StylesUI.this, mStylesListAdapter.getStylesSelected());

            return null;
        }

        @SuppressWarnings("deprecation")
        @Override
        protected void onPostExecute(Void unused)
        {
            progressDialog.dismiss();

            if (correct)
            {
                Log.d(Properties.TAG, "[STYLES_UI] Cambios realizados correctamente");
                Log.d(Properties.TAG, "[STYLES_UI] Se vuelve a la activity con el RESULT_CODE -> RESULT_OK");

                Intent intent = new Intent();

                setResult(RESULT_OK, intent);

                finish();

            } else {
                Snackbar snackbar = Snackbar
                        .make(mCoordinatorLayout, getResources().getString(R.string.error_message), Snackbar.LENGTH_INDEFINITE)
                        .setAction("Reintentar", new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                mAcceptFAB.performClick();
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

    @Override
    public boolean onOptionsItemSelected(final MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            Log.d(Properties.TAG, "[STYLES_UI] Se pulsa el botón Atrás");

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
