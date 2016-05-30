package com.wallakoala.wallakoala.Activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.wallakoala.wallakoala.Fragments.ProductsFragment;
import com.wallakoala.wallakoala.Fragments.SuggestionsFragment;
import com.wallakoala.wallakoala.Fragments.TopsFragment;
import com.wallakoala.wallakoala.Properties.Properties;
import com.wallakoala.wallakoala.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @class Pantalla principal de la app.
 * Created by Daniel Mancebo on 09/11/2015.
 */

public class MainScreenUI extends AppCompatActivity
{
    /* Constants */
    protected static final int FILTER_REQUEST = 0;
    protected static final int EXIT_TIME_INTERVAL = 2000;
    protected static String SEARCH_QUERY;

    /* Container Views */
    protected NavigationView mLeftNavigationVew;
    protected DrawerLayout mDrawerLayout;
    protected CoordinatorLayout mCoordinatorLayout;
    protected ViewPager mViewPager;
    protected TabLayout mTabLayout;

    /* Toolbar */
    protected Toolbar mToolbar;
    protected ActionBarDrawerToggle mLeftDrawerToggle;

    /* Menu */
    protected Menu mMenu;

    /* Animations */
    protected Animation mExplodeAnimation, mImplodeAnimation;

    /* Fragments */
    protected TopsFragment mTopsFragment;
    protected SuggestionsFragment mSugestionsFragment;
    protected ProductsFragment mProductsFragment;

