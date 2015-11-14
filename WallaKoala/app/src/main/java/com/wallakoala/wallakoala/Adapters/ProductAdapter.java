package com.wallakoala.wallakoala.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wallakoala.wallakoala.Beans.Product;
import com.wallakoala.wallakoala.R;

import org.w3c.dom.Text;

/**
 * @class Adapter para el grid de productos
 * Created by Daniel Mancebo Aldea on 10/11/2015.
 */

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductHolder>
{
    private String[] names;
    private int[] images;

    public static class ProductHolder extends RecyclerView.ViewHolder
    {
        private TextView text;
        private ImageView image;

        public ProductHolder( View itemView )
        {
            super( itemView );

            text = ( TextView )itemView.findViewById( R.id.grid_text );
            image = ( ImageView )itemView.findViewById( R.id.grid_image );
        }

        public void bindProduct( Product product )
        {

        }
    }

    public ProductAdapter()
    {
        names = new String[]{"Prueba1", "Prueba2", "Prueba3", "Prueba4","Prueba5", "Prueba6", };
        images = new int[]{ R.drawable.imagen1
                    , R.drawable.imagen2
                    , R.drawable.imagen3
                    , R.drawable.imagen4
                    , R.drawable.imagen5
                    , R.drawable.imagen6 };
    }

    @Override
    public ProductHolder onCreateViewHolder( ViewGroup viewGroup, int viewType )
    {
        View itemView = LayoutInflater.from( viewGroup.getContext() )
                                      .inflate( R.layout.product, viewGroup, false );

        return new ProductHolder( itemView );
    }

    @Override
    public void onBindViewHolder( ProductHolder productHolder, int pos )
    {
        productHolder.text.setText( names[ pos ] );
        productHolder.image.setImageResource( images[ pos ] );
    }

    @Override
    public int getItemCount()
    {
        return names.length;
    }
}
