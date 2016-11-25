package com.wallakoala.wallakoala.Activities;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
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
import com.wallakoala.wallakoala.Utils.SharedPreferencesManager;
import com.wallakoala.wallakoala.Utils.Utils;
import com.wallakoala.wallakoala.Views.StaggeredRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_shops);

        _initData();
        _initToolbar();
        _initFloatingButton();
        _getDataFromServer();
    }

    /**
     * Metodo que inicializa las distintas ED's y datos.
     */
    private void _initData()
    {
        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(ShopsUI.this);

        User mUser = sharedPreferencesManager.retreiveUser();

        mMyShopsList = new ArrayList<>();
        mAllShopsList = new ArrayList<>();

        mMyShopsList.addAll(mUser.getShops());
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
        new RetrieveDataFromServer().execute();
    }

    /**
     * Tarea en segundo plano que trae la lista de tiendas y de favoritos del servidor.
     */
    private class RetrieveDataFromServer extends AsyncTask<String, Void, Void>
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
            content = RestClientSingleton.retrieveShops(ShopsUI.this);

            // Si content esta vacio, es que ha fallado la conexion.
            if (content == null)
            {
                error = "Imposible conectar con el servidor";
                Log.d(Properties.TAG, error);

            } else {
                try
                {
                    List<JSONObject> jsonList = new ArrayList<>();

                    // Sacamos cada JSON (tienda).
                    for (int j = 0; j < content.length(); j++)
                    {
                        JSONObject js = content.getJSONObject(j);

                        jsonList.add(js);
                    }

                    // Parseamos el JSON manualmente.
                    for (JSONObject jsonObject : jsonList)
                    {
                        String name = jsonObject.getString("name");
                        int numberOfProducts = jsonObject.getInt("products");

                        Shop shop = new Shop(name, false, false, numberOfProducts);

                        mAllShopsList.add(shop);
                    }

                    mFavoriteList = RestClientSingleton.getFavoriteProducts(ShopsUI.this);

                    if (mFavoriteList == null)
                    {
                        error = "Error al obtener los productos favoritos";
                    }

                    // Ordenamos alfabeticamente.
                    Collections.sort(mAllShopsList);
                    // Precargamos los logos.
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

            mAcceptFAB.setVisibility(View.VISIBLE);

            if (error == null)
            {
                Animation mExplode = AnimationUtils.loadAnimation(ShopsUI.this, R.anim.explode_animation);
                mExplode.setStartOffset(275);

                mAcceptFAB.startAnimation(mExplode);

                _initRecyclerView();

            } else {
                Snackbar.make(mCoordinatorLayout, "Ops, algo ha ido mal", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Reintentar", new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                new RetrieveDataFromServer().execute();
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
        StaggeredRecyclerView shopsRecyclerView = (StaggeredRecyclerView) findViewById(R.id.shops_recyclerview);

        mShopListAdapter = new ShopsListAdapter(this, mAllShopsList, mMyShopsList, mFavoriteList);

        shopsRecyclerView.setItemViewCacheSize(Properties.CACHED_SHOPS);
        shopsRecyclerView.setVisibility(View.VISIBLE);
        shopsRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        shopsRecyclerView.setAdapter(mShopListAdapter);
        shopsRecyclerView.setHasFixedSize(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            shopsRecyclerView.scheduleLayoutAnimation();
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
                new SendShopsToServer().execute();
            }
        });
    }

    /**
     * Tarea en segundo plano que envia la seleccion de tiendas al servidor.
     */
    private class SendShopsToServer extends AsyncTask<String, Void, Void>
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
            correct = RestClientSingleton.sendShops(ShopsUI.this, mShopListAdapter.getListOfShops());

            return null;
        }

        @Override
        protected void onPostExecute(Void unused)
        {
            progressDialog.dismiss();

            if (correct)
            {
                Intent intent = new Intent();

                setResult(RESULT_OK, intent);

                finish();

            } else {
                Snackbar.make(mCoordinatorLayout, "Ops, algo ha ido mal", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Reintentar", new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                mAcceptFAB.performClick();
                            }
                        }).show();
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
