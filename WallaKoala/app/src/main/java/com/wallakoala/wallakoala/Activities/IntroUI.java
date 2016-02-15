package com.wallakoala.wallakoala.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

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
    protected AppCompatCheckBox man;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mSharedPreferencesManager = new SharedPreferencesManager(this);

        // Especificamos el layout 'intro.xml'
        setContentView(R.layout.intro);

        man   = (AppCompatCheckBox)findViewById(R.id.checkBox);
        enter = (Button)findViewById(R.id.enter);

        enter.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Set<String> shops = new HashSet<>();
                //shops.add( "Blanco" );
                shops.add("HyM");
                shops.add("Springfield");
                shops.add("Pedro Del Hierro");
                shops.add("Blanco");

                mSharedPreferencesManager.insertMan(man.isChecked());
                mSharedPreferencesManager.insertShops(shops);
                mSharedPreferencesManager.insertNewness(true);

                Intent intent = new Intent(IntroUI.this, ProductsUI.class);
                startActivity(intent);

                // Animacion de transicion para pasar de una activity a otra.
                overridePendingTransition(R.anim.right_in, R.anim.right_out);

                // Terminamos la activity, si se comenta, se da la posibilidad de volver a esta pantalla.
                finish();
            }
        });
    }
}
