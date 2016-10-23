package com.wallakoala.wallakoala.Singletons;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.wallakoala.wallakoala.Beans.Product;
import com.wallakoala.wallakoala.Beans.User;
import com.wallakoala.wallakoala.Properties.Properties;
import com.wallakoala.wallakoala.Utils.SharedPreferencesManager;
import com.wallakoala.wallakoala.Utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Clase que realiza todas las peticiones al servidor.
 * Created by Daniel Mancebo Aldea on 21/10/2016.
 */

public class RestClientSingleton
{
    /**
     * Metodo que envia las modificaciones del usuario.
     * @param context: contexto.
     * @param name: nombre del usuario.
     * @param email: email del usuario.
     * @param password: contraseña del usuario.
     * @param age: edad del usuario.
     * @param postalCode: codigo postal del usuario.
     */
    public static boolean sendUserModification(final Context context
                                , final String name
                                , final String email
                                , final String password
                                , final short age
                                , final int postalCode)
    {
        final SharedPreferencesManager mSharedPreferencesManager = new SharedPreferencesManager(context);

        final User user = mSharedPreferencesManager.retreiveUser();
        final long id = user.getId();

        final String fixedURL = Utils.fixUrl(Properties.SERVER_URL + ":" + Properties.SERVER_SPRING_PORT
                + "/users/" + id);

        Log.d(Properties.TAG, "Conectando con: " + fixedURL + " para modificar el usuario");

        try
        {
            RequestFuture<String> future = RequestFuture.newFuture();

            // Creamos el JSON con los datos del usuario
            final JSONObject jsonObject = new JSONObject();

            jsonObject.put("name", (name.isEmpty()) ? null : name);
            jsonObject.put("age", age);
            jsonObject.put("email", (email.isEmpty()) ? null: email);
            jsonObject.put("password", (password.isEmpty()) ? null : password);
            jsonObject.put("postalCode", postalCode);

            Log.d(Properties.TAG, "JSON con las modificaciones:\n    " + jsonObject.toString());

            StringRequest stringRequest = new StringRequest(Request.Method.POST
                    , fixedURL
                    , future
                    , future)
                    {
                        @Override
                        public byte[] getBody() throws AuthFailureError
                        {
                            return jsonObject.toString().getBytes();
                        }

                        @Override
                        public String getBodyContentType()
                        {
                            return "application/json";
                        }
                    };

            // Enviamos la peticion.
            VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);

            try
            {
                String response = future.get(20, TimeUnit.SECONDS);

                Log.d(Properties.TAG, "Respuesta del servidor: " + response);

                if (response.equals(Properties.ACCEPTED))
                {
                    Log.d(Properties.TAG, "Usuario modificado correctamente (ID: " + id + ")");

                    if (!name.isEmpty())
                    {
                        user.setName(name);
                    }

                    if (!email.isEmpty())
                    {
                        user.setEmail(email);
                    }

                    if (!password.isEmpty())
                    {
                        user.setPassword(password);
                    }

                    if (postalCode != -1)
                    {
                        user.setPostalCode(postalCode);
                    }

                    if (age != -1)
                    {
                        user.setAge(age);
                    }

                    mSharedPreferencesManager.insertUser(user);

                    return true;
                }

            } catch (ExecutionException | InterruptedException | TimeoutException e) {
                Log.d(Properties.TAG, "Error modificando usuario: " + e.getMessage());

                return false;
            }

        } catch (JSONException e) {
            Log.d(Properties.TAG, "Error creando JSON (" + e.getMessage() + ")");

            return false;
        }

        return false;
    }

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
