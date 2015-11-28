package com.wallakoala.wallakoala.Activities;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wallakoala.wallakoala.Adapters.ExpandableAdapter;
import com.wallakoala.wallakoala.Adapters.ProductAdapter;
import com.wallakoala.wallakoala.Decorators.ProductDecorator;
import com.wallakoala.wallakoala.R;
import com.wallakoala.wallakoala.Views.AnimatedExpandableListView;

import java.util.ArrayList;
import java.util.List;

/**
 * @class Pantalla principal de la app, donde se muestran los productos
 * Created by Daniel Mancebo on 09/11/2015.
 */

public class ProductsUI extends AppCompatActivity
{
    protected RecyclerView mProductsRecyclerView;
    protected ListView mLeftDrawerListView;
    protected AnimatedExpandableListView mRightDrawerExpandableListView;

    protected ExpandableAdapter mRightDrawerExpandableAdapter;

    protected DrawerLayout mDrawerLayout;

    protected ActionBarDrawerToggle mLeftDrawerToggle;
    protected TextView mActionBarTextView;
    protected EditText mSearchEditText;
    protected ImageView mSearchImageView;
    protected Button mSearchClearButton;

    protected Menu mMenu;

    protected Animation hideToRight, showFromRight;

    /*
     * AUX
     */
    protected String[] aux = new String[]{ "Prueba 1", "Prueba 2", "Prueba 3" };
    List<ExpandableAdapter.GroupItem> expandableItems = new ArrayList<>();
    protected ArrayAdapter<String> menuAdapter;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );

        // Especificamos el layout 'products_grid.xml'
        setContentView(R.layout.products_grid);

        menuAdapter = new ArrayAdapter<>( this
                , android.R.layout.simple_list_item_activated_1
                , aux );

        initActionBar();
        initRecyclerView();
        initNavigationDrawers();
        initSearch();
    }

    /*
     * Configuracion de la action bar
     */
    private void initActionBar()
    {
        // Especificamos que vamos a usar un layout personalizado para la actionBar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);

        // Cargamos el toggle del navigation drawer izquierdo
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Cargamos el textview del titulo de la action bar
        mActionBarTextView = ( TextView )findViewById( R.id.actionBarTitle );
    }

    /*
     * Inicializacion del icono y del edittext de busqueda
     */
    private void initSearch()
    {
        // Inicializamos el icono a la izquierda del edittext
        mSearchImageView = ( ImageView )findViewById( R.id.searchImageView );
        mSearchImageView.setImageResource(android.R.drawable.ic_menu_search);

        mSearchEditText = ( EditText )findViewById( R.id.searchEditText );

        // Inicializamos el boton de borrar y establecemos el listener
        mSearchClearButton = ( Button )findViewById( R.id.searchClearButton );
        mSearchClearButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick( View v )
            {
                mSearchEditText.setText("");
            }
        });
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
        mRightDrawerExpandableListView = ( AnimatedExpandableListView )findViewById( R.id.rightlistviewdrawer );
        mLeftDrawerListView            = ( ListView )findViewById( R.id.leftlistviewdrawer );
        mDrawerLayout                  = ( DrawerLayout )findViewById( R.id.drawer_layout );

        initRightDrawerExpandableList();

        mRightDrawerExpandableAdapter = new ExpandableAdapter(this);
        mRightDrawerExpandableAdapter.setData(expandableItems);
        mRightDrawerExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener()
        {
            @Override
            public boolean onGroupClick( ExpandableListView parent
                                , View v
                                , int groupPosition
                                , long id )
            {
                // Para realizar la animacion al expandir o al cerrar se llama a un metodo de la ExpandableListView
                if (mRightDrawerExpandableListView.isGroupExpanded(groupPosition))
                    mRightDrawerExpandableListView.collapseGroupWithAnimation(groupPosition);

                else
                    mRightDrawerExpandableListView.expandGroupWithAnimation(groupPosition);

                return true;
            }

        });

        mLeftDrawerListView.setAdapter(menuAdapter);
        mRightDrawerExpandableListView.setAdapter(mRightDrawerExpandableAdapter);

        initDrawerToggle();

        mDrawerLayout.setDrawerListener(mLeftDrawerToggle);
    }

    /*
     * Inicializacion y configuracion del drawer toggle del leftDrawer
     */
    private void initDrawerToggle()
    {
        // Inicializamos el control en la action bar
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

                    // Restauramos los expandableItems de la action bar con una translacion
                    for (int i = 0; i < mMenu.size(); i++)
                    {
                        // Sacamos la vista de cada item
                        final View itemView = findViewById(mMenu.getItem(i).getItemId());

                        // Cargamos la animacion y decimos que mantenga el estado cuando termine
                        showFromRight = AnimationUtils.loadAnimation( ProductsUI.this
                                                , R.anim.show_translation_horizontal );
                        showFromRight.setFillAfter(true);

                        itemView.startAnimation(showFromRight);

                        // Habilitamos de nuevo el item
                        itemView.setEnabled( true );
                    }

                    mLeftDrawerToggle.syncState();
                }

                // Si se cierra el drawer derecho, reestablecemos el icono y el titulo
                if ( drawerView == findViewById( R.id.rightDrawerLayout ) )
                {
                    // Reestablecemos el titulo de la action bar
                    mActionBarTextView.setText( R.string.app_name );

                    // Sacamos la vista del toggle derecho
                    final View itemView = findViewById(mMenu.getItem(0).getItemId());

                    // Cargamos las animaciones de entrada y salida
                    hideToRight = AnimationUtils.loadAnimation( ProductsUI.this
                                        , R.anim.hide_translation_horizontal);
                    showFromRight = AnimationUtils.loadAnimation( ProductsUI.this
                                        , R.anim.show_translation_horizontal );
                    showFromRight.setFillAfter(true);

                    // Iniciamos la animacion de salida
                    itemView.startAnimation(hideToRight);

                    // Establecemos un listener para que arranque la animacion de entrada cuando termine la de salida
                    hideToRight.setAnimationListener(new Animation.AnimationListener()
                    {
                        @Override
                        public void onAnimationStart(Animation animation) {}

                        @Override
                        public void onAnimationEnd(Animation animation)
                        {
                            // Cambiamos el icono cuando termine la animacion de salida
                            MenuItem searchItem = mMenu.findItem(R.id.right_drawer);
                            searchItem.setIcon(android.R.drawable.ic_menu_search);

                            itemView.startAnimation(showFromRight);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {}
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
                    // Cambiamos el titulo de la action bar
                    mActionBarTextView.setText( R.string.right_drawer_title );

                    // Borramos lo que se haya escrito anteriormente
                    mSearchEditText.setText( "" );

                    // Sacamos la vista del toggle derecho
                    final View itemView = findViewById(mMenu.getItem(0).getItemId());

                    // Cargamos las animaciones de entrada y salida
                    hideToRight = AnimationUtils.loadAnimation( ProductsUI.this, R.anim.hide_translation_horizontal);
                    showFromRight = AnimationUtils.loadAnimation( ProductsUI.this, R.anim.show_translation_horizontal );
                    showFromRight.setFillAfter(true);

                    // Iniciamos la animacion de salida
                    itemView.startAnimation(hideToRight);

                    // Establecemos un listener para que arranque la animacion de entrada cuando termine la de salida
                    hideToRight.setAnimationListener(new Animation.AnimationListener()
                    {
                        @Override
                        public void onAnimationStart(Animation animation) {}

                        @Override
                        public void onAnimationEnd(Animation animation)
                        {
                            // Cuando termine la animacion de salida, cambiamos el icono
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
        // Si el Navigation Drawer izquierdo esta abierto, ocultamos los expandableItems de la action bar
        if ( mDrawerLayout.isDrawerOpen(Gravity.LEFT) )
        {
            // Cambiamos el titulo de la action bar
            mActionBarTextView.setText( R.string.left_drawer_title );

            // Hacemos desaparecer los expandableItems del menu con una translacion horizontal y los deshabilitamos
            for (int i = 0; i < menu.size(); i++)
            {
                final View itemView = findViewById( menu.getItem( i ).getItemId() );

                hideToRight = AnimationUtils.loadAnimation( this
                                    , R.anim.hide_translation_horizontal );
                hideToRight.setFillAfter( true );
                hideToRight.setAnimationListener( new Animation.AnimationListener()
                {
                    @Override
                    public void onAnimationStart( Animation animation ) {}

                    @Override
                    public void onAnimationEnd( Animation animation ) {
                        itemView.setEnabled( false );
                    }

                    @Override
                    public void onAnimationRepeat( Animation animation ) {}
                });

                itemView.startAnimation( hideToRight );
            }
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu )
    {
        // Guardamos el menu para poder acceder a los expandableItems mas adelante
        this.mMenu = menu;

        // Inflamos la ActionBar
        getMenuInflater().inflate( R.menu.action_bar, menu );

        return super.onCreateOptionsMenu(menu);
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

        // Deshabilitamos el toggle derecho si el izquierdo esta abierto
        if ( ! mDrawerLayout.isDrawerOpen( Gravity.LEFT ) )
        {
            // Funcionamiento del toggle derecho
            if (item.getItemId() == R.id.right_drawer)
            {
                if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT))
                    mDrawerLayout.closeDrawer(Gravity.RIGHT);

                else
                    mDrawerLayout.openDrawer(Gravity.RIGHT);

                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void initRightDrawerExpandableList()
    {
        // Populate our list with groups and its children
        ExpandableAdapter.GroupItem item = new ExpandableAdapter.GroupItem();
        ExpandableAdapter.ChildItem child = new ExpandableAdapter.ChildItem();

        item.header = "Colores";
        child.title = "Rojo";
        item.items.add( child );
        child = new ExpandableAdapter.ChildItem();
        child.title = "Azul";
        item.items.add( child );
        child = new ExpandableAdapter.ChildItem();
        child.title = "Negro";
        item.items.add( child );
        child = new ExpandableAdapter.ChildItem();
        child.title = "Blanco";
        item.items.add( child );
        child = new ExpandableAdapter.ChildItem();

        expandableItems.add(item);

        item = new ExpandableAdapter.GroupItem();
        item.header = "Tallas";
        child.title = "XS";
        item.items.add( child );
        child = new ExpandableAdapter.ChildItem();
        child.title = "S";
        item.items.add( child );
        child = new ExpandableAdapter.ChildItem();
        child.title = "M";
        item.items.add( child );
        child = new ExpandableAdapter.ChildItem();
        child.title = "L";
        item.items.add( child );
        child = new ExpandableAdapter.ChildItem();
        child.title = "XL";
        item.items.add( child );
        child = new ExpandableAdapter.ChildItem();
        child.title = "XXL";
        item.items.add( child );
        child = new ExpandableAdapter.ChildItem();

        expandableItems.add(item);

        item = new ExpandableAdapter.GroupItem();
        item.header = "Secciones";
        child.title = "Camisas";
        item.items.add( child );
        child = new ExpandableAdapter.ChildItem();
        child.title = "Faldas";
        item.items.add( child );
        child = new ExpandableAdapter.ChildItem();
        child.title = "Vestidos";
        item.items.add( child );
        child = new ExpandableAdapter.ChildItem();
        child.title = "Pantalones";
        item.items.add( child );

        expandableItems.add(item);
    }
}
