package com.wallakoala.wallakoala.Singletons;

import android.content.Context;
import android.os.AsyncTask;
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
import com.wallakoala.wallakoala.Utils.CustomRequest;
import com.wallakoala.wallakoala.Utils.ExceptionPrinter;
import com.wallakoala.wallakoala.Utils.JSONParser;
import com.wallakoala.wallakoala.Utils.SharedPreferencesManager;
import com.wallakoala.wallakoala.Utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
     * Metodo que marca una notificacion como leida.
     * @param context: contexto.
     * @param notifId: id de la notificacion.
     * @return true si se ha enviado correctamente.
     */
    public synchronized static boolean markNotificationAsRead(final Context context, long notifId)
    {
        String response;

        try
        {
            RequestFuture<String> future = RequestFuture.newFuture();

            SharedPreferencesManager mSharedPreferencesManager = new SharedPreferencesManager(context);

            User user = mSharedPreferencesManager.retrieveUser();

            final String fixedURL = Utils.fixUrl(Properties.SERVER_URL + ":" + Properties.SERVER_SPRING_PORT
                    + "/notification/" + user.getId() + "/" + notifId);

            Log.d(Properties.TAG, "[REST_CLIENT_SINGLETON] Conectando con: " + fixedURL
                    + " para marcar la notificacion " + notifId + " como leida");

            // Creamos una peticion
            final StringRequest jsonObjReq = new StringRequest(Request.Method.GET
                    , fixedURL
                    , future
                    , future);

            // La mandamos a la cola de peticiones
            VolleySingleton.getInstance(context).addToRequestQueue(jsonObjReq);
            Log.d(Properties.TAG, "[REST_CLIENT_SINGLETON] Petición creada y enviada");

            try
            {
                response = future.get(20, TimeUnit.SECONDS);
                Log.d(Properties.TAG, "[REST_CLIENT_SINGLETON] Respuesta recibida: " + response);

                // Si ha ido bien, guardamos la notificacion y actualizamos el usuario.
                if (response != null && response.equals(Properties.ACCEPTED))
                {
                    Log.d(Properties.TAG, "[REST_CLIENT_SINGLETON] Se añade la notificación como leída");

                    user = mSharedPreferencesManager.retrieveUser();

                    user.addNotificationAsRead(notifId);

                    mSharedPreferencesManager.insertUser(user);
                }

            } catch (InterruptedException e) {
                ExceptionPrinter.printException("REST_CLIENT_SINGLETON", e);

                return false;
            }

        } catch (Exception e) {
            ExceptionPrinter.printException("REST_CLIENT_SINGLETON", e);

            return false;
        }

        return true;
    }

    /**
     * Metodo que devuelve las notificaciones del usuario.
     * @param context: contexto.
     * @return Array de JSONs con las notificaciones del usuario.
     */
    @Nullable
    public static JSONArray retrieveNotifications(final Context context)
    {
        JSONArray content;

        try
        {
            final RequestFuture<JSONArray> future = RequestFuture.newFuture();

            final SharedPreferencesManager mSharedPreferencesManager = new SharedPreferencesManager(context);

            final User user = mSharedPreferencesManager.retrieveUser();

            final String fixedURL = Utils.fixUrl(
                    Properties.SERVER_URL + ":" + Properties.SERVER_SPRING_PORT + "/notification/" + user.getId());

            Log.d(Properties.TAG, "[REST_CLIENT_SINGLETON] Conectando con: " + fixedURL + " para traer las notificaciones del usuario");

            // Creamos una peticion
            final JsonArrayRequest jsonObjReq = new JsonArrayRequest(Request.Method.GET
                                                                , fixedURL
                                                                , null
                                                                , future
                                                                , future);

            // La mandamos a la cola de peticiones
            VolleySingleton.getInstance(context).addToRequestQueue(jsonObjReq);
            Log.d(Properties.TAG, "[REST_CLIENT_SINGLETON] Petición creada y enviada");

            try
            {
                content = future.get(20, TimeUnit.SECONDS);

            } catch (InterruptedException e) {
                ExceptionPrinter.printException("REST_CLIENT_SINGLETON", e);

                return null;
            }

            // Si content es vacio, es que han fallado todas las conexiones.
            if (content == null)
            {
                Log.e(Properties.TAG, "[REST_CLIENT_SINGLETON] No se ha recibido ningún dato");

                return null;
            }

        } catch (Exception e) {
            ExceptionPrinter.printException("REST_CLIENT_SINGLETON", e);

            return null;
        }

        return content;
    }

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

        final User user = mSharedPreferencesManager.retrieveUser();

        final String fixedURL = Utils.fixUrl(
                Properties.SERVER_URL + ":" + Properties.SERVER_SPRING_PORT + "/hasNotification/" + user.getId());

        Log.d(Properties.TAG, "[REST_CLIENT_SINGLETON] Conectando con: " + fixedURL + " para saber si tiene alguna notificacion por leer");

        StringRequest stringRequest = new StringRequest(Request.Method.GET
                , fixedURL
                , new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        if (response.equals(Properties.NEW_NOTIFICATIONS))
                        {
                            Log.d(Properties.TAG, "[REST_CLIENT_SINGLETON] Hay nuevas notificaciones");
                            Log.d(Properties.TAG, "[REST_CLIENT_SINGLETON] Se actualizan los iconos");

                            // Cambiamos el Hamburger Icon
                            toolbar.setNavigationIcon(R.drawable.ic_menu_notif);

                            // Cambiamos el icono de las notificaciones en el menu
                            MenuItem menuItem = navigationVew.getMenu().findItem(R.id.nav_notifications);
                            menuItem.setIcon(context.getResources().getDrawable(R.drawable.ic_notification_new));

                        } else {
                            Log.d(Properties.TAG, "[REST_CLIENT_SINGLETON] No hay notificaciones nuevas");
                        }
                    }
                }
                , new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        ExceptionPrinter.printException("REST_CLIENT_SINGLETON", error);
                    }
                });

        // Enviamos la peticion.
        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
        Log.d(Properties.TAG, "[REST_CLIENT_SINGLETON] Petición creada y enviada al servidor");
    }

    /**
     * Metodo que envia una sugerencia.
     * @param context: contexto.
     * @param feedback: sugerencia del usuario.
     * @return true si ha ido correctamente.
     */
    public static boolean sendFeedback(final Context context, final Feedback feedback)
    {
        try
        {
            final String fixedURL = Utils.fixUrl(Properties.SERVER_URL + ":" + Properties.SERVER_SPRING_PORT
                    + "/feedback");

            Log.d(Properties.TAG, "[REST_CLIENT_SINGLETON] Conectando con: " + fixedURL + " para enviar una valoracion");

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
            ExceptionPrinter.printException("REST_CLIENT_SINGLETON", e);
        }

        return true;
    }

    /**
     * Metodo que envia una tienda sugerida.
     * @param context: contexto.
     * @param shopSuggested: tienda sugerida.
     * @return true si ha ido correctamente.
     */
    public static boolean sendSuggestion(final Context context, final ShopSuggested shopSuggested)
    {
        try
        {
            final String fixedURL = Utils.fixUrl(
                    Properties.SERVER_URL + ":" + Properties.SERVER_SPRING_PORT + "/suggested");

            Log.d(Properties.TAG, "[REST_CLIENT_SINGLETON] Conectando con: " + fixedURL + " para enviar una tienda sugerida");

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
            ExceptionPrinter.printException("REST_CLIENT_SINGLETON", e);
        }

        return true;
    }

    /**
     * Metodo que devuelve la lista de tiendas del usuario.
     * @param context: contexto.
     * @return Array de JSONs con las tiendas.
     */
    @Nullable
    public static JSONArray retrieveShops(final Context context)
    {
        JSONArray content;

        try
        {
            RequestFuture<JSONArray> future = RequestFuture.newFuture();

            final SharedPreferencesManager mSharedPreferencesManager = new SharedPreferencesManager(context);

            final User user = mSharedPreferencesManager.retrieveUser();

            final String fixedURL = Utils.fixUrl(Properties.SERVER_URL + ":" + Properties.SERVER_SPRING_PORT
                    + "/shops/" + user.getMan());

            Log.d(Properties.TAG, "[REST_CLIENT_SINGLETON] Conectando con: " + fixedURL + " para traer las tiendas del usuario");

            // Creamos una peticion
            final JsonArrayRequest jsonObjReq = new JsonArrayRequest(Request.Method.GET
                    , fixedURL
                    , null
                    , future
                    , future);

            // La mandamos a la cola de peticiones
            VolleySingleton.getInstance(context).addToRequestQueue(jsonObjReq);

            Log.d(Properties.TAG, "[REST_CLIENT_SINGLENTON] Petición creada y enviada");

            try
            {
                content = future.get(20, TimeUnit.SECONDS);

            } catch (InterruptedException e) {
                ExceptionPrinter.printException("REST_CLIENT_SINGLETON", e);

                return null;
            }

            // Si content es vacio, es que han fallado todas las conexiones.
            if (content == null)
            {
                Log.e(Properties.TAG, "[REST_CLIENT_SINGLENTON] No se ha recibido nada");

                return null;
            }

        } catch (Exception e) {
            ExceptionPrinter.printException("REST_CLIENT_SINGLETON", e);

            return null;
        }

        Log.d(Properties.TAG, "[REST_CLIENT_SINGLENTON] Respuesta recibida");

        return content;
    }

    /**
     * Metodo que devuelve la lista de sugerencias del servidor.
     * @param context: contexto.
     * @param word: palabra para encontrar las sugerencias.
     * @return Array de JSONs con las sugerencias.
     */
    @Nullable
    public static JSONArray retrieveSuggestions(final Context context, String word)
    {
        JSONArray content;

        try
        {
            RequestFuture<JSONArray> future = RequestFuture.newFuture();

            SharedPreferencesManager mSharedPreferencesManager = new SharedPreferencesManager(context);

            User user = mSharedPreferencesManager.retrieveUser();

            final String fixedURL = Utils.fixUrl(
                    Properties.SERVER_URL + ":" + Properties.SERVER_SPRING_PORT + "/suggest/" + user.getId() + "/" + word);

            Log.d(Properties.TAG, "[REST_CLIENT_SINGLETON] Conectando con: " + fixedURL + " para traer las sugerencias");

            // Creamos una peticion
            final JsonArrayRequest jsonObjReq = new JsonArrayRequest(Request.Method.GET
                    , fixedURL
                    , null
                    , future
                    , future);

            // La mandamos a la cola de peticiones
            VolleySingleton.getInstance(context).addToRequestQueue(jsonObjReq);
            Log.d(Properties.TAG, "[REST_CLIENT_SINGLETON] Petición creada y recibida");

            try
            {
                content = future.get(20, TimeUnit.SECONDS);

            } catch (InterruptedException e) {
                ExceptionPrinter.printException("REST_CLIENT_SINGLETON", e);

                return null;
            }

            // Si content es vacio, es que han fallado todas las conexiones.
            if (content == null)
            {
                Log.e(Properties.TAG, "[REST_CLIENT_SINGLETON] No se ha recibido nada");

                return null;
            }

        } catch (Exception e) {
            ExceptionPrinter.printException("REST_CLIENT_SINGLETON", e);

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
    public static JSONArray retrieveRecommendedProducts(final Context context)
    {
        JSONArray content;

        try
        {
            SharedPreferencesManager mSharedPreferencesManager = new SharedPreferencesManager(context);

            long id = mSharedPreferencesManager.retrieveUser().getId();

            RequestFuture<JSONArray> future = RequestFuture.newFuture();

            final String fixedURL = Utils.fixUrl(Properties.SERVER_URL + ":" + Properties.SERVER_SPRING_PORT
                    + "/recommended/" + id);

            Log.d(Properties.TAG, "[REST_CLIENT_SINGLETON] Conectando con: " + fixedURL + " para traer los productos recomendados");

            // Creamos una peticion
            final JsonArrayRequest jsonObjReq = new JsonArrayRequest(Request.Method.GET
                    , fixedURL
                    , null
                    , future
                    , future);

            // La mandamos a la cola de peticiones
            VolleySingleton.getInstance(context).addToRequestQueue(jsonObjReq);
            Log.d(Properties.TAG, "[REST_CLIENT_SINGLETON] Petición creada y enviada");

            try
            {
                content = future.get(20, TimeUnit.SECONDS);
                Log.d(Properties.TAG, "[REST_CLIENT_SINGLETON] Respuesta recibida del servidor" +
                        "");

            } catch (InterruptedException e) {
                ExceptionPrinter.printException("REST_CLIENT_SINGLETON", e);

                return null;
            }

            // Si content es vacio, es que han fallado todas las conexiones.
            if (content == null)
            {
                Log.d(Properties.TAG, "[REST_CLIENT_SINGLETON] No se ha recibido nada");

                return null;
            }

        } catch (Exception e) {
            ExceptionPrinter.printException("REST_CLIENT_SINGLETON", e);

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
    public static List<JSONArray> retrieveProducts(final Context context, int offset, List<String> shopList)
    {
        List<JSONArray> content = new ArrayList<>();

        try
        {
            final SharedPreferencesManager mSharedPreferencesManager = new SharedPreferencesManager(context);

            final boolean man = mSharedPreferencesManager.retrieveUser().getMan();

            final List<RequestFuture<JSONArray>> futures = new ArrayList<>();

            // Metemos en content el resultado de cada uno
            for (int i = 0; i < shopList.size(); i++)
            {
                final String fixedURL = Utils.fixUrl(Properties.SERVER_URL + ":" + Properties.SERVER_SPRING_PORT
                        + "/products/" + shopList.get(i) + "/" + man + "/" + offset);

                Log.d(Properties.TAG, "[REST_CLIENT_SINGLETON] Conectando con: " + fixedURL
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
                Log.d(Properties.TAG, "[REST_CLIENT_SINGLETON] Petición (" + shopList.get(i) + ") creada y enviada");
            }

            for (int i = 0; i < shopList.size(); i++)
            {
                try
                {
                    JSONArray response = futures.get(i).get(20, TimeUnit.SECONDS);

                    Log.d(Properties.TAG, "[REST_CLIENT_SINGLETON] Respuesta (" + shopList.get(i) + ") recibida");

                    content.add(response);

                } catch (InterruptedException e) {
                    ExceptionPrinter.printException("REST_CLIENT_SINGLETON", e);

                    return null;
                }
            }

            // Si content es vacio, es que han fallado todas las conexiones.
            if (content.isEmpty())
            {
                Log.d(Properties.TAG, "[REST_CLIENT_SINGLETON] Todas las peticiones han fallado");

                return null;
            }

        } catch(Exception ex)  {
            ExceptionPrinter.printException("REST_CIENT_SINGLETON", ex);

            return null;
        }

        return content;
    }

    /**
     * Metodo que obtiene del servidor los datos del usuario.
     * @param context: contexto.
     * @return true si se ha obtenido correctamente.
     */
    public static boolean retrieveUser(final Context context)
    {
        final SharedPreferencesManager mSharedPreferencesManager = new SharedPreferencesManager(context);

        final long id = mSharedPreferencesManager.retrieveUser().getId();

        final String fixedURL = Utils.fixUrl(
                Properties.SERVER_URL + ":" + Properties.SERVER_SPRING_PORT + "/users/" + id);

        Log.d(Properties.TAG, "[REST_CLIENT_SINGLETON] Conectando con: " + fixedURL + " para obtener los datos del usuario");

        RequestFuture<JSONObject> future = RequestFuture.newFuture();

        // Creamos una peticion.
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET
                , fixedURL
                , null
                , future
                , future);

        // La mandamos a la cola de peticiones.
        Log.d(Properties.TAG, "[REST_CLIENT_SINGLETON] Petición creada y enviada al servidor");
        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjReq);

        try
        {
            Log.d(Properties.TAG, "[REST_CLIENT_SINGLETON] Esperando respuesta del servidor");
            JSONObject response = future.get(20, TimeUnit.SECONDS);

            Log.d(Properties.TAG, "[REST_CLIENT_SINGLETON] Respuesta recibida, se parsea el JSON");
            User user = JSONParser.convertJSONtoUser(response, id);

            Log.d(Properties.TAG, "[REST_CLIENT_SINGLETON] User creado, se inserta en las SharedPreferences");
            mSharedPreferencesManager.insertUser(user);

            return true;

        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            Log.e(Properties.TAG, "[REST_CLIENT_SINGLETON] Error conectando con el servidor: " + e.getMessage());
            ExceptionPrinter.printException("REST_CLIENT_SINGLETON", e);

            return false;

        } catch (JSONException e) {
            Log.e(Properties.TAG, "[REST_CLIENT_SINGLETON] Error parseando el usuario: " + e.getMessage());
            ExceptionPrinter.printException("REST_CLIENT_SINGLETON", e);

            return false;
        }
    }

    /**
     * Metodo que recibe la lista de productos favoritos del usuario.
     * @param context: contexto.
     * @return lista de productos favoritos.
     */
    @Nullable
    public static List<Product> getFavoriteProducts(final Context context)
    {
        final SharedPreferencesManager mSharedPreferencesManager = new SharedPreferencesManager(context);

        final User user = mSharedPreferencesManager.retrieveUser();

        final String fixedURL = Utils.fixUrl(
                Properties.SERVER_URL + ":" + Properties.SERVER_SPRING_PORT + "/favorites/" + user.getId());

        Log.d(Properties.TAG, "[REST_CLIENT_SINGLETON] Conectando con: " + fixedURL + " para otener los productos favoritos");

        RequestFuture<JSONArray> future = RequestFuture.newFuture();

        // Creamos una peticion.
        JsonArrayRequest jsonObjReq = new JsonArrayRequest(Request.Method.GET
                                                    , fixedURL
                                                    , null
                                                    , future
                                                    , future);

        // La mandamos a la cola de peticiones.
        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjReq);
        Log.d(Properties.TAG, "[REST_CLIENT_SINGLENTON] Petición creada y enviada");

        try
        {
            JSONArray response = future.get(20, TimeUnit.SECONDS);

            Log.d(Properties.TAG, "[REST_CLIENT_SINGLENTON] Se parsean los JSONs recibidos");
            List<Product> favorites = JSONParser.convertJSONsToProducts(response);
            Set<Long> userFavorites = new HashSet<>();
            for (Product favorite : favorites)
            {
                userFavorites.add(favorite.getId());
            }

            Log.d(Properties.TAG, "[REST_CLIENT_SINGLENTON] Se actualiza la lista de favoritos del usuario");
            user.setFavoriteProducts(userFavorites);
            mSharedPreferencesManager.insertUser(user);

            return favorites;

        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            Log.e(Properties.TAG, "[REST_CLIENT_SINGLETON] Error conectando con el servidor: " + e.getMessage());
            ExceptionPrinter.printException("REST_CLIENT_SINGLETON", e);

            return null;

        } catch (JSONException e) {
            Log.e(Properties.TAG, "[REST_CLIENT_SINGLETON] Error parseando los productos: " + e.getMessage());
            ExceptionPrinter.printException("REST_CLIENT_SINGLETON", e);

            return null;
        }
    }

    /**
     * Metodo que envia la lista de tiendas del usuario.
     * @param context: contexto.
     * @param listOfShops: lista de tiendas a enviar.
     * @return true si se han enviado correctamente.
     */
    public static boolean sendShops(final Context context, List<String> listOfShops)
    {
        final SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(context);

        final User user = sharedPreferencesManager.retrieveUser();
        final long id = user.getId();

        final String fixedURL = Utils.fixUrl(
                Properties.SERVER_URL + ":" + Properties.SERVER_SPRING_PORT + "/shops/" + id);

        Log.d(Properties.TAG, "[REST_CLIENT_SINGLETON] Conectando con: " + fixedURL + " para añadir las tiendas");

        // Creamos el JSON con la lista de las tiendas.
        final JSONArray jsonArray = new JSONArray();
        for (String shop : listOfShops)
        {
            jsonArray.put(shop);
        }

        Log.d(Properties.TAG, "[REST_CLIENT_SINGLETON] JSON con las tiendas:\n - " + jsonArray.toString());

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
        Log.d(Properties.TAG, "[REST_CLIENT_SINGLENTON] Petición creada y enviada");

        try
        {
            String response = future.get(20, TimeUnit.SECONDS);
            Log.d(Properties.TAG, "[REST_CLIENT_SINGLENTON] Respuesta recibida: " + response);

            if (response.equals(Properties.ACCEPTED))
            {
                Log.d(Properties.TAG, "[REST_CLIENT_SINGLENTON] Tiendas actualizadas correctamente");

                // Metemos las tiendas en un Set
                Set<String> shopSet = new HashSet<>();
                for (String shop : listOfShops)
                {
                    shopSet.add(shop);
                }

                // Guardamos el conjunto de tiendas en las preferencias.
                user.setShops(shopSet);
                sharedPreferencesManager.insertUser(user);

                return true;
            }

        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            Log.e(Properties.TAG, "[REST_CLIENT_SINGLETON] Error enviando tiendas: " + e.getMessage());
            ExceptionPrinter.printException("REST_CLIENT_SINGLETON", e);

            return false;
        }

        return false;
    }

    /**
     * Metodo que borra la cuenta del usuario.
     * @param context: contexto
     * @return true si se ha borrado correctamente.
     */
    public static boolean deleteUser(final Context context)
    {
        SharedPreferencesManager mSharedPreferencesManager = new SharedPreferencesManager(context);

        User user = mSharedPreferencesManager.retrieveUser();
        long id = user.getId();

        final String fixedURL = Utils.fixUrl(Properties.SERVER_URL + ":" + Properties.SERVER_SPRING_PORT
                + "/users/" + id);

        Log.d(Properties.TAG, "[REST_CLIENT_SINGLETON] Conectando con: " + fixedURL + " para borrar un usuario");

        RequestFuture<String> future = RequestFuture.newFuture();

        StringRequest stringRequest = new StringRequest(Request.Method.DELETE
                , fixedURL
                , future
                , future);

        // Enviamos la peticion.
        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
        Log.d(Properties.TAG, "[REST_CLIENT_SINGLETON] Petición creada y enviada");

        try
        {
            String response = future.get(20, TimeUnit.SECONDS);

            Log.d(Properties.TAG, "[REST_CLIENT_SINGLETON] Respuesta del servidor: " + response);

            if (response.equals(Properties.ACCEPTED))
            {
                Log.d(Properties.TAG, "[REST_CLIENT_SINGLETON] Usuario borrado correctamente (ID: " + id + ")");

                mSharedPreferencesManager.clear();

                return true;
            }

        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            ExceptionPrinter.printException("REST_CLIENT_SINGLETON", e);

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
    public static boolean sendUserModification(final Context context
                                    , String name
                                    , String email
                                    , String password
                                    , short age
                                    , int postalCode)
    {
        SharedPreferencesManager mSharedPreferencesManager = new SharedPreferencesManager(context);

        User user = mSharedPreferencesManager.retrieveUser();
        long id = user.getId();

        final String fixedURL = Utils.fixUrl(Properties.SERVER_URL + ":" + Properties.SERVER_SPRING_PORT
                + "/users/" + id);

        Log.d(Properties.TAG, "[REST_CLIENT_SINGLETON] Conectando con: " + fixedURL + " para modificar el usuario");

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

            Log.d(Properties.TAG, "[REST_CLIENT_SINGLETON] JSON con las modificaciones:\n  - " + jsonObject.toString());

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
            Log.d(Properties.TAG, "[REST_CLIENT_SINGLETON] Petición creada y enviada");

            try
            {
                String response = future.get(20, TimeUnit.SECONDS);

                Log.d(Properties.TAG, "[REST_CLIENT_SINGLETON] Respuesta del servidor: " + response);

                if (response.equals(Properties.ACCEPTED))
                {
                    Log.d(Properties.TAG, "[REST_CLIENT_SINGLETON] Usuario modificado correctamente (ID: " + id + ")");

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
                ExceptionPrinter.printException("REST_CLIENT_SINGLETON", e);

                return false;
            }

        } catch (JSONException e) {
            ExceptionPrinter.printException("REST_CLIENT_SINGLETON", e);

            return false;
        }

        return false;
    }

    /**
     * Metodo que envia al servidor el producto favorito.
     * @param context: contexto
     * @param product: producto favorito
     */
    public static void sendFavoriteProduct(final Context context, final Product product)
    {
        final SharedPreferencesManager mSharedPreferencesManager = new SharedPreferencesManager(context);

        final long id = mSharedPreferencesManager.retrieveUser().getId();

        final String fixedURL = Utils.fixUrl(Properties.SERVER_URL + ":" + Properties.SERVER_SPRING_PORT
                + "/users/" + id + "/" + product.getId() + "/" + Properties.ACTION_FAVORITE);

        Log.d(Properties.TAG, "[REST_CLIENT_SINGLETON] Conectando con: " + fixedURL + " para anadir/quitar un producto de favoritos");

        final StringRequest stringRequest = new StringRequest(Request.Method.GET
                , fixedURL
                , new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(final String response)
                    {
                        Log.d(Properties.TAG, "[REST_CLIENT_SINGLETON] Se recibe respuesta del servidor: " + response);

                        AsyncTask.execute(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                synchronized (RestClientSingleton.class)
                                {
                                    if (!response.equals(Properties.PRODUCT_NOT_FOUND) || !response.equals(Properties.USER_NOT_FOUND))
                                    {
                                        User user = mSharedPreferencesManager.retrieveUser();

                                        // Si contiene el producto, es que se quiere quitar de favoritos.
                                        if (user.getFavoriteProducts().contains(product.getId()))
                                        {
                                            Log.d(Properties.TAG, "[REST_CLIENT_SINGLETON] Se elimina el producto de favoritos");
                                            user.getFavoriteProducts().remove(product.getId());

                                        } else {
                                            Log.d(Properties.TAG, "[REST_CLIENT_SINGLETON] Se añade el producto de favoritos: " + product.getId());
                                            user.getFavoriteProducts().add(product.getId());
                                        }

                                        Log.d(Properties.TAG, "[REST_CLIENT_SINGLETON] Se actualiza el usuario en las SharedPreferences");
                                        mSharedPreferencesManager.insertUser(user);
                                    }
                                }
                            }
                        });
                    }
                }
                , new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        ExceptionPrinter.printException("REST_CLIENT_SINGLETON", error);
                    }
                });

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    /**
     * Metodo que envia al servidor el producto visto.
     * @param context: contexto
     * @param product: producto favorito
     */
    public static void sendViewedProduct(final Context context, final Product product)
    {
        SharedPreferencesManager mSharedPreferencesManager = new SharedPreferencesManager(context);

        User user = mSharedPreferencesManager.retrieveUser();
        long id = user.getId();

        String fixedURL = Utils.fixUrl(Properties.SERVER_URL + ":" + Properties.SERVER_SPRING_PORT
                + "/users/" + id + "/" + product.getId() + "/" + Properties.ACTION_VIEWED);

        Log.d(Properties.TAG, "[REST_CLIENT_SINGLETON] Conectando con: " + fixedURL + " para marcar el producto como visto");

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

    /**
     * Metodo que envia una peticion para filtrar productos.
     * @param context: contexto.
     * @param shopsList: lista de tiendas.
     * @param filterMap: mapa de filtros.
     * @return lista de JSONs con los productos devueltos.
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public static List<JSONArray> sendFilterRequest(final Context context, List<String> shopsList, Map<String, Object> filterMap)
    {
        List<JSONArray> content = new ArrayList<>();

        SharedPreferencesManager mSharedPreferencesManager = new SharedPreferencesManager(context);

        User user = mSharedPreferencesManager.retrieveUser();

        List<RequestFuture<JSONArray>> futures = new ArrayList<>();

        try
        {
            for (int i = 0; i < shopsList.size(); i++)
            {
                String fixedURL = Utils.fixUrl(Properties.SERVER_URL + ":" + Properties.SERVER_SPRING_PORT
                        + "/filter/" + user.getId() + "/" + shopsList.get(i));

                Log.d(Properties.TAG, "[REST_CLIENT_SINGLETON] Conectando con: " + fixedURL + " + para realizar un filtro");

                // Creamos el JSON con los filtros
                JSONObject jsonObject = new JSONObject();

                List<String> sectionsList = (ArrayList<String>)filterMap.get("sections");
                List<String> colorsList   = (ArrayList<String>)filterMap.get("colors");

                jsonObject.put("newness", filterMap.get("newness"));
                jsonObject.put("man", user.getMan());
                jsonObject.put("priceFrom", filterMap.get("minPrice"));
                jsonObject.put("priceTo", filterMap.get("maxPrice"));
                jsonObject.put("colors", new JSONArray(colorsList));
                jsonObject.put("sections", new JSONArray(sectionsList));

                Log.d(Properties.TAG, "[REST_CLIENT_SINGLETON] JSON con los filtros:\n " + jsonObject.toString());

                futures.add(RequestFuture.<JSONArray>newFuture());

                // Creamos una peticion
                CustomRequest jsonObjReq = new CustomRequest(Request.Method.POST
                        , fixedURL
                        , jsonObject
                        , futures.get(i)
                        , futures.get(i));

                // La mandamos a la cola de peticiones
                VolleySingleton.getInstance(context).addToRequestQueue(jsonObjReq);
            }

        } catch (JSONException e) {
            ExceptionPrinter.printException("REST_CLIENT_SINGLETON", e);

            return null;
        }

        // Metemos en content el resultado de cada uno
        for (int i = 0; i < shopsList.size(); i++)
        {
            try
            {
                JSONArray response = futures.get(i).get(20, TimeUnit.SECONDS);

                content.add(response);

            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                ExceptionPrinter.printException("REST_CLIENT_SINGLETON", e);

                return null;
            }
        }

        return content;
    }

    /**
     * Metodo que envia una peticion para realizar una busqueda.
     * @param context: contexto.
     * @param query: texto a buscar.
     * @return lista de JSONs con los productos devueltos.
     */
    @Nullable
    public static List<JSONArray> sendSearchRequest(final Context context, String query)
    {
        List<JSONArray> content = new ArrayList<>();

        SharedPreferencesManager mSharedPreferencesManager = new SharedPreferencesManager(context);

        User user = mSharedPreferencesManager.retrieveUser();

        String fixedURL = Utils.fixUrl(Properties.SERVER_URL + ":" + Properties.SERVER_SPRING_PORT
                + "/search/" + user.getId() + "/" + query);

        RequestFuture<JSONArray> future = RequestFuture.newFuture();

        Log.d(Properties.TAG, "[REST_CLIENT_SINGLETON] Conectando con: " + fixedURL + " + para realizar una búsqueda");

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
            JSONArray response = future.get(20, TimeUnit.SECONDS);

            content.add(response);

        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            ExceptionPrinter.printException("REST_CLIENT_SINGLETON", e);

            return null;
        }

        return content;
    }
}
