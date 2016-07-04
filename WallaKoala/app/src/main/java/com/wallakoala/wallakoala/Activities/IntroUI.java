package com.wallakoala.wallakoala.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.wallakoala.wallakoala.R;
import com.wallakoala.wallakoala.Utils.SharedPreferencesManager;

/**
 * @class Pantalla de introduccion de la app.
 * Created by Daniel Mancebo on 09/11/2015.
 */

public class IntroUI extends AppCompatActivity
{
    /* SharedPreferences */
    protected SharedPreferencesManager mSharedPreferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mSharedPreferencesManager = new SharedPreferencesManager(this);

        // Especificamos el layout 'activity_intro.xml'
        setContentView(R.layout.activity_intro);

        Button enter = (Button)findViewById(R.id.enter);
        enter.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent;

                if (mSharedPreferencesManager.retreiveLoggedIn())
                {
                    intent = new Intent(IntroUI.this, MainScreenUI.class);

                } else {
                    intent = new Intent(IntroUI.this, LoginUI.class);
                }

                startActivity(intent);

                finish();
            }
        });

        Button login = (Button)findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSharedPreferencesManager.insertLoggedIn(false);
            }
        });
    }
}
