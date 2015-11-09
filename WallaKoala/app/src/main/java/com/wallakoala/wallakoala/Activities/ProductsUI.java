package com.wallakoala.wallakoala.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import com.wallakoala.wallakoala.R;

/**
 * @class Pantalla principal de la app, donde se muestran los productos
 * Created by Daniel Mancebo on 09/11/2015.
 */

public class ProductsUI extends AppCompatActivity
{
    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );

        // Ocultamos el titulo de la app (antes de especificar el layout)
        this.requestWindowFeature( Window.FEATURE_NO_TITLE );

        // Espeficamos el layout 'products.xml'
        setContentView( R.layout.products );
    }
}
