package com.wallakoala.wallakoala.Utils;

import android.util.Log;

import com.wallakoala.wallakoala.Beans.ColorVariant;
import com.wallakoala.wallakoala.Beans.Notification;
import com.wallakoala.wallakoala.Beans.Product;
import com.wallakoala.wallakoala.Beans.Shop;
import com.wallakoala.wallakoala.Beans.User;
import com.wallakoala.wallakoala.Properties.Properties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Clase que se encarga de parsear los JSON.
 * Created by Daniel Mancebo Aldea on 28/10/2016.
 */

public class JSONParser
{
    /**
     * Metodo que parsea un array de JSONs en una lista de Shops.
     * @param jsonArray: array de JSONs.
     * @return lista de tiendas.
     * @throws JSONException
     */
    public static List<Shop> convertJSONsToShops(final JSONArray jsonArray) throws JSONException
    {
        Log.d(Properties.TAG, "[JSON_PARSER] Array de JSONs recibido para parsearlos a una lista de Shops");

        List<JSONObject> jsonList = new ArrayList<>();
        List<Shop> shopList = new ArrayList<>();

        // Se saca cada JSON (tienda).
        for (int j = 0; j < jsonArray.length(); j++)
        {
            JSONObject js = jsonArray.getJSONObject(j);

            jsonList.add(js);
        }

        // Se parsea cada JSON.
        for (JSONObject jsonObject : jsonList)
        {
            String name = jsonObject.getString("name");
            int numberOfProducts = jsonObject.getInt("products");

            Shop shop = new Shop(name, false, false, numberOfProducts);

            shopList.add(shop);

            Log.d(Properties.TAG, "[JSON_PARSER] Tienda (" + name + ") parseada y añadida");
        }

        Log.d(Properties.TAG, "[JSON_PARSER] Tiendas parseadas correctamente, se devuelven " + shopList.size() + " tiendas");
        return shopList;
    }

    /**
     * Metodo que convierte un JSON en un objeto Notification.
     * @param jsonObject: objeto JSON.
     * @return objeto Notification.
     * @throws JSONException
     */
    public static Notification convertJSONtoNotification(JSONObject jsonObject) throws JSONException
    {
        Log.d(Properties.TAG, "[JSON_PARSER] JSON recibido para parsearlo a un objeto Notification");
        Log.d(Properties.TAG, "[JSON_PARSER] JSON -> " + jsonObject.toString());

        long id = jsonObject.getLong("id");
        String title = jsonObject.getString("title");
        String text = jsonObject.getString("text");
        String extraInfo = jsonObject.getString("extraInfo");
        String image = jsonObject.getString("image");
        short offset = (short)jsonObject.getInt("offset");
        short action = (short)jsonObject.getInt("action");

        Log.d(Properties.TAG, "[JSON_PARSER] Datos de la notificacion: ");
        Log.d(Properties.TAG, "[JSON_PARSER] - ID: " + id);
        Log.d(Properties.TAG, "[JSON_PARSER] - Titulo: " + title);
        Log.d(Properties.TAG, "[JSON_PARSER] - Texto: " + text);
        Log.d(Properties.TAG, "[JSON_PARSER] - Extra Info: " + extraInfo);
        Log.d(Properties.TAG, "[JSON_PARSER] - Imagen: " + image);
        Log.d(Properties.TAG, "[JSON_PARSER] - Dias: " + offset);
        Log.d(Properties.TAG, "[JSON_PARSER] - Tipo: " + action);

        return new Notification(id, text, title, extraInfo, image, offset, action);
    }

