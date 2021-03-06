package com.wallakoala.wallakoala.Activities;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wallakoala.wallakoala.Adapters.ShopsListAdapter;
import com.wallakoala.wallakoala.Beans.Product;
import com.wallakoala.wallakoala.Beans.Shop;
import com.wallakoala.wallakoala.Beans.User;
import com.wallakoala.wallakoala.Properties.Properties;
import com.wallakoala.wallakoala.R;
import com.wallakoala.wallakoala.Singletons.RestClientSingleton;
import com.wallakoala.wallakoala.Singletons.TypeFaceSingleton;
import com.wallakoala.wallakoala.Utils.ExceptionPrinter;
import com.wallakoala.wallakoala.Utils.JSONParser;
import com.wallakoala.wallakoala.Utils.SharedPreferencesManager;
import com.wallakoala.wallakoala.Utils.SmootherGridLayoutManager;
import com.wallakoala.wallakoala.Utils.Utils;
import com.wallakoala.wallakoala.Views.StaggeredRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pantalla que muestra las tiendas del usuario y las disponibles.
 * Created by Daniel Mancebo Aldea on 23/10/2016.
 */

public class ShopsUI extends AppCompatActivity
{
    /* ContainerViews */
    private CoordinatorLayout mCoordinatorLayout;

    /* Adapter */
    private ShopsListAdapter mShopListAdapter;

    /* FAB */
    private FloatingActionButton mAcceptFAB;

