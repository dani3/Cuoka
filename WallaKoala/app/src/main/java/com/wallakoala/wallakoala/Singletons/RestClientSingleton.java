package com.wallakoala.wallakoala.Singletons;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.wallakoala.wallakoala.Beans.Product;
import com.wallakoala.wallakoala.Beans.User;
import com.wallakoala.wallakoala.Properties.Properties;
import com.wallakoala.wallakoala.Utils.SharedPreferencesManager;
import com.wallakoala.wallakoala.Utils.Utils;

/**
 * Clase que realiza todas las peticiones al servidor.
 * Created by Daniel Mancebo Aldea on 21/10/2016.
 */

public class RestClientSingleton
{
    /**
     * Metodo que envia al servidor el producto favorito.
     * @param context: contexto
     * @param product: producto favorito
     */
    public static void sendFavoriteProduct(Context context, final Product product)
    {
        final SharedPreferencesManager mSharedPreferencesManager = new SharedPreferencesManager(context);

        final User user = mSharedPreferencesManager.retreiveUser();
        final long id = user.getId();

        final String fixedURL = Utils.fixUrl(Properties.SERVER_URL + ":" + Properties.SERVER_SPRING_PORT
                + "/users/" + id + "/" + product.getId() + "/" + Properties.ACTION_FAVORITE);

        Log.d(Properties.TAG, "Conectando con: " + fixedURL + " para anadir/quitar un producto de favoritos");

        final StringRequest stringRequest = new StringRequest(Request.Method.GET
                , fixedURL
                , new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        Log.d(Properties.TAG, "Respuesta del servidor: " + response);

                        if (!response.equals(Properties.PRODUCT_NOT_FOUND) || !response.equals(Properties.USER_NOT_FOUND))
                        {
                            // Si contiene el producto, es que se quiere quitar de favoritos.
                            if (user.getFavoriteProducts().contains(product.getId()))
                            {
                                user.getFavoriteProducts().remove(product.getId());

                            } else {
                                user.getFavoriteProducts().add(product.getId());
                            }

                            mSharedPreferencesManager.insertUser(user);
                        }
                    }
                }
                , new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {}
                });

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    /**
     * Metodo que envia al servidor el producto visto.
     * @param context: contexto
     * @param product: producto favorito
     */
    public static void sendViewedProduct(Context context, final Product product)
    {
        final SharedPreferencesManager mSharedPreferencesManager = new SharedPreferencesManager(context);

        final User user = mSharedPreferencesManager.retreiveUser();
        final long id = user.getId();

        final String fixedURL = Utils.fixUrl(Properties.SERVER_URL + ":" + Properties.SERVER_SPRING_PORT
                + "/users/" + id + "/" + product.getId() + "/" + Properties.ACTION_VIEWED);

        Log.d(Properties.TAG, "Conectando con: " + fixedURL + " para marcar el producto como visto");

        StringRequest stringRequest = new StringRequest(Request.Method.GET
                , fixedURL
                , new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {}
                }
                , new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {}
                });

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }
}
