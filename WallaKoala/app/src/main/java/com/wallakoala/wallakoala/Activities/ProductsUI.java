package com.wallakoala.wallakoala.Activities;

import android.content.res.Configuration;
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
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.greenfrvr.rubberloader.RubberLoaderView;
import com.wallakoala.wallakoala.Adapters.ProductAdapter;
import com.wallakoala.wallakoala.Beans.ColorVariant;
import com.wallakoala.wallakoala.Beans.Image;
import com.wallakoala.wallakoala.Beans.Product;
import com.wallakoala.wallakoala.Beans.Size;
import com.wallakoala.wallakoala.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * @class Pantalla principal de la app, donde se muestran los productos
 * Created by Daniel Mancebo on 09/11/2015.
 */

public class ProductsUI extends AppCompatActivity
{
    /* Constants */
    private final int NUM_PRODUCTS_DISPLAYED = 20;

    /* Data */
    protected HashMap< String, List< Product > > mProductsMap;
    protected List<Product> mProductsNewnessList;
    protected List<Boolean> mProductsDisplayedList;

    /* Container Views */
    protected RecyclerView mProductsRecyclerView;
    protected NavigationView mLeftNavigationVew;

    /* Layouts */
    protected DrawerLayout mDrawerLayout;

    /* Views */
    protected ActionBarDrawerToggle mLeftDrawerToggle;
    protected TextView mToolbarTextView;
    protected RubberLoaderView mRubberLoader;
    protected View mDarkenScreenView;
    protected TextView mNoDataTextView, mErrorTextView;

    /* Animations */
    protected Animation hideToRight, showFromRight;

    /* Toolbar */
    protected Toolbar mToolbar;

    /* Others */
    protected Menu mMenu;
    protected int number_of_shops;

    /* Temp */

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate(savedInstanceState);

        // Especificamos el layout 'products_grid.xml'
        setContentView( R.layout.products_grid );

        _initData();
        _initAuxViews();
        _initToolbar();
        _initNavigationDrawers();

