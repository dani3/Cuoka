package com.wallakoala.wallakoala.Activities;

import android.animation.LayoutTransition;
import android.content.res.Configuration;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.wallakoala.wallakoala.Adapters.ProductAdapter;
import com.wallakoala.wallakoala.Decorators.ProductDecorator;
import com.wallakoala.wallakoala.R;

/**
 * @class Pantalla principal de la app, donde se muestran los productos
 * Created by Daniel Mancebo on 09/11/2015.
 */

public class ProductsUI extends AppCompatActivity implements  SearchView.OnQueryTextListener
{
    protected RecyclerView mProductsRecyclerView;
    protected SearchView mSearchView;
    protected ListView mRightDrawerListView, mLeftDrawerListView;
    protected DrawerLayout mDrawerLayout;
    protected ActionBarDrawerToggle mLeftDrawerToggle;
    protected TextView mActionBarTextView;
    protected Menu mMenu;

    protected Animation hideToRight, showFromRight, hideToTop, showFromTop;

    /*
     * AUX
     */
    final String[] aux = new String[]{ "Prueba 1", "Prueba 2", "Prueba 3" };
    ArrayAdapter<String> menuAdapter;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate(savedInstanceState);

        // Especificamos el layout 'products_grid.xml'
        setContentView(R.layout.products_grid);

        menuAdapter = new ArrayAdapter<>( this
                , android.R.layout.simple_list_item_activated_1
                , aux );

