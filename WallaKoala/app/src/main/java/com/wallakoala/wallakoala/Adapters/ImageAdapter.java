package com.wallakoala.wallakoala.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wallakoala.wallakoala.R;

/**
 * Created by Dani on 10/11/2015.
 */

public class ImageAdapter extends BaseAdapter
{
    private Context mContext;
    private LayoutInflater inflater;

    private String[] names;
    private int[] images;

    public ImageAdapter( Context context )
    {
        mContext = context;
        inflater = ( LayoutInflater )mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

        names = new String[]{"Prueba1", "Prueba2", "Prueba3", "Prueba4","Prueba5", "Prueba6", };
        images = new int[]{ R.drawable.imagen1
                    , R.drawable.imagen2
                    , R.drawable.imagen3
                    , R.drawable.imagen4
                    , R.drawable.imagen5
                    , R.drawable.imagen6 };
    }

    public int getCount() { return names.length; }

    public Object getItem( int position ) { return null; }

    public long getItemId( int position ) { return 0; }

    private class Holder
    {
        TextView text;
        ImageView image;
    }

    public View getView( int position, View convertView, ViewGroup parent )
    {
        View view  = inflater.inflate( R.layout.grid_single, null );

        Holder holder = new Holder();

        holder.text = ( TextView )view.findViewById(R.id.grid_text);
        holder.text.setText( names[ position ] );
        holder.image = ( ImageView )view.findViewById( R.id.grid_image );
        holder.image.setImageResource( images[ position ] );

        return view;
    }
}
