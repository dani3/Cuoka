package com.wallakoala.wallakoala.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
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
import com.squareup.picasso.Target;
import com.wallakoala.wallakoala.Activities.ProductUI;
import com.wallakoala.wallakoala.Beans.ColorVariant;
import com.wallakoala.wallakoala.Beans.Product;
import com.wallakoala.wallakoala.R;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * @class Adapter para el grid de productos
 * Created by Daniel Mancebo Aldea on 10/11/2015.
 */

public class ProductsGridAdapter extends RecyclerView.Adapter<ProductsGridAdapter.ProductHolder>
{
    /* Constants */
    private static final String TAG = "CUOKA";
    private static final String PACKAGE = "com.wallakoala.wallakoala";

    /* Context */
    private static Context mContext;

    /* Data */
    private static List<Product> mProductList;

    /**
     * ViewHolder del producto con todos los componentes graficos necesarios
     */
    public static class ProductHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private Product mProduct;

        private ImageButton mFavImageButton;
        private ImageView mProductImageView;
        private ImageView mErrorImageView;
        private View mLoadingView;
        private View mBackgroundView;
        private View mProductFooterView, mProductFooterExtraView, mProductFooterMainView;
        private TextView mTitleTextView, mSubtitleTextView, mNameTextView, mPriceTextView;

        private Animation scaleUpFooterExtra, scaleDownFooterExtra;

        public ProductHolder(View itemView)
        {
            super(itemView);

            mErrorImageView   = (ImageView)itemView.findViewById(R.id.broken_image);
            mTitleTextView    = (TextView)itemView.findViewById(R.id.footer_title);
            mSubtitleTextView = (TextView)itemView.findViewById(R.id.footer_subtitle);
            mProductImageView = (ImageView)itemView.findViewById(R.id.grid_image);
            mFavImageButton   = (ImageButton)itemView.findViewById(R.id.footer_fav_button);
            mNameTextView     = (TextView)itemView.findViewById(R.id.name);
            mPriceTextView    = (TextView)itemView.findViewById(R.id.price);

            mBackgroundView         = itemView.findViewById(R.id.grid_background);
            mLoadingView            = itemView.findViewById(R.id.avloadingitem);
            mProductFooterView      = itemView.findViewById(R.id.footer);
            mProductFooterExtraView = itemView.findViewById(R.id.extraInfo);
            mProductFooterMainView  = itemView.findViewById(R.id.mainFooter);

            mProductFooterView.setOnClickListener(this);
            //mProductImageView.setOnClickListener( this );

            scaleUpFooterExtra = AnimationUtils.loadAnimation(mContext, R.anim.scale_up);
            scaleDownFooterExtra = AnimationUtils.loadAnimation(mContext, R.anim.scale_down);
            scaleDownFooterExtra.setAnimationListener(new Animation.AnimationListener()
            {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation)
                {
                    mProductFooterExtraView.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
        }

        /**
         * Metodo que inicializa las vistas con los datos del producto recibido, se llama cada vez que se visualiza el item.
         * @param product: producto con el que se inicializa un item.
         */
        public void bindProduct(Product product)
        {
            /* Inicializamos los TextViews */
            mTitleTextView.setText(product.getShop());
            mSubtitleTextView.setText(product.getSection());
            mNameTextView.setText(product.getName());
            mPriceTextView.setText(String.format("%.2f", product.getPrice()) + "€");

            /* Ocultamos la info, IMPORTANTE. Cosas malas pasan si no se pone. Tambien la imagen de error. */
            mProductFooterExtraView.setVisibility(View.GONE);
            mProductFooterMainView.setVisibility(View.GONE);
            mErrorImageView.setVisibility(View.GONE);

            /* Mostramos la view de carga y el background */
            mLoadingView.setVisibility(View.VISIBLE);
            mBackgroundView.setVisibility(View.VISIBLE);

            /* Cargamos la imagen usando Picasso */
            String url = product.getColors().get(0).getImages().get(0).getPath().replaceAll(".jpg", "_Small.jpg");
            Picasso.with(mContext)
                   .load(url)
                   .into(mProductImageView, new Callback()
                   {
                        @Override
                        public void onSuccess()
                        {
                            mBackgroundView.setVisibility(View.GONE);
                            mLoadingView.setVisibility(View.GONE);
                            mProductFooterMainView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError()
                        {
                            mLoadingView.setVisibility(View.GONE);
                            mErrorImageView.setVisibility(View.VISIBLE);
                        }
                   } );

            /* Ponemos el icono del corazon. */
            mFavImageButton.setBackgroundResource(R.drawable.ic_favorite_border_white);

            mProduct = product;
        }

        @Override
        public void onClick( View view )
        {
            /* Si se pulsa en el pie de foto */
            if (view.getId() == mProductFooterView.getId())
            {
                /* Abrimos la info extra si esta oculta */
                if (mProductFooterExtraView.getVisibility() == View.GONE)
                {
                    mProductFooterExtraView.setVisibility(View.VISIBLE);
                    mProductFooterExtraView.startAnimation(scaleUpFooterExtra);

                } else
                    mProductFooterExtraView.startAnimation(scaleDownFooterExtra);

            }

            /* Si se pulsa en la imagen */
            if (view.getId() == mProductImageView.getId())
            {
                final View v = view;

                Target target = new Target()
                {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
                    {
                        Activity activity = (Activity)mContext;

                        /* Sacamos las coordenadas de la imagen */
                        int[] screenLocation = new int[2];
                        v.getLocationInWindow(screenLocation);

                        ColorVariant color = mProduct.getColors().get(0);

                        /* Creamos el intent */
                        Intent intent = new Intent(mContext, ProductUI.class);

                        /* Enviamos toda la informacion necesaria para que la siguiente activity
                         * realice la animacion */
                        intent.putExtra(PACKAGE + ".Beans.ColorVariant", color)
                              .putExtra(PACKAGE + ".bitmap", getImageUri(mContext, bitmap).toString())
                              .putExtra(PACKAGE + ".left", screenLocation[0])
                              .putExtra(PACKAGE + ".top", screenLocation[1])
                              .putExtra(PACKAGE + ".width", v.getWidth())
                              .putExtra(PACKAGE + ".height", v.getHeight());

                        mContext.startActivity(intent);

                        /* Desactivamos las transiciones por defecto */
                        activity.overridePendingTransition(0, 0);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {}

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {}
                };

                /* Descargamos la imagen en HQ y la guardamos en un bitmap */
                String url = mProduct.getColors().get(0).getImages().get(0).getPath().replaceAll(".jpg", "_Large.jpg");
                Picasso.with(mContext)
                       .load(url)
                       .into(target);
            }
        }

        /**
         * Metodo que saca la Uri de un bitmap.
         * @param inContext: contexto.
         * @param inImage: bitmap de la que se quiere sacar la Uri.
         * @return Uri del bitmap.
         */
        private Uri getImageUri(Context inContext, Bitmap inImage)
        {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();

            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

            String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);

            return Uri.parse(path);
        }

    } /* [END] ViewHolder */


    public ProductsGridAdapter(Context context, List<Product> productList)
    {
        mContext = context;
        mProductList = productList;
    }

    public void updateProductList(List<Product> productList)
    {
        mProductList = productList;
    }

    @Override
    public ProductHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                                      .inflate(R.layout.product_item_grid
                                                    , viewGroup
                                                    , false );

        return new ProductHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ProductHolder productHolder, int pos)
    {
        productHolder.bindProduct(mProductList.get(pos));
    }

    @Override
    public int getItemCount()
    {
        return mProductList.size();
    }
}
