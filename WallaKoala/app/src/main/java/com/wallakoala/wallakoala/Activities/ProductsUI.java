package com.wallakoala.wallakoala.Activities;

import android.animation.LayoutTransition;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.wallakoala.wallakoala.Adapters.ProductAdapter;
import com.wallakoala.wallakoala.Decorators.ProductDecorator;
import com.wallakoala.wallakoala.R;

/**
 * @class Pantalla principal de la app, donde se muestran los productos
 * Created by Daniel Mancebo on 09/11/2015.
 */

public class ProductsUI extends AppCompatActivity implements  SearchView.OnQueryTextListener
{
    protected RecyclerView productsRecyclerView;
    protected SearchView mSearchView;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );

        // Especificamos el layout 'products_recycler.xml'
        setContentView( R.layout.product_recycler );

        // Cargamos la action bar personalizada
        getSupportActionBar().setDisplayOptions( ActionBar.DISPLAY_SHOW_CUSTOM );
        getSupportActionBar().setCustomView( R.layout.action_bar );

        // Inicializamos el grid de productos
        productsRecyclerView = ( RecyclerView )findViewById( R.id.grid_recycler );
        productsRecyclerView.setAdapter( new ProductAdapter() );
        productsRecyclerView.setLayoutManager( new GridLayoutManager( this, 2 ) );
        productsRecyclerView.addItemDecoration(
                                new ProductDecorator( getResources().getDimensionPixelSize( R.dimen.vertical_spacing_grid )
                                        , getResources().getDimensionPixelSize( R.dimen.horizontal_spacing_grid ) ) );
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu )
    {
        // Inflamos la ActionBar
        getMenuInflater().inflate( R.menu.action_bar, menu );

        // Obtenemos la View de busqueda para establecer los listeners
        MenuItem searchItem = menu.findItem( R.id.action_search );
        mSearchView = ( SearchView ) MenuItemCompat.getActionView( searchItem );
        mSearchView.setQueryHint("Tiendas, secciones, etc.");
        mSearchView.setOnQueryTextListener( this );

        LinearLayout searchBar = ( LinearLayout )mSearchView.findViewById( R.id.search_bar );
        searchBar.setLayoutTransition( new LayoutTransition() );

        return super.onCreateOptionsMenu( menu );
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item )
    {
        int id = item.getItemId();
        if ( id == R.id.action_settings )
            return true;

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
