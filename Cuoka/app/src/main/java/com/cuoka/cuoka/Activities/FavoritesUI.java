package com.cuoka.cuoka.Activities;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.cuoka.cuoka.Adapters.FavoritesSectionedAdapter;
import com.cuoka.cuoka.Beans.Product;
import com.cuoka.cuoka.Properties.Properties;
import com.cuoka.cuoka.R;
import com.cuoka.cuoka.Singletons.RestClientSingleton;
import com.cuoka.cuoka.Views.StaggeredRecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

/**
 * Activity que muestra los productos favoritos.
 * Created by Daniel Mancebo Aldea on 29/10/2016.
 */

public class FavoritesUI extends AppCompatActivity
{
    /* Container Views */
    private StaggeredRecyclerView mProductsRecyclerView;

    /* Layouts */
    private CoordinatorLayout mFrameLayout;

    /* Adapters */
    private SectionedRecyclerViewAdapter mProductAdapter;

    /* Data */
    private List<Product> mFavoriteList;
    private Map<String, List<Product>> mProductMap;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_favorites);

        _initData();
        _initViews();
        _initToolbar();

        new RetrieveFavoriteProducts().execute();
    }

    /**
     * Inicializacion de las distintias estructuras de datos.
     */
    protected void _initData()
    {
        mFavoriteList = new ArrayList<>();
        mProductMap   = new TreeMap<>();
    }

    /**
     * Metodo que inicializa las vistas.
     */
    protected void _initViews()
    {
        mFrameLayout          = (CoordinatorLayout) findViewById(R.id.favorites_frame);
        mProductsRecyclerView = (StaggeredRecyclerView) findViewById(R.id.favorites_grid_recycler);
    }

    /**
     * Inicializacion de la Toolbar.
     */
    protected void _initToolbar()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.favorites_appbar);

        ((TextView)findViewById(R.id.toolbar_textview)).setText(getResources().getString(R.string.toolbar_favorites));

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
     * Inicializacion y configuracion del recyclerView.
     */
    @SuppressWarnings("deprecation")
    private void _initRecyclerView()
    {
        mProductsRecyclerView.setVisibility(View.VISIBLE);

        mProductAdapter = new SectionedRecyclerViewAdapter();

        // Creamos una seccion por cada tienda en la que tenga un favorito.
        for (Map.Entry<String, List<Product>> entry : mProductMap.entrySet())
        {
            mProductAdapter.addSection(new FavoritesSectionedAdapter(this
                                , mProductAdapter
                                , entry.getValue()
                                , mFrameLayout
                                , entry.getKey()));
        }

        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mProductsRecyclerView.setItemViewCacheSize(Properties.CACHED_PRODUCTS_MIN);
        mProductsRecyclerView.setLayoutManager(gridLayoutManager);
        mProductsRecyclerView.setAdapter(mProductAdapter);
        mProductsRecyclerView.setHasFixedSize(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            mProductsRecyclerView.scheduleLayoutAnimation();
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
        boolean hasChanges = false;
        if (mProductAdapter != null)
        {
            for (Map.Entry<String, Section> entry : mProductAdapter.getSectionsMap().entrySet())
            {
                hasChanges = ((FavoritesSectionedAdapter)entry.getValue()).hasChanged();

                if (hasChanges)
                {
                    break;
                }
            }
        }

        if (hasChanges)
        {
            setResult(RESULT_OK);

        } else {
            setResult(RESULT_CANCELED);
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
    public void onResume()
    {
        super.onResume();

        // Si venimos de un producto, tenemos que ver si ha quitado/añadido a favorito.
        if (mProductAdapter != null)
        {
            Log.d(Properties.TAG, "Volviendo de ProductUI");

            for (Map.Entry<String, Section> entry : mProductAdapter.getSectionsMap().entrySet())
            {
                ((FavoritesSectionedAdapter)entry.getValue()).restore();
            }
        }
    }

    /**
     * Tarea en segundo plano que trae los productos favoritos del usuario.
     */
    private class RetrieveFavoriteProducts extends AsyncTask<String, Void, Void>
    {
        private String error = null;

        @Override
        protected void onPreExecute()
        {
            findViewById(R.id.favorites_avloadingIndicatorView).setVisibility(View.VISIBLE);
            findViewById(R.id.favorites_nodata).setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(String... unused)
        {
            mFavoriteList = RestClientSingleton.getFavoriteProducts(FavoritesUI.this);

            if (mFavoriteList == null)
            {
                error = "Error al obtener los productos favoritos";

                Log.d(Properties.TAG, error);

            } else {
                Collections.sort(mFavoriteList);

                // Metemos los productos en un mapa <Tienda, Productos>
                for (Product product : mFavoriteList)
                {
                    List<Product> list = mProductMap.get(product.getShop());
                    if (list == null)
                    {
                        list = new ArrayList<>();
                    }

                    list.add(product);

                    mProductMap.put(product.getShop(), list);
                }
            }

            return null;
        }

        @Override
        @SuppressWarnings("deprecation")
        protected void onPostExecute(Void unused)
        {
            findViewById(R.id.favorites_avloadingIndicatorView).setVisibility(View.GONE);

            if ((error == null) && (mFavoriteList != null) && (!mFavoriteList.isEmpty()))
            {
                _initRecyclerView();

            } else if ((error == null) && (mFavoriteList != null) && (mFavoriteList.isEmpty())) {
                findViewById(R.id.favorites_nodata).setVisibility(View.VISIBLE);

            } else {
                Snackbar snackbar = Snackbar.make(mFrameLayout, getResources().getString(R.string.error_message), Snackbar.LENGTH_INDEFINITE)
                        .setAction("Reintentar", new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                new RetrieveFavoriteProducts().execute();
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
}
