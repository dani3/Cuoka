package com.wallakoala.wallakoala.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.wallakoala.wallakoala.Beans.ColorVariant;
import com.wallakoala.wallakoala.Beans.Image;
import com.wallakoala.wallakoala.R;

/**
 * @class Adapter para la lista de imagenes de un producto.
 * Created by Daniel Mancebo Aldea on 23/01/2016.
 */

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductHolder>
{
    /* Constants */
    private static final String TAG = "CUOKA";

    /* Context */
    private static Context mContext;

    /* Data */
    private static ColorVariant mColor;

    /**
     * ViewHolder de la imagen con todos los componentes graficos necesarios
     */
    public static class ProductHolder extends RecyclerView.ViewHolder
    {
        private static ImageView image;
        private View loading;

        public ProductHolder(View itemView)
        {
            super(itemView);
        }

        /**
         * Metodo que inicializa las vistas con los datos del producto recibido, se llama cada vez que se visualiza el item.
         * @param imageProduct: producto con el que se inicializa un item.
         */
        public void bindProduct( Image imageProduct )
        {
            loading.setVisibility(View.VISIBLE);

            Log.d( TAG, "Image URL: "
                    + imageProduct.getPath().replaceAll( ".jpg", "_Large.jpg" ) );

            // Cargamos la imagen utilizando Picasso.
            Picasso.with( mContext )
                    .load( imageProduct.getPath().replaceAll(".jpg", "_Large.jpg"))
                    .into( image, new Callback()
                    {
                        @Override
                        public void onSuccess() {
                            loading.setVisibility( View.GONE );
                        }

                        @Override
                        public void onError() {
                            loading.setVisibility( View.GONE );
                        }
                    });
        }

    } /* [END] ViewHolder */

    public ProductAdapter( Context context, ColorVariant color )
    {
        mContext = context;
        mColor = color;
    }

    @Override
    public ProductHolder onCreateViewHolder( ViewGroup viewGroup, int viewType )
    {
        View itemView = LayoutInflater.from( viewGroup.getContext() )
                .inflate( R.layout.product
                        , viewGroup
                        , false );

        return new ProductHolder( itemView );
    }

    @Override
    public void onBindViewHolder( ProductHolder holder, int position )
    {
        holder.bindProduct( mColor.getImages().get( position ) );
    }

    @Override
    public int getItemCount()
    {
        return mColor.getImages().size();
    }
}
