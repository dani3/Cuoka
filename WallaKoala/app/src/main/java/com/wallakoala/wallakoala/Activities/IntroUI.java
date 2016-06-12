package com.wallakoala.wallakoala.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
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
    protected AppCompatCheckBox man;
    protected AppCompatCheckBox blanco, hm, spf, pdh;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mSharedPreferencesManager = new SharedPreferencesManager(this);

        // Especificamos el layout 'intro.xml'
        setContentView(R.layout.intro);

        man      = (AppCompatCheckBox)findViewById(R.id.checkBox);
        blanco   = (AppCompatCheckBox)findViewById(R.id.blanco);
        hm       = (AppCompatCheckBox)findViewById(R.id.hm);
        spf      = (AppCompatCheckBox)findViewById(R.id.spf);
        pdh      = (AppCompatCheckBox)findViewById(R.id.pdh);
        enter    = (Button)findViewById(R.id.enter);

        blanco.setChecked(true);
        hm.setChecked(true);
        pdh.setChecked(true);
        spf.setChecked(true);

        enter.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Set<String> shops = new HashSet<>();

                if (hm.isChecked())
                    shops.add("HyM");

                if (spf.isChecked())
                    shops.add("Springfield");

                if (pdh.isChecked())
                    shops.add("Pedro Del Hierro");

                if (blanco.isChecked())
                    shops.add("Blanco");

                mSharedPreferencesManager.insertMan(man.isChecked());
                mSharedPreferencesManager.insertShops(shops);

                Intent intent = new Intent(IntroUI.this, SignUpUI.class);
                startActivity(intent);

                // Animacion de transicion para pasar de una activity a otra.
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
            }
        });
    }
}