    /* Other */
    protected long mBackPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_tabs);

        _initToolbar();
        _initViewPager();
        _initNavigationDrawer();
        _initAnimations();

        mBackPressed = 0;
    }

    /**
     * Metodo para inicializar la Toolbar
     */
    protected void _initToolbar()
    {
        mToolbar = (Toolbar)findViewById(R.id.appbar);

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    /**
     * Inicializacion del ViewPager.
     */
    private void _initViewPager()
    {
        mViewPager = (ViewPager) findViewById(R.id.viewPager);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        mTopsFragment       = new TopsFragment();
        mProductsFragment   = new ProductsFragment();
        mSugestionsFragment = new SuggestionsFragment();

        // Añadimos los fragmentos al adapter
        adapter.addFragment(mTopsFragment, "DESCUBRE");
        adapter.addFragment(mProductsFragment, "NOVEDADES");
        adapter.addFragment(mSugestionsFragment, "TOPS CUOKA");
        mViewPager.setAdapter(adapter);

        // Marcamos como activo la segunda pestaña
        mViewPager.setCurrentItem(1);

        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                Log.d(Properties.TAG, "Pestaña cambiada a " + tab.getText());

                if (tab.getText().equals("NOVEDADES"))
                {
                    findViewById(R.id.menu_item_filter).startAnimation(mExplodeAnimation);

                } else {
                    findViewById(R.id.menu_item_filter).startAnimation(mImplodeAnimation);

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    /**
     * Inicializacion y configuracion del NavigationDrawer.
     */
    protected void _initNavigationDrawer()
    {
        mCoordinatorLayout = (CoordinatorLayout)findViewById(R.id.coordinator_layout);
        mLeftNavigationVew = (NavigationView)findViewById(R.id.nav_view);
        mDrawerLayout      = (DrawerLayout)findViewById(R.id.drawer_layout);

        _initDrawerToggle();

        mDrawerLayout.setDrawerListener(mLeftDrawerToggle);
    }

    /**
     * Inicializacion y configuracion del drawer toggle del leftDrawer.
     */
    protected void _initDrawerToggle()
    {
        // Inicializamos el control en la action bar.
        mLeftDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open_drawer, R.string.close_drawer)
        {
            // Metodo llamado cuando el drawer esta completamente cerrado.
            @Override
            public void onDrawerClosed(View drawerView)
            {
                if (drawerView == findViewById(R.id.leftDrawerLayout))
                    mLeftDrawerToggle.syncState();
            }

            // Metodo llamado cuando el drawer esta completamente abierto.
            @Override
            public void onDrawerOpened(View drawerView)
            {
                if (drawerView == findViewById(R.id.leftDrawerLayout))
                {
                    // Crea la llamada a onPrepareOptionsMenu()
                    supportInvalidateOptionsMenu();

                    mLeftDrawerToggle.syncState();
                }
            }

            // Metodo para realizar la animacion del drawerToggle.
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset)
            {
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };
    }

    /**
     * Metodo para inicializar las animaciones.
     */
    protected void _initAnimations()
    {
        mExplodeAnimation = AnimationUtils.loadAnimation(this, R.anim.explode);
        mImplodeAnimation = AnimationUtils.loadAnimation(this, R.anim.implode);

        mImplodeAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                findViewById(R.id.menu_item_filter).setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mExplodeAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                findViewById(R.id.menu_item_filter).setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    public void onBackPressed()
    {
        // Si el navigation drawer esta abierto, lo cerramos.
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START))
        {
            mDrawerLayout.closeDrawer(GravityCompat.START);

        } else {
            if (mBackPressed + EXIT_TIME_INTERVAL > System.currentTimeMillis())
            {
                super.onBackPressed();
                return;

            } else {
                Snackbar.make(mCoordinatorLayout
                        , getResources().getString( R.string.exit_message )
                        , Snackbar.LENGTH_SHORT).show();
            }

            mBackPressed = System.currentTimeMillis();

        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        if(mLeftDrawerToggle != null)
            mLeftDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        if(mLeftDrawerToggle != null)
            mLeftDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Guardamos el menu para poder acceder a los expandableItems mas adelante.
        this.mMenu = menu;

        // Inflamos el menu.
        getMenuInflater().inflate(R.menu.toolbar_menu_grid, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.menu_item_filter)
        {
            // Solo si no se esta cargando los productos.
            if (mProductsFragment.canFilter())
            {
                // Creamos un Intent.
                Intent intent = new Intent(MainScreenUI.this, FilterUI.class);

                // Obtenemos el mapa de filtros.
                Map<String, Object> filterMap = mProductsFragment.getFilterMap();

                // Lo mandamos en el Intent
                intent.putExtra(Properties.PACKAGE + ".newness", (Boolean)filterMap.get("newness"));
                intent.putExtra(Properties.PACKAGE + ".sections", (ArrayList<String>)filterMap.get("sections"));
                intent.putExtra(Properties.PACKAGE + ".colors", (ArrayList<String>)filterMap.get("colors"));
                intent.putExtra(Properties.PACKAGE + ".shops", (ArrayList<String>)filterMap.get("shops"));
                intent.putExtra(Properties.PACKAGE + ".minPrice", (Integer)filterMap.get("minPrice"));
                intent.putExtra(Properties.PACKAGE + ".maxPrice", (Integer)filterMap.get("maxPrice"));
                intent.putExtra(Properties.PACKAGE + ".man", mProductsFragment.getMan());

                // Iniciamos la activity FilterUI.
                startActivityForResult(intent, FILTER_REQUEST);

                // Animacion de transicion para pasar de una activity a otra.
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        // Si ha ido bien
        if (resultCode == RESULT_OK)
        {
            // Sacamos la cadena de busqueda.
            SEARCH_QUERY = data.getStringExtra(Properties.PACKAGE + ".search");

            // Si es null significa que se quiere filtrar.
            if (SEARCH_QUERY == null)
            {
                Log.d(Properties.TAG, "Filtro establecido:");

                Map<String, Object> filterMap = new HashMap<>();

                filterMap.put("newness", data.getBooleanExtra(Properties.PACKAGE + ".newness", false));
                filterMap.put("sections", data.getSerializableExtra(Properties.PACKAGE + ".sections"));
                filterMap.put("colors", data.getSerializableExtra(Properties.PACKAGE + ".colors"));
                filterMap.put("shops", data.getSerializableExtra(Properties.PACKAGE + ".shops"));
                filterMap.put("minPrice", data.getIntExtra(Properties.PACKAGE + ".minPrice", -1));
                filterMap.put("maxPrice", data.getIntExtra(Properties.PACKAGE + ".maxPrice", -1));

                mProductsFragment.processFilter(filterMap);

            } else {
                Log.d(Properties.TAG, "Busqueda: " + SEARCH_QUERY);

                mProductsFragment.processSearch(SEARCH_QUERY);
            }
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter
    {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager)
        {
            super(manager);
        }

        @Override
        public Fragment getItem(int position)
        {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount()
        {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title)
        {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            return mFragmentTitleList.get(position);
        }
    }
}