    /**
     * Metodo que convierte un JSON en un objeto User.
     * @param jsonObject: objeto JSON
     * @param id: id del usuario.
     * @return objeto User.
     * @throws JSONException
     */
    public static User convertJSONtoUser(JSONObject jsonObject, long id) throws JSONException
    {
        Log.d(Properties.TAG, "[JSON_PARSER] JSON recibido para parsearlo a un objeto User");
        Log.d(Properties.TAG, "[JSON_PARSER] JSON -> " + jsonObject.toString());

        final User user = new User();

        user.setId(id);
        user.setName(jsonObject.getString("name"));
        user.setAge(jsonObject.getInt("age"));
        user.setEmail(jsonObject.getString("email"));
        user.setPassword(jsonObject.getString("password"));
        user.setMan(jsonObject.getBoolean("man"));
        user.setPostalCode(jsonObject.getInt("postalCode"));

        // Sacamos los productos favoritos.
        JSONArray jsonArray = jsonObject.getJSONArray("favoriteProducts");
        Set<Long> favorites = new ConcurrentSet<>();
        for (int i = 0; i < jsonArray.length(); i++)
        {
            favorites.add(Long.valueOf((String.valueOf(jsonArray.get(i)))));
        }

        user.setFavoriteProducts(favorites);

        // Sacamos la lista de notificaciones leidas.
        jsonArray = jsonObject.getJSONArray("notificationsRead");
        Set<Long> notificationsRead = new HashSet<>();
        for (int i = 0; i < jsonArray.length(); i++)
        {
            notificationsRead.add(Long.valueOf((String.valueOf(jsonArray.get(i)))));
        }

        user.setNotificationsRead(notificationsRead);

        // Sacamos la lista de tiendas
        jsonArray = jsonObject.getJSONArray("shops");
        Set<String> shops = new HashSet<>();
        for (int i = 0; i < jsonArray.length(); i++)
        {
            shops.add((String.valueOf(jsonArray.get(i))));
        }

        if (!shops.isEmpty()) {
            user.setShops(shops);
        } else {
            user.setShops(new HashSet<String>());
        }

        Log.d(Properties.TAG, "[JSON_PARSER] Datos del usuario: ");
        Log.d(Properties.TAG, "[JSON_PARSER] - ID: " + id);
        Log.d(Properties.TAG, "[JSON_PARSER] - Nombre: " + user.getName());
        Log.d(Properties.TAG, "[JSON_PARSER] - Email: " + user.getEmail());
        Log.d(Properties.TAG, "[JSON_PARSER] - Contraseña: " + user.getPassword());
        Log.d(Properties.TAG, "[JSON_PARSER] - Hombre: " + user.getMan());
        Log.d(Properties.TAG, "[JSON_PARSER] - Edad: " + user.getAge());
        Log.d(Properties.TAG, "[JSON_PARSER] - Código Postal: " + user.getPostalCode());
        Log.d(Properties.TAG, "[JSON_PARSER] - Numero de favoritos: " + user.getFavoriteProducts().size());
        Log.d(Properties.TAG, "[JSON_PARSER] - Numero de notificaciones leídas: " + user.getNotificationsRead().size());
        Log.d(Properties.TAG, "[JSON_PARSER] - Tiendas: " + jsonArray);

        return user;
    }

    /**
     * Metodo que convierte la lista de JSON en productos y los inserta en las distintas ED's.
     * @param jsonArray: lista de JSON a convertir.
     * @throws JSONException
     */
    public static List<Product> convertJSONsToProducts(final JSONArray jsonArray) throws JSONException
    {
        Log.d(Properties.TAG, "[JSON_PARSER] Array de JSONs recibido para parsearlos a una lista de Products");

        List<Product> productList = new ArrayList<>();
        List<JSONObject> jsonList = new ArrayList<>();

        for (int j = 0; j < jsonArray.length(); j++)
        {
            JSONObject js = jsonArray.getJSONObject(j);

            jsonList.add(js);
        }

        for (JSONObject jsonObject : jsonList)
        {
            double price = jsonObject.getDouble("1");
            String name = jsonObject.getString("2");
            String shop = jsonObject.getString("3");
            String section = jsonObject.getString("4");
            String link = jsonObject.getString("5");
            String description = jsonObject.getString("7");
            long id = jsonObject.getLong("8");
            float aspectRation = (float) jsonObject.getDouble("9");
            double discount = jsonObject.getDouble("11");

            JSONArray jsColors = jsonObject.getJSONArray("6");
            List<ColorVariant> colors = new ArrayList<>();
            for (int i = 0; i < jsColors.length(); i++)
            {
                JSONObject jsColor = jsColors.getJSONObject(i);

                String reference = jsColor.getString("1");
                String colorName = jsColor.getString("2");
                String colorPath = jsColor.getString("4");
                short numerOfImages = (short) jsColor.getInt("3");

                colors.add(new ColorVariant(reference, colorName, colorPath, numerOfImages));
            }

            Product product = new Product(id, name, shop
                                    , section, price, discount
                                    , aspectRation, link
                                    , description, colors);

            if (product.isOkay())
            {
                productList.add(product);
            }
        }

        Log.d(Properties.TAG, "[JSON_PARSER] Productos parseados correctamente, se devuelven " + productList.size() + " productos");
        return productList;
    }
}
