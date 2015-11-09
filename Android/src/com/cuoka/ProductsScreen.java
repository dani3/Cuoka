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

import com.cuoka.bean.ColorVariant;
import com.cuoka.bean.Image;
import com.cuoka.bean.Product;
import com.cuoka.bean.Size;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class ProductsScreen extends Activity 
{
	private List<Product> products;
	
	private GridView gridView;
	
    @Override
    protected void onCreate( Bundle savedInstanceState ) 
    {
        super.onCreate( savedInstanceState );
        
        setContentView( R.layout.products_layout );
        
        products = new ArrayList<Product>();
        
        gridView = ( GridView )findViewById( R.id.gridview );
        gridView.setOnItemClickListener( new OnItemClickListener() 
        {
        	@Override
            public void onItemClick( AdapterView<?> parent, View v, int position, long id) 
        	{
                Toast.makeText( ProductsScreen.this, "" + position,
                        Toast.LENGTH_SHORT ).show();
            }
        });   
    
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() 
        {         
        	private String content = "";
        	private String error = null;

        	@Override
        	protected void onPreExecute() 
        	{
        		super.onPreExecute();
        		Toast.makeText( ProductsScreen.this, "Conectando",
        				Toast.LENGTH_SHORT ).show();
        	}

        	@Override
        	protected String doInBackground( Void... urls )
        	{           
        		BufferedReader reader = null;

        		try
        		{         		
        			URL url = new URL( "http://192.168.1.134:8080/getProducts/Blanco" );

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
        			
        			JSONArray jsonResponse;

        			try {
        				// Convertimos la respuesta del servidor a un array de JSON
        				jsonResponse = new JSONArray( content );

        				Log.d( "CUOKA" , "JSON size: " + jsonResponse.length() );
        				
        				// 
        				for ( int i=0; i < jsonResponse.length(); i++)
        				{
        					JSONObject js = jsonResponse.getJSONObject( i );

        					String name = js.getString( "name" );
        					String shop = js.getString( "shop" );
        					String section = js.getString( "section" );
        					String link = js.getString( "link" );
        					double price = js.getDouble( "price" );
        					boolean man = js.getBoolean( "man" );
        					JSONArray jsColors = js.getJSONArray( "colors" );

        					List<ColorVariant> colors = new ArrayList<ColorVariant>();
        					for ( int j = 0; j < jsColors.length(); j++ )
        					{
        						JSONObject jsColor = jsColors.getJSONObject( j );

        						String reference = jsColor.getString( "reference" );
        						String colorName = jsColor.getString( "colorName" );
        						String colorURL = jsColor.getString( "colorURL" );
        						String colorPath = jsColor.getString( "colorPath" );

        						JSONArray jsImages = jsColor.getJSONArray( "images" );
        						JSONArray jsSizes = jsColor.getJSONArray( "sizes" );

        						List<Image> images = new ArrayList<Image>();
        						List<Size> sizes = new ArrayList<Size>();
        						for ( int k = 0; k < jsImages.length(); k++ )
        						{
        							JSONObject jsImage = jsImages.getJSONObject( k );

        							String imageURL = jsImage.getString( "url" );
        							String pathSmallSize = jsImage.getString( "pathSmallSize" );
        							String pathLargeSize = jsImage.getString( "pathLargeSize" );

        							images.add( new Image( imageURL, pathSmallSize, pathLargeSize ) );
        						}

        						for ( int k = 0; k < jsSizes.length(); k++ )
        						{
        							JSONObject jsSize = jsSizes.getJSONObject( k );

        							String size = jsSize.getString( "size" );
        							boolean stock = jsSize.getBoolean( "stock" );

        							sizes.add( new Size( size, stock ) );
        						}

        						colors.add( new ColorVariant( reference, colorName, colorURL, colorPath, images, sizes ) );                        	
        					} // for Colors

        					products.add( new Product( name, shop, section, price, man, link, colors ) );

        				} // for Products                          

        				Log.d( "CUOKA" , "Products size: " + products.size() );
        				gridView.setAdapter( new ImageAdapter( ProductsScreen.this, products.subList( 0, 10 ) ) );

        			} catch ( JSONException e ) {           
        				e.printStackTrace();
        			}                 

        		} catch( Exception ex )  {
        			error = ex.getMessage();

        		} finally {
        			try
        			{          
        				reader.close();
        				Log.d( "CUOKA" , "doInBackground terminado" );
        			}

        			catch( Exception ex ) {}
        		}

        		return null;

        	} // doInBackground

        	@Override
        	protected void onPostExecute( String error ) 
        	{
        		Log.d( "CUOKA" , "onPostExecute" );
        		if ( error != null ) {

        		} else {          
        			
        		}
        	} // onPostExecute      

         }; // task
         
         if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB )
        	 task.executeOnExecutor( AsyncTask.THREAD_POOL_EXECUTOR );
         else
        	 task.execute();
         
    } // onCreate
}
