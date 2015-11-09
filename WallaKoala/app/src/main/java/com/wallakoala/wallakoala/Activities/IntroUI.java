package com.wallakoala.wallakoala.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.wallakoala.wallakoala.R;

/**
 * @class Pantalla de introduccion de la app
 * Created by Daniel Mancebo on 09/11/2015.
 */

public class IntroUI extends AppCompatActivity
{
    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );

        // Ocultamos la barra de notificaciones y el titulo de la app (antes de especificar el layout)
        this.requestWindowFeature( Window.FEATURE_NO_TITLE );
        this.getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN
                , WindowManager.LayoutParams.FLAG_FULLSCREEN );

        // Especificamos el layout 'intro.xml'
        setContentView( R.layout.intro );
    }
}
