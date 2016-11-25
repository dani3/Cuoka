package com.wallakoala.wallakoala.Singletons;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.wallakoala.wallakoala.Beans.Feedback;
import com.wallakoala.wallakoala.Beans.Product;
import com.wallakoala.wallakoala.Beans.ShopSuggested;
import com.wallakoala.wallakoala.Beans.User;
import com.wallakoala.wallakoala.Properties.Properties;
import com.wallakoala.wallakoala.R;
import com.wallakoala.wallakoala.Utils.JSONParser;
import com.wallakoala.wallakoala.Utils.SharedPreferencesManager;
import com.wallakoala.wallakoala.Utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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
     * Metodo que pregunta al servidor si tiene alguna notificacion por leer.
     * @param context: contexto.
     * @param toolbar: toolbar a la que actualizar el Hamburger Icon.
     * @param navigationVew: navigationView necesaria para actualizar el icono si es necesario.
     */
    @SuppressWarnings("deprecation")
    public static void hasNotification(final Context context, final Toolbar toolbar, final NavigationView navigationVew)
    {
        final SharedPreferencesManager mSharedPreferencesManager = new SharedPreferencesManager(context);

        final User user = mSharedPreferencesManager.retreiveUser();

        final String fixedURL = Utils.fixUrl(
                Properties.SERVER_URL + ":" + Properties.SERVER_SPRING_PORT + "/hasNotification/" + user.getId());

        Log.d(Properties.TAG, "Conectando con: " + fixedURL + " para saber si tiene alguna notificacion por leer");

        StringRequest stringRequest = new StringRequest(Request.Method.GET
                , fixedURL
                , new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        if (response.equals(Properties.NEW_NOTIFICATIONS))
                        {
                            Log.d(Properties.TAG, "Hay nuevas notificaciones");

                            // Cambiamos el Hamburger Icon
                            toolbar.setNavigationIcon(R.drawable.ic_menu_notif);

                            // Cambiamos el icono de las notificaciones en el menu
                            MenuItem menuItem = navigationVew.getMenu().findItem(R.id.nav_notifications);
                            menuItem.setIcon(context.getResources().getDrawable(R.drawable.ic_notification_new));
                            
                        } else {
                            Log.d(Properties.TAG, "No hay nuevas notificaciones");
                        }
                    }
                }
                , new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {}
                });

        // Enviamos la peticion.
        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    /**
     * Metodo que envia una sugerencia.
     * @param context: contexto.
     * @param feedback: sugerencia del usuario.
     * @return true si ha ido correctamente.
     */
    public static boolean sendFeedback(Context context, Feedback feedback)
    {
        try
        {
            final String fixedURL = Utils.fixUrl(Properties.SERVER_URL + ":" + Properties.SERVER_SPRING_PORT
                    + "/feedback");

            Log.d(Properties.TAG, "Conectando con: " + fixedURL + " para enviar una valoracion");

            final JSONObject jsonObject = new JSONObject();

            jsonObject.put("stars", feedback.getStars());
            jsonObject.put("opinion", feedback.getOpinion());

            // Creamos una peticion
            final StringRequest jsonObjReq = new StringRequest(Request.Method.POST
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
                    })
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

            // La mandamos a la cola de peticiones
            VolleySingleton.getInstance(context).addToRequestQueue(jsonObjReq);

        } catch (Exception e) {
            return false;
        }

        return true;
    }

    /**
     * Metodo que envia una tienda sugerida.
     * @param context: contexto.
     * @param shopSuggested: tienda sugerida.
     * @return true si ha ido correctamente.
     */
    public static boolean sendSuggestion(Context context, ShopSuggested shopSuggested)
    {
        try
        {
            final String fixedURL = Utils.fixUrl(Properties.SERVER_URL + ":" + Properties.SERVER_SPRING_PORT
                    + "/suggested");

            Log.d(Properties.TAG, "Conectando con: " + fixedURL + " para enviar una tienda sugerida");

            final JSONObject jsonObject = new JSONObject();

            jsonObject.put("name", shopSuggested.getShop());
            jsonObject.put("link", shopSuggested.getLink());

            // Creamos una peticion
            final StringRequest jsonObjReq = new StringRequest(Request.Method.POST
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
            })
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

            // La mandamos a la cola de peticiones
            VolleySingleton.getInstance(context).addToRequestQueue(jsonObjReq);

        } catch (Exception e) {
            return false;
        }

        return true;
    }

    /**
     * Metodo que devuelve la lista de tiendas del usuario.
     * @param context: contexto.
     * @return Array de JSONs con las tiendas.
     */
    @Nullable
    public static JSONArray retrieveShops(Context context)
    {
        JSONArray content;

        try
        {
            RequestFuture<JSONArray> future = RequestFuture.newFuture();

            final SharedPreferencesManager mSharedPreferencesManager = new SharedPreferencesManager(context);

            final User user = mSharedPreferencesManager.retreiveUser();

            final String fixedURL = Utils.fixUrl(Properties.SERVER_URL + ":" + Properties.SERVER_SPRING_PORT
                    + "/shops/" + user.getMan());

            Log.d(Properties.TAG, "Conectando con: " + fixedURL + " para traer las tiendas del usuario");

            // Creamos una peticion
            final JsonArrayRequest jsonObjReq = new JsonArrayRequest(Request.Method.GET
                    , fixedURL
                    , null
                    , future
                    , future);

            // La mandamos a la cola de peticiones
            VolleySingleton.getInstance(context).addToRequestQueue(jsonObjReq);

            try
            {
                content = future.get(20, TimeUnit.SECONDS);

            } catch (InterruptedException e) {
                Log.d(Properties.TAG, e.getMessage());

                return null;
            }

            // Si content es vacio, es que han fallado todas las conexiones.
            if (content == null)
            {
                return null;
            }

        } catch (Exception e) {
            return null;
        }

        return content;
    }

    /**
     * Metodo que devuelve la lista de sugerencias del servidor.
     * @param context: contexto.
     * @param word: palabra para encontrar las sugerencias.
     * @return Array de JSONs con las sugerencias.
     */
    @Nullable
    public static JSONArray retrieveSuggestions(Context context, String word)
    {
        JSONArray content;

        try
        {
            RequestFuture<JSONArray> future = RequestFuture.newFuture();

            final SharedPreferencesManager mSharedPreferencesManager = new SharedPreferencesManager(context);

            final User user = mSharedPreferencesManager.retreiveUser();

            final String fixedURL = Utils.fixUrl(
                    Properties.SERVER_URL + ":" + Properties.SERVER_SPRING_PORT + "/suggest/" + user.getId() + "/" + word);

            Log.d(Properties.TAG, "Conectando con: " + fixedURL + " para traer las sugerencias");

            // Creamos una peticion
            final JsonArrayRequest jsonObjReq = new JsonArrayRequest(Request.Method.GET
                    , fixedURL
                    , null
                    , future
                    , future);

            // La mandamos a la cola de peticiones
            VolleySingleton.getInstance(context).addToRequestQueue(jsonObjReq);

            try
            {
                content = future.get(20, TimeUnit.SECONDS);

            } catch (InterruptedException e) {
                Log.d(Properties.TAG, e.getMessage());

                return null;
            }

            // Si content es vacio, es que han fallado todas las conexiones.
            if (content == null)
            {
                return null;
            }

        } catch (Exception e) {
            return null;
        }

        return content;
    }

    /**
     * Metodo que obtiene los productos recomendados de un usuario.
     * @param context: contexto.
     * @return Array de JSONs con las recomendaciones del usuario.
     */
    @Nullable
    public static JSONArray retrieveRecommendedProducts(Context context)
    {
        JSONArray content;

        try
        {
            final SharedPreferencesManager mSharedPreferencesManager = new SharedPreferencesManager(context);

            final long id = mSharedPreferencesManager.retreiveUser().getId();

            RequestFuture<JSONArray> future = RequestFuture.newFuture();

            final String fixedURL = Utils.fixUrl(Properties.SERVER_URL + ":" + Properties.SERVER_SPRING_PORT
                    + "/recommended/" + id);

            Log.d(Properties.TAG, "Conectando con: " + fixedURL + " para traer los productos recomendados");

            // Creamos una peticion
            final JsonArrayRequest jsonObjReq = new JsonArrayRequest(Request.Method.GET
                    , fixedURL
                    , null
                    , future
                    , future);

            // La mandamos a la cola de peticiones
            VolleySingleton.getInstance(context).addToRequestQueue(jsonObjReq);

            try
            {
                content = future.get(20, TimeUnit.SECONDS);

            } catch (InterruptedException e) {
                Log.d(Properties.TAG, e.getMessage());

                return null;
            }

            // Si content es vacio, es que han fallado todas las conexiones.
            if (content == null)
            {
                return null;
            }

        } catch (Exception e) {
            return null;
        }

        return content;
    }

    /**
     * Metodo que devuelve una lista de JSONs con los productos de cada tienda.
     * @param context: contexto.
     * @param offset: dia del que hay que traer productos.
     * @param shopList: lista de tiendas.
     * @return Lista de arrays de JSONs con los productos del dia.
     */
    @Nullable
    public static List<JSONArray> retrieveProducts(Context context, int offset, List<String> shopList)
    {
        List<JSONArray> content = new ArrayList<>();

        try
        {
            final SharedPreferencesManager mSharedPreferencesManager = new SharedPreferencesManager(context);

            final boolean man = mSharedPreferencesManager.retreiveUser().getMan();

            final List<RequestFuture<JSONArray>> futures = new ArrayList<>();

            // Metemos en content el resultado de cada uno
            for (int i = 0; i < shopList.size(); i++)
            {
                final String fixedURL = Utils.fixUrl(Properties.SERVER_URL + ":" + Properties.SERVER_SPRING_PORT
                        + "/products/" + shopList.get(i) + "/" + man + "/" + offset);

                Log.d(Properties.TAG, "Conectando con: " + fixedURL
                        + " para traer los productos de hace " + Integer.toString(offset) + " dias");

                futures.add(RequestFuture.<JSONArray>newFuture());

                // Creamos una peticion
                final JsonArrayRequest jsonObjReq = new JsonArrayRequest(Request.Method.GET
                        , fixedURL
                        , null
                        , futures.get(i)
                        , futures.get(i));

                // La mandamos a la cola de peticiones
                VolleySingleton.getInstance(context).addToRequestQueue(jsonObjReq);
            }

            for (int i = 0; i < shopList.size(); i++)
            {
                try
                {
                    final JSONArray response = futures.get(i).get(20, TimeUnit.SECONDS);

                    content.add(response);

                } catch (InterruptedException e) {
                    Log.d(Properties.TAG, e.getMessage());

                    return null;
                }
            }

            // Si content es vacio, es que han fallado todas las conexiones.
            if (content.isEmpty())
            {
                return null;
            }

        } catch(Exception ex)  {
            return null;
        }

        return content;
    }

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
