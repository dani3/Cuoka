package com.wallakoala.wallakoala.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.wallakoala.wallakoala.R;

/**
 * @class Pantalla de introduccion de la app.
 * Created by Daniel Mancebo on 09/11/2015.
 */

public class IntroUI extends AppCompatActivity
{
    /* Views */
    protected Button enter;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate(savedInstanceState);

        // Especificamos el layout 'intro.xml'
        setContentView( R.layout.intro );

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
    }
}
