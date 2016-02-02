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
import com.wallakoala.wallakoala.R;
import com.wallakoala.wallakoala.Utils.Utils;

/**
 * @class Adapter para la lista de imagenes de un producto.
 * Created by Daniel Mancebo Aldea on 23/01/2016.
 */

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductHolder>
{
    /* Constants */
    private static final String TAG = "CUOKA";
    protected static final String SERVER_URL = "http://cuoka-ws.cloudapp.net";
    protected static final String IMAGES_PATH = "/images/products/";

    /* Context */
    private static Context mContext;

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
        private Target mTarget;

        public ProductHolder(View itemView)
        {
            super(itemView);

            mProductImageView = (ImageView)itemView.findViewById(R.id.product_image);
        }

        /**
         * Metodo que inicializa las vistas con los datos del producto recibido, se llama cada vez que se visualiza el item.
         * @param colorVariant: producto con el que se inicializa un item.
         */
        public void bindProduct(ColorVariant colorVariant)
        {
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

            String imageFile = mShop + "_"
                    + mSection + "_"
                    + colorVariant.getReference() + "_"
                    + colorVariant.getColorName() + "_" + getAdapterPosition() + "_Large.jpg";

            String url = Utils.fixUrl(SERVER_URL + IMAGES_PATH + mShop + "/" + imageFile);

            // Cargamos la imagen utilizando Picasso.
            Picasso.with(mContext)
                   .load(url)
                   .into(mTarget);
        }

    } /* [END] ViewHolder */

    public ProductAdapter(Context context, ColorVariant color, double ratio, String shop, String section)
    {
        mContext = context;
        mColor = color;
        mAspectRatio = ratio;
        mShop = shop;
        mSection = section;
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
