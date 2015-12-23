package com.wallakoala.wallakoala.Activities;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wallakoala.wallakoala.Adapters.ProductAdapter;
import com.wallakoala.wallakoala.Beans.ColorVariant;
import com.wallakoala.wallakoala.Beans.Image;
import com.wallakoala.wallakoala.Beans.Product;
import com.wallakoala.wallakoala.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
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
    protected int NUMBER_OF_CORES;
    private final int NUM_PRODUCTS_DISPLAYED = 10;
    protected final String SERVER_URL = "http://cuoka-ws.cloudapp.net";
    protected final String SERVER_SPRING_PORT = "8080";
    private enum STATE
    {
        ERROR,
        LOADING,
        NODATA,
        NORMAL
    }

    /* Data */
    protected ConcurrentMap<String, List<Product>> mProductsMap;
    protected Map<String, List<Product>> mProductsNonFilteredMap;
    protected Map<String, ?> mFilterMap;
    protected Deque<Product> mProductsCandidatesDeque;
    protected List<Product> mProductsDisplayedList;

    /* Container Views */
    protected RecyclerView mProductsRecyclerView;
    protected NavigationView mLeftNavigationVew;

    /* Layouts */
    protected DrawerLayout mDrawerLayout;

    /* LayoutManagers */
    protected GridLayoutManager mGridLayoutManager;

    /* Views */
    protected ActionBarDrawerToggle mLeftDrawerToggle;
    protected TextView mToolbarTextView;
    protected View mLoadingView;
    protected TextView mNoDataTextView, mErrorTextView;

    /* Adapters */
    protected ProductAdapter mProductAdapter;

    /* Animations */
    protected Animation hideToRight, showFromRight;
    protected Animation implode, explode;

    /* Toolbar */
    protected Toolbar mToolbar;

    /* Others */
    protected Menu mMenu;
    protected STATE mState;
    protected int mProductsInsertedPreviously, start, count;

    /* Temp */

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate(savedInstanceState);

        // Especificamos el layout 'products_grid.xml'
        setContentView(R.layout.products_grid);

        _initData();
        _initAuxViews();
        _initToolbar();
        _initNavigationDrawers();
        _initAnimations();

        new ConnectToServer().execute("Springfield", "Blanco", "HyM");
    }

    /**
     * Inicializacion de las distintias estructuras de datos.
     */
    protected void _initData()
    {
        mProductsMap             = new ConcurrentHashMap<>();
        mFilterMap               = _initFilterMap();
        mProductsNonFilteredMap  = new HashMap<>();
        mProductsDisplayedList   = new ArrayList<>();
        mProductsCandidatesDeque = new ArrayDeque<>();

        start = 0;
        count = 0;

        NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    }

    /**
     * Metodo que inicializa el mapa de filtros.
     * @return: Mapa con los filtros actuales.
     */
    protected Map<String, ?> _initFilterMap()
    {
        Map<String, Object> map = new HashMap<>();

        map.put(getResources().getString(R.string.filter_newness), true);
        map.put(getResources().getString(R.string.filter_colors), new ArrayList<String>());
        map.put(getResources().getString(R.string.filter_sections), new ArrayList<String>());
        map.put(getResources().getString(R.string.filter_sizes), new ArrayList<String>());

        return map;
    }

    /**
     * Inicializacion de vistas auxiliares.
     */
    protected void _initAuxViews()
    {
        // LoaderView
        mLoadingView = findViewById( R.id.avloadingIndicatorView );

        // TextViews que muestran que no hay productos disponibles o se ha producido un error
        mNoDataTextView = ( TextView )findViewById( R.id.nodata_textview );
        mErrorTextView  = ( TextView )findViewById( R.id.error_textview );
    }

    /**
     * Inicializacion de la toolbar.
     */
    protected void _initToolbar()
    {
        mToolbar = ( Toolbar )findViewById( R.id.appbar );
        mToolbarTextView = ( TextView )findViewById( R.id.toolbar_textview );

        setSupportActionBar( mToolbar );
        getSupportActionBar().setDisplayShowTitleEnabled( false );
    }

    /**
     * Inicializacion y configuracion del recyclerView.
     */
    protected void _initRecyclerView()
    {
        mProductsRecyclerView = ( RecyclerView )findViewById( R.id.grid_recycler );
        mGridLayoutManager    = new GridLayoutManager(this, 2);
        mProductAdapter       = new ProductAdapter(this, mProductsDisplayedList);

        mProductsRecyclerView.setVisibility( View.GONE );

        mProductsRecyclerView.setLayoutManager(mGridLayoutManager);
        mProductsRecyclerView.setAdapter(mProductAdapter);
        mProductsRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            int verticalOffset;
            boolean scrollingUp;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                    if (scrollingUp)
                        if (verticalOffset > mToolbar.getHeight())
                            _toolbarAnimateHide();

                        else
                            _toolbarAnimateShow(verticalOffset);

                    else if (mToolbar.getTranslationY() < (mToolbar.getHeight() * -0.6f) &&
                            (verticalOffset > mToolbar.getHeight()))
                        _toolbarAnimateHide();

                    else
                        _toolbarAnimateShow(verticalOffset);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                verticalOffset += dy;
                scrollingUp = dy > 0;

                int toolbarYOffset = (int) (dy - mToolbar.getTranslationY());

                mToolbar.animate().cancel();

                if ( scrollingUp )
                {
                    // Animacion de la toolbar
                    if (toolbarYOffset < mToolbar.getHeight())
                        mToolbar.setTranslationY(-toolbarYOffset);

                    else
                        mToolbar.setTranslationY(-mToolbar.getHeight());

                    // Detectamos cuando llegamos abajo para cargar nuevos productos
                    if ( ! mProductsCandidatesDeque.isEmpty() )
                    {
                        if (mProductsDisplayedList.size() ==
                                (mGridLayoutManager.findLastCompletelyVisibleItemPosition() + 1))
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

        // Iniciamos una animacion para hacer aparecer el recycler view
        explode.setDuration( 250 );
        mProductsRecyclerView.startAnimation(explode);
        mProductsRecyclerView.setVisibility(View.VISIBLE);
    }

    protected void _toolbarAnimateShow( final int verticalOffset )
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
        mLeftNavigationVew = ( NavigationView )findViewById( R.id.nav_view );
        mDrawerLayout      = ( DrawerLayout )findViewById( R.id.drawer_layout );

        _initDrawerToggle();

        mDrawerLayout.setDrawerListener(mLeftDrawerToggle);
    }

    /**
     * Inicializacion y configuracion del drawer toggle del leftDrawer.
     */
    protected void _initDrawerToggle()
    {
        // Inicializamos el control en la action bar
        mLeftDrawerToggle = new ActionBarDrawerToggle( this, mDrawerLayout, mToolbar, R.string.open_drawer, R.string.close_drawer )
        {
            // Metodo llamado cuando el drawer esta completamente cerrado
            @Override
            public void onDrawerClosed( View drawerView )
            {
                if( drawerView == findViewById( R.id.leftDrawerLayout ) )
                {
                    // Sacamos la vista del item
                    final View itemView = findViewById( mMenu.getItem( 0 ).getItemId() );

                    // Cargamos la animacion y decimos que mantenga el estado cuando termine
                    showFromRight.setFillAfter( true );

                    itemView.startAnimation( showFromRight );

                    // Habilitamos de nuevo el item
                    itemView.setEnabled( true );

                    mLeftDrawerToggle.syncState();
                }
            }

            // Metodo llamado cuando el drawer esta completamente abierto
            @Override
            public void onDrawerOpened( View drawerView )
            {
                if (drawerView == findViewById( R.id.leftDrawerLayout ) )
                {
                    // Crea la llamada a onPrepareOptionsMenu()
                    supportInvalidateOptionsMenu();

                    mLeftDrawerToggle.syncState();
                }
            }

            // Metodo para realizar la animacion del drawerToggle, solo se realiza con el drawer izquierdo
            @Override
            public void onDrawerSlide( View drawerView, float slideOffset )
            {
                super.onDrawerSlide( drawerView, slideOffset );
            }
        };
    }

    /**
     * Metodo que inicializa las animaciones.
     */
    protected void _initAnimations()
    {
        showFromRight = AnimationUtils.loadAnimation( ProductsUI.this
                , R.anim.show_translation_horizontal );

        hideToRight = AnimationUtils.loadAnimation( ProductsUI.this
            , R.anim.hide_translation_horizontal );

        implode = AnimationUtils.loadAnimation( ProductsUI.this
            , R.anim.implode );

        explode = AnimationUtils.loadAnimation( ProductsUI.this
                , R.anim.explode );
    }

    @Override
    protected void onPostCreate( Bundle savedInstanceState )
    {
        super.onPostCreate(savedInstanceState);
        mLeftDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged( Configuration newConfig )
    {
        super.onConfigurationChanged(newConfig);
        mLeftDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onPrepareOptionsMenu( Menu menu )
    {
        // Si el Navigation Drawer izquierdo esta abierto, ocultamos los expandableItems de la action bar
        if ( mDrawerLayout.isDrawerOpen( Gravity.LEFT ) )
        {
            final View itemView = findViewById( menu.getItem( 0 ).getItemId() );

            hideToRight = AnimationUtils.loadAnimation( this
                    , R.anim.hide_translation_horizontal );
            hideToRight.setFillAfter(true);
            hideToRight.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    itemView.setEnabled(false);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

            itemView.startAnimation(hideToRight);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu )
    {
        // Guardamos el menu para poder acceder a los expandableItems mas adelante
        this.mMenu = menu;

        // Inflamos la ActionBar
        getMenuInflater().inflate(R.menu.action_bar, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item )
    {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    /**
     * Metodo que determina si un producto pasa el filtro o no.
     * @param product: Producto a comprobar.
     * @return: true si el producto ha pasado el filtro.
     */
    protected boolean _isDisplayable( Product product )
    {
        boolean displayable = true;

        // Recorremos el mapa de filtros
        for ( String key : mFilterMap.keySet() )
        {
            // Si es el filtro de novedades
            if ( key.equals("newness") )
            {
                if (mFilterMap.get(key) == null)
                    displayable = true;

                else
                    displayable = ( (Boolean)mFilterMap.get(key) == product.isNewness() );
            }
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
        for ( String key : mProductsMap.keySet() )
            indexMap.put(key, 0);

        Iterator<String> iterator = mProductsMap.keySet().iterator();

        // Mientras queden productos pendientes.
        while ( ! finished )
        {
            String key = iterator.next();

            // Sacamos el indice de donde nos quedamos y la lista de productos.
            int index = indexMap.get(key);
            List<Product> list = mProductsMap.get(key);

            // Mientras queden productos y no encontremos un producto mostrable.
            while( ( index < list.size() ) && ( turn ) )
            {
                // Si el producto pasa el filtro, se añade a la cola
                if ( _isDisplayable( list.get(index) ) )
                {
                    mProductsCandidatesDeque.addLast( list.get(index) );
                    turn = false;
                }

                index++;
            } // while #2

            // Actualizamos el mapa de indices.
            indexMap.put(key, index);
            turn = true;

            // Si se ha terminado el recorrido, lo iniciamos de nuevo.
            if ( ! iterator.hasNext() )
                iterator = mProductsMap.keySet().iterator();

            finished = _checkIfFinished( indexMap );

        } // while #1

        Log.e("INFO", "Lista de candidatos: " + mProductsCandidatesDeque.size());
    }

    /**
     * Metodo que inserta los productos en la cola ordenados.
     */
    protected void getNextProductsToBeDisplayed()
    {
        mProductsInsertedPreviously = NUM_PRODUCTS_DISPLAYED;

        if ( NUM_PRODUCTS_DISPLAYED > mProductsCandidatesDeque.size() )
            mProductsInsertedPreviously = mProductsCandidatesDeque.size();

        for ( int i = 0; i < mProductsInsertedPreviously; i++ )
        {
            mProductsDisplayedList.add(mProductsCandidatesDeque.getFirst());

            mProductsCandidatesDeque.removeFirst();
        }

        Log.e("INFO", "Lista de mostrados: " + mProductsDisplayedList.size());
    }

    /**
     * Metodo que convierte la lista de JSON en productos y los inserta en las distintas ED's.
     * @param jsonArray: lista de JSON a convertir.
     * @throws JSONException
     */
    protected void convertJSONtoProduct( JSONArray jsonArray ) throws JSONException
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
            String name = jsonObject.getString("name");
            String shop = key = jsonObject.getString("shop");
            String section = jsonObject.getString("section");
            double price = jsonObject.getDouble("price");
            boolean man = jsonObject.getBoolean("man");
            String link = jsonObject.getString("link");
            boolean newness = jsonObject.getBoolean("newness");

            JSONArray jsColors = jsonObject.getJSONArray("colors");
            List<ColorVariant> colors = new ArrayList<>();
            for( int i = 0; i < jsColors.length(); i++ )
            {
                JSONObject jsColor = jsColors.getJSONObject(i);

                String reference = jsColor.getString("reference");
                String colorName = jsColor.getString("colorName");
                String colorURL = jsColor.getString("colorURL");
                String colorPath = jsColor.getString("colorPath");

                List<Image> images = new ArrayList<>();
                JSONArray jsImages = jsColor.getJSONArray("images");
                for ( int j = 0; j < jsImages.length(); j++ )
                {
                    JSONObject jsImage = jsImages.getJSONObject(j);

                    String url = jsImage.getString("url");
                    String path = SERVER_URL + jsImage.getString("path")
                                                      .replace(" ", "%20");

                    images.add( new Image( url, path ) );
                }

                colors.add( new ColorVariant( reference, colorName, colorURL, colorPath, images ) );
            }

            productsList.add( new Product( name, shop, section, price, man, link, colors, newness ) );

        }

        mProductsMap.put(key, productsList);
    }

    /**
     * Tarea en segundo plano que descargara la lista de JSON del servidor.
     */
    private class ConnectToServer extends AsyncTask<String, Void, Void>
    {
        private List<String> content = new ArrayList<>();
        private String error = null;

        @Override
        protected void onPreExecute()
        {
            _loading(true);
        }

        @Override
        protected Void doInBackground( String... shops )
        {
            BufferedReader reader = null;

            try
            {
                for ( int i = 0; i < shops.length; i++ )
                {
                    URL url = null;
                    if ( i == 0 )
                        url = new URL( SERVER_URL + ":" + SERVER_SPRING_PORT + "/getProducts/" + shops[i] + "/true");
                    if ( i == 1 )
                        url = new URL( SERVER_URL + ":" + SERVER_SPRING_PORT + "/getProducts/" + shops[i] + "/false");
                    if ( i == 2 )
                        url = new URL( SERVER_URL + ":" + SERVER_SPRING_PORT + "/getProducts/" + shops[i] + "/false");

                    Log.e("TIME INI", Calendar.getInstance().toString());

                    URLConnection conn = url.openConnection();

                    // Get the server response
                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line = "";

                    // Read Server Response
                    while ((line = reader.readLine()) != null)
                        sb.append(line + "");

                    // Append Server Response To Content String
                    content.add(sb.toString());

                    Log.e("TIME END", Calendar.getInstance().toString());
                }

            } catch( Exception ex )  {
                error = ex.getMessage();

            } finally {
                try {
                    reader.close();

                } catch( Exception ex ) {
                    error = ex.getMessage();
                }
            }

            return null;

        } // doInBackground

        @Override
        protected void onPostExecute( Void unused )
        {
            if ( error != null )
                _error(true);

            else
                new MultithreadConversion().execute( content );

        } // onPostExecute

    } // ConnectToServer

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
            executor = new ThreadPoolExecutor( NUMBER_OF_CORES * 4
                    , NUMBER_OF_CORES * 4
                    , 60L
                    , TimeUnit.SECONDS
                    , new LinkedBlockingQueue<Runnable>() );

            completionService = new ExecutorCompletionService<>( executor );
        }

        @Override
        protected Void doInBackground( List<String>... params )
        {
            List<String> content = params[0];

            try
            {
                // Creamos un callable por cada tienda
                for (int i = 0; i < content.size(); i++)
                {
                    ConversionTask task = new ConversionTask( new JSONArray( content.get(i) ) );

                    completionService.submit( task );
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

            } catch ( Exception e ) {
                error = e.getMessage();

            }

            return null;
        }

        @Override
        protected void onPostExecute( Void unused )
        {
            if ( error != null )
                _error(true);

            else
            {
                if (mProductsCandidatesDeque.isEmpty())
                    _noData(true);

                else
                    _initRecyclerView();
            }

            _loading(false);
        }

    } // MultithreadConversion

    /**
     * Task que convierte una array de JSON en una lista de productos.
     */
    private class ConversionTask implements Callable<Boolean>
    {
        private JSONArray mJsonArray;

        public ConversionTask( JSONArray jsonArray )
        {
            mJsonArray = jsonArray;
        }

        @Override
        public Boolean call()
        {
            try
            {
                convertJSONtoProduct( mJsonArray );

            } catch ( Exception e ) {
                return false;
            }

            return true;
        }
    }

    /**
     * Metodo que crea la pantalla de carga.
     * @param loading: true indica que se inicia la carga, false que ha terminado.
     */
    protected void _loading( boolean loading )
    {
        if ( ! loading )
        {
            mLoadingView.setVisibility(View.GONE);

            mState = STATE.NORMAL;

        } else {
            mLoadingView.setVisibility(View.VISIBLE);

            mState = STATE.LOADING;
        }
    }

    /**
     * Metodo que muestra un mensaje cuando no hay ningun producto que mostrar.
     * @param noData: true indica que no hay ningun producto que mostrar.
     */
    protected void _noData( boolean noData )
    {
        if ( ! noData )
        {
            if ( mProductsRecyclerView != null )
                mProductsRecyclerView.setVisibility(View.VISIBLE);

            mNoDataTextView.setVisibility(View.GONE);

            mState = STATE.NORMAL;

        } else {
            if ( mProductsRecyclerView != null )
                mProductsRecyclerView.setVisibility(View.GONE);

            mNoDataTextView.setVisibility(View.VISIBLE);

            mState = STATE.NODATA;
        }
    }

    /**
     * Metodo que muestra un mensaje cuando se ha producido un error.
     * @param error: true si se ha producido un error.
     */
    protected void _error( boolean error )
    {
        if ( ! error )
        {
            if ( mProductsRecyclerView != null )
                mProductsRecyclerView.setVisibility(View.VISIBLE);

            mErrorTextView.setVisibility(View.GONE);

            mState = STATE.NORMAL;

        } else {
            if ( mProductsRecyclerView != null )
                mProductsRecyclerView.setVisibility(View.GONE);

            mErrorTextView.setVisibility(View.VISIBLE);

            mState = STATE.ERROR;
        }
    }

    protected boolean _checkIfFinished( Map<String, Integer> indexMap )
    {
        boolean finished = true;
        Iterator<String> iterator = indexMap.keySet().iterator();
        while ( ( iterator.hasNext() ) && ( finished ) )
        {
            String key = iterator.next();

            finished = ( mProductsMap.get(key).size() == indexMap.get(key) );
        }

        return finished;
    }

} // Activity
