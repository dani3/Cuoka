package com.wallakoala.wallakoala.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
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
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.wallakoala.wallakoala.Beans.User;
import com.wallakoala.wallakoala.Fragments.ProductsFragment;
import com.wallakoala.wallakoala.Fragments.RecommendedFragment;
import com.wallakoala.wallakoala.Properties.Properties;
import com.wallakoala.wallakoala.R;
import com.wallakoala.wallakoala.Singletons.TypeFaceSingleton;
import com.wallakoala.wallakoala.Utils.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Pantalla principal de la app.
 * Created by Daniel Mancebo on 09/11/2015.
 */

public class MainScreenUI extends AppCompatActivity
{
    /* Constants */
    protected static final int FILTER_REQUEST = 0;
    protected static final int EXIT_TIME_INTERVAL = 2000;
    protected static String SEARCH_QUERY;

    /* Container Views */
    protected NavigationView mNavigationVew;
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
    protected RecommendedFragment mRecommendedFragment;
    protected ProductsFragment mProductsFragment;

    /* SharedPreference */
    protected SharedPreferencesManager mSharedPreferencesManager;

    /* Other */
    protected long mBackPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        _initData();
        _initToolbar();
        _initViewPager();
        _initNavigationDrawer();
        _initAnimations();

        mBackPressed = 0;
    }

    protected void _initData()
    {
        mSharedPreferencesManager = new SharedPreferencesManager(this);
    }

    /**
     * Metodo para inicializar la Toolbar
     */
    protected void _initToolbar()
    {
        mToolbar = (Toolbar)findViewById(R.id.appbar);

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    /**
     * Inicializacion del ViewPager.
     */
    @SuppressWarnings("deprecation")
    private void _initViewPager()
    {
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        mProductsFragment    = new ProductsFragment();
        mRecommendedFragment = new RecommendedFragment();

        // Añadimos los fragmentos al adapter
        adapter.addFragment(mRecommendedFragment, "DESCUBRE");
        adapter.addFragment(mProductsFragment, "NOVEDADES");
        mViewPager.setAdapter(adapter);

        // Marcamos como activo la segunda pestaña (NOVEDADES)
        mViewPager.setCurrentItem(1);
        mTabLayout.setupWithViewPager(mViewPager);

        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                mRecommendedFragment.select();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        ViewGroup vg = (ViewGroup)mTabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++)
        {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++)
            {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView)
                {
                    ((TextView)tabViewChild).setTypeface(TypeFaceSingleton.getTypeFace(this, "Existence-StencilLight.otf"));
                }
            }
        }
    }

    /**
     * Inicializacion y configuracion del NavigationDrawer.
     */
    @SuppressWarnings("deprecation")
    protected void _initNavigationDrawer()
    {
        // Obtenemos las vistas del navigation drawer
        mCoordinatorLayout = (CoordinatorLayout)findViewById(R.id.coordinator_layout);
        mNavigationVew     = (NavigationView)findViewById(R.id.nav_view);
        mDrawerLayout      = (DrawerLayout)findViewById(R.id.drawer_layout);

        // Obtenemos el usuario para inicializar la cabecera
        User user = mSharedPreferencesManager.retreiveUser();

        // Sacamos la cabecera del navigation drawer
        View navHeader = mNavigationVew.getHeaderView(0);

        // Establecemos el nombre y el email de la cabecera
        TextView name  = (TextView)navHeader.findViewById(R.id.username);
        TextView email = (TextView)navHeader.findViewById(R.id.email);
        name.setText(user.getName());
        email.setText(user.getEmail());

        // Establecemos la imagen del usuario en funcion del sexo
        final CircleImageView profilePic = (CircleImageView)navHeader.findViewById(R.id.profile_pic);
        Bitmap profile = (user.getMan() ?
                BitmapFactory.decodeResource(getResources(), R.drawable.male_icon): BitmapFactory.decodeResource(getResources(), R.drawable.female_icon));

        profilePic.setImageBitmap(profile);

        profilePic.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Activity activity = MainScreenUI.this;

                Intent intent = new Intent(MainScreenUI.this, ProfileUI.class);

                // Sacamos las coordenadas de la imagen
                int[] imageScreenLocation = new int[2];
                profilePic.getLocationInWindow(imageScreenLocation);

                intent.putExtra(Properties.PACKAGE + ".left", imageScreenLocation[0])
                      .putExtra(Properties.PACKAGE + ".top", imageScreenLocation[1])
                      .putExtra(Properties.PACKAGE + ".width", profilePic.getWidth())
                      .putExtra(Properties.PACKAGE + ".height", profilePic.getHeight());

                startActivity(intent);

                // Desactivamos las transiciones por defecto
                activity.overridePendingTransition(0, 0);
            }
        });

        _initDrawerToggle();

        mDrawerLayout.setDrawerListener(mLeftDrawerToggle);
    }

    /**
     * Inicializacion y configuracion del drawer toggle del leftDrawer.
     */
    @SuppressWarnings("deprecation")
    protected void _initDrawerToggle()
    {
        // Inicializamos el control en la action bar.
        mLeftDrawerToggle = new ActionBarDrawerToggle(this
                , mDrawerLayout
                , mToolbar
                , R.string.open_drawer
                , R.string.close_drawer)
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

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset)
            {
                // Offset = 1.0 -> Totalmente abierto
                // Se normalizan los valores entre [0-1] a valores entre [0-0.1]
                float normalizedOffset = 1 - ((0.1f - 0.0f) / (1.0f - 0.0f) * (slideOffset - 1.0f) + 0.1f);

                mProductsFragment.resizeGrid(normalizedOffset);
                mRecommendedFragment.resizeGrid(normalizedOffset);
            }
        };

        mLeftDrawerToggle.setDrawerIndicatorEnabled(false);
        mToolbar.setNavigationIcon(R.drawable.ic_menu);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mDrawerLayout.isDrawerOpen(GravityCompat.START))
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                else
                    mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    /**
     * Metodo para inicializar las animaciones.
     */
    protected void _initAnimations()
    {
        mExplodeAnimation = AnimationUtils.loadAnimation(this, R.anim.explode_animation);
        mImplodeAnimation = AnimationUtils.loadAnimation(this, R.anim.implode_animation);

        mImplodeAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                findViewById(R.id.menu_item_filter).setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        mExplodeAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                findViewById(R.id.menu_item_filter).setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {}

            @Override
            public void onAnimationRepeat(Animation animation) {}
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
                        , getResources().getString(R.string.exit_message)
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
        {
            mLeftDrawerToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        if(mLeftDrawerToggle != null)
        {
            mLeftDrawerToggle.onConfigurationChanged(newConfig);
        }
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
    @SuppressWarnings("unchecked")
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
                overridePendingTransition(R.anim.right_in_animation, R.anim.right_out_animation);
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
            mViewPager.setCurrentItem(1);

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

    class ViewPagerAdapter extends FragmentStatePagerAdapter
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
