package com.wallakoala.wallakoala.Utils;

import com.wallakoala.wallakoala.Beans.ColorVariant;
import com.wallakoala.wallakoala.Beans.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase que se encarga de parsear los JSON.
 * Created by Daniel Mancebo Aldea on 28/10/2016.
 */

public class JSONParser
{
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
