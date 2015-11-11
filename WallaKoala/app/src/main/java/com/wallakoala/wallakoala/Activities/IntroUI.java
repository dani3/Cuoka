package com.wallakoala.wallakoala.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.greenfrvr.rubberloader.RubberLoaderView;
import com.wallakoala.wallakoala.Beans.Product;
import com.wallakoala.wallakoala.R;

/**
 * @class Pantalla de introduccion de la app
 * Created by Daniel Mancebo on 09/11/2015.
 */

public class IntroUI extends AppCompatActivity
{
    protected RubberLoaderView loader;
    protected Button enter;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        // Ocultamos la barra de notificaciones y el titulo de la app (debe ejecutarse antes del super.onCreate y de cargar el layout)
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                , WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);

        // Especificamos el layout 'intro.xml'
        setContentView( R.layout.intro );

        loader = ( RubberLoaderView )findViewById( R.id.rubber_loader );
        enter = ( Button )findViewById( R.id.enter );
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v )
            {
                Intent intent = new Intent( IntroUI.this, ProductsUI.class );
                startActivity( intent );
                finish();
            }
        });

        loader.startLoading();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }
}
