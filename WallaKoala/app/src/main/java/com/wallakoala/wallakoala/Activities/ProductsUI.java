package com.wallakoala.wallakoala.Activities;

import android.animation.LayoutTransition;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.LinearLayout;

import com.github.ksoichiro.android.observablescrollview.ObservableGridView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.wallakoala.wallakoala.Adapters.ImageAdapter;
import com.wallakoala.wallakoala.R;

/**
 * @class Pantalla principal de la app, donde se muestran los productos
 * Created by Daniel Mancebo on 09/11/2015.
 */

public class ProductsUI extends AppCompatActivity implements ObservableScrollViewCallbacks, SearchView.OnQueryTextListener
{
    protected ObservableGridView productsGrid;
    protected SearchView mSearchView;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate(savedInstanceState);

        // Especificamos el layout 'products.xml'
        setContentView( R.layout.products );

        // Cargamos la action bar personalizada
        getSupportActionBar().setDisplayOptions( ActionBar.DISPLAY_SHOW_CUSTOM );
        getSupportActionBar().setCustomView( R.layout.action_bar );

        // Inicializamos el grid de productos
        productsGrid = ( ObservableGridView )findViewById( R.id.gridview );
        productsGrid.setAdapter( new ImageAdapter( ProductsUI.this ) );
        productsGrid.setScrollViewCallbacks( this );
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

    @Override
    public void onScrollChanged( int scrollY
                    , boolean firstScroll
                    , boolean dragging ) {}

    @Override
    public void onDownMotionEvent() {}

    @Override
    public void onUpOrCancelMotionEvent( ScrollState scrollState )
    {
        ActionBar mActionBar = getSupportActionBar();

        if ( scrollState == ScrollState.UP )
        {
            if ( mActionBar.isShowing() )
                mActionBar.hide();
        }

        else if ( scrollState == ScrollState.DOWN )
        {
            if ( ! mActionBar.isShowing() )
                mActionBar.show();
        }
    }
}