    /* Data */
    private List<Shop> mAllShopsList;
    private List<String> mMyShopsList;
    private List<Product> mFavoriteList;
    private boolean mScroll;
    private String mShopToScroll;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_shops);

        _initData(savedInstanceState);
        _initToolbar();
        _initFloatingButton();
        _getDataFromServer();
    }

    /**
     * Metodo que inicializa las distintas ED's y datos.
     */
    private void _initData(Bundle savedInstanceState)
    {
        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(ShopsUI.this);

        User user = sharedPreferencesManager.retrieveUser();

        mMyShopsList = new ArrayList<>();
        mAllShopsList = new ArrayList<>();

        mMyShopsList.addAll(user.getShops());

        // Solo lo ejecutamos si venimos de una activity.
        if (savedInstanceState == null)
        {
            Bundle bundle = getIntent().getExtras();

            if (bundle != null && !bundle.isEmpty())
            {
                if (bundle.getString("shop") != null)
                {
                    mScroll = true;
                    mShopToScroll =  bundle.getString("shop");

                    Log.d(Properties.TAG, "[SHOPS_UI] Se viene de la pantalla NotificationsUI");
                    Log.d(Properties.TAG, "[SHOPS_UI] Hay que hacer scroll automático hasta la tienda " + mShopToScroll);
                }
            }
        }
    }

    /**
     * Inicializacion de la Toolbar.
     */
    private void _initToolbar()
    {
        Toolbar toolbar = (Toolbar)findViewById(R.id.shops_toolbar);
        TextView toolbarTextView = (TextView)findViewById(R.id.toolbar_textview);

        toolbarTextView.setText(getResources().getString(R.string.toolbar_shops));

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
     * Metodo que precarga los logos de las tiendas.
     */
    private void _fetchImages()
    {
        Log.d(Properties.TAG, "[SHOPS_UI] Se precargan los logos de las tiendas");

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
     * Metodo que llama al servidor para traer la lista de tiendas y de favoritos.
     */
    private void _getDataFromServer()
    {
        new RetrieveShopsTask().execute();
    }

    /**
     * Tarea en segundo plano que trae la lista de tiendas y de favoritos del servidor.
     */
    private class RetrieveShopsTask extends AsyncTask<String, Void, Void>
    {
        private JSONArray content = null;
        private boolean error = false;

        @Override
        protected void onPreExecute()
        {
            findViewById(R.id.shops_loading).setVisibility(View.VISIBLE);

            mCoordinatorLayout = (CoordinatorLayout)findViewById(R.id.shops_coordinator);
        }

        @Override
        protected Void doInBackground(String... unused)
        {
            Log.d(Properties.TAG, "[SHOPS_UI] Se conecta con el servidor para traer la lista de tiendas");

            content = RestClientSingleton.retrieveShops(ShopsUI.this);

            // Si content esta vacio, es que ha fallado la conexion.
            if (content == null)
            {
                error = true;

            } else {
                try
                {
                    Log.d(Properties.TAG, "[SHOPS_UI] Se parsean los JSONs con las tiendas");
                    mAllShopsList = JSONParser.convertJSONsToShops(content);
                    Log.d(Properties.TAG, "[SHOPS_UI] Se conecta con el servidor para traer la lista de favoritos del usuario");
                    mFavoriteList = RestClientSingleton.getFavoriteProducts(ShopsUI.this);

                    // Ordenamos alfabeticamente.
                    Collections.sort(mAllShopsList);
                    // Precargamos los logos.
                    _fetchImages();

                } catch (JSONException e) {
                    ExceptionPrinter.printException("SHOPS_UI", e);

                    error = false;
                }
            }

            return null;
        }

        @SuppressWarnings("deprecation")
        @Override
        protected void onPostExecute(Void unused)
        {
            findViewById(R.id.shops_loading).setVisibility(View.GONE);

            mAcceptFAB.setVisibility(View.VISIBLE);

            if (!error)
            {
                Log.d(Properties.TAG, "[SHOPS_UI] Todo correcto -> se muestran las tiendas");

                Animation explode = AnimationUtils.loadAnimation(ShopsUI.this, R.anim.explode_animation);
                explode.setStartOffset(275);

                mAcceptFAB.startAnimation(explode);

                _initRecyclerView();

            } else {
                Snackbar snackbar = Snackbar
                        .make(mCoordinatorLayout, getResources().getString(R.string.error_message), Snackbar.LENGTH_INDEFINITE)
                        .setAction("Reintentar", new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                new RetrieveShopsTask().execute();
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
        final StaggeredRecyclerView shopsRecyclerView = (StaggeredRecyclerView) findViewById(R.id.shops_recyclerview);

        mShopListAdapter = new ShopsListAdapter(this, mAllShopsList, mMyShopsList, mFavoriteList);

        shopsRecyclerView.setItemViewCacheSize(Properties.CACHED_SHOPS);
        shopsRecyclerView.setVisibility(View.VISIBLE);
        shopsRecyclerView.setLayoutManager(new SmootherGridLayoutManager(this, 1));
        shopsRecyclerView.setAdapter(mShopListAdapter);
        shopsRecyclerView.setHasFixedSize(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            shopsRecyclerView.scheduleLayoutAnimation();

            if (mScroll)
            {
                Log.d(Properties.TAG, "[SHOPS_UI] Se realiza scroll automático hasta la tienda " + mShopToScroll);

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        shopsRecyclerView.smoothScrollToPosition(mShopListAdapter.getShopPosition(mShopToScroll));
                    }

                }, 500 + (int)((500*0.15)*5));
            }

        } else {
            shopsRecyclerView.smoothScrollToPosition(mShopListAdapter.getShopPosition(mShopToScroll));
        }
    }

    /**
     * Metodo que inicializa el FAB para aceptar la seleccion.
     */
    private void _initFloatingButton()
    {
        mAcceptFAB = (FloatingActionButton)findViewById(R.id.shops_accept);

        mAcceptFAB.setVisibility(View.GONE);
        mAcceptFAB.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d(Properties.TAG, "[SHOPS_UI] Se hace click -> Aceptar");

                new SendShopsTask().execute();
            }
        });
    }

    /**
     * Tarea en segundo plano que envia la seleccion de tiendas al servidor.
     */
    private class SendShopsTask extends AsyncTask<String, Void, Void>
    {
        private ProgressDialog progressDialog;
        private boolean correct;

        @Override
        protected void onPreExecute()
        {
            progressDialog = new ProgressDialog(ShopsUI.this, R.style.MyDialogTheme);
            progressDialog.setTitle("");
            progressDialog.setMessage("Realizando cambios...");
            progressDialog.setIndeterminate(true);

            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... unused)
        {
            Log.d(Properties.TAG, "[SHOPS_UI] Se envían nos cambios al servidor");
            correct = RestClientSingleton.sendShops(ShopsUI.this, mShopListAdapter.getListOfShops());

            return null;
        }

        @SuppressWarnings("deprecation")
        @Override
        protected void onPostExecute(Void unused)
        {
            progressDialog.dismiss();

            if (correct)
            {
                Log.d(Properties.TAG, "[SHOPS_UI] Cambios realizados correctamente");
                Log.d(Properties.TAG, "[SHOPS_UI] Se vuelve a la activity con el RESULT_CODE -> RESULT_OK");

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
    @SuppressWarnings("deprecation")
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.toolbar_menu_shops, menu);

        // Associate searchable configuration with the SearchView
        final SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView =
                (SearchView) menu.findItem(R.id.menu_item_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchView.setQueryHint(getResources().getString(R.string.search_hint_shops));

        ((EditText)searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text))
                .setTextColor(getResources().getColor(R.color.colorText));

        ((EditText)searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text))
                .setHintTextColor(getResources().getColor(R.color.colorText));

        ((EditText)searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text)).setTypeface(
                TypeFaceSingleton.getTypeFace(this, "Existence-StencilLight.otf"));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query) { return true; }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                if (!newText.isEmpty())
                {
                    List<Shop> potentialShopsList = new ArrayList<>();

                    // Buscamos primero por las tiendas que empiezan por el texto introducido.
                    for (Shop shop : mAllShopsList)
                    {
                        if (shop.getName().toUpperCase().startsWith(newText.toUpperCase()))
                        {
                            potentialShopsList.add(shop);
                        }
                    }

                    // Si no se ha encontrado nada, se busca si la tienda contiene el texto introducido.
                    if (potentialShopsList.isEmpty())
                    {
                        for (Shop shop : mAllShopsList)
                        {
                            if (shop.getName().toUpperCase().contains(newText.toUpperCase()))
                            {
                                potentialShopsList.add(shop);
                            }
                        }
                    }

                    // Actualizamos la lista del adpter y notificamos el cambio.
                    mShopListAdapter.updateShopList(potentialShopsList);
                    mShopListAdapter.notifyDataSetChanged();

                } else {
                    mShopListAdapter.updateShopList(mAllShopsList);
                    mShopListAdapter.notifyDataSetChanged();
                }

                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            Log.d(Properties.TAG, "[SHOPS_UI] Se pulsa el botón Atrás");

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