        initActionBar();
        initRecyclerView();
        initNavigationDrawers();
    }

    /*
     * Configuracion de la action bar
     */
    private void initActionBar()
    {
        // Cargamos la action bar personalizada
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);

        // Cargamos el boton del left drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Cargamos el textview del titulo de la action bar
        mActionBarTextView = ( TextView )findViewById( R.id.actionBarTitle );
    }

    /*
     * Inicializacion y configuracion del recyclerView
     */
    private void initRecyclerView()
    {
        mProductsRecyclerView = ( RecyclerView )findViewById(R.id.grid_recycler);
        mProductsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mProductsRecyclerView.setAdapter(new ProductAdapter());
        mProductsRecyclerView.addItemDecoration(
                new ProductDecorator(getResources().getDimensionPixelSize(R.dimen.vertical_spacing_grid)
                        , getResources().getDimensionPixelSize(R.dimen.horizontal_spacing_grid)));
    }

    /*
     * Inicializacion y configuracion de los navigation drawers
     */
    private void initNavigationDrawers()
    {
        mRightDrawerListView = ( ListView )findViewById( R.id.rightlistviewdrawer );
        mLeftDrawerListView  = ( ListView )findViewById( R.id.leftlistviewdrawer );
        mDrawerLayout        = ( DrawerLayout )findViewById( R.id.drawer_layout );

        mRightDrawerListView.setAdapter(menuAdapter);
        mLeftDrawerListView.setAdapter(menuAdapter);

        initDrawerToggle();

        mDrawerLayout.setDrawerListener(mLeftDrawerToggle);
    }

    /*
     * Inicializacion y configuracion del drawer toggle del leftDrawer
     */
    private void initDrawerToggle()
    {
        // Inicializamos el navigation drawer y el control en la action bar
        mLeftDrawerToggle = new ActionBarDrawerToggle( this, mDrawerLayout, R.string.open_drawer, R.string.close_drawer )
        {
            // Metodo llamado cuando el drawer esta completamente cerrado
            @Override
            public void onDrawerClosed( View drawerView )
            {
                // Comprobamos si es el drawer izquierdo el que se ha cerrado
                if( drawerView == findViewById( R.id.leftDrawerLayout ) )
                {
                    // Reestablecemos el titulo de la action bar
                    mActionBarTextView.setText(R.string.app_name);

                    // Restauramos los items de la action bar con una translacion
                    for (int i = 0; i < mMenu.size(); i++)
                    {
                        View itemView = findViewById(mMenu.getItem(i).getItemId());

                        showFromRight = AnimationUtils.loadAnimation( ProductsUI.this
                                                , R.anim.show_translation_horizontal );
                        showFromRight.setFillAfter(true);

                        itemView.startAnimation(showFromRight);
                    }

                    mLeftDrawerToggle.syncState();
                }

                // Comprobamos si es el drawer derecho el que se ha cerrado
                if ( drawerView == findViewById( R.id.rightDrawerLayout ) )
                {
                    // Reestablecemos el titulo de la action bar
                    mActionBarTextView.setText(R.string.app_name);

                    // Restauramos el item de busqueda de la action bar
                    View itemView = findViewById( R.id.action_search );

                    showFromTop = AnimationUtils.loadAnimation( ProductsUI.this
                            , R.anim.show_translation_vertical );
                    showFromTop.setFillAfter(true);

                    itemView.startAnimation(showFromTop);
                }
            }

            // Metodo llamado cuando el drawer esta completamente abierto
            @Override
            public void onDrawerOpened( View drawerView )
            {
                // Comprobamos si es el drawer izquierdo el que se ha abierto
                if( drawerView == findViewById( R.id.leftDrawerLayout ) )
                {
                    // Crea la llamada a onPrepareOptionsMenu()
                    supportInvalidateOptionsMenu();
                    mLeftDrawerToggle.syncState();
                }

                // Comprobamos si es el drawer derecho el que se ha abierto
                if ( drawerView == findViewById( R.id.rightDrawerLayout ) )
                {
                    // Crea la llamada a onPrepareOptionsMenu()
                    supportInvalidateOptionsMenu();
                }
            }

            // Metodo para realizar la animacion del drawerToggle, solo se realiza con el drawer izquierdo
            @Override
            public void onDrawerSlide( View drawerView, float slideOffset )
            {
                if( drawerView == findViewById( R.id.leftDrawerLayout ) )
                    super.onDrawerSlide( drawerView, slideOffset );
            }
        };
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
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        // Si el Navigation Drawer izquierdo esta abierto, ocultamos los items de la action bar
        if ( mDrawerLayout.isDrawerOpen( Gravity.LEFT ) )
        {
            // Cambiamos el titulo de la action bar
            mActionBarTextView.setText( R.string.left_drawer_title );

            // Hacemos desaparecer los items del menu con una translacion horizontal
            for (int i = 0; i < menu.size(); i++)
            {
                View itemView = findViewById( menu.getItem( i ).getItemId() );

                hideToRight = AnimationUtils.loadAnimation( this
                                    , R.anim.hide_translation_horizontal );
                hideToRight.setFillAfter( true );

                itemView.startAnimation( hideToRight );
            }
        }

        // Si el Navigation Drawer derecho esta abierto, ocultamos solo la busqueda
        if ( mDrawerLayout.isDrawerOpen( Gravity.RIGHT ) )
        {
            // Cambiamos el titulo de la action bar
            mActionBarTextView.setText( R.string.right_drawer_title );

            // Hacemos desaparecer el item de busqueda con una translacion vertical
            View itemView = findViewById( R.id.action_search );

            hideToTop = AnimationUtils.loadAnimation( this
                            , R.anim.hide_translation_vertical );
            hideToTop.setFillAfter( true );

            itemView.startAnimation( hideToTop );
        }

        return super.onPrepareOptionsMenu( menu );
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu )
    {
        // Guardamos el menu para poder acceder a los items mas adelante
        mMenu = menu;

        // Inflamos la ActionBar
        getMenuInflater().inflate( R.menu.action_bar, menu );

        // Obtenemos la View de busqueda para establecer los listeners
        MenuItem searchItem = menu.findItem( R.id.action_search );
        mSearchView = ( SearchView ) MenuItemCompat.getActionView( searchItem );
        mSearchView.setQueryHint( "Tiendas, secciones, etc." );
        mSearchView.setOnQueryTextListener( this );

        // Establecemos una transicion en el boton de busqueda
        LinearLayout searchBar = ( LinearLayout )mSearchView.findViewById( R.id.search_bar );
        searchBar.setLayoutTransition( new LayoutTransition() );

        return super.onCreateOptionsMenu( menu );
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item )
    {
        // Comprobar que si se pulsa el izquierdo, cierre antes el derecho (si esta abierto)
        if ( mLeftDrawerToggle.onOptionsItemSelected( item ) )
        {
            if ( mDrawerLayout.isDrawerOpen( Gravity.RIGHT ) )
                mDrawerLayout.closeDrawer( Gravity.RIGHT );

            return true;
        }

        // Funcionamiento del right drawer
        if ( item.getItemId() == R.id.right_drawer )
        {
            if ( mDrawerLayout.isDrawerOpen( Gravity.RIGHT ) )
                mDrawerLayout.closeDrawer( Gravity.RIGHT );

            else
                mDrawerLayout.openDrawer( Gravity.RIGHT );

            return true;
        }

        return super.onOptionsItemSelected( item );
    }

    @Override
    public boolean onQueryTextChange( String newText )
    {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit( String text )
    {
        return false;
    }
}
