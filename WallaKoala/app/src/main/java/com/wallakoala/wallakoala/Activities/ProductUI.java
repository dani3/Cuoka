package com.wallakoala.wallakoala.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.wallakoala.wallakoala.Adapters.ProductAdapter;
import com.wallakoala.wallakoala.Beans.ColorVariant;
import com.wallakoala.wallakoala.R;

/**
 * @class Pantalla de un producto.
 * Created by Dani on 23/01/2016.
 */

public class ProductUI extends AppCompatActivity
{
    /* Container Views */
    protected RecyclerView mImageRecyclerView;

    /* LayoutManagers */
    protected GridLayoutManager mGridLayoutManager;

    /* Adapters */
    protected ProductAdapter mProductAdapter;

    /* Data */
    protected ColorVariant mColor;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );

        mColor = ( ColorVariant )getIntent()
                                    .getExtras()
                                    .getSerializable( "com.wallakoala.wallakoala.Beans.ColorVariant" );

        // Especificamos el layout 'product.xml'
        setContentView( R.layout.product );

        _initRecyclerView();
    }

    /**
     * Inicializacion y configuracion del recyclerView.
     */
    protected void _initRecyclerView()
    {
        mImageRecyclerView = ( RecyclerView )findViewById( R.id.product_recycler_view );
        mGridLayoutManager = new GridLayoutManager( this, 1 );
        mProductAdapter    = new ProductAdapter( this, mColor );

        mImageRecyclerView.setLayoutManager( mGridLayoutManager );
        mImageRecyclerView.setAdapter( mProductAdapter );
    }
}
