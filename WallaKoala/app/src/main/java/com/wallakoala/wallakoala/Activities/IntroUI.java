package com.wallakoala.wallakoala.Activities;

import android.animation.Animator;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wallakoala.wallakoala.BroadcastReceivers.NotificationReceiver;
import com.wallakoala.wallakoala.Properties.Properties;
import com.wallakoala.wallakoala.R;
import com.wallakoala.wallakoala.Singletons.RestClientSingleton;
import com.wallakoala.wallakoala.Utils.ExceptionPrinter;
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
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Especificamos el layout 'activity_intro.xml'
        setContentView(R.layout.activity_intro);

        _initLogFile();
        //_scheduleAlarm();

        new RetrieveUserTask(this).execute();
    }

    /**
     * Metodo que vuelca en un fichero los logs.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void _initLogFile()
    {
        try
        {
            if (_isExternalStorageWritable())
            {
                File logFile = new File(getExternalCacheDir(), "cuoka.log");

                if (logFile.exists())
                {
                    logFile.delete();
                }

                // Limpiamos el logcat antiguo y escribimos el nuevo.
                Runtime.getRuntime().exec("logcat -c");
                Runtime.getRuntime().exec("logcat -f " + logFile);
            }

        } catch (IOException e) {
            ExceptionPrinter.printException("INTRO", e);
        }
    }

    /**
     * Tarea en segundo plano que trae los datos del usuario.
     */
    private static class RetrieveUserTask extends AsyncTask<String, Void, Void>
    {
        private WeakReference<IntroUI> context;

        private boolean isNetworkAvailable;

        public RetrieveUserTask(IntroUI context)
        {
            this.context = new WeakReference<>(context);
        }

        @Override
        @SuppressWarnings("deprecation")
        protected void onPreExecute()
        {
            if (context.get() != null)
            {
                Log.d(Properties.TAG, "[INTRO] Comprobamos que hay red");
                isNetworkAvailable = _isNetworkAvailable(context.get());

                if (!isNetworkAvailable)
                {
                    Log.d(Properties.TAG, "[INTRO] NO hay conexión a Internet");

                    Snackbar snackbar = Snackbar.make(context.get().findViewById(R.id.intro_frame_layout), "No hay conexión a Internet", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Reintentar", new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    new RetrieveUserTask(context.get()).execute();
                                }
                            });

                    snackbar.getView().setBackgroundColor(context.get().getResources().getColor(android.R.color.white));
                    snackbar.setActionTextColor(context.get().getResources().getColor(R.color.colorAccent));
                    ((TextView)snackbar.getView().findViewById(android.support.design.R.id.snackbar_text))
                            .setTextColor(context.get().getResources().getColor(R.color.colorText));

                    snackbar.show();
                }
            }
        }

        @Override
        protected Void doInBackground(String... unused)
        {
            if (isNetworkAvailable)
            {
                Log.d(Properties.TAG, "[INTRO] Hay conexión a Internet");

                _retrieveUser(context.get(), context.get());
            }

            return null;
        }
    }

    /**
     * Metodo que se conecta al servidor para traer los datos del usuario.
     */
    @SuppressWarnings("deprecation")
    private static void _retrieveUser(final Context context, final IntroUI introUI)
    {
        final SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(context);

        Log.d(Properties.TAG, "[INTRO] Comprobamos que el usuario está logueado");
        if (sharedPreferencesManager.retreiveLoggedIn())
        {
            Log.d(Properties.TAG, "[INTRO] El usuario está logueado, se traen sus datos");
            boolean correct = RestClientSingleton.retrieveUser(context);

            if (correct)
            {
                Log.d(Properties.TAG, "[INTRO] Todo correcto -> MainScreenUI");

                ImageView logoImageView = (ImageView) ((Activity) context).findViewById(R.id.intro_logo);
                logoImageView.animate()
                             .setDuration(250)
                             .alpha(0.0f)
                             .setListener(new Animator.AnimatorListener()
                             {
                                 @Override
                                 public void onAnimationStart(Animator animator) {}

                                 @Override
                                 public void onAnimationEnd(Animator animator)
                                 {
                                     // Creamos el intent
                                     Intent intent = new Intent(context, MainScreenUI.class);

                                     context.startActivity(intent);

                                     ((Activity) context).finish();

                                     ((Activity) context).overridePendingTransition(0, 0);
                                 }

                                 @Override
                                 public void onAnimationCancel(Animator animator) {}

                                 @Override
                                 public void onAnimationRepeat(Animator animator) {}
                             });

            } else {
                Snackbar snackbar = Snackbar.make(((Activity)context).findViewById(R.id.intro_frame_layout)
                        , context.getResources().getString(R.string.error_message)
                        , Snackbar.LENGTH_INDEFINITE).setAction("Reintentar", new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                Log.d(Properties.TAG, "[INTRO] El usuario hace CLICK -> 'Reintentar'");
                                Log.d(Properties.TAG, "[INTRO] Se desloguea al usuario y se llama de nuevo a RetrieveUserTask");

                                // Si ocurre algun error (raro), forzamos a que se loguee,
                                sharedPreferencesManager.insertLoggedIn(false);

                                new RetrieveUserTask(introUI).execute();
                            }
                        });

                snackbar.getView().setBackgroundColor(context.getResources().getColor(android.R.color.white));
                snackbar.setActionTextColor(context.getResources().getColor(R.color.colorAccent));
                ((TextView)snackbar.getView().findViewById(android.support.design.R.id.snackbar_text))
                        .setTextColor(context.getResources().getColor(R.color.colorText));

                snackbar.show();
            }

        } else {
            Log.d(Properties.TAG, "[INTRO] El usuario no está logueado -> LoginUI");

            Intent intent;

            /*if (!sharedPreferencesManager.retrieveTourVisited())
            {
                intent = new Intent(context, TourUI.class);
            } else {
                intent = new Intent(context, LoginUI.class);
            }*/

            intent = new Intent(context, LoginUI.class);

            context.startActivity(intent);

            ((Activity)context).finish();

            // Animacion de transicion para pasar de una activity a otra.
            ((Activity)context).overridePendingTransition(R.anim.right_in_animation, R.anim.right_out_animation);
        }
    }

    /**
     * Metodo que comprueba si hay conexion a Internet.
     * @return true si hay conexion a Internet.
     */
    private static boolean _isNetworkAvailable(Context context)
    {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return (activeNetworkInfo != null) && activeNetworkInfo.isConnected();
    }

    private static boolean _isExternalStorageWritable()
    {
        String state = Environment.getExternalStorageState();

        return Environment.MEDIA_MOUNTED.equals(state);
    }

    private void _scheduleAlarm()
    {
        // Construct an intent that will execute the AlarmReceiver
        Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);

        // Create a PendingIntent to be triggered when the alarm goes off
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, NotificationReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Setup periodic alarm every every half hour from this point onwards
        long firstMillis = System.currentTimeMillis();

        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        // First parameter is the type: ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, RTC_WAKEUP
        // Interval can be INTERVAL_FIFTEEN_MINUTES, INTERVAL_HALF_HOUR, INTERVAL_HOUR, INTERVAL_DAY
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis,
                AlarmManager.INTERVAL_FIFTEEN_MINUTES, pIntent);
    }
}
