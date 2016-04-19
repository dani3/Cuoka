package com.wallakoala.wallakoala.Adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.wallakoala.wallakoala.Beans.ColorVariant;
import com.wallakoala.wallakoala.Properties.Properties;
import com.wallakoala.wallakoala.R;
import com.wallakoala.wallakoala.Utils.Utils;
import com.wang.avi.AVLoadingIndicatorView;

/**
 * @class Adapter para la lista de imagenes de un producto.
 * Created by Daniel Mancebo Aldea on 23/01/2016.
 */

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductHolder>
{
    /* Context */
    private static Context mContext;

    /* Views */
    private static ImageView mImageView;

    /* Data */
    private static ColorVariant mColor;
    private static double mAspectRatio;
    private static String mShop;
    private static String mSection;

    /**
     * ViewHolder de la imagen con todos los componentes graficos necesarios
     */
    public static class ProductHolder extends RecyclerView.ViewHolder
    {
        private ImageView mProductImageView;
        private AVLoadingIndicatorView mLoadingView;
        private Target mTarget;

        public ProductHolder(View itemView)
        {
            super(itemView);

            mProductImageView = (ImageView)itemView.findViewById(R.id.product_image);
            mLoadingView      = (AVLoadingIndicatorView)itemView.findViewById(R.id.av_loading_image);
        }

        /**
         * Metodo que inicializa las vistas con los datos del producto recibido, se llama cada vez que se visualiza el item.
         * @param colorVariant: producto con el que se inicializa un item.
         */
        public void bindProduct(ColorVariant colorVariant)
        {
            mProductImageView.getLayoutParams().height = (int)(Resources.getSystem()
                                                                        .getDisplayMetrics().widthPixels * mAspectRatio);

            mProductImageView.setBackgroundColor(mContext.getResources()
                                                         .getColor(android.R.color.transparent));

            if (getAdapterPosition() != 0)
                mLoadingView.setVisibility(View.VISIBLE);

            mTarget = new Target()
            {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
                {
                    if (getAdapterPosition() != 0)
                        mLoadingView.setVisibility(View.GONE);
                    else
                        mImageView.setVisibility(View.GONE);

                    mProductImageView.setImageBitmap(bitmap);
                    mProductImageView.setBackgroundColor(-1);
                    mProductImageView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {}

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {}
            };

            String imageFile = mShop + "_"
                    + mSection + "_"
                    + colorVariant.getReference() + "_"
                    + colorVariant.getColorName() + "_" + getAdapterPosition() + "_Large.jpg";

            String url = Utils.fixUrl(Properties.SERVER_URL + Properties.IMAGES_PATH + mShop + "/" + imageFile);

            // Cargamos la imagen utilizando Picasso.
            Picasso.with(mContext)
                   .load(url)
                   .into(mTarget);
        }

    } /* [END] ViewHolder */

    /**
     * Constructor del Adapter.
     * @param context: contexto de la aplicacion.
     * @param color: ColorVariant del que se van a mostrar las imagenes.
     * @param ratio: aspect ratio de las imagenes.
     * @param shop: tienda a la que pertenece el producto.
     * @param section: seccion a la que pertenece el producto.
     * @param image: imagen de baja calidad que se coloca debajo del RecyclerView.
     */
    public ProductAdapter(Context context
                , ColorVariant color
                , double ratio
                , String shop
                , String section
                , ImageView image)
    {
        mContext = context;
        mColor = color;
        mAspectRatio = ratio;
        mShop = shop;
        mSection = section;
        mImageView = image;
    }

    @Override
    public ProductHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.product_image
                    , viewGroup
                    , false);

        return new ProductHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ProductHolder holder, int position)
    {
        holder.bindProduct(mColor);
    }

    @Override
    public int getItemCount()
    {
        return mColor.getNumberOfImages();
    }

}
