package com.wallakoala.wallakoala.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
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
    private static double mAspectRatio;

    /**
     * ViewHolder de la imagen con todos los componentes graficos necesarios
     */
    public static class ProductHolder extends RecyclerView.ViewHolder
    {
        private ImageView mProductImageView;
        private Target mTarget;

        public ProductHolder(View itemView)
        {
            super(itemView);

            mProductImageView = (ImageView)itemView.findViewById(R.id.product_image);
        }

        /**
         * Metodo que inicializa las vistas con los datos del producto recibido, se llama cada vez que se visualiza el item.
         * @param imageProduct: producto con el que se inicializa un item.
         */
        public void bindProduct(Image imageProduct)
        {
            String url = imageProduct.getPath().replaceAll(".jpg", "_Large.jpg");

            mTarget = new Target()
            {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
                {
                    mProductImageView.setImageBitmap(bitmap);
                    mProductImageView.setBackgroundColor(-1);
                    mProductImageView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {}

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable)
                {
                    mProductImageView.getLayoutParams().height = (int)(mProductImageView.getWidth() * mAspectRatio);
                    mProductImageView.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
                }
            };

            // Cargamos la imagen utilizando Picasso.
            Picasso.with(mContext)
                   .load(url)
                   .into(mTarget);
        }

    } /* [END] ViewHolder */

    public ProductAdapter(Context context, ColorVariant color, double ratio)
    {
        mContext = context;
        mColor = color;
        mAspectRatio = ratio;
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
        holder.bindProduct(mColor.getImages().get(position));
    }

    @Override
    public int getItemCount()
    {
        return mColor.getImages().size();
    }
}
