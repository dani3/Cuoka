package com.wallakoala.wallakoala.Activities;

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
import com.wallakoala.wallakoala.R;
import com.wallakoala.wallakoala.Utils.SharedPreferencesManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Calendar;
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
    protected static final String TAG = "CUOKA";
    protected static final int EXIT_TIME_INTERVAL = 2000;
    protected static final int NUM_PRODUCTS_DISPLAYED = 10;
    protected static final String SERVER_URL = "http://cuoka-ws.cloudapp.net";
    protected static final String SERVER_SPRING_PORT = "8080";
    protected static boolean MAN;
    protected static boolean ON_CREATE_FLAG;
    protected static int NUMBER_OF_CORES;
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
    protected ConcurrentMap<String, List<Product>> mProductsMap;
    protected Map<String, List<Product>> mProductsNonFilteredMap;
    protected Map<String, ?> mFilterMap;
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

    /* Adapters */
    protected ProductsGridAdapter mProductAdapter;

    /* Animations */
    protected Animation moveAndFade;

    /* Snackbar */
    protected Snackbar mSnackbar;

    /* Toolbar */
    protected Toolbar mToolbar;

    /* SharedPreferences */
    protected SharedPreferencesManager mSharedPreferences;

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

        new ConnectToServer().execute();
    }

    /**
     * Inicializacion de las distintias estructuras de datos.
     */
    protected void _initData()
    {
        mSharedPreferences = new SharedPreferencesManager(this);

        mProductsMap             = new ConcurrentHashMap<>();
        mFilterMap               = _initFilterMap();
        mProductsNonFilteredMap  = new HashMap<>();
        mProductsDisplayedList   = new ArrayList<>();
        mProductsCandidatesDeque = new ArrayDeque<>();
        mShopsList               = new ArrayList<>();

        for (String shop : mSharedPreferences.retreiveShops())
            mShopsList.add(shop);

        start = count = 0;
        mBackPressed = 0;

        MAN = mSharedPreferences.retreiveMan();

        NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

        ON_CREATE_FLAG = true;

        Log.d(TAG, "Numero de procesadores: " + NUMBER_OF_CORES);
    }

    /**
     * Metodo que inicializa el mapa de filtros.
     * @return: Mapa con los filtros actuales.
     */
    protected Map<String, ?> _initFilterMap()
    {
        Map<String, Object> map = new HashMap<>();

        map.put(getResources().getString(R.string.filter_newness), mSharedPreferences.retreiveNewness());

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
        mLoadingView = findViewById(R.id.avloadingIndicatorView);

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
        mProductsRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener()
        {
            int verticalOffset;
            boolean scrollingUp;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                {
                    if (scrollingUp)
                    {
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
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                verticalOffset += dy;
                scrollingUp = dy > 0;

                int toolbarYOffset = (int) (dy - mToolbar.getTranslationY());

                mToolbar.animate().cancel();

                if (scrollingUp)
                {
                    // Animacion de la toolbar
                    if (toolbarYOffset < mToolbar.getHeight())
                        mToolbar.setTranslationY(-toolbarYOffset);
                    else
                        mToolbar.setTranslationY(-mToolbar.getHeight());

                    // Detectamos cuando llegamos abajo para cargar nuevos productos
                    if (!mProductsCandidatesDeque.isEmpty())
                    {
                        int[] lastItemsPosition = new int[2];
                        mStaggeredGridLayoutManager.findLastCompletelyVisibleItemPositions(lastItemsPosition);

                        if ((lastItemsPosition[0] >= mProductsDisplayedList.size() - 2) ||
                            (lastItemsPosition[1] >= mProductsDisplayedList.size() - 1))
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
        // Inicializamos el control en la action bar
        mLeftDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open_drawer, R.string.close_drawer)
        {
            // Metodo llamado cuando el drawer esta completamente cerrado
            @Override
            public void onDrawerClosed(View drawerView)
            {
                if (drawerView == findViewById(R.id.leftDrawerLayout))
                    mLeftDrawerToggle.syncState();
            }

            // Metodo llamado cuando el drawer esta completamente abierto
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

            // Metodo para realizar la animacion del drawerToggle, solo se realiza con el drawer izquierdo
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
        moveAndFade = AnimationUtils.loadAnimation(ProductsUI.this
                , R.anim.translate_and_fade);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        // Si no venimos del onCreate (ON_CREATE_FLAG = FALSE) significa que venimos de
        // la pantalla de un producto, por lo que hay que restaurar el pie de foto.
        if ((!ON_CREATE_FLAG) && (mProductAdapter != null) && (mProductAdapter.productClicked()))
        {
            Log.d(TAG, "Volviendo de ProductUI");
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
        getMenuInflater().inflate(R.menu.action_bar, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        return super.onOptionsItemSelected(item);
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
    }

    /**
     * Metodo que determina si un producto pasa el filtro o no.
     * @param product: Producto a comprobar.
     * @return: true si el producto ha pasado el filtro y, por tanto, se puede mostrar.
     */
    protected boolean _isDisplayable(Product product)
    {
        boolean displayable = true;

        // Recorremos el mapa de filtros
        for (String key : mFilterMap.keySet())
        {
            // Si es el filtro de novedades
            if (key.equals("newness"))
            {
                if (mFilterMap.get(key) != null)
                    displayable =  ((Boolean)mFilterMap.get(key) == product.isNewness());
            }

            // Rompemos el bucle cuando el producto no pase algun filtro.
            if (!displayable)
                break;
        }

        return displayable;
    }

    /**
     * Metodo que actualiza la cola de candidatos, realiza una lectura del mapa de productos como un RoundRobin.
     * Solo se queda con los productos que pasen el filtro.
     */
    protected void updateCandidates()
    {
        // Mapa de indices para trackear por donde nos hemos quedado en la iteracion anterior.
        Map<String, Integer> indexMap = new HashMap<>();
        boolean finished = false;
        boolean turn = true;

        // Inicializar mapa de indices con todos a 0.
        for (String key : mProductsMap.keySet())
            indexMap.put(key, 0);

        Iterator<String> iterator = mProductsMap.keySet().iterator();

        // Mientras queden productos pendientes.
        while (!finished)
        {
            String key = iterator.next();

            // Sacamos el indice de donde nos quedamos y la lista de productos.
            int index = indexMap.get(key);
            List<Product> list = mProductsMap.get(key);

            // Mientras queden productos y no encontremos un producto mostrable.
            while((index < list.size()) && (turn))
            {
                // Si el producto pasa el filtro, se añade a la cola
                if (_isDisplayable(list.get(index)))
                {
                    mProductsCandidatesDeque.addLast(list.get(index));
                    turn = false;
                }

                index++;
            } // while #2

            // Actualizamos el mapa de indices.
            indexMap.put(key, index);
            turn = true;

            // Si se ha terminado el recorrido, lo iniciamos de nuevo.
            if (!iterator.hasNext())
                iterator = mProductsMap.keySet().iterator();

            finished = _checkIfFinished(indexMap);

        } // while #1

        Log.d(TAG, "Lista de candidatos: " + mProductsCandidatesDeque.size());
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
            mProductsDisplayedList.add( mProductsCandidatesDeque.getFirst() );

            mProductsCandidatesDeque.removeFirst();
        }

        Log.d(TAG, "Lista de candidatos: " + mProductsCandidatesDeque.size());
        Log.d(TAG, "Lista de mostrados: " + mProductsDisplayedList.size());
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

        for( JSONObject jsonObject : jsonList )
        {
            String name = jsonObject.getString("2");
            String shop = key = jsonObject.getString("3");
            String section = jsonObject.getString("4");
            double price = jsonObject.getDouble("1");
            String link = jsonObject.getString("5");
            boolean newness = jsonObject.getBoolean("7");

            JSONArray jsColors = jsonObject.getJSONArray("6");
            List<ColorVariant> colors = new ArrayList<>();
            for( int i = 0; i < jsColors.length(); i++ )
            {
                JSONObject jsColor = jsColors.getJSONObject(i);

                String reference = jsColor.getString("1");
                String colorName = jsColor.getString("2");
                String colorPath = jsColor.getString("4");
                short numerOfImages = (short)jsColor.getInt("3");

                colors.add( new ColorVariant( reference, colorName, colorPath, numerOfImages ) );
            }

            Product product = new Product( name, shop, section, price, link, colors, newness );

            if (product.isOkay())
                productsList.add( new Product( name, shop, section, price, link, colors, newness ) );

        }

        mProductsMap.put(key, productsList);
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
        private List<String> content = new ArrayList<>();
        private String error = null;

        @Override
        protected void onPreExecute()
        {
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
        protected Void doInBackground(String... shops)
        {
            try
            {
                // Creamos un thread por cada tienda a la que tenemos que conectarnos.
                for (int i = 0; i < mShopsList.size(); i++)
                {
                    ConnectionTask connectionTask = new ConnectionTask(i);

                    completionService.submit(connectionTask);
                }

                // Metemos en content el resultado de cada uno
                for (int i = 0; i < mShopsList.size(); i++)
                {
                    String future = completionService.take().get();
                    if (future != null)
                        content.add(future);
                }

                // Si content es vacio, es que han fallado todas las conexiones.
                if (content.isEmpty())
                {
                    error = "Imposible conectar con el servidor";
                    Log.d(TAG, error);
                }

            } catch(Exception ex)  {
                error = ex.getMessage();

            } finally {
                executor.shutdown();
            }

            return null;

        } // doInBackground

        @Override
        protected void onPostExecute( Void unused )
        {
            if (error != null)
            {
                mLoadingView.setVisibility(View.GONE);

                _errorConnectingToServer();

            } else
                new MultithreadConversion().execute(content);

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
                String fixedURL = SERVER_URL + ":" + SERVER_SPRING_PORT
                        + "/newness/" + mShopsList.get(myPos).replaceAll(" ", "%20") + "/" + MAN;

                Log.d(TAG, "Conectando con: " + fixedURL);

                url = new URL(fixedURL);

                if (url != null)
                {
                    Log.d(TAG, "Time INI (" + mShopsList.get(myPos) + "): " + Calendar.getInstance().toString());

                    URLConnection conn = url.openConnection();

                    // Obtenemos la respuesta del servidor
                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line = "";

                    // Leemos la respuesta
                    while ((line = reader.readLine()) != null)
                        sb.append(line + "");

                    Log.d(TAG, "Time FIN (" + mShopsList.get(myPos) + "): " + Calendar.getInstance().toString());

                    // Devolvemos la respuesta
                    return sb.toString();
                }

            } catch ( Exception e ) {
                Log.d( TAG, "Error conectando con " + mShopsList.get(myPos) );

            } finally {
                try {
                    if ( reader != null )
                        reader.close();

                } catch ( IOException e ) {
                    Log.d(TAG, "Error cerrando conexion con " + mShopsList.get(myPos));

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

            completionService = new ExecutorCompletionService<>( executor );
        }

        @Override
        protected Void doInBackground(List<String>... params)
        {
            List<String> content = params[0];

            try
            {
                // Creamos un callable por cada tienda
                for (int i = 0; i < content.size(); i++)
                {
                    Log.d(TAG, "Tamano en bytes: " + (content.get(i).getBytes().length / 1000 ) + "kB");

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

                _errorConnectingToServer();

            } else {

                if (mProductsDisplayedList.isEmpty())
                {
                    _noData(true);
                    mLoadingView.setVisibility(View.GONE);

                } else {
                    _loading(false, true);
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
                // Cuando termine la animacion de la view de carga, iniciamos la del recyclerView
                moveAndFade.setAnimationListener(new Animation.AnimationListener()
                {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mLoadingView.setVisibility(View.GONE);

                        // La animacion de cada item solo esta disponible para 5.0+
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
                        {
                            _initRecyclerView();
                            mProductsRecyclerView.startAnimation(AnimationUtils.loadAnimation(ProductsUI.this
                                                                            , android.R.anim.fade_in ));

                        } else {
                            _initRecyclerView();
                            mProductsRecyclerView.scheduleLayoutAnimation();
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                } );

                mLoadingView.startAnimation(moveAndFade);

                mState = STATE.NORMAL;

            } else
                mLoadingView.setVisibility(View.GONE);

        } else {
            // Pantalla de carga
            mLoadingView.setVisibility(View.VISIBLE);

            mState = STATE.LOADING;
        }

        Log.d(TAG, "Estado = " + mState.toString());
    }

    /**
     * Metodo que muestra un mensaje cuando no hay ningun producto que mostrar.
     * @param noData: true indica que no hay ningun producto que mostrar.
     */
    protected void _noData( boolean noData )
    {
        if (!noData)
        {
            if (mProductsRecyclerView != null)
                mProductsRecyclerView.setVisibility(View.VISIBLE);

            mNoDataTextView.setVisibility(View.GONE);

            mState = STATE.NORMAL;

        } else {
            if (mProductsRecyclerView != null)
                mProductsRecyclerView.setVisibility(View.GONE);

            mNoDataTextView.setVisibility(View.VISIBLE);

            mState = STATE.NODATA;
        }

        Log.d(TAG, "Estado = " + mState.toString());
    }

    /**
     * Metodo que muestra un mensaje cuando se ha producido un error al conectar con el server.
     */
    protected void _errorConnectingToServer()
    {
        mSnackbar = Snackbar.make(mCoordinatorLayout
                , getResources().getString( R.string.error_message )
                , Snackbar.LENGTH_INDEFINITE ).setAction("Reintentar", new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                new ConnectToServer().execute("Springfield", "Blanco", "HyM");
            }
        });

        mSnackbar.show();

        mState = STATE.ERROR;

        Log.d(TAG, "Estado = " + mState.toString());
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

            finished = (mProductsMap.get(key).size() == indexMap.get(key));
        }

        return finished;
    }

} // Activity
