package com.wallakoala.wallakoala.Activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.wallakoala.wallakoala.Adapters.FavoritesSectionedAdapter;
import com.wallakoala.wallakoala.Adapters.RecommendedListAdapter;
import com.wallakoala.wallakoala.Beans.Product;
import com.wallakoala.wallakoala.Beans.User;
import com.wallakoala.wallakoala.Properties.Properties;
import com.wallakoala.wallakoala.R;
import com.wallakoala.wallakoala.Singletons.RestClientSingleton;
import com.wallakoala.wallakoala.Utils.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

/**
 * Activity que muestra los productos favoritos.
 * Created by Daniel Mancebo Aldea on 29/10/2016.
 */

public class FavoritesUI extends AppCompatActivity
{
    /* Container Views */
    protected RecyclerView mProductsRecyclerView;

    /* Toolbar */
    protected Toolbar mToolbar;

    /* Layouts */
    protected FrameLayout mFrameLayout;

    /* LayoutManagers */
    protected GridLayoutManager mGridLayoutManager;

    /* Adapters */
    protected SectionedRecyclerViewAdapter mProductAdapter;

    /* SharedPreferenceManager */
    protected SharedPreferencesManager mSharedPreferences;

    /* Animations */
    protected Animation mMoveAndFadeAnimation;

    /* Data */
    protected User mUser;
    protected List<Product> mFavoriteList;
    protected Map<String, List<Product>> mProductMap;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_favorites);

        _initAnimations();
        _initData();
        _initViews();
        _initToolbar();

        new RetrieveFavoriteProducts().execute();
    }

    /**
     * Metodo que inicializa las animaciones.
     */
    private void _initAnimations()
    {
        mMoveAndFadeAnimation = AnimationUtils.loadAnimation(this
                , R.anim.translate_and_fade_animation);
    }

    /**
     * Inicializacion de las distintias estructuras de datos.
     */
    protected void _initData()
    {
        mSharedPreferences = new SharedPreferencesManager(this);

        mUser = mSharedPreferences.retreiveUser();

        mFavoriteList = new ArrayList<>();
        mProductMap   = new HashMap<>();
    }

    /**
     * Metodo que inicializa las vistas.
     */
    protected void _initViews()
    {
        mFrameLayout          = (FrameLayout)findViewById(R.id.favorites_frame);
        mProductsRecyclerView = (RecyclerView)findViewById(R.id.favorites_grid_recycler);
    }

    /**
     * Inicializacion de la Toolbar.
     */
    protected void _initToolbar()
    {
        mToolbar = (Toolbar)findViewById(R.id.favorites_appbar);

        ((TextView)findViewById(R.id.toolbar_textview)).setText(getResources().getString(R.string.toolbar_favorites));

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
     * Inicializacion y configuracion del recyclerView.
     */
    @SuppressWarnings("deprecation")
    private void _initRecyclerView()
    {
        mProductsRecyclerView.setVisibility(View.VISIBLE);

        mProductAdapter = new SectionedRecyclerViewAdapter();

        for (Map.Entry<String, List<Product>> entry : mProductMap.entrySet())
        {
            mProductAdapter.addSection(new FavoritesSectionedAdapter(this
                                , mProductAdapter
                                , entry.getValue()
                                , mFrameLayout
                                , entry.getKey()));
        }

        mGridLayoutManager = new GridLayoutManager(this, 1);
        mProductsRecyclerView.setLayoutManager(mGridLayoutManager);
        mProductsRecyclerView.setAdapter(mProductAdapter);
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
        setResult(RESULT_OK);

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

        // Si venimos de un producto, tenemos que actualizar los cambios (si los hay)
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
        String error = null;

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

            } else {
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
        protected void onPostExecute(Void unused)
        {
            findViewById(R.id.favorites_avloadingIndicatorView).setVisibility(View.GONE);

            if ((error == null) && (mFavoriteList != null) && (!mFavoriteList.isEmpty()))
            {
                _initRecyclerView();

            } else if ((error == null) && (mFavoriteList != null) && (mFavoriteList.isEmpty())) {
                findViewById(R.id.favorites_nodata).setVisibility(View.VISIBLE);

            } else {
                Snackbar.make(mFrameLayout, "Ops, algo ha ido mal", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Reintentar", new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                new RetrieveFavoriteProducts().execute();
                            }
                        }).show();
            }
        }
    }
}
