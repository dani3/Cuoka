package com.wallakoala.wallakoala.Utils;

import android.util.Log;

import com.wallakoala.wallakoala.Beans.ColorVariant;
import com.wallakoala.wallakoala.Beans.Notification;
import com.wallakoala.wallakoala.Beans.Product;
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
     * Metodo que convierte un JSON en un objeto Notification.
     * @param jsonObject: objeto JSON.
     * @return objeto Notification.
     * @throws JSONException
     */
    public static Notification convertJSONtoNotification(JSONObject jsonObject) throws JSONException
    {
        String title = jsonObject.getString("title");
        String text = jsonObject.getString("text");
        String extraInfo = jsonObject.getString("extraInfo");
        String image = jsonObject.getString("image");
        short offset = (short)jsonObject.getInt("offset");
        short action = (short)jsonObject.getInt("action");

        return new Notification(title, text, extraInfo, image, offset, action);
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
        Set<Long> favorites = new HashSet<>();
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
        Log.d(Properties.TAG, " - Numero de notificaciones leidas: " + user.getNotificationsRead().size());
        Log.d(Properties.TAG, " - Tiendas: " + jsonArray);

        return user;
    }

    /**
     * Metodo que convierte la lista de JSON en productos y los inserta en las distintas ED's.
     * @param jsonArray: lista de JSON a convertir.
     * @throws JSONException
     */
    public static List<Product> convertJSONtoProduct(final JSONArray jsonArray) throws JSONException
    {
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
            boolean obsolete = jsonObject.getBoolean("10");

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

            Product product = new Product(id
                    , name
                    , shop
                    , section
                    , price
                    , aspectRation
                    , obsolete
                    , link
                    , description
                    , colors);

            if (product.isOkay())
            {
                productList.add(product);
            }
        }

        return productList;
    }
}
