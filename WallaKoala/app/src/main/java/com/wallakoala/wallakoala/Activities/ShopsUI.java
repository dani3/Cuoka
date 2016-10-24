package com.wallakoala.wallakoala.Activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.RequestFuture;
import com.wallakoala.wallakoala.Adapters.ShopsListAdapter;
import com.wallakoala.wallakoala.Beans.Shop;
import com.wallakoala.wallakoala.Beans.User;
import com.wallakoala.wallakoala.Properties.Properties;
import com.wallakoala.wallakoala.R;
import com.wallakoala.wallakoala.Singletons.VolleySingleton;
import com.wallakoala.wallakoala.Utils.SharedPreferencesManager;
import com.wallakoala.wallakoala.Utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Pantalla que muestra las tiendas del usuario y las disponibles.
 * Created by Daniel Mancebo Aldea on 23/10/2016.
 */

public class ShopsUI extends AppCompatActivity
{
    /* Container Views */
    protected RecyclerView mShopsRecyclerView;
    protected CoordinatorLayout mCoordinatorLayout;

    /* Adapter */
    protected ShopsListAdapter mShopListAdapter;

    /* SharedPreferences */
    protected SharedPreferencesManager mSharedPreferencesManager;

    /* Data */
    protected List<Shop> mAllShopsList;
    protected List<String> mMyShopsList;

    /* User */
    protected User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_shops);

        _initData();
        _retrieveShops();
    }

    /**
     * Metodo que inicializa las distintas ED's y datos.
     */
    private void _initData()
    {
        mSharedPreferencesManager = new SharedPreferencesManager(ShopsUI.this);

        mUser = mSharedPreferencesManager.retreiveUser();

        mMyShopsList.addAll(mUser.getShops());
    }

    /**
     * Metodo que llama al servidor para traer la lista de tiendas.
     */
    private void _retrieveShops()
    {
        new RetrieveShopsFromServer().execute();
    }

    /**
     * Tarea en segundo plano que trae la lista de tiendas del servidor.
     */
    private class RetrieveShopsFromServer extends AsyncTask<String, Void, Void>
    {
        private JSONArray content = null;
        private String error = null;

        @Override
        protected void onPreExecute()
        {
            findViewById(R.id.shops_loading).setVisibility(View.VISIBLE);

            mCoordinatorLayout = (CoordinatorLayout)findViewById(R.id.shops_coordinator);
        }

        @Override
        protected Void doInBackground(String... unused)
        {

            RequestFuture<JSONArray> future = RequestFuture.newFuture();

            final String fixedURL = Utils.fixUrl(Properties.SERVER_URL + ":" + Properties.SERVER_SPRING_PORT
                    + "/shops/" + mUser.getMan());

            Log.d(Properties.TAG, "Conectando con: " + fixedURL + " para traer la lista de tiendas");

            // Creamos una peticion
            final JsonArrayRequest jsonObjReq = new JsonArrayRequest(Request.Method.GET
                                                                , fixedURL
                                                                , null
                                                                , future
                                                                , future);

            // La mandamos a la cola de peticiones
            VolleySingleton.getInstance(ShopsUI.this).addToRequestQueue(jsonObjReq);

            if (isCancelled())
            {
                return null;
            }

            try
            {
                content = future.get(20, TimeUnit.SECONDS);

            } catch (InterruptedException e) {
                error = "Thread interrumpido";
                Log.d(Properties.TAG, error);
            } catch (ExecutionException | TimeoutException e) {
                error = "Timeout exception";
                Log.d(Properties.TAG, error);
            }

            if (isCancelled())
            {
                return null;
            }

            // Si content esta vacio, es que ha fallado la conexion.
            if (content == null)
            {
                error = "Imposible conectar con el servidor";
                Log.d(Properties.TAG, error);

            } else {
                try
                {
                    List<JSONObject> jsonList = new ArrayList<>();

                    for (int j = 0; j < content.length(); j++)
                    {
                        JSONObject js = content.getJSONObject(j);

                        jsonList.add(js);
                    }

                    for (JSONObject jsonObject : jsonList)
                    {
                        String name = jsonObject.getString("name");
                        int numberOfProducts = jsonObject.getInt("products");

                        Shop shop = new Shop(name, false, false, numberOfProducts);

                        mAllShopsList.add(shop);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused)
        {
            findViewById(R.id.shops_loading).setVisibility(View.GONE);

            if (error != null)
            {
                _initRecyclerView();

            } else {
                Snackbar.make(mCoordinatorLayout, "Ops, algo ha ido mal", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Reintentar", new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                new RetrieveShopsFromServer().execute();
                            }
                        });
            }
        }
    }

    /**
     * Metodo que inicializa el RecyclerView con las tiendas.
     */
    private void _initRecyclerView()
    {
        mShopsRecyclerView = (RecyclerView)findViewById(R.id.shops_recyclerview);

        mShopListAdapter = new ShopsListAdapter(this, mAllShopsList, mMyShopsList);
        mShopsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mShopsRecyclerView.setAdapter(mShopListAdapter);
        mShopsRecyclerView.setHasFixedSize(true);
    }
}
