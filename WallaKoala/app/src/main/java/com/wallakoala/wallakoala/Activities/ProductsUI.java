package com.wallakoala.wallakoala.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.GridView;

import com.wallakoala.wallakoala.Adapters.ImageAdapter;
import com.wallakoala.wallakoala.R;

/**
 * @class Pantalla principal de la app, donde se muestran los productos
 * Created by Daniel Mancebo on 09/11/2015.
 */

public class ProductsUI extends AppCompatActivity
{
    protected GridView productsGrid;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        // Ocultamos el titulo de la app (debe ejecutarse antes del super.onCreate y de cargar el layout)
        this.requestWindowFeature( Window.FEATURE_NO_TITLE );

        super.onCreate( savedInstanceState );

        // Especificamos el layout 'products.xml'
        setContentView( R.layout.products );

        productsGrid = ( GridView )findViewById( R.id.gridview );
        productsGrid.setAdapter( new ImageAdapter( ProductsUI.this ) );

        // Hacer cada item del grid que dependa del tamaño de la pantalla, no con valores absolutos
        // http://stackoverflow.com/questions/6557220/defining-a-percentage-width-for-a-linearlayout
    }
}
