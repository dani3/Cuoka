package com.wallakoala.wallakoala.Activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.wallakoala.wallakoala.R;
import com.wallakoala.wallakoala.Singletons.RestClientSingleton;
import com.wallakoala.wallakoala.Utils.SharedPreferencesManager;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * Pantalla de introduccion de la app.
 * Created by Daniel Mancebo Aldea on 09/11/2015.
 */

public class IntroUI extends AppCompatActivity
{
    /* SharedPreferences */
    private SharedPreferencesManager mSharedPreferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Especificamos el layout 'activity_intro.xml'
        setContentView(R.layout.activity_intro);

        _initLogFile();

        new RetrieveUserTask(this).execute();
    }

    private void _initLogFile()
    {
        try
        {
            File filename = new File(Environment.getExternalStorageDirectory() + "/cuoka.log");
            filename.createNewFile();

            String cmd = "logcat -d -f " + filename.getAbsolutePath();

            Runtime.getRuntime().exec(cmd);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tarea en segundo plano que trae los datos del usuario.
     */
    private class RetrieveUserTask extends AsyncTask<String, Void, Void>
    {
        private WeakReference<IntroUI> context;

        private boolean isNetworkAvailable;

        public RetrieveUserTask(IntroUI context)
        {
            this.context = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute()
        {
            if (context.get() != null)
            {
                isNetworkAvailable = _isNetworkAvailable();

                if (!isNetworkAvailable)
                {
                    Snackbar.make(findViewById(R.id.intro_frame_layout), "No hay conexión a Internet", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Reintentar", new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    new RetrieveUserTask(IntroUI.this).execute();
                                }
                            }).show();
                }
            }
        }

        @Override
        protected Void doInBackground(String... unused)
        {
            if (isNetworkAvailable)
            {
                _retrieveUser();
            }

            return null;
        }
    }

    /**
     * Metodo que se conecta al servidor para traer los datos del usuario.
     */
    private void _retrieveUser()
    {
        mSharedPreferencesManager = new SharedPreferencesManager(this);

        if (mSharedPreferencesManager.retreiveLoggedIn())
        {
            boolean correct = RestClientSingleton.retrieveUser(this);

            if (correct)
            {
                Intent intent = new Intent(this, MainScreenUI.class);

                startActivity(intent);

                finish();

                // Animacion de transicion para pasar de una activity a otra.
                overridePendingTransition(R.anim.right_in_animation, R.anim.right_out_animation);

            } else {
                Snackbar.make(findViewById(R.id.intro_frame_layout), "Ops, algo ha ido mal", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Reintentar", new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                // Si ocurre algun error (raro), forzamos a que se loguee
                                mSharedPreferencesManager.insertLoggedIn(false);

                                new RetrieveUserTask(IntroUI.this).execute();
                            }
                        }).show();
            }

        } else {
            Intent intent = new Intent(this, LoginUI.class);

            startActivity(intent);

            finish();

            // Animacion de transicion para pasar de una activity a otra.
            overridePendingTransition(R.anim.right_in_animation, R.anim.right_out_animation);
        }
    }

    /**
     * Metodo que comprueba si hay conexion a Internet.
     * @return true si hay conexion a Internet.
     */
    private boolean _isNetworkAvailable()
    {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return (activeNetworkInfo != null) && activeNetworkInfo.isConnected();
    }
}
