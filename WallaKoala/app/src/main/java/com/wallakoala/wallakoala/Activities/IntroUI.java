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
import com.wallakoala.wallakoala.Beans.Product;
import com.wallakoala.wallakoala.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

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
    protected List<Product> productsList;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate(savedInstanceState);

        // Especificamos el layout 'intro.xml'
        setContentView( R.layout.intro );

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

        jsonList = new ArrayList<>();

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
                    }

                    Log.e("CUCU", "Numero de productos: " + jsonList.size());

                    enter.setEnabled(true);

                } catch ( JSONException e ) {
                    e.printStackTrace();
                }
            }
        } // onPostExecute

    } // Products
}
