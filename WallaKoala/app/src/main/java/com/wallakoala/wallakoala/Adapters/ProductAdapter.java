package com.wallakoala.wallakoala.Adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.wallakoala.wallakoala.Beans.Product;
import com.wallakoala.wallakoala.R;

import java.util.List;

/**
 * @class Adapter para el grid de productos
 * Created by Daniel Mancebo Aldea on 10/11/2015.
 */

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductHolder>
{
    /* Constants */
    private static final String TAG = "CUOKA";

    /* Context */
    private static Context mContext;

    /* Data */
    private static List<Product> mProductList;

    /**
     * ViewHolder del producto con todos los componentes graficos necesarios
     */
    public static class ProductHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private TextView title, subtitle, name, price;
        private ImageButton fav;
        private ImageView image;
        private ImageView error;
        private View loading;
        private View footer, footerExtra;

        private Animation scaleUpFooterExtra, scaleDownFooterExtra;

        private CardView container;

        public ProductHolder( View itemView )
        {
            super( itemView );

            error       = ( ImageView )itemView.findViewById( R.id.broken_image );
            title       = ( TextView )itemView.findViewById( R.id.footer_title );
            subtitle    = ( TextView )itemView.findViewById( R.id.footer_subtitle );
            image       = ( ImageView )itemView.findViewById( R.id.grid_image );
            fav         = ( ImageButton )itemView.findViewById( R.id.footer_fav_button );
            container   = ( CardView )itemView.findViewById( R.id.card_item );
            name        = ( TextView )itemView.findViewById( R.id.name );
            price       = ( TextView )itemView.findViewById( R.id.price );

            loading     = itemView.findViewById( R.id.avloadingitem );
            footer      = itemView.findViewById( R.id.footer );
            footerExtra = itemView.findViewById( R.id.extraInfo );

            footer.setOnClickListener( this );

            scaleUpFooterExtra = AnimationUtils.loadAnimation( mContext, R.anim.scale_up );
            scaleDownFooterExtra = AnimationUtils.loadAnimation( mContext, R.anim.scale_down );
            scaleDownFooterExtra.setAnimationListener( new Animation.AnimationListener()
            {
                @Override
                public void onAnimationStart( Animation animation ) {}

                @Override
                public void onAnimationEnd( Animation animation )
                {
                    footerExtra.setVisibility( View.GONE );
                }

                @Override
                public void onAnimationRepeat( Animation animation ) {}
            });
        }

        /**
         * Metodo que inicializa las vistas con los datos del producto recibido, se llama cada vez que se visualiza el item.
         * @param product: producto con el que se inicializa un item.
         */
        public void bindProduct( Product product )
        {
            // Establecemos todos los textViews
            title.setText( product.getShop() );
            subtitle.setText( product.getSection() );
            name.setText( product.getName() );
            price.setText( Double.toString(product.getPrice()).replaceAll( ".0", "" ) + "€" );

            // Ocultamos la info extra, IMPORTANTE. Cosas malas pasan si no se pone.
            footerExtra.setVisibility( View.GONE );
            // Mostramos la view de carga
            loading.setVisibility( View.VISIBLE );

            // Cargamos la imagen utilizando Picasso.
            Picasso.with( mContext )
                   .load( product.getColors().get( 0 ).getImages().get( 0 ).getPath().replaceAll( ".jpg", "_Small.jpg" ) )
                   .into( image, new Callback() {
                       @Override
                       public void onSuccess() {
                           loading.setVisibility(View.GONE);
                       }

                       @Override
                       public void onError() {
                           loading.setVisibility(View.GONE);
                           error.setVisibility(View.VISIBLE);
                       }
                   } );

            // Ponemos el icono del corazon.
            fav.setBackgroundResource( R.drawable.ic_favorite_border_white );
        }

        @Override
        public void onClick( View view )
        {
            // Si se pulsa en el pie de foto
            if ( view.getId() == footer.getId() )
            {
                // Abrimos la info extra si esta oculta
                if ( footerExtra.getVisibility() == View.GONE )
                {
                    footerExtra.setVisibility(View.VISIBLE);
                    footerExtra.startAnimation(scaleUpFooterExtra);

                } else
                    footerExtra.startAnimation(scaleDownFooterExtra);
            }
        }

    } /* [END] ViewHolder */

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
