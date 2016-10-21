package com.wallakoala.wallakoala.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.wallakoala.wallakoala.Beans.User;
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
 * Pantalla de introduccion de la app.
 * Created by Daniel Mancebo Aldea on 09/11/2015.
 */

public class IntroUI extends AppCompatActivity
{
    private static boolean DONE = false;

    /* SharedPreferences */
    protected SharedPreferencesManager mSharedPreferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mSharedPreferencesManager = new SharedPreferencesManager(this);

        // Especificamos el layout 'activity_intro.xml'
        setContentView(R.layout.activity_intro);

        if (mSharedPreferencesManager.retreiveLoggedIn())
        {
            final long id = mSharedPreferencesManager.retreiveUser().getId();

            final String fixedURL = Utils.fixUrl(
                    Properties.SERVER_URL + ":" + Properties.SERVER_SPRING_PORT + "/users/" + id);

            Log.d(Properties.TAG, "Conectando con: " + fixedURL + " para obtener los datos del usuario");

            final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET
                    , fixedURL
                    , null
                    , new Response.Listener<JSONObject>()
                    {
                        @Override
                        public void onResponse(JSONObject response)
                        {
                            try
                            {
                                final User user = new User();

                                user.setId(id);
                                user.setName(response.getString("name"));
                                user.setAge(response.getInt("age"));
                                user.setEmail(response.getString("email"));
                                user.setPassword(response.getString("password"));
                                user.setMan(response.getBoolean("man"));
                                user.setPostalCode(response.getInt("postalCode"));

                                // Sacamos los productos favoritos
                                JSONArray jsonArray = response.getJSONArray("favoriteProducts");
                                Set<Long> favorites = new HashSet<>();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    favorites.add(Long.valueOf((String.valueOf(jsonArray.get(i)))));
                                }

                                user.setFavoriteProducts(favorites);

                                // Sacamos la lista de tiendas
                                jsonArray = response.getJSONArray("shops");
                                Set<String> shops = new HashSet<>();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    shops.add((String.valueOf(jsonArray.get(i))));
                                }

                                if (!shops.isEmpty())
                                {
                                    user.setShops(shops);
                                } else {
                                    user.setShops(new HashSet<String>());
                                }

                                Log.d(Properties.TAG, "Datos del usuario: ");
                                Log.d(Properties.TAG, " - ID: " + id);
                                Log.d(Properties.TAG, " - Nombre: " + user.getName());
                                Log.d(Properties.TAG, " - Email: " + user.getEmail());
                                Log.d(Properties.TAG, " - Contraseña: " + user.getPassword());
                                Log.d(Properties.TAG, " - Hombre: " + user.getMan());
                                Log.d(Properties.TAG, " - Edad: " + user.getAge());
                                Log.d(Properties.TAG, " - Codigo Postal: " + user.getPostalCode());
                                Log.d(Properties.TAG, " - Numero de favoritos: " + user.getFavoriteProducts().size());
                                Log.d(Properties.TAG, " - Tiendas: " + jsonArray);

                                mSharedPreferencesManager.insertUser(user);

                                DONE = true;

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    , new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) { DONE = true; }
                    });

            VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

        } else {
            DONE = true;
        }

        final Button enter = (Button)findViewById(R.id.enter);
        enter.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent;

                if (DONE)
                {
                    if (mSharedPreferencesManager.retreiveLoggedIn())
                    {
                        intent = new Intent(IntroUI.this, MainScreenUI.class);

                    } else {
                        intent = new Intent(IntroUI.this, LoginUI.class);
                    }

                    startActivity(intent);

                    finish();
                }
            }
        });

        final Button login = (Button)findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSharedPreferencesManager.insertLoggedIn(false);
            }
        });
    }
}
