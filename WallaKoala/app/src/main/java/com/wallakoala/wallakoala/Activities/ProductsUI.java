package com.wallakoala.wallakoala.Activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.wallakoala.wallakoala.Adapters.ProductsGridAdapter;
import com.wallakoala.wallakoala.Beans.ColorVariant;
import com.wallakoala.wallakoala.Beans.Product;
import com.wallakoala.wallakoala.Properties.Properties;
import com.wallakoala.wallakoala.R;
import com.wallakoala.wallakoala.Utils.SharedPreferencesManager;
import com.wallakoala.wallakoala.Utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @class Pantalla principal de la app, donde se muestran los productos
 * Created by Daniel Mancebo on 09/11/2015.
 */

public class ProductsUI extends AppCompatActivity
{
    /* Constants */
    protected static final int EXIT_TIME_INTERVAL = 2000;
    protected static final int NUM_PRODUCTS_DISPLAYED = 10;
    protected static final int MIN_PRODUCTS = 8;
    protected static final int FILTER_REQUEST = 1;
    protected static boolean MAN;
    protected static boolean FIRST_CONNECTION;
    protected static boolean ON_CREATE_FLAG;
    protected static int NUMBER_OF_CORES;
    protected static int DAYS_OFFSET;
    protected static String SEARCH_QUERY;
    protected enum STATE
    {
        ERROR
                { @Override
                  public String toString() { return "ERROR"; }
                },
        LOADING
                { @Override
                  public String toString() { return "LOADING"; }
                },
        NODATA
                { @Override
                  public String toString() { return "NO_DATA"; }
                },
        NORMAL
                { @Override
                  public String toString() { return "NORMAL"; }
                },
    }

    /* Data */
    protected List<ConcurrentMap<String, List<Product>>> mProductsListMap;
    protected Map<String, List<Product>> mProductsNonFilteredMap;
    protected Map<String, Object> mFilterMap;
    protected Deque<Product> mProductsCandidatesDeque;
    protected List<Product> mProductsDisplayedList;
    protected List<String> mShopsList;

    /* Container Views */
    protected RecyclerView mProductsRecyclerView;
    protected NavigationView mLeftNavigationVew;

    /* Layouts */
    protected DrawerLayout mDrawerLayout;
    protected CoordinatorLayout mCoordinatorLayout;

    /* LayoutManagers */
    protected StaggeredGridLayoutManager mStaggeredGridLayoutManager;

    /* Views */
    protected ActionBarDrawerToggle mLeftDrawerToggle;
    protected TextView mToolbarTextView;
    protected TextView mNoDataTextView;
    protected View mLoadingView;
    protected View mLoadingServerView;

    /* Adapters */
    protected ProductsGridAdapter mProductAdapter;

    /* Animations */
    protected Animation mMoveAndFadeAnimation;
    protected Animation mShowFromDown, mHideFromUp;

    /* Snackbar */
    protected Snackbar mSnackbar;

    /* Toolbar */
    protected Toolbar mToolbar;

    /* SharedPreferences */
    protected SharedPreferencesManager mSharedPreferences;

    /* AsynTasks */
    protected AsyncTask mConnectToServer, mRetreiveProductsFromServer;

    /* Others */
    protected Menu mMenu;
    protected STATE mState;
    protected int mProductsInsertedPreviously, start, count;
    protected long mBackPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Especificamos el layout 'products_grid.xml'
        setContentView(R.layout.products_grid);

        _initData();
        _initAuxViews();
        _initToolbar();
        _initNavigationDrawers();
        _initAnimations();

