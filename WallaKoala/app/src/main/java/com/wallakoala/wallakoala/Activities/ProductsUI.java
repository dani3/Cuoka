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
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

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
    protected ListView listViewDrawer;
    protected DrawerLayout mDrawerLayout;
    protected ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate(savedInstanceState);

        // Especificamos el layout 'products_grid.xml'
        setContentView(R.layout.products_grid);

        // Cargamos la action bar personalizada
        getSupportActionBar().setDisplayOptions( ActionBar.DISPLAY_SHOW_CUSTOM );
        getSupportActionBar().setCustomView(R.layout.action_bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);

        // Inicializamos el navigation drawer y el control en la action bar
        mDrawerLayout = ( DrawerLayout )findViewById( R.id.drawer_layout );
        mDrawerToggle = new ActionBarDrawerToggle( this, mDrawerLayout, R.string.open_drawer, R.string.close_drawer )
        {
            @Override
            public void onDrawerOpened( View drawerView ) {
                super.onDrawerOpened( drawerView );
            }

            @Override
            public void onDrawerClosed( View drawerView ) {
                super.onDrawerClosed( drawerView );
            }
        };

        mDrawerLayout.setDrawerListener( mDrawerToggle );

        listViewDrawer = ( ListView )findViewById( R.id.listviewdrawer );
        final String[] aux = new String[]{ "Prueba 1", "Prueba 2", "Prueba 3" };
        ArrayAdapter<String> menuAdapter = new ArrayAdapter<>( this, android.R.layout.simple_list_item_activated_1, aux );
        listViewDrawer.setAdapter( menuAdapter );

        // Inicializamos el grid de productos
        mProductsRecyclerView = ( RecyclerView )findViewById(R.id.grid_recycler);
        mProductsRecyclerView.setAdapter( new ProductAdapter() );
        mProductsRecyclerView.setLayoutManager( new GridLayoutManager( this, 2 ) );
        mProductsRecyclerView.addItemDecoration(
                new ProductDecorator( getResources().getDimensionPixelSize( R.dimen.vertical_spacing_grid )
                        , getResources().getDimensionPixelSize( R.dimen.horizontal_spacing_grid ) ) );
    }

    @Override
    protected void onPostCreate( Bundle savedInstanceState )
    {
        super.onPostCreate( savedInstanceState );
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged( Configuration newConfig )
    {
        super.onConfigurationChanged( newConfig );
        mDrawerToggle.onConfigurationChanged( newConfig );
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu )
    {
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

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item )
    {
        if ( mDrawerToggle.onOptionsItemSelected( item ) )
            return true;

        if ( item.getItemId() == R.id.right_drawer ) {
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