        new Products().execute("Blanco", "HyM");
    }

    /**
     * Inicializacion de las estructuras de datos.
     */
    private void _initData()
    {
        mProductsMap = new HashMap<>();
        mProductsNewnessList = new ArrayList<>();
        mProductsDisplayedList = new ArrayList<>();
    }

    /**
     * Inicializacion de vistas auxiliares
     */
    private void _initAuxViews()
    {
        // LoaderView
        mRubberLoader = ( RubberLoaderView )findViewById(R.id.rubber_loader);

        // ImageView que oscurece la pantalla
        mDarkenScreenView = findViewById(R.id.darken_screen);

        // TextViews que muestran que no hay productos disponibles o se ha producido un error
        mNoDataTextView = (TextView)findViewById(R.id.nodata_textview);
        mErrorTextView = (TextView)findViewById(R.id.error_textview);
    }

    /**
     * Inicializacion de la toolbar.
     */
    private void _initToolbar()
    {
        mToolbar = ( Toolbar )findViewById( R.id.appbar );
        mToolbarTextView = ( TextView )findViewById( R.id.toolbar_textview );

        setSupportActionBar( mToolbar );
        getSupportActionBar().setDisplayShowTitleEnabled( false );
    }

    /**
     * Inicializacion y configuracion del recyclerView.
     */
    private void _initRecyclerView()
    {
        mProductsRecyclerView = ( RecyclerView )findViewById( R.id.grid_recycler );
        mProductsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mProductsRecyclerView.setAdapter(new ProductAdapter(this));

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

                if (scrollingUp)
                    if (toolbarYOffset < mToolbar.getHeight())
                        mToolbar.setTranslationY(-toolbarYOffset);

                    else
                        mToolbar.setTranslationY(-mToolbar.getHeight());


                else if (toolbarYOffset < 0)
                    mToolbar.setTranslationY(0);

                else
                    mToolbar.setTranslationY(-toolbarYOffset);
            }
        });
    }

    private void _toolbarAnimateShow( final int verticalOffset )
    {
        mToolbar.animate()
                .translationY(0)
                .setInterpolator(new LinearInterpolator())
                .setDuration(180);
    }

    private void _toolbarAnimateHide()
    {
        mToolbar.animate()
                .translationY(-mToolbar.getHeight())
                .setInterpolator(new LinearInterpolator())
                .setDuration(180);
    }

    /**
     * Inicializacion y configuracion de los navigation drawers.
     */
    private void _initNavigationDrawers()
    {
        mLeftNavigationVew = ( NavigationView )findViewById( R.id.nav_view );
        mDrawerLayout      = ( DrawerLayout )findViewById( R.id.drawer_layout );

        _initDrawerToggle();

        mDrawerLayout.setDrawerListener(mLeftDrawerToggle);
    }

    /**
     * Inicializacion y configuracion del drawer toggle del leftDrawer.
     */
    private void _initDrawerToggle()
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
                    showFromRight = AnimationUtils.loadAnimation( ProductsUI.this
                            , R.anim.show_translation_horizontal );
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
     * Metodo que inhabilita ciertos controles cuando está la pantalla de carga.
     * @param loading: true indica que se inicia la carga, false que ha terminado.
     */
    private void _loading( boolean loading )
    {
        if ( ! loading )
        {
            mRubberLoader.setVisibility(View.GONE);
            mDarkenScreenView.setVisibility(View.GONE);

            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);


        } else {
            mRubberLoader.setVisibility(View.VISIBLE);
            mRubberLoader.startLoading();
            mDarkenScreenView.setVisibility(View.VISIBLE);

            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    /**
     * Metodo que muestra un mensaje cuando no hay ningun producto que mostrar.
     * @param noData: true indica que no hay ningun producto que mostrar.
     */
    private void _noData( boolean noData )
    {
        if ( ! noData )
        {
            mProductsRecyclerView.setVisibility(View.VISIBLE);
            mNoDataTextView.setVisibility(View.GONE);

        } else {
            mProductsRecyclerView.setVisibility(View.GONE);
            mNoDataTextView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Metodo que muestra un mensaje cuando se ha producido un error.
     * @param error: si se ha producido un error.
     */
    private void _error( boolean error )
    {
        if ( ! error )
        {
            mProductsRecyclerView.setVisibility(View.VISIBLE);
            mErrorTextView.setVisibility(View.GONE);

        } else {
            mProductsRecyclerView.setVisibility(View.GONE);
            mErrorTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onPostCreate( Bundle savedInstanceState )
    {
        super.onPostCreate( savedInstanceState );
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
            hideToRight.setFillAfter( true );
            hideToRight.setAnimationListener( new Animation.AnimationListener()
            {
                @Override
                public void onAnimationStart( Animation animation ) {}

                @Override
                public void onAnimationEnd( Animation animation )
                {
                    itemView.setEnabled( false );
                }

                @Override
                public void onAnimationRepeat( Animation animation ) {}
            });

            itemView.startAnimation( hideToRight );
        }

        return super.onPrepareOptionsMenu( menu );
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu )
    {
        // Guardamos el menu para poder acceder a los expandableItems mas adelante
        this.mMenu = menu;

        // Inflamos la ActionBar
        getMenuInflater().inflate( R.menu.action_bar, menu );

        return super.onCreateOptionsMenu( menu );
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item )
    {
        return super.onOptionsItemSelected( item );
    }

    private class Products  extends AsyncTask<String, Void, Void>
    {
        private List<String> content = new ArrayList<>();
        private List<JSONObject> jsonList = new ArrayList<>();
        private String error = null;

        protected void onPreExecute()
        {
            _loading(true);
        }

        protected Void doInBackground( String... shops )
        {
            BufferedReader reader = null;

            try
            {
                number_of_shops = shops.length;

                for ( int i = 0; i < shops.length; i++ )
                {
                    URL url = new URL("http://cuoka.cloudapp.net:8080/getProducts/" + shops[i]);
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
                }

            } catch( Exception ex )  {
                error = ex.getMessage();

            } finally {
                try {
                    reader.close();
                }

                catch( Exception ex ) {
                    error = ex.getMessage();
                }
            }

            return null;

        } // doInBackground

        protected void onPostExecute( Void unused )
        {
            if ( error != null )
            {
                _error(true);

            } else {
                JSONArray jsonResponse;

                try {
                    for( int i = 0; i < content.size(); i++ )
                    {
                        jsonResponse = new JSONArray(content.get(i));

                        for (int j = 0; j < jsonResponse.length(); j++)
                        {
                            JSONObject js = jsonResponse.getJSONObject(j);

                            jsonList.add(js);
                        }

                        convertJSONtoProduct(jsonList);
                    }

                    _loading(false);

                    // Sacamos los siguientes productos que se tienen que mostrar en el grid
                    getNextProductsToBeDisplayed();

                    _initRecyclerView();

                } catch ( JSONException e ) {
                    e.printStackTrace();
                }
            } // else
        } // onPostExecute


        private List<Product> getNextProductsToBeDisplayed()
        {

            return null;
        }

        /**
         * Metodo que convierte la lista de JSON en productos y los inserta en las distintas ED's
         * @param jsonList: lista de JSON a convertir
         * @throws JSONException
         */
        private void convertJSONtoProduct( List<JSONObject> jsonList ) throws JSONException
        {
            List<Product> productsList = new ArrayList<>();
            String key = null;

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
                    List<Size> sizes = new ArrayList<>();
                    JSONArray jsImages = jsColor.getJSONArray("images");
                    JSONArray jsSizes = jsColor.getJSONArray("sizes");
                    for ( int j = 0; j < jsImages.length(); j++ )
                    {
                        JSONObject jsImage = jsImages.getJSONObject(j);

                        String url = jsImage.getString("url");
                        String pathLargeSize = jsImage.getString("pathLargeSize");
                        String pathSmallSize = jsImage.getString("pathSmallSize");

                        images.add( new Image( url, pathSmallSize, pathLargeSize ) );
                    }

                    for ( int j = 0; j < jsSizes.length(); j++ )
                    {
                        JSONObject jsSize = jsSizes.getJSONObject(j);

                        String size = jsSize.getString("size");
                        boolean stock = jsSize.getBoolean("stock");

                        sizes.add( new Size( size, stock ) );
                    }

                    colors.add( new ColorVariant( reference, colorName, colorURL, colorPath, images, sizes ) );
                }

                if ( ! newness )
                    productsList.add( new Product( name, shop, section, price, man, link, colors, newness, null ) );

                else
                {
                    mProductsNewnessList.add(new Product(name, shop, section, price, man, link, colors, newness, null));
                    mProductsDisplayedList.add( new Boolean( false ) );
                }

            }

            mProductsMap.put(key, productsList);
        }

    } // Products
}
