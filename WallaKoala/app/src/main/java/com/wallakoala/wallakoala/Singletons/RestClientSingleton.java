package com.wallakoala.wallakoala.Singletons;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.wallakoala.wallakoala.Beans.Product;
import com.wallakoala.wallakoala.Beans.User;
import com.wallakoala.wallakoala.Properties.Properties;
import com.wallakoala.wallakoala.Utils.JSONParser;
import com.wallakoala.wallakoala.Utils.SharedPreferencesManager;
import com.wallakoala.wallakoala.Utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
     * Metodo que obtiene del servidor los datos del usuario.
     * @param context: contexto.
     * @return true si se ha obtenido correctamente.
     */
    public static boolean retrieveUser(Context context)
    {
        final SharedPreferencesManager mSharedPreferencesManager = new SharedPreferencesManager(context);

        final long id = mSharedPreferencesManager.retreiveUser().getId();

        final String fixedURL = Utils.fixUrl(
                Properties.SERVER_URL + ":" + Properties.SERVER_SPRING_PORT + "/users/" + id);

        Log.d(Properties.TAG, "Conectando con: " + fixedURL + " para obtener los datos del usuario");

        RequestFuture<JSONObject> future = RequestFuture.newFuture();

        // Creamos una peticion
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET
                                                    , fixedURL
                                                    , null
                                                    , future
                                                    , future);

        // La mandamos a la cola de peticiones
        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjReq);

        try
        {
            JSONObject response = future.get(20, TimeUnit.SECONDS);

            User user = JSONParser.convertJSONtoUser(response, id);

            mSharedPreferencesManager.insertUser(user);

            return true;

        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            Log.d(Properties.TAG, "Error conectando con el servidor: " + e.getMessage());

            return false;

        } catch (JSONException e) {
            Log.d(Properties.TAG, "Error parseando el usuario: " + e.getMessage());

            return false;
        }
    }

    /**
     * Metodo que recibe la lista de productos favoritos del usuario.
     * @param context: contexto.
     * @return lista de productos favoritos.
     */
    @Nullable
    public static List<Product> getFavoriteProducts(Context context)
    {
        final SharedPreferencesManager mSharedPreferencesManager = new SharedPreferencesManager(context);

        final User user = mSharedPreferencesManager.retreiveUser();
        final long id = user.getId();

        final String fixedURL = Utils.fixUrl(
                Properties.SERVER_URL + ":" + Properties.SERVER_SPRING_PORT + "/favorites/" + id);

        Log.d(Properties.TAG, "Conectando con: " + fixedURL + " para otener los productos favoritos");

        RequestFuture<JSONArray> future = RequestFuture.newFuture();

        // Creamos una peticion
        JsonArrayRequest jsonObjReq = new JsonArrayRequest(Request.Method.GET
                                                    , fixedURL
                                                    , null
                                                    , future
                                                    , future);

        // La mandamos a la cola de peticiones
        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjReq);

        try
        {
            JSONArray response = future.get(20, TimeUnit.SECONDS);

            return JSONParser.convertJSONtoProduct(response);

        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            Log.d(Properties.TAG, "Error conectando con el servidor: " + e.getMessage());

            return null;

        } catch (JSONException e) {
            Log.d(Properties.TAG, "Error parseando los productos: " + e.getMessage());

            return null;
        }
    }

    /**
     * Metodo que envia la lista de tiendas del usuario.
     * @param context: contexto.
     * @param listOfShops: lista de tiendas a enviar.
     * @return true si se han enviado correctamente.
     */
    public static boolean sendShops(Context context, List<String> listOfShops)
    {
        final SharedPreferencesManager mSharedPreferencesManager = new SharedPreferencesManager(context);

        final User user = mSharedPreferencesManager.retreiveUser();
        final long id = user.getId();

        final String fixedURL = Utils.fixUrl(
                Properties.SERVER_URL + ":" + Properties.SERVER_SPRING_PORT + "/shops/" + id);

        Log.d(Properties.TAG, "Conectando con: " + fixedURL + " para añadir las tiendas");

        // Creamos el JSON con la lista de las tiendas.
        final JSONArray jsonArray = new JSONArray();
        for (String shop : listOfShops)
        {
            jsonArray.put(shop);
        }

        Log.d(Properties.TAG, "JSON con las tiendas:\n    " + jsonArray.toString());

        RequestFuture<String> future = RequestFuture.newFuture();

        StringRequest stringRequest = new StringRequest(Request.Method.POST
                , fixedURL
                , future
                , future)
                {
                    @Override
                    public byte[] getBody() throws AuthFailureError
                    {
                        return jsonArray.toString().getBytes();
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
                Log.d(Properties.TAG, "Tiendas actualizadas correctamente");

                // Metemos las tiendas en un Set
                Set<String> shopSet = new HashSet<>();
                for (String shop : listOfShops)
                {
                    shopSet.add(shop);
                }

                // Guardamos el conjunto de tiendas en las preferencias.
                user.setShops(shopSet);
                mSharedPreferencesManager.insertUser(user);

                return true;
            }

        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            Log.d(Properties.TAG, "Error borrando usuario: " + e.getMessage());

            return false;
        }

        return false;
    }

    /**
     * Metodo que borra la cuenta del usuario.
     * @param context: contexto
     * @return true si se ha borrado correctamente.
     */
    public static boolean deleteUser(Context context)
    {
        final SharedPreferencesManager mSharedPreferencesManager = new SharedPreferencesManager(context);

        final User user = mSharedPreferencesManager.retreiveUser();
        final long id = user.getId();

        final String fixedURL = Utils.fixUrl(Properties.SERVER_URL + ":" + Properties.SERVER_SPRING_PORT
                + "/users/" + id);

        Log.d(Properties.TAG, "Conectando con: " + fixedURL + " para borrar un usuario");

        RequestFuture<String> future = RequestFuture.newFuture();

        StringRequest stringRequest = new StringRequest(Request.Method.DELETE
                , fixedURL
                , future
                , future);

        // Enviamos la peticion.
        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);

        try
        {
            String response = future.get(20, TimeUnit.SECONDS);

            Log.d(Properties.TAG, "Respuesta del servidor: " + response);

            if (response.equals(Properties.ACCEPTED))
            {
                Log.d(Properties.TAG, "Usuario borrado correctamente (ID: " + id + ")");

                mSharedPreferencesManager.clear();

                return true;
            }

        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            Log.d(Properties.TAG, "Error borrando usuario: " + e.getMessage());

            return false;
        }

        return false;
    }

    /**
     * Metodo que envia las modificaciones del usuario.
     * @param context: contexto.
     * @param name: nombre del usuario.
     * @param email: email del usuario.
     * @param password: contraseña del usuario.
     * @param age: edad del usuario.
     * @param postalCode: codigo postal del usuario.
     */
    public static boolean sendUserModification(Context context
                                , String name
                                , String email
                                , String password
                                , short age
                                , int postalCode)
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
