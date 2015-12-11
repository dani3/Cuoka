package com.wallakoala.wallakoala.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.greenfrvr.rubberloader.RubberLoaderView;
import com.wallakoala.wallakoala.Beans.ColorVariant;
import com.wallakoala.wallakoala.Beans.Image;
import com.wallakoala.wallakoala.Beans.Product;
import com.wallakoala.wallakoala.Beans.Size;
import com.wallakoala.wallakoala.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @class Pantalla de introduccion de la app
 * Created by Daniel Mancebo on 09/11/2015.
 */

public class IntroUI extends AppCompatActivity
{
    /* Views */
    protected RubberLoaderView loader;
    protected Button enter;

    /* Data */
    protected List<JSONObject> jsonList;
    protected Map<String, List<Product>> productsMap;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate(savedInstanceState);

        // Especificamos el layout 'intro.xml'
        setContentView( R.layout.intro );

        jsonList = new ArrayList<>();
        productsMap = new HashMap<>();

        loader = ( RubberLoaderView )findViewById( R.id.rubber_loader );
        enter = ( Button )findViewById( R.id.enter );
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v )
            {
                Intent intent = new Intent( IntroUI.this, ProductsUI.class );
                startActivity( intent );
                finish();
            }
        });

        new Products().execute( "Blanco", "HyM" );
    }

    private class Products  extends AsyncTask<String, Void, Void>
    {
        private List<String> content = new ArrayList<>();
        private String error = null;

        protected void onPreExecute()
        {
            loader.startLoading();
            enter.setEnabled(false);
        }

        protected Void doInBackground( String... shops )
        {
            BufferedReader reader = null;

            try
            {
                for ( int i = 0; i < shops.length; i++ )
                {
                    URL url = new URL("http://cuoka.cloudapp.net:8080/getProducts/" + shops[i]);
                    URLConnection conn = url.openConnection();

                    Log.e("CUCU", "Conectado");

                    // Get the server response
                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line = "";

                    // Read Server Response
                    while ((line = reader.readLine()) != null)
                        sb.append(line + "");

                    // Append Server Response To Content String
                    content.add(sb.toString());
                }

            } catch( Exception ex )  {
                error = ex.getMessage();

            } finally {
                try
                {
                    reader.close();
                }

                catch(Exception ex) {}
            }

            return null;

        } // doInBackground

        protected void onPostExecute( Void unused )
        {
            if ( error != null ) {

            } else {
                JSONArray jsonResponse;

                try {
                    for( int i = 0; i < content.size(); i++ )
                    {
                        jsonResponse = new JSONArray(content.get(i));

                        for (int j = 0; j < jsonResponse.length(); j++)
                        {
                            JSONObject js = jsonResponse.getJSONObject(j);

                            jsonList.add(js);
                        }

                        convertJSONtoProduct(jsonList);
                    }

                    Log.e("CUCU", "Numero de productos: " + jsonList.size());

                    enter.setEnabled(true);

                } catch ( JSONException e ) {
                    e.printStackTrace();
                }

            } // else

        } // onPostExecute

        /**
         * Metodo que inicializa el mapa de productos
         */
        private void convertJSONtoProduct( List<JSONObject> jsonList ) throws JSONException
        {
            List<Product> productsList = new ArrayList<>();
            String key = null;

            for( JSONObject jsonObject : jsonList )
            {
                String name = jsonObject.getString("name");
                String shop = key = jsonObject.getString("shop");
                String section = jsonObject.getString("section");
                double price = jsonObject.getDouble("price");
                boolean man = jsonObject.getBoolean("man");
                String link = jsonObject.getString("link");
                boolean newness = jsonObject.getBoolean("newness");

                JSONArray jsColors = jsonObject.getJSONArray("colors");
                List<ColorVariant> colors = new ArrayList<>();
                for( int i = 0; i < jsColors.length(); i++ )
                {
                    JSONObject jsColor = jsColors.getJSONObject(i);

                    String reference = jsColor.getString("reference");
                    String colorName = jsColor.getString("colorName");
                    String colorURL = jsColor.getString("colorURL");
                    String colorPath = jsColor.getString("colorPath");

                    List<Image> images = new ArrayList<>();
                    List<Size> sizes = new ArrayList<>();
                    JSONArray jsImages = jsColor.getJSONArray("images");
                    JSONArray jsSizes = jsColor.getJSONArray("sizes");
                    for ( int j = 0; j < jsImages.length(); j++ )
                    {
                        JSONObject jsImage = jsImages.getJSONObject(j);

                        String url = jsImage.getString("url");
                        String pathLargeSize = jsImage.getString("pathLargeSize");
                        String pathSmallSize = jsImage.getString("pathSmallSize");

                        images.add( new Image( url, pathSmallSize, pathLargeSize ) );
                    }

                    for ( int j = 0; j < jsSizes.length(); j++ )
                    {
                        JSONObject jsSize = jsSizes.getJSONObject(j);

                        String size = jsSize.getString("size");
                        boolean stock = jsSize.getBoolean("stock");

                        sizes.add( new Size( size, stock ) );
                    }

                    colors.add( new ColorVariant( reference, colorName, colorURL, colorPath, images, sizes ) );
                }

                productsList.add( new Product( name, shop, section, price, man, link, colors, newness, null ) );
            }

            productsMap.put( key, productsList );
        }

    } // Products
}
