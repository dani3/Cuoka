package com.wallakoala.wallakoala.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.wallakoala.wallakoala.R;
import com.wallakoala.wallakoala.Utils.SharedPreferencesManager;

import java.util.HashSet;
import java.util.Set;

/**
 * @class Pantalla de introduccion de la app.
 * Created by Daniel Mancebo on 09/11/2015.
 */

public class IntroUI extends AppCompatActivity
{
    /* SharedPreferences */
    protected SharedPreferencesManager mSharedPreferencesManager;

    /* Views */
    protected Button enter;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate(savedInstanceState);

        // Especificamos el layout 'intro.xml'
        setContentView( R.layout.intro );

        Set<String> shops = new HashSet<>();
        shops.add( "Blanco" );
        shops.add( "HyM" );
        shops.add( "Springfield" );
        shops.add( "PedroDelHierro" );

        mSharedPreferencesManager = new SharedPreferencesManager( this );
        mSharedPreferencesManager.insertMan( true );
        mSharedPreferencesManager.insertShops( shops );
        mSharedPreferencesManager.insertNewness( true );

        enter = ( Button )findViewById( R.id.enter );
        enter.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick( View v )
            {
                Intent intent = new Intent( IntroUI.this, ProductsUI.class );
                startActivity( intent );

                // Animacion de transicion para pasar de una activity a otra.
                overridePendingTransition( R.anim.push_down_in, R.anim.push_down_out );

                // Terminamos la activity, si se comenta, se da la posibilidad de volver a esta pantalla.
                finish();
            }
        });
    }
}