        mConnectToServer = new ConnectToServer().execute();
    }

    /**
     * Inicializacion de las distintias estructuras de datos.
     */
    protected void _initData()
    {
        mSharedPreferences = new SharedPreferencesManager(this);

        mProductsListMap         = new ArrayList<>();
        mFilterMap               = _initFilterMap();
        mProductsNonFilteredMap  = new HashMap<>();
        mProductsDisplayedList   = new ArrayList<>();
        mProductsCandidatesDeque = new ArrayDeque<>();
        mShopsList               = new ArrayList<>();

        for (String shop : mSharedPreferences.retreiveShops())
            mShopsList.add(shop);

        start = count = 0;
        mBackPressed = 0;

        SEARCH_QUERY = null;

        DAYS_OFFSET = 0;
        MAN = mSharedPreferences.retreiveMan();

        NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

        FIRST_CONNECTION = true;
        ON_CREATE_FLAG = true;

        Log.d(Properties.TAG, "Numero de procesadores: " + NUMBER_OF_CORES);
    }

    /**
     * Metodo que reinicializa ciertas variables.
     */
    protected void _reinitializeData()
    {
        mProductsListMap         = new ArrayList<>();
        mProductsNonFilteredMap  = new HashMap<>();
        mProductsDisplayedList   = new ArrayList<>();
        mProductsCandidatesDeque = new ArrayDeque<>();

        start = count = 0;
        mBackPressed = 0;

        DAYS_OFFSET = 0;

        FIRST_CONNECTION = true;
    }

    /**
     * Metodo que inicializa el mapa de filtros.
     * @return: Mapa con los filtros actuales.
     */
    protected Map<String, Object> _initFilterMap()
    {
        Map<String, Object> map = new HashMap<>();

        return map;
    }

    /**
     * Inicializacion de vistas auxiliares.
     */
    protected void _initAuxViews()
    {
        // CoordinatorLayout
        mCoordinatorLayout = (CoordinatorLayout)findViewById(R.id.coordinator_layout);

        // LoaderView
        mLoadingView       = findViewById(R.id.avloadingIndicatorView);
        mLoadingServerView = findViewById(R.id.loading);

        mLoadingServerView.setVisibility(View.GONE);

        // TextView que muestran que no hay productos disponibles
        mNoDataTextView = (TextView)findViewById(R.id.nodata_textview);
    }

    /**
     * Inicializacion de la toolbar.
     */
    protected void _initToolbar()
    {
        mToolbar         = (Toolbar)findViewById(R.id.appbar);
        mToolbarTextView = (TextView)findViewById(R.id.toolbar_textview);

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    /**
     * Inicializacion y configuracion del recyclerView.
     */
    protected void _initRecyclerView()
    {
        mProductsRecyclerView = (RecyclerView)findViewById(R.id.grid_recycler);
        mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mProductAdapter = new ProductsGridAdapter(this
                                    , mProductsDisplayedList
                                    , mProductsCandidatesDeque.size() + mProductsDisplayedList.size()
                                    , mCoordinatorLayout);

        mProductsRecyclerView.setHasFixedSize(true);
        mProductsRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);
        mProductsRecyclerView.setAdapter(mProductAdapter);
        mProductsRecyclerView.setVisibility(View.VISIBLE);
        mProductsRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            int verticalOffset;
            boolean scrollingUp;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (scrollingUp) {
                        if (verticalOffset > mToolbar.getHeight())
                            _toolbarAnimateHide();

                        else
                            _toolbarAnimateShow();

                    } else if ((mToolbar.getTranslationY() < (mToolbar.getHeight() * -0.6f) &&
                            (verticalOffset > mToolbar.getHeight())))
                        _toolbarAnimateHide();

                    else
                        _toolbarAnimateShow();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                verticalOffset += dy;
                scrollingUp = dy > 0;

                int toolbarYOffset = (int) (dy - mToolbar.getTranslationY());

                mToolbar.animate().cancel();

                if (scrollingUp) {
                    // Animacion de la toolbar
                    if (toolbarYOffset < mToolbar.getHeight())
                        mToolbar.setTranslationY(-toolbarYOffset);
                    else
                        mToolbar.setTranslationY(-mToolbar.getHeight());

                    // Detectamos cuando llegamos abajo para cargar nuevos productos
                    int[] lastItemsPosition = new int[2];
                    mStaggeredGridLayoutManager.findLastCompletelyVisibleItemPositions(lastItemsPosition);

                    if ((lastItemsPosition[0] >= mProductsDisplayedList.size() - 2) ||
                            (lastItemsPosition[1] >= mProductsDisplayedList.size() - 1))
                    {
                        if (!mProductsCandidatesDeque.isEmpty())
                        {
                            // Sacamos los siguientes productos
                            getNextProductsToBeDisplayed();

                            // Sacamos el indice del primer producto a insertar
                            start = mProductsDisplayedList.size() - mProductsInsertedPreviously;
                            count = mProductsInsertedPreviously;

                            // Actualizamos la lista de productos del adapter
                            mProductAdapter.updateProductList(mProductsDisplayedList);

                            // Notificamos el cambio
                            mProductAdapter.notifyItemRangeInserted(start, count);

                        } else {
                            if ((mState != STATE.LOADING) && (DAYS_OFFSET >= 0))
                            {
                                DAYS_OFFSET++;

                                mConnectToServer = new ConnectToServer().execute();
                            }
                        }
                    }

                } else if (toolbarYOffset < 0) {
                    mToolbar.setTranslationY(0);

                } else
                    mToolbar.setTranslationY(-toolbarYOffset);
            }
        });
    }

    protected void _toolbarAnimateShow()
    {
        mToolbar.animate()
                .translationY(0)
                .setInterpolator(new LinearInterpolator())
                .setDuration(180);
    }

    protected void _toolbarAnimateHide()
    {
        mToolbar.animate()
                .translationY(-mToolbar.getHeight())
                .setInterpolator(new LinearInterpolator())
                .setDuration(180);
    }

    /**
     * Inicializacion y configuracion de los navigation drawers.
     */
    protected void _initNavigationDrawers()
    {
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
     * Metodo que inicializa las animaciones.
     */
    protected void _initAnimations()
    {
        mMoveAndFadeAnimation = AnimationUtils.loadAnimation(ProductsUI.this
                , R.anim.translate_and_fade);

        mHideFromUp = AnimationUtils.loadAnimation(ProductsUI.this
                , R.anim.hide_to_down);

        mShowFromDown = AnimationUtils.loadAnimation(ProductsUI.this
                , R.anim.show_up_from_down);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        // Si no venimos del onCreate (ON_CREATE_FLAG = FALSE) significa que venimos de
        // la pantalla de un producto, por lo que hay que restaurar el pie de foto.
        if ((!ON_CREATE_FLAG) && (mProductAdapter != null) && (mProductAdapter.productClicked()))
        {
            Log.d(Properties.TAG, "Volviendo de ProductUI");
            mProductAdapter.restoreProductFooter();

        } else if (ON_CREATE_FLAG) {
            ON_CREATE_FLAG = false;
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
        // Guardamos el menu para poder acceder a los expandableItems mas adelante
        this.mMenu = menu;

        // Inflamos la ActionBar
        getMenuInflater().inflate(R.menu.toolbar_menu_grid, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.menu_item_filter)
        {
            if ((mState != STATE.LOADING) && (mLoadingView.getVisibility() == View.GONE))
            {
                Intent intent = new Intent(ProductsUI.this, FilterUI.class);

                intent.putExtra(Properties.PACKAGE + ".newness", (Boolean)mFilterMap.get("newness"));
                intent.putExtra(Properties.PACKAGE + ".sections", (ArrayList<String>)mFilterMap.get("sections"));
                intent.putExtra(Properties.PACKAGE + ".colors", (ArrayList<String>)mFilterMap.get("colors"));
                intent.putExtra(Properties.PACKAGE + ".shops", (ArrayList<String>)mFilterMap.get("shops"));
                intent.putExtra(Properties.PACKAGE + ".minPrice", (Integer)mFilterMap.get("minPrice"));
                intent.putExtra(Properties.PACKAGE + ".maxPrice", (Integer)mFilterMap.get("maxPrice"));
                intent.putExtra(Properties.PACKAGE + ".man", MAN);

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

        if (resultCode == RESULT_OK)
        {
            SEARCH_QUERY = data.getStringExtra(Properties.PACKAGE + ".search");

            if (SEARCH_QUERY == null)
            {
                Log.d(Properties.TAG, "Filtro establecido:");

                mFilterMap.put("newness", data.getBooleanExtra(Properties.PACKAGE + ".newness", false));
                mFilterMap.put("sections", data.getSerializableExtra(Properties.PACKAGE + ".sections"));
                mFilterMap.put("colors", data.getSerializableExtra(Properties.PACKAGE + ".colors"));
                mFilterMap.put("shops", data.getSerializableExtra(Properties.PACKAGE + ".shops"));
                mFilterMap.put("minPrice", data.getIntExtra(Properties.PACKAGE + ".minPrice", -1));
                mFilterMap.put("maxPrice", data.getIntExtra(Properties.PACKAGE + ".maxPrice", -1));

                Log.d(Properties.TAG, " Novedades = " + Boolean.toString((boolean) mFilterMap.get("newness")));
                Log.d(Properties.TAG, " Precio Min = " + Integer.toString((int) mFilterMap.get("minPrice")));
                Log.d(Properties.TAG, " Precio Max = " + Integer.toString((int) mFilterMap.get("maxPrice")));

                List<String> shopsList = (ArrayList<String>) mFilterMap.get("shops");
                if (shopsList != null)
                    for (String shop : shopsList)
                        Log.d(Properties.TAG, " Tienda = " + shop);

                List<String> sectionsList = (ArrayList<String>) mFilterMap.get("sections");
                if (sectionsList != null)
                    for (String section : sectionsList)
                        Log.d(Properties.TAG, " Seccion = " + section);

                List<String> colorsList = (ArrayList<String>) mFilterMap.get("colors");
                if (colorsList != null)
                    for (String color : colorsList)
                        Log.d(Properties.TAG, " Color = " + color);

                if (shopsList != null)
                    if (shopsList.size() == mShopsList.size())
                        if (shopsList.containsAll(mShopsList))
                            shopsList = null;

                boolean newness = (boolean) mFilterMap.get("newness");
                int from = (int) mFilterMap.get("minPrice");
                int to = (int) mFilterMap.get("maxPrice");

                _reinitializeData();

                if (mProductsRecyclerView != null)
                    mProductsRecyclerView.setVisibility(View.GONE);

                // Se comprueba si los filtros son los por defecto, si es asi, se realiza una peticion normal.
                if ((shopsList == null)
                        && (colorsList == null)
                        && (sectionsList == null)
                        && (!newness)
                        && (from < 0)
                        && (to < 0))
                {
                    Log.d(Properties.TAG, "Filtros por defecto");

                    mFilterMap = _initFilterMap();

                    mConnectToServer = new ConnectToServer().execute();

                } else {
                    // Lo ponemos a -1 para detectar cuando estamos en los filtros
                    DAYS_OFFSET = -1;

                    mRetreiveProductsFromServer = new RetreiveProductsFromServer().execute();

                }

            } else {
                Log.d(Properties.TAG, "Busqueda: " + SEARCH_QUERY);

                _reinitializeData();

                mFilterMap = _initFilterMap();

                if (mProductsRecyclerView != null)
                    mProductsRecyclerView.setVisibility(View.GONE);

                // Lo ponemos a -1 para detectar cuando estamos en los filtros
                DAYS_OFFSET = -1;

                mRetreiveProductsFromServer = new RetreiveProductsFromServer().execute();

            }

        }

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
                mSnackbar = Snackbar.make(mCoordinatorLayout
                                        , getResources().getString( R.string.exit_message )
                                        , Snackbar.LENGTH_SHORT);

                mSnackbar.show();
            }

            mBackPressed = System.currentTimeMillis();

        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        if (mConnectToServer != null)
            if (!mConnectToServer.isCancelled())
                mConnectToServer.cancel(true);

        if (mRetreiveProductsFromServer != null)
            if (!mRetreiveProductsFromServer.isCancelled())
                mRetreiveProductsFromServer.cancel(true);
    }

    /**
     * Tarea en segundo plano que contacta con el servidor para traer nuevos productos
     * que cumplan los filtros establecidos.
     */
    private class RetreiveProductsFromServer extends AsyncTask<String, Void, Void>
    {
        private ThreadPoolExecutor executor;
        private CompletionService<String> completionService;
        private List<String> content = new ArrayList<>();
        private String error = null;
        private boolean EMPTY;

        @Override
        protected void onPreExecute()
        {
            if (mState != STATE.LOADING)
                _loading(true, true);

            // Creamos un executor, con cuatro veces mas de threads que nucleos fisicos.
            executor = new ThreadPoolExecutor(NUMBER_OF_CORES * 4
                    , NUMBER_OF_CORES * 4
                    , 60L
                    , TimeUnit.SECONDS
                    , new LinkedBlockingQueue<Runnable>());

            completionService = new ExecutorCompletionService<>(executor);
        }

        @Override
        protected Void doInBackground(String... params)
        {
            try
            {
                List<String> aux = (ArrayList<String>)mFilterMap.get("shops");
                List<String> shopsList = (aux == null) ? mShopsList : aux;

                // Creamos un thread por cada tienda a la que tenemos que conectarnos.
                for (int i = 0; i < shopsList.size(); i++)
                {
                    ConnectionFilterTask connectionFilterTask = null;
                    ConnectionSearchTask connectionSearchTask = null;

                    if (SEARCH_QUERY == null)
                    {
                        connectionFilterTask = new ConnectionFilterTask(shopsList.get(i));
                        completionService.submit(connectionFilterTask);

                    } else {
                        connectionSearchTask = new ConnectionSearchTask(shopsList.get(i));
                        completionService.submit(connectionSearchTask);
                    }

                    if (isCancelled())
                        return null;
                }

                // Metemos en content el resultado de cada uno
                for (int i = 0; i < shopsList.size(); i++)
                {
                    String future = completionService.take().get();
                    if (future != null)
                        content.add(future);

                    if (isCancelled())
                        return null;
                }

                EMPTY = content.isEmpty();

            } catch(Exception ex)  {
                error = ex.getMessage();

            } finally {
                executor.shutdown();
            }

            return null;

        }

        @Override
        protected void onPostExecute(Void unused)
        {
            if (error != null)
            {
                _loading(false, false);

                _errorConnectingToServer(true);

            } else {
                if (!EMPTY)
                    new MultithreadConversion().execute(content);

            }
        }

    } /* [END RetreiveProductsFromServer] */

    /**
     * Task que realiza una peticion al servidor con los filtros actuales.
     */
    private class ConnectionFilterTask implements Callable<String>
    {
        private String shop;

        public ConnectionFilterTask(String shop)
        {
            this.shop = shop;
        }

        @Override
        public String call()
        {
            BufferedReader reader = null;
            DataOutputStream writer = null;
            URL url;

            try
            {
                String fixedURL = Utils.fixUrl(Properties.SERVER_URL + ":" + Properties.SERVER_SPRING_PORT + "/filter/" + shop);

                Log.d(Properties.TAG, "Conectando con: " + fixedURL);

                url = new URL(fixedURL);

                if (url != null)
                {
                    URLConnection conn = url.openConnection();

                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);

                    conn.connect();

                    // Creamos el JSON con los filtros
                    JSONObject jsonObject = new JSONObject();

                    List<String> sectionsList = (ArrayList<String>)mFilterMap.get("sections");
                    List<String> colorsList   = (ArrayList<String>)mFilterMap.get("colors");

                    jsonObject.put("newness", mFilterMap.get("newness"));
                    jsonObject.put("man", MAN);
                    jsonObject.put("priceFrom", mFilterMap.get("minPrice"));
                    jsonObject.put("priceTo", mFilterMap.get("maxPrice"));
                    jsonObject.put("colors", new JSONArray(colorsList));
                    jsonObject.put("sections", new JSONArray(sectionsList));

                    Log.d(Properties.TAG, "JSON de filtros:\n    " + jsonObject.toString());

                    // Enviamos el JSON
                    writer = new DataOutputStream(conn.getOutputStream());

                    String str = jsonObject.toString();
                    byte[] data = str.getBytes("UTF-8");

                    writer.write(data);
                    writer.flush();

                    // Obtenemos la respuesta del servidor
                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line = "";

                    // Leemos la respuesta
                    while ((line = reader.readLine()) != null)
                        sb.append(line + "");

                    return sb.toString();
                }

            } catch(Exception ex) {
                Log.d(Properties.TAG, "Error conectando con el servidor");

            } finally {
                try
                {
                    if (reader != null)
                        reader.close();

                    if (writer != null)
                        writer.close();

                } catch ( IOException e ) {
                    Log.d(Properties.TAG, "Error cerrando conexion");

                }

            }

            return null;

        }

    } /* [END ConnectionFilterTask] */

    /**
     * Task que realiza una peticion al servidor con la busqueda introducida.
     */
    private class ConnectionSearchTask implements Callable<String>
    {
        private String shop;

        public ConnectionSearchTask(String shop)
        {
            this.shop = shop;
        }

        @Override
        public String call()
        {
            BufferedReader reader = null;
            URL url = null;

            try {
                String fixedURL = Utils.fixUrl(Properties.SERVER_URL + ":" + Properties.SERVER_SPRING_PORT
                        + "/search/" + shop + "/" + MAN + "/" + SEARCH_QUERY);

                Log.d(Properties.TAG, "Realizando busqueda: " + fixedURL);

                url = new URL(fixedURL);

                if (url != null)
                {
                    URLConnection conn = url.openConnection();

                    // Obtenemos la respuesta del servidor
                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line = "";

                    // Leemos la respuesta
                    while ((line = reader.readLine()) != null)
                        sb.append(line + "");

                    // Devolvemos la respuesta
                    return sb.toString();
                }

            } catch (Exception e) {
                Log.d(Properties.TAG, "Error realizando busqueda con " + shop);

            } finally {
                try {
                    if (reader != null)
                        reader.close();

                } catch (IOException e) {
                    Log.d(Properties.TAG, "Error cerrando conexion con " + shop);

                }

            }

            return null;
        }
    }

    /**
     * Metodo que actualiza la cola de candidatos, realiza una lectura del mapa de productos como un RoundRobin.
     */
    protected void updateCandidates()
    {
        // Mapa de indices para trackear por donde nos hemos quedado en la iteracion anterior.
        Map<String, Integer> indexMap = new HashMap<>();
        boolean finished = false;
        boolean turn = true;

        // Inicializar mapa de indices con todos a 0.
        for (String key : mProductsListMap.get(mProductsListMap.size()-1).keySet())
            indexMap.put(key, 0);

        Iterator<String> iterator = mProductsListMap.get(mProductsListMap.size()-1).keySet().iterator();

        // Mientras queden productos pendientes.
        while (!finished)
        {
            String key = iterator.next();

            // Sacamos el indice de donde nos quedamos y la lista de productos.
            int index = indexMap.get(key);
            List<Product> list = mProductsListMap.get(mProductsListMap.size()-1).get(key);

            // Mientras queden productos y no encontremos un producto mostrable.
            while((index < list.size()) && (turn))
            {
                mProductsCandidatesDeque.addLast(list.get(index++));

                turn = false;
            } // while #2

            // Actualizamos el mapa de indices.
            indexMap.put(key, index);

            turn = true;

            // Si se ha terminado el recorrido, lo iniciamos de nuevo.
            if (!iterator.hasNext())
                iterator = mProductsListMap.get(mProductsListMap.size()-1).keySet().iterator();

            finished = _checkIfFinished(indexMap);

        } // while #1

        Log.d(Properties.TAG, "Lista de candidatos: " + mProductsCandidatesDeque.size());
    }

    /**
     * Metodo que inserta los productos en la cola ordenados.
     */
    protected void getNextProductsToBeDisplayed()
    {
        mProductsInsertedPreviously = NUM_PRODUCTS_DISPLAYED;

        // Si no hay tantos suficientes productos en la cola...
        if (NUM_PRODUCTS_DISPLAYED > mProductsCandidatesDeque.size())
            mProductsInsertedPreviously = mProductsCandidatesDeque.size();

        for (int i = 0; i < mProductsInsertedPreviously; i++)
        {
            mProductsDisplayedList.add(mProductsCandidatesDeque.getFirst());

            mProductsCandidatesDeque.removeFirst();
        }

        Log.d(Properties.TAG, "Lista de candidatos: " + mProductsCandidatesDeque.size());
        Log.d(Properties.TAG, "Lista de mostrados: " + mProductsDisplayedList.size());
    }

    /**
     * Metodo que convierte la lista de JSON en productos y los inserta en las distintas ED's.
     * @param jsonArray: lista de JSON a convertir.
     * @throws JSONException
     */
    protected void convertJSONtoProduct(JSONArray jsonArray) throws JSONException
    {
        List<Product> productsList = new ArrayList<>();
        List<JSONObject> jsonList = new ArrayList<>();
        String key = null;

        for (int j = 0; j < jsonArray.length(); j++)
        {
            JSONObject js = jsonArray.getJSONObject(j);

            jsonList.add(js);
        }

        for(JSONObject jsonObject : jsonList)
        {
            String name        = jsonObject.getString("2");
            String shop = key  = jsonObject.getString("3");
            String section     = jsonObject.getString("4");
            double price       = jsonObject.getDouble("1");
            String link        = jsonObject.getString("5");
            String description = jsonObject.getString("7");

            JSONArray jsColors = jsonObject.getJSONArray("6");
            List<ColorVariant> colors = new ArrayList<>();
            for(int i = 0; i < jsColors.length(); i++)
            {
                JSONObject jsColor = jsColors.getJSONObject(i);

                String reference = jsColor.getString("1");
                String colorName = jsColor.getString("2");
                String colorPath = jsColor.getString("4");
                short numerOfImages = (short)jsColor.getInt("3");

                colors.add(new ColorVariant(reference, colorName, colorPath, numerOfImages));
            }

            Product product = new Product(name, shop, section, price, link, description, colors);

            if (product.isOkay())
                productsList.add(product);

        }

        mProductsListMap.get(mProductsListMap.size()-1).put(key, productsList);
    }

    /**
     * Tarea en segundo plano que descargara la lista de JSON del servidor en paralelo.
     * Si falla alguna conexion no pasa nada ya que se ignora, sin embargo, si fallan
     * todas las conexiones, se muestra la SnackBar para reintentar.
     */
    private class ConnectToServer extends AsyncTask<String, Void, Void>
    {
        private ThreadPoolExecutor executor;
        private CompletionService<String> completionService;

        private List<String> content;

        private String error = null;

        @Override
        protected void onPreExecute()
        {
            if (mState != STATE.LOADING)
                _loading(true, true);

            // Creamos un executor, con cuatro veces mas de threads que nucleos fisicos.
            executor = new ThreadPoolExecutor(NUMBER_OF_CORES * 4
                    , NUMBER_OF_CORES * 4
                    , 60L
                    , TimeUnit.SECONDS
                    , new LinkedBlockingQueue<Runnable>());

            completionService = new ExecutorCompletionService<>(executor);

            content = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(String... shops)
        {
            try
            {
                // Creamos un thread por cada tienda a la que tenemos que conectarnos.
                for (int i = 0; i < mShopsList.size(); i++)
                {
                    ConnectionTask connectionTask = new ConnectionTask(i);

                    completionService.submit(connectionTask);

                    if (isCancelled())
                        return null;
                }

                // Metemos en content el resultado de cada uno
                for (int i = 0; i < mShopsList.size(); i++)
                {
                    String future = completionService.take().get();
                    if (future != null)
                        content.add(future);

                    if (isCancelled())
                        return null;
                }

                // Si content es vacio, es que han fallado todas las conexiones.
                if (content.isEmpty())
                {
                    error = "Imposible conectar con el servidor";
                    Log.d(Properties.TAG, error);
                }

            } catch(Exception ex)  {
                error = ex.getMessage();

            } finally {
                executor.shutdown();

            }

            return null;

        } // doInBackground

        @Override
        protected void onPostExecute(Void unused)
        {
            if (error != null)
            {
                _loading(false, false);

                _errorConnectingToServer(false);

            } else {
                new MultithreadConversion().execute(content);

            }

        } // onPostExecute

    } /* [END ConnectToServer] */

    /**
     * Task que se conecta al servidor y trae una tienda determinada. Devuelve un string si ha ido bien, null EOC.
     * En caso de que falle, se captura la excepcion y se devuelve null. El hecho de que falle una conexion no deberia
     * parar el flujo entero.
     */
    private class ConnectionTask implements Callable<String>
    {
        private int myPos;

        public ConnectionTask( int pos )
        {
            myPos = pos;
        }

        @Override
        public String call()
        {
            BufferedReader reader = null;
            URL url = null;

            try {
                String fixedURL = Properties.SERVER_URL + ":" + Properties.SERVER_SPRING_PORT
                        + "/products/" + mShopsList.get(myPos).replaceAll(" ", "%20") + "/" + MAN + "/" + DAYS_OFFSET;

                Log.d(Properties.TAG, "Conectando con: " + fixedURL
                        + " para traer los productos de hace " + Integer.toString(DAYS_OFFSET) + " dias");

                url = new URL(fixedURL);

                if (url != null)
                {
                    URLConnection conn = url.openConnection();

                    // Obtenemos la respuesta del servidor
                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line = "";

                    // Leemos la respuesta
                    while ((line = reader.readLine()) != null)
                        sb.append(line + "");

                    // Devolvemos la respuesta
                    return sb.toString();
                }

            } catch (Exception e) {
                Log.d(Properties.TAG, "Error conectando con " + mShopsList.get(myPos));

            } finally {
                try {
                    if (reader != null)
                        reader.close();

                } catch (IOException e) {
                    Log.d(Properties.TAG, "Error cerrando conexion con " + mShopsList.get(myPos));

                }

            }

            return null;
        }

    } /* [END ConnectionTask] */

    /**
     * Tarea en segundo plano que convertira concurrentemente el array de JSONs.
     */
    private class MultithreadConversion extends AsyncTask<List<String>, Void, Void>
    {
        private ThreadPoolExecutor executor;
        private CompletionService<Boolean> completionService;

        private String error = null;

        @Override
        protected void onPreExecute()
        {
            // Creamos un executor, con cuatro veces mas de threads que nucleos fisicos.
            executor = new ThreadPoolExecutor(NUMBER_OF_CORES * 4
                    , NUMBER_OF_CORES * 4
                    , 60L
                    , TimeUnit.SECONDS
                    , new LinkedBlockingQueue<Runnable>());

            completionService = new ExecutorCompletionService<>(executor);
        }

        @Override
        protected Void doInBackground(List<String>... params)
        {
            List<String> content = params[0];

            try
            {
                mProductsListMap.add(new ConcurrentHashMap<String, List<Product>>());

                // Creamos un callable por cada tienda
                for (int i = 0; i < content.size(); i++)
                {
                    Log.d(Properties.TAG, "Tamano en bytes: " + (content.get(i).getBytes().length / 1000) + "kB");

                    ConversionTask task = new ConversionTask(new JSONArray(content.get(i)));

                    completionService.submit(task);
                }

                // Nos quedamos esperando a que terminen los threads
                for (int i = 0; i < content.size(); i++)
                    completionService.take();

                // Liberamos el executor ya que no hara falta.
                executor.shutdown();

                // Una vez cargados los productos, actualizamos la cola de candidatos...
                updateCandidates();
                // ... y actualizamos la lista de los que se van a mostrar
                getNextProductsToBeDisplayed();

                // Si no es la primera conexion
                if (!FIRST_CONNECTION)
                {
                    // Sacamos el indice del primer producto a insertar
                    start = mProductsDisplayedList.size() - mProductsInsertedPreviously;
                    count = mProductsInsertedPreviously;

                    // Actualizamos la lista de productos del adapter
                    mProductAdapter.updateProductList(mProductsDisplayedList);

                    // Notificamos el cambio
                    mProductAdapter.notifyItemRangeInserted(start, count);
                }

            } catch (Exception e) {
                error = e.getMessage();

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused)
        {
            if (error != null)
            {
                _loading(false, false);

                _errorConnectingToServer(false);

            } else {

                if ((mProductsListMap.get(mProductsListMap.size()-1).isEmpty()) && (DAYS_OFFSET >= 0))
                {
                    DAYS_OFFSET++;

                    mConnectToServer = new ConnectToServer().execute();

                } else if (mProductsCandidatesDeque.isEmpty() && mProductsDisplayedList.size() < MIN_PRODUCTS && (DAYS_OFFSET >= 0)) {

                    DAYS_OFFSET++;

                    mConnectToServer = new ConnectToServer().execute();

                } else {

                    if ((mProductsListMap.get(mProductsListMap.size()-1).isEmpty() && (DAYS_OFFSET == -1)))
                    {
                        _noData(true);

                    } else {
                        _loading(false, true);

                    }
                }
            }
        }

    } /* [END MultithreadConversion] */

    /**
     * Task que convierte una array de JSON en una lista de productos. Devuelve true cuando ha terminado.
     */
    private class ConversionTask implements Callable<Boolean>
    {
        private JSONArray mJsonArray;

        public ConversionTask(JSONArray jsonArray)
        {
            mJsonArray = jsonArray;
        }

        @Override
        public Boolean call()
        {
            try
            {
                convertJSONtoProduct(mJsonArray);

            } catch (Exception e) {
                return false;
            }

            return true;
        }
    } /* [END ConversionTask] */

    /**
     * Metodo que crea maneja la interfaz en funcion de si esta cargando o no los productos.
     * @param loading: true indica que se inicia la carga, false que ha terminado.
     */
    protected void _loading(boolean loading, boolean ok)
    {
        // Si hemos terminado de cargar los productos
        if (!loading)
        {
            if (ok)
            {
                if (FIRST_CONNECTION)
                {
                    // Cuando termine la animacion de la view de carga, iniciamos la del recyclerView
                    mMoveAndFadeAnimation.setAnimationListener(new Animation.AnimationListener()
                    {
                        @Override
                        public void onAnimationStart(Animation animation) {}

                        @Override
                        public void onAnimationEnd(Animation animation)
                        {
                            mLoadingView.setVisibility(View.GONE);

                            // La animacion de cada item solo esta disponible para 5.0+
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
                            {
                                _initRecyclerView();
                                mProductsRecyclerView.startAnimation(AnimationUtils.loadAnimation(ProductsUI.this
                                        , android.R.anim.fade_in));

                            } else {
                                _initRecyclerView();
                                mProductsRecyclerView.scheduleLayoutAnimation();
                            }
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {}
                    });

                    mLoadingView.startAnimation(mMoveAndFadeAnimation);

                    mState = STATE.NORMAL;

                    FIRST_CONNECTION = false;

                } else {
                    mHideFromUp.setAnimationListener(new Animation.AnimationListener()
                    {
                        @Override
                        public void onAnimationStart(Animation animation) {}

                        @Override
                        public void onAnimationEnd(Animation animation)
                        {
                            mLoadingServerView.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {}
                    });

                    mLoadingServerView.startAnimation(mHideFromUp);

                    mState = STATE.NORMAL;
                }

            } else {
                mLoadingView.setVisibility(View.GONE);
                mLoadingServerView.setVisibility(View.GONE);
            }

        } else if (FIRST_CONNECTION) {
            _noData(false);

            // Pantalla de carga cuando es la primera conexion
            mLoadingView.setVisibility(View.VISIBLE);

            mState = STATE.LOADING;

        } else {
            // Icono de carga
            mLoadingServerView.startAnimation(mShowFromDown);
            mLoadingServerView.setVisibility(View.VISIBLE);

            mState = STATE.LOADING;
        }

        Log.d(Properties.TAG, "Estado = " + mState.toString());
    }

    /**
     * Metodo que muestra un mensaje cuando no hay ningun producto que mostrar.
     * @param noData: true indica que no hay ningun producto que mostrar.
     */
    protected void _noData(boolean noData)
    {
        if (!noData)
        {
            mNoDataTextView.setVisibility(View.GONE);

            mState = STATE.NORMAL;

        } else {
            mNoDataTextView.setVisibility(View.VISIBLE);
            mLoadingView.setVisibility(View.GONE);

            mState = STATE.NODATA;
        }

        Log.d(Properties.TAG, "Estado = " + mState.toString());
    }

    /**
     * Metodo que muestra un mensaje cuando se ha producido un error al conectar con el server.
     * @param isFiltering: true si el error se ha producido con los filtros.
     */
    protected void _errorConnectingToServer(boolean isFiltering)
    {
        if (!isFiltering)
        {
            mSnackbar = Snackbar.make(mCoordinatorLayout
                    , getResources().getString( R.string.error_message )
                    , Snackbar.LENGTH_INDEFINITE ).setAction("Reintentar", new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    mConnectToServer = new ConnectToServer().execute();
                }
            });

        } else {
            mSnackbar = Snackbar.make(mCoordinatorLayout
                    , getResources().getString( R.string.error_message )
                    , Snackbar.LENGTH_INDEFINITE ).setAction("Reintentar", new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    mRetreiveProductsFromServer = new RetreiveProductsFromServer().execute();
                }
            });
        }

        mSnackbar.show();

        mState = STATE.ERROR;

        Log.d(Properties.TAG, "Estado = " + mState.toString());
    }

    /**
     * Metodo que comprueba si se han recorrido todos los productos del mapa de productos.
     * @param indexMap: Mapa de indices donde guardamos el indice de la ultima iteracion.
     * @return: true si se han recorrido todos los productos.
     */
    protected boolean _checkIfFinished(Map<String, Integer> indexMap)
    {
        boolean finished = true;
        Iterator<String> iterator = indexMap.keySet().iterator();
        while ((iterator.hasNext()) && (finished))
        {
            String key = iterator.next();

            finished = (mProductsListMap.get(mProductsListMap.size()-1).get(key).size() == indexMap.get(key));
        }

        return finished;
    }

} // Activity
