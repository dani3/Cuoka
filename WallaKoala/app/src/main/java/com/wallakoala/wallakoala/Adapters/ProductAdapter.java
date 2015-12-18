package com.wallakoala.wallakoala.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.wallakoala.wallakoala.Beans.Product;
import com.wallakoala.wallakoala.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * @class Adapter para el grid de productos
 * Created by Daniel Mancebo Aldea on 10/11/2015.
 */

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductHolder>
{
    /* Context */
    private Context mContext;

    /* Data */
    private List<Product> mProductList;

    /* Animations */
    private Animation implode, explode;

    /**
     * ViewHolder del producto con todos los componentes graficos necesarios
     */
    public static class ProductHolder extends RecyclerView.ViewHolder
    {
        private TextView title, subtitle;
        private ImageButton fav;
        private ImageView image;

        public ProductHolder( View itemView )
        {
            super( itemView );

            title    = ( TextView )itemView.findViewById( R.id.footer_title );
            subtitle = ( TextView )itemView.findViewById( R.id.footer_subtitle );
            image    = ( ImageView )itemView.findViewById( R.id.grid_image );
            fav      = ( ImageButton )itemView.findViewById( R.id.footer_fav_button );
        }

        /**
         * Metodo que inicializa las vistas con los datos del producto recibido.
         * @param product: producto con el que se inicializa un item.
         */
        public void bindProduct( Product product )
        {
            title.setText( product.getShop() );
            subtitle.setText( product.getSection() );
            image.setImageBitmap( product.getMainImage() );
            fav.setBackgroundResource( R.drawable.ic_favorite_border_white );
        }
    }

    public ProductAdapter( Context context, List<Product> productList )
    {
        mContext = context;
        mProductList = productList;
    }

    public void updateProductList( List<Product> productList )
    {
        mProductList = productList;
    }

    @Override
    public ProductHolder onCreateViewHolder( ViewGroup viewGroup, int viewType )
    {
        View itemView = LayoutInflater.from( viewGroup.getContext() )
                                      .inflate( R.layout.product_item_grid
                                                    , viewGroup
                                                    , false );

        return new ProductHolder( itemView );
    }

    @Override
    public void onBindViewHolder( final ProductHolder productHolder, int pos )
    {
        productHolder.bindProduct( mProductList.get( pos ) );
    }

    @Override
    public int getItemCount()
    {
        return mProductList.size();
    }
}
