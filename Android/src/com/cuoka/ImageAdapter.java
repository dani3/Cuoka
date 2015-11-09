package com.cuoka;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.cuoka.bean.Product;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ImageAdapter extends BaseAdapter 
{
	private Context mContext;
	private static LayoutInflater inflater;
    
    private String[] productNameList;    
    private String[] productURLList;

	public ImageAdapter( Context c, List<Product> products ) 
	{
	    mContext = c;
	    inflater = ( LayoutInflater )mContext.
                getSystemService( Context.LAYOUT_INFLATER_SERVICE );
	    
	    productNameList = new String[ products.size() ];
	    productURLList = new String[ products.size() ];
	    for ( int i = 0; i < products.size(); i++ )
	    {
	    	productNameList[i] = products.get(i).getSection();
	    	productURLList[i] = "http://192.168.1.134/images/products/" 
	    												+ products.get( i ).getShop() + "/"
	    												+ products.get(i).getColors().get( 0 ).getImages().get( 0 ).getPathSmallSize().replace( "/var/www/html/" , "" );
	    	
	    	Log.v( "CUOKA" , "http://192.168.1.134/images/products/" 
					+ products.get( i ).getShop() + "/"
					+ products.get(i).getColors().get( 0 ).getImages().get( 0 ).getPathSmallSize().replace( "/var/www/html/" , "" ) );
	    }
	}
	
	public int getCount() { return productNameList.length; }
	
	public Object getItem( int position ) { return null; }
	
	public long getItemId( int position ) { return 0; }
	
	private class Holder
	{
		TextView name;
		ImageUI imageView;
	}
	
	// create a new ImageView for each item referenced by the Adapter
    public View getView( int position, View convertView, ViewGroup parent ) 
    {    	
    	View view  = inflater.inflate( R.layout.product, null );
    	
    	try 
    	{
	    	Holder holder = new Holder();
	    	
	        holder.name=(TextView) view.findViewById(R.id.grid_text);
	        holder.imageView=(ImageUI) view.findViewById(R.id.grid_image);
	        
	        holder.name.setText( productNameList[position] );
	        
			holder.imageView.setImageBitmap( BitmapFactory
														.decodeStream( ( InputStream ) new URL( productURLList[ position ] ).getContent() ) );
		
			return view;	
			
    	} catch (MalformedURLException e) {
			e.printStackTrace();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        return view;
    }
}