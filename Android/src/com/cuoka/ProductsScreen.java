package com.cuoka;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class ProductsScreen extends Activity 
{
	private List<JSONObject> productsList;
	
	private TextView productsListTxt;
	
    @Override
    protected void onCreate( Bundle savedInstanceState ) 
    {
        super.onCreate( savedInstanceState );
        
        setContentView( R.layout.products_layout );
        
        productsListTxt = ( TextView )findViewById( R.id.listTxt );
        productsList = new ArrayList<JSONObject>();
        
        new Products().execute();
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) 
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.products_screen, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) 
    {
        int id = item.getItemId();
        if ( id == R.id.action_settings ) 
            return true;
        
        return super.onOptionsItemSelected( item );
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
                  URL url = new URL( "http://192.168.1.42:8080/getProducts/Blanco" );
        
                  URLConnection conn = url.openConnection(); 
                  
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
        
                    catch( Exception ex ) {}
                }
                
            return null;
            
        } // doInBackground
        
        protected void onPostExecute( Void unused ) 
        {
            if ( error != null ) {
                  
                // Toast
                  
            } else {          
                JSONArray jsonResponse;
                       
                try {
                         jsonResponse = new JSONArray( content );
                           
                         for ( int j=0; j < jsonResponse.length(); j++)
                         {
                        	 JSONObject js = jsonResponse.getJSONObject(j);
                             
                             productsList.add( js );
                         }
                       
                 } catch ( JSONException e ) {           
                     e.printStackTrace();
                 }                 
             }
        } // onPostExecute      
 
    } // Products
}
