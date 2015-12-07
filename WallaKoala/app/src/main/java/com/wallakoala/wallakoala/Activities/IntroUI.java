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

        loader.startLoading();

        new Products().execute();
    }

    private class Products  extends AsyncTask<String, Void, Void>
    {
        private String content = "";
        private String error = null;

        protected void onPreExecute() {}

        protected Void doInBackground( String... urls )
        {
            BufferedReader reader = null;

            try
            {
                URL url = new URL( "http://192.168.1.47:8080/getProducts/Blanco" );

                Log.e("CUCU", "URL Creada");

                URLConnection conn = url.openConnection();

                Log.e("CUCU", "Conectado");

                // Get the server response
                reader = new BufferedReader( new InputStreamReader( conn.getInputStream() ) );
                StringBuilder sb = new StringBuilder( );
                String line = "";

                // Read Server Response
                while( ( line = reader.readLine() ) != null )
                    sb.append( line + "" );

                // Append Server Response To Content String
                content = sb.toString();

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

                // Toast

            } else {
                JSONArray jsonResponse;
                Toast.makeText(IntroUI.this, "OnPostExecute", Toast.LENGTH_SHORT).show();

                try {
                    jsonResponse = new JSONArray( content );

                    Toast.makeText(IntroUI.this, "JSON Array creado", Toast.LENGTH_SHORT).show();

                    for ( int j=0; j < jsonResponse.length(); j++)
                    {
                        JSONObject js = jsonResponse.getJSONObject(j);

                        jsonList.add(js);
                    }

                    Log.e("CUCU", "Numero de productos: " + jsonList.size());

                } catch ( JSONException e ) {
                    e.printStackTrace();
                }
            }
        } // onPostExecute

    } // Products
}
