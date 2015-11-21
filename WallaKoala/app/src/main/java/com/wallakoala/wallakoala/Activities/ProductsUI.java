package com.wallakoala.wallakoala.Activities;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wallakoala.wallakoala.Adapters.ProductAdapter;
import com.wallakoala.wallakoala.Decorators.ProductDecorator;
import com.wallakoala.wallakoala.R;

/**
 * @class Pantalla principal de la app, donde se muestran los productos
 * Created by Daniel Mancebo on 09/11/2015.
 */

public class ProductsUI extends AppCompatActivity
{
    protected RecyclerView mProductsRecyclerView;
    protected ListView mRightDrawerListView, mLeftDrawerListView;
    protected DrawerLayout mDrawerLayout;
    protected ActionBarDrawerToggle mLeftDrawerToggle;
    protected TextView mActionBarTextView;
    protected EditText mSearchEditText;
    protected ImageView mSearchImageView;
    protected Menu mMenu;

    protected Animation hideToRight, showFromRight, fadeIn, fadeOut;

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
        initSearch();

        mSearchEditText = ( EditText )findViewById( R.id.searchEditText );
        mSearchEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() - 200 >= (mSearchEditText.getRight() - mSearchEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        mSearchEditText.setText("");

                        return true;
                    }
                }

                return false;
            }
        });
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
     * Inicializacion del icono de busqueda
     */
    private void initSearch()
    {
        mSearchImageView = ( ImageView )findViewById( R.id.searchImageView );
        mSearchImageView.setImageResource(android.R.drawable.ic_menu_search);
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

                        itemView.startAnimation( showFromRight );
                    }

                    mLeftDrawerToggle.syncState();
                }

                // Si se cierra el drawer derecho, reestablecemos el icono y el titulo
                if ( drawerView == findViewById( R.id.rightDrawerLayout ) )
                {
                    mActionBarTextView.setText( R.string.app_name );

                    final View itemView = findViewById(mMenu.getItem(0).getItemId());

                    hideToRight = AnimationUtils.loadAnimation( ProductsUI.this, R.anim.hide_translation_horizontal);
                    showFromRight = AnimationUtils.loadAnimation( ProductsUI.this, R.anim.show_translation_horizontal );
                    showFromRight.setFillAfter(true);

                    itemView.startAnimation(hideToRight);

                    hideToRight.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            MenuItem searchItem = mMenu.findItem(R.id.right_drawer);
                            searchItem.setIcon(android.R.drawable.ic_menu_search);

                            itemView.startAnimation(showFromRight);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });

                    // Si se cierra el drawer derecho con el teclado abierto, lo ocultamos
                    View view = ProductsUI.this.getCurrentFocus();
                    if (view != null)
                    {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
            }

            // Metodo llamado cuando el drawer esta completamente abierto
            @Override
            public void onDrawerOpened( View drawerView )
            {
                // Comprobamos si es el drawer izquierdo el que se ha abierto
                if (drawerView == findViewById( R.id.leftDrawerLayout ) )
                {
                    // Crea la llamada a onPrepareOptionsMenu()
                    supportInvalidateOptionsMenu();
                    mLeftDrawerToggle.syncState();
                }

                // Si se abre el drawer derecho, cambiamos el icono y el titulo
                if( drawerView == findViewById( R.id.rightDrawerLayout ) )
                {
                    mActionBarTextView.setText( R.string.right_drawer_title );

                    final View itemView = findViewById(mMenu.getItem(0).getItemId());

                    hideToRight = AnimationUtils.loadAnimation( ProductsUI.this, R.anim.hide_translation_horizontal);
                    showFromRight = AnimationUtils.loadAnimation( ProductsUI.this, R.anim.show_translation_horizontal );
                    showFromRight.setFillAfter(true);

                    itemView.startAnimation(hideToRight);

                    hideToRight.setAnimationListener(new Animation.AnimationListener()
                    {
                        @Override
                        public void onAnimationStart(Animation animation) {}

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            MenuItem searchItem = mMenu.findItem(R.id.right_drawer);
                            searchItem.setIcon(android.R.drawable.ic_menu_revert);

                            itemView.startAnimation(showFromRight);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {}
                    });
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
        if ( mDrawerLayout.isDrawerOpen(Gravity.LEFT) )
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

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu )
    {
        // Guardamos el menu para poder acceder a los items mas adelante
        mMenu = menu;

        // Inflamos la ActionBar
        getMenuInflater().inflate( R.menu.action_bar, menu );

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

        if ( ! mDrawerLayout.isDrawerOpen( Gravity.LEFT ) )
        {
            // Funcionamiento del right drawer
            if (item.getItemId() == R.id.right_drawer)
            {
                if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT))
                    mDrawerLayout.closeDrawer(Gravity.RIGHT);

                else
                    mDrawerLayout.openDrawer(Gravity.RIGHT);

                return true;
            }
        }

        return super.onOptionsItemSelected( item );
    }
}
