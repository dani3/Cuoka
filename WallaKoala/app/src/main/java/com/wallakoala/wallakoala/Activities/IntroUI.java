package com.wallakoala.wallakoala.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.wallakoala.wallakoala.Beans.UserActivity;
import com.wallakoala.wallakoala.Properties.Properties;
import com.wallakoala.wallakoala.R;
import com.wallakoala.wallakoala.Singletons.VolleySingleton;
import com.wallakoala.wallakoala.Utils.SharedPreferencesManager;
import com.wallakoala.wallakoala.Utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mSharedPreferencesManager = new SharedPreferencesManager(this);

        // Especificamos el layout 'activity_intro.xml'
        setContentView(R.layout.activity_intro);

        // Si tiene el flag de LoggedIn, llamamos al servidor para traer la actividad del usuario.
        if (mSharedPreferencesManager.retreiveLoggedIn())
        {
            long id = mSharedPreferencesManager.retreiveUserId();

            _getUserActivity(id);
        }

        Button enter = (Button)findViewById(R.id.enter);
        enter.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent;

                // TODO: temporal
                //mSharedPreferencesManager.insertLoggedIn(false);

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

    @SuppressWarnings("unchecked")
    private void _getUserActivity(final long id)
    {
        final String fixedURL = Utils.fixUrl(
                Properties.SERVER_URL + ":" + Properties.SERVER_SPRING_PORT + "/users/" + id);

        Log.d(Properties.TAG, "Conectando con: " + fixedURL + " para obtener la actividad del usuario");

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET
                , fixedURL
                , null
                , new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        UserActivity userActivity = new UserActivity();

                        try
                        {
                            JSONArray jsonArray = response.getJSONArray("addedToCartProducts");
                            Set<Long> set = new HashSet<>();
                            for (int i = 0; i < jsonArray.length(); i++)
                            {
                                set.add(Long.valueOf((String.valueOf(jsonArray.get(i)))));
                            }

                            userActivity.setAddedToCartProducts(set);

                            jsonArray = response.getJSONArray("favoriteProducts");
                            set = new HashSet<>();
                            for (int i = 0; i < jsonArray.length(); i++)
                            {
                                set.add(Long.valueOf((String.valueOf(jsonArray.get(i)))));
                            }

                            userActivity.setFavoriteProducts(set);

                            jsonArray = response.getJSONArray("sharedProducts");
                            set = new HashSet<>();
                            for (int i = 0; i < jsonArray.length(); i++)
                            {
                                set.add(Long.valueOf((String.valueOf(jsonArray.get(i)))));
                            }

                            userActivity.setSharedProducts(set);

                            jsonArray = response.getJSONArray("viewedProducts");
                            set = new HashSet<>();
                            for (int i = 0; i < jsonArray.length(); i++)
                            {
                                set.add(Long.valueOf((String.valueOf(jsonArray.get(i)))));
                            }

                            userActivity.setViewedProducts(set);

                            jsonArray = response.getJSONArray("visitedProducts");
                            set = new HashSet<>();
                            for (int i = 0; i < jsonArray.length(); i++)
                            {
                                set.add(Long.valueOf((String.valueOf(jsonArray.get(i)))));
                            }

                            userActivity.setVisitedProducts(set);

                            mSharedPreferencesManager.insertUserActivity(userActivity);

                            Log.d(Properties.TAG, " - Productos favoritos: " + userActivity.getFavoriteProducts().size());
                            Log.d(Properties.TAG, " - Productos visitados en la web: " + userActivity.getVisitedProducts().size());
                            Log.d(Properties.TAG, " - Productos compartidos: " + userActivity.getSharedProducts().size());
                            Log.d(Properties.TAG, " - Productos añadidos al carro: " + userActivity.getAddedToCartProducts().size());
                            Log.d(Properties.TAG, " - Productos vistos: " + userActivity.getViewedProducts().size());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                , new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });

        VolleySingleton.getInstance(IntroUI.this).addToRequestQueue(jsonObjectRequest);
    }
}
