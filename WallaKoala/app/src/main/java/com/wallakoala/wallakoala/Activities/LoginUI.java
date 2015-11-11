package com.wallakoala.wallakoala.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import com.wallakoala.wallakoala.R;

/**
 * @class Pantalla de login que se ejecutara la primera vez que se ejecute la app
 * Created by Daniel Mancebo on 09/11/2015.
 */

public class LoginUI extends AppCompatActivity
{
    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        // Ocultamos el titulo de la app (debe ejecutarse antes del super.onCreate y de cargar el layout)
        this.requestWindowFeature( Window.FEATURE_NO_TITLE );

        super.onCreate( savedInstanceState );

        // Especificamos el layout 'login.xml'
        setContentView( R.layout.login );
    }
}
