package com.wallakoala.wallakoala.Adapters;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

/**
 * Adapter para la lista de imagenes de un producto.
 * Created by Daniel Mancebo Aldea on 23/01/2016.
 */

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductHolder>
{
    /* Context */
    private Context mContext;

    /* Views */
    private ImageView mImageView;

    /* Data */
    private Bitmap mFirstImageBitmap;
    private ColorVariant mColor;
    private String mShop;
    private String mSection;
    private double mAspectRatio;
    private boolean mLoaded;

    /**
     * ViewHolder de la imagen con todos los componentes graficos necesarios.
     */
    public class ProductHolder extends RecyclerView.ViewHolder
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
        @SuppressWarnings("deprecation")
        public void bindProduct(ColorVariant colorVariant)
        {
            mProductImageView.getLayoutParams().height = (int)(Resources.getSystem()
                                                                        .getDisplayMetrics().widthPixels * mAspectRatio);

            mProductImageView.setBackgroundColor(
                    mContext.getResources().getColor(android.R.color.transparent));

            mTarget = new Target()
            {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
                {
                    if (getAdapterPosition() == 0)
                    {
                        mFirstImageBitmap = bitmap;

                        mImageView.setVisibility(View.GONE);

                        mLoaded = true;
                    }

                    mProductImageView.setImageBitmap(bitmap);
                    mProductImageView.setBackgroundColor(-1);
                    mProductImageView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {}

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {}
            };

            final String imageFile = mShop + "_" + mSection + "_"
                            + colorVariant.getReference() + "_"
                            + colorVariant.getColorName() + "_" + getAdapterPosition() + "_Large.jpg";

            final String url = Utils.fixUrl(
                    Properties.SERVER_URL + Properties.IMAGES_PATH + mShop + "/" + imageFile);

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
        mLoaded = false;
    }

    /**
     * Metodo que devuelve la URI de la primera imagen para compartirla.
     * @return URI de la primera imagen si ha sido descargada, null EOC.
     */
    @Nullable
    public Uri getFirstImageUri()
    {
        // Comprobamos que el usuario ha dado permisos para acceder a las imagenes.
        Log.d(Properties.TAG, "[PRODUCT_ADAPTER] Se comprueba si tiene permisos de escritura");
        int permissionCheck = ContextCompat.checkSelfPermission(
                mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        // Si no tiene permisos, se piden al usuario.
        if (permissionCheck != PackageManager.PERMISSION_GRANTED)
        {
            Log.d(Properties.TAG, "[PRODUCT_ADAPTER] NO se tiene permisos de escritura, se piden");
            ActivityCompat.requestPermissions(
                    (Activity) mContext, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);

        } else if (mLoaded) {
            Log.d(Properties.TAG, "[PRODUCT_ADAPTER] Sí se tienen permisos de escritura, se guarda la imagen temporalmente");
            String path = MediaStore.Images.Media.insertImage(
                    mContext.getContentResolver(), mFirstImageBitmap, "Image compartida", null);

            return Uri.parse(path);
        }

        return null;
    }

    @Override
    public ProductHolder onCreateViewHolder(final ViewGroup viewGroup, int viewType)
    {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                                      .inflate(R.layout.product_image
                                            , viewGroup
                                            , false);

        return new ProductHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ProductHolder holder, final int position)
    {
        holder.bindProduct(mColor);
    }

    @Override
    public int getItemCount()
    {
        return mColor.getNumberOfImages();
    }

}
