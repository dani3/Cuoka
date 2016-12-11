package com.wallakoala.wallakoala.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wallakoala.wallakoala.Beans.User;
import com.wallakoala.wallakoala.Fragments.ProductsFragment;
import com.wallakoala.wallakoala.Fragments.RecommendedFragment;
import com.wallakoala.wallakoala.Properties.Properties;
import com.wallakoala.wallakoala.R;
import com.wallakoala.wallakoala.Singletons.RestClientSingleton;
import com.wallakoala.wallakoala.Singletons.TypeFaceSingleton;
import com.wallakoala.wallakoala.Utils.CustomTypeFaceSpan;
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
    private static final int FILTER_REQUEST           = 0;
    private static final int MODIFICATION_REQUEST     = 1;
    private static final int MANAGE_SHOPS_REQUEST     = 2;
    private static final int MANAGE_FAVORITES_REQUEST = 3;
    private static final int NOTIFICATION_REQUEST     = 4;
    private static final int EXIT_TIME_INTERVAL       = 2000;

    /* Container Views */
    private NavigationView mNavigationVew;
    private DrawerLayout mDrawerLayout;
    private CoordinatorLayout mCoordinatorLayout;
    private ViewPager mViewPager;

    /* Toolbar */
    private Toolbar mToolbar;
    private ActionBarDrawerToggle mDrawerToggle;

    /* Fragments */
    private RecommendedFragment mRecommendedFragment;
    private ProductsFragment mProductsFragment;

    /* SharedPreference */
    private SharedPreferencesManager mSharedPreferencesManager;

    /* ImageView */
    private CircleImageView mProfilePic;

    /* Other */
    private long mBackPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        _initData();
        _initToolbar();
        _initViewPager();
        _initNavigationDrawer();
        _checkForNotifications();
    }

    /**
     * Metodo que inicializa ED's y distintos datos.
     */
    private void _initData()
    {
        mSharedPreferencesManager = new SharedPreferencesManager(this);

        mBackPressed = 0;
    }

    /**
     * Metodo para inicializar la Toolbar
     */
    private void _initToolbar()
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
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        mProductsFragment    = new ProductsFragment();
        mRecommendedFragment = new RecommendedFragment();

        // Añadimos los fragmentos al adapter
        adapter.addFragment(mRecommendedFragment, "DESCUBRE");
        adapter.addFragment(mProductsFragment, "NOVEDADES");
        mViewPager.setAdapter(adapter);

        // Marcamos como activo la segunda pestaña (NOVEDADES)
        mViewPager.setCurrentItem(1);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
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

        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
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
    private void _initNavigationDrawer()
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

        // Establecemos la imagen del usuario en funcion del sexo.
        mProfilePic = (CircleImageView)navHeader.findViewById(R.id.profile_pic);
        Bitmap profile = (user.getMan() ?
                BitmapFactory.decodeResource(getResources(), R.drawable.male_icon): BitmapFactory.decodeResource(getResources(), R.drawable.ic_female_thumbnail));
        mProfilePic.setImageBitmap(profile);

        mProfilePic.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                _openActivityProfile();
            }
        });

        // Cambiamos la fuente de los items del navigation drawer
        Menu m = mNavigationVew.getMenu();
        for (int i = 0; i < m.size(); i++)
        {
            MenuItem mi = m.getItem(i);

            SubMenu subMenu = mi.getSubMenu();
            if (subMenu != null && subMenu.size() > 0)
            {
                for (int j = 0; j < subMenu.size(); j++)
                {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    _applyFontToMenuItem(subMenuItem);
                }
            }

            _applyFontToMenuItem(mi);
        }

        // Listener para cada item del navigation drawer
        mNavigationVew.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                switch (item.getItemId())
                {
                    case (R.id.nav_my_profile):
                        _openActivityProfile();
                        break;

                    case (R.id.nav_my_shops):
                        _openActivityShops();
                        break;

                    case (R.id.nav_logout):
                        _logout();
                        break;

                    case (R.id.nav_favorites):
                        _openActivityFavorites();
                        break;

                    case (R.id.nav_feedback):
                        _openActivityFeedback();
                        break;

                    case (R.id.nav_more_shops):
                        _openActivitySuggested();
                        break;

                    case (R.id.nav_notifications):
                        _openActivityNotifications();
                        break;
                }

                return true;
            }
        });

        _initDrawerToggle();

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    /**
     * Inicializacion y configuracion del drawer toggle del leftDrawer.
     */
    @SuppressWarnings("deprecation")
    private void _initDrawerToggle()
    {
        // Inicializamos el control en la action bar.
        mDrawerToggle = new ActionBarDrawerToggle(this
                , mDrawerLayout
                , mToolbar
                , R.string.open_drawer
                , R.string.close_drawer)
        {
            // Metodo llamado cuando el drawer esta completamente cerrado.
            @Override
            public void onDrawerClosed(View drawerView)
            {
                mDrawerToggle.syncState();
            }

            // Metodo llamado cuando el drawer esta completamente abierto.
            @Override
            public void onDrawerOpened(View drawerView)
            {
                // Crea la llamada a onPrepareOptionsMenu()
                supportInvalidateOptionsMenu();

                mDrawerToggle.syncState();
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

        mDrawerToggle.setDrawerIndicatorEnabled(false);
        mToolbar.setNavigationIcon(R.drawable.ic_menu);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mDrawerLayout.isDrawerOpen(GravityCompat.START))
                {
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                }else {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });
    }

    /**
     * Metodo que comprueba si tiene notificaciones.
     */
    private void _checkForNotifications()
    {
        RestClientSingleton.hasNotification(this, mToolbar, mNavigationVew);
    }

    /**
     * Metodo que abre la pantalla del perfil.
     */
    private void _openActivityProfile()
    {
        Activity activity = MainScreenUI.this;

        Intent intent = new Intent(MainScreenUI.this, ProfileUI.class);

        // Sacamos las coordenadas de la imagen
        int[] imageScreenLocation = new int[2];
        mProfilePic.getLocationInWindow(imageScreenLocation);

        intent.putExtra(Properties.PACKAGE + ".left", imageScreenLocation[0])
              .putExtra(Properties.PACKAGE + ".top", imageScreenLocation[1])
              .putExtra(Properties.PACKAGE + ".width", mProfilePic.getWidth())
              .putExtra(Properties.PACKAGE + ".height", mProfilePic.getHeight());

        startActivityForResult(intent, MODIFICATION_REQUEST);

        // Desactivamos las transiciones por defecto
        activity.overridePendingTransition(0, 0);
    }

    /**
     * Metodo que abre la pantalla de Notificaciones.
     */
    private void _openActivityNotifications()
    {
        Intent intent = new Intent(MainScreenUI.this, NotificationsUI.class);

        // Iniciamos la activity NotificationsUI
        startActivityForResult(intent, NOTIFICATION_REQUEST);

        // Animacion de transicion para pasar de una activity a otra.
        overridePendingTransition(R.anim.right_in_animation, R.anim.right_out_animation);
    }

    /**
     * Metodo que abre la pantalla de Mis tiendas.
     */
    private void _openActivityShops()
    {
        Intent intent = new Intent(MainScreenUI.this, ShopsUI.class);

        // Iniciamos la activity ShopsUI
        startActivityForResult(intent, MANAGE_SHOPS_REQUEST);

        // Animacion de transicion para pasar de una activity a otra.
        overridePendingTransition(R.anim.right_in_animation, R.anim.right_out_animation);
    }

    /**
     * Metodo que abre la pantalla de Mis favoritos.
     */
    private void _openActivityFavorites()
    {
        Intent intent = new Intent(MainScreenUI.this, FavoritesUI.class);

        // Iniciamos la activity FavoritesUI
        startActivityForResult(intent, MANAGE_FAVORITES_REQUEST);

        // Animacion de transicion para pasar de una activity a otra.
        overridePendingTransition(R.anim.right_in_animation, R.anim.right_out_animation);
    }

    /**
     * Metodo que abre la pantalla de Danos tu opinion.
     */
    private void _openActivityFeedback()
    {
        Intent intent = new Intent(MainScreenUI.this, FeedbackUI.class);

        // Iniciamos la activity FavoritesUI
        startActivity(intent);

        // Animacion de transicion para pasar de una activity a otra.
        overridePendingTransition(R.anim.right_in_animation, R.anim.right_out_animation);
    }

    /**
     * Metodo que abre la pantalla de Mis favoritos.
     */
    private void _openActivitySuggested()
    {
        Intent intent = new Intent(MainScreenUI.this, SuggestedUI.class);

        // Iniciamos la activity FavoritesUI
        startActivity(intent);

        // Animacion de transicion para pasar de una activity a otra.
        overridePendingTransition(R.anim.right_in_animation, R.anim.right_out_animation);
    }

    /**
     * Metodo que aplica la fuente a los items del menu del navigation drawer.
     * @param mi: item del menu.
     */
    private void _applyFontToMenuItem(MenuItem mi)
    {
        Typeface font = TypeFaceSingleton.getTypeFace(this, "Existence-StencilLight.otf");

        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypeFaceSpan("" , font), 0 , mNewTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        mi.setTitle(mNewTitle);
    }

    /**
     * Metodo que desloguea al usuario.
     */
    private void _logout()
    {
        mSharedPreferencesManager.insertLoggedIn(false);

        Intent intent = new Intent(this, LoginUI.class);

        startActivity(intent);

        finish();

        overridePendingTransition(R.anim.left_in_animation, R.anim.left_out_animation);
    }

    /**
     * Metodo publico llamado desde un fragmento para abrir la pantalla de las tiendas.
     */
    public void openActivityShops()
    {
        _openActivityShops();
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

        if(mDrawerToggle != null)
        {
            mDrawerToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        if(mDrawerToggle != null)
        {
            mDrawerToggle.onConfigurationChanged(newConfig);
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
    @SuppressWarnings("deprecation")
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        // Si ha ido bien
        if (resultCode == RESULT_OK)
        {
            // Si venimos de los filtros
            if (requestCode == FILTER_REQUEST)
            {
                mViewPager.setCurrentItem(1);

                // Sacamos la cadena de busqueda.
                String searchQuery = data.getStringExtra(Properties.PACKAGE + ".search");

                // Si es null significa que se quiere filtrar.
                if (searchQuery == null)
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
                    Log.d(Properties.TAG, "Busqueda: " + searchQuery);

                    mProductsFragment.processSearch(searchQuery);
                }

            } else if (requestCode == MODIFICATION_REQUEST) {
                // Si venimos de la pantalla del perfil

                // Obtenemos el usuario para actualizar la cabecera
                User user = mSharedPreferencesManager.retreiveUser();

                // Sacamos la cabecera del navigation drawer
                View navHeader = mNavigationVew.getHeaderView(0);

                // Establecemos el nombre y el email de la cabecera
                TextView name  = (TextView)navHeader.findViewById(R.id.username);
                TextView email = (TextView)navHeader.findViewById(R.id.email);
                name.setText(user.getName());
                email.setText(user.getEmail());

            } else if (requestCode == MANAGE_SHOPS_REQUEST) {
                // Si venimos de la pantalla de Mis tiendas

                mViewPager.setCurrentItem(1);

                mProductsFragment.restart();
                mRecommendedFragment.restart();

            } else if (requestCode == MANAGE_FAVORITES_REQUEST) {
                // Si venimos de la pantalla de Mis favoritos

                mProductsFragment.notifyDataSetChanged();
                mRecommendedFragment.notifyDataSetChanged();

            } else if (requestCode == NOTIFICATION_REQUEST) {
                // Si venimos de la pantalla de Notificaciones con OK, es que todas las notificaciones se han leido.

                // Cambiamos el Hamburger Icon
                mToolbar.setNavigationIcon(R.drawable.ic_menu);

                // Cambiamos el icono de las notificaciones en el menu
                MenuItem menuItem = mNavigationVew.getMenu().findItem(R.id.nav_notifications);
                menuItem.setIcon(this.getResources().getDrawable(R.drawable.ic_notification));
            }
        }

        if (resultCode == RESULT_CANCELED)
        {
            // Desde las notificaciones puede no haber marcado las notificaciones como leidas, pero si haber modificado sus tiendas.
            if (requestCode == NOTIFICATION_REQUEST)
            {
                if (data.getBooleanExtra("shops", false))
                {
                    mViewPager.setCurrentItem(1);

                    mProductsFragment.restart();
                    mRecommendedFragment.restart();
                }
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
