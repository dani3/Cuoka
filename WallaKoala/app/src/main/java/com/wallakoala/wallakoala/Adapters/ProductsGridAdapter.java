package com.wallakoala.wallakoala.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
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
    private static double[] mProductBitmapArray;
    private static ProductHolder mProductClicked;

    /**
     * ViewHolder del producto con todos los componentes graficos necesarios
     */
    public static class ProductHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private Product mProduct;
        private Target mTarget;
        private Bitmap mBitmap;
        private String mBitmapUri;

        private ImageButton mFavImageButton;
        private ImageView mProductImageView;
        private View mLoadingView;
        private View mProductFooterView, mProductFooterExtraView, mProductFooterMainView;
        private TextView mTitleTextView, mSubtitleTextView, mNameTextView, mPriceTextView;

        private Animation scaleUp, scaleDownFooterExtra, scaleDownFooter;

        public ProductHolder(View itemView)
        {
            super(itemView);

            mTitleTextView    = (TextView)itemView.findViewById(R.id.footer_title);
            mSubtitleTextView = (TextView)itemView.findViewById(R.id.footer_subtitle);
            mProductImageView = (ImageView)itemView.findViewById(R.id.grid_image);
            mFavImageButton   = (ImageButton)itemView.findViewById(R.id.footer_fav_button);
            mNameTextView     = (TextView)itemView.findViewById(R.id.name);
            mPriceTextView    = (TextView)itemView.findViewById(R.id.price);

            mLoadingView            = itemView.findViewById(R.id.avloadingitem);
            mProductFooterView      = itemView.findViewById(R.id.footer);
            mProductFooterExtraView = itemView.findViewById(R.id.extraInfo);
            mProductFooterMainView  = itemView.findViewById(R.id.mainFooter);

            mProductFooterView.setOnClickListener(this);
            mProductImageView.setOnClickListener(this);

            scaleUp              = AnimationUtils.loadAnimation(mContext, R.anim.scale_up);
            scaleDownFooterExtra = AnimationUtils.loadAnimation(mContext, R.anim.scale_down);
            scaleDownFooter      = AnimationUtils.loadAnimation(mContext, R.anim.scale_down);

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

            scaleDownFooter.setAnimationListener(new Animation.AnimationListener()
            {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation)
                {
                    mProductFooterView.setVisibility(View.GONE);

                    Activity activity = (Activity)mContext;

                    /* Sacamos las coordenadas de la imagen */
                    int[] screenLocation = new int[2];
                    mProductImageView.getLocationInWindow(screenLocation);

                    ColorVariant color = mProduct.getColors().get(0);

                    /* Creamos el intent */
                    Intent intent = new Intent(mContext, ProductUI.class);

                    /* Enviamos toda la informacion necesaria para que la siguiente activity
                    * realice la animacion */
                    intent.putExtra(PACKAGE + ".Beans.ColorVariant", color)
                          .putExtra(PACKAGE + ".bitmap", mBitmapUri)
                          .putExtra(PACKAGE + ".left", screenLocation[0])
                          .putExtra(PACKAGE + ".top", screenLocation[1])
                          .putExtra(PACKAGE + ".width", mProductImageView.getWidth())
                          .putExtra(PACKAGE + ".height", mProductImageView.getHeight());

                    mContext.startActivity(intent);

                    /* Desactivamos las transiciones por defecto */
                    activity.overridePendingTransition(0, 0);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
        }

        /**
         * Metodo que inicializa las vistas con los datos del producto recibido, se llama cada vez que se visualiza el item.
         * @param product: producto con el que se inicializa un item.
         * @param pos: posicion del producto en la lista.
         */
        public void bindProduct(Product product, int pos)
        {
            final int position = pos;

            /* Inicializamos los TextViews */
            mTitleTextView.setText(product.getShop());
            mSubtitleTextView.setText(product.getColors().get(0).getReference());
            mNameTextView.setText(product.getName());
            mPriceTextView.setText(String.format("%.2f", product.getPrice()) + "€");

            /* Ocultamos la info, IMPORTANTE. Cosas malas pasan si no se pone */
            mProductFooterExtraView.setVisibility(View.GONE);
            mProductFooterMainView.setVisibility(View.GONE);

            /* Mostramos la view de carga */
            mLoadingView.setVisibility(View.VISIBLE);

            /* Ponemos el icono del corazon. */
            mFavImageButton.setBackgroundResource(R.drawable.ic_favorite_border_white);

            /* Cargamos la imagen usando Picasso */
            mTarget = new Target()
            {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
                {
                    // Ocultamos la View de carga, y mostramos el pie de foto.
                    mLoadingView.setVisibility(View.GONE);
                    mProductFooterMainView.setVisibility(View.VISIBLE);

                    // Reestablecemos la opacidad y el valor de la altura a WRAP_CONTENT, eliminamos el color de fondo.
                    mProductImageView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    mProductImageView.setBackgroundColor(-1);
                    mProductImageView.setAlpha(1.0f);

                    // Si la imagen es nueva, calculamos el aspect ratio y lo almacenamos el el array en la pos correspondiente.
                    if (mProductBitmapArray[position] == 0.0f)
                        mProductBitmapArray[position] = (double)bitmap.getHeight() / (double)bitmap.getWidth();

                    // Guardamos el bitmap, para asi pasarlo a ProductUI.
                    mBitmap = bitmap;

                    // Por ultimo, cargamos el Bitmap en la ImageView
                    mProductImageView.setImageBitmap(bitmap);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable)
                {
                    mLoadingView.setVisibility(View.GONE);

                    mProductImageView.setBackgroundColor(mContext.getResources()
                                                                 .getColor(android.R.color.holo_red_dark));
                    mProductImageView.setAlpha(0.2f);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable)
                {
                    // Debido a que los ViewHolder se reciclan, eliminamos el Bitmap antiguo
                    mProductImageView.setImageBitmap(null);

                    // Si esta imagen ya se ha cargado, establecemos la altura de la ImageView
                    // usando el aspect ratio almacenado en el array, si no, se carga un valor cualquiera
                    if (mProductBitmapArray[position] != 0.0f)
                        mProductImageView.getLayoutParams().height = (int)(mProductImageView.getWidth()
                                                                                * mProductBitmapArray[position]);
                    else
                        mProductImageView.getLayoutParams().height = 600;

                    // Establecemos un color de fondo y un 10% de opacidad.
                    mProductImageView.setBackgroundColor(mContext.getResources().getColor(R.color.colorText));
                    mProductImageView.setAlpha(0.1f);
                }
            };

            String url = product.getColors().get(0)
                                            .getImages()
                                            .get(0)
                                            .getPath().replaceAll(".jpg", "_Small.jpg");
            Picasso.with(mContext)
                   .load(url)
                   .into(mTarget);

            mProduct = product;
        }

        @Override
        public void onClick(View view)
        {
            /* Si se pulsa en el pie de foto */
            if (view.getId() == mProductFooterView.getId())
            {
                /* Abrimos la info extra si esta oculta */
                if (mProductFooterExtraView.getVisibility() == View.GONE)
                {
                    mProductFooterExtraView.setVisibility(View.VISIBLE);
                    mProductFooterExtraView.startAnimation(scaleUp);

                } else
                    mProductFooterExtraView.startAnimation(scaleDownFooterExtra);

            }

            /* Si se pulsa en la imagen */
            if (view.getId() == mProductImageView.getId())
            {
                // Guardamos el bitmap antes de iniciar la animacion, ya que es una operacion pesada
                mBitmapUri = getImageUri(mContext, mBitmap).toString();

                // Guardamos que producto se ha pinchado para reestablecer despues el pie de foto
                mProductClicked = this;

                mProductFooterView.startAnimation(scaleDownFooter);
            }
        }

        /**
         * Metodo que inicia una animacion para restaurar el pie de foto
         */
        private void restoreFooter()
        {
            mProductFooterView.setVisibility(View.VISIBLE);
            mProductFooterExtraView.setVisibility(View.GONE);

            mProductFooterView.startAnimation(scaleUp);
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

            String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver()
                                                                , inImage
                                                                , "Title"
                                                                , null);

            return Uri.parse(path);
        }

    } /* [END] ViewHolder */


    public ProductsGridAdapter(Context context, List<Product> productList, int total)
    {
        mContext = context;
        mProductList = productList;

        mProductBitmapArray = new double[total];
        for (int i = 0; i < total; i++)
            mProductBitmapArray[i] = 0.0f;

        mProductClicked = null;
    }

    /**
     * Metodo que actualiza la lista de productos del adapter
     * @param productList: lista con todos los productos
     */
    public void updateProductList(List<Product> productList)
    {
        mProductList = productList;
    }

    /**
     * Restauramos el pie de foto, comprobando que se haya clickado en algun producto.
     */
    public void restoreProductFooter()
    {
        if (mProductClicked != null)
        {
            mProductClicked.restoreFooter();

            mProductClicked = null;
        }
    }

    public boolean productClicked()
    {
        return (mProductClicked != null);
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
        productHolder.bindProduct(mProductList.get(pos), pos);
    }

    @Override
    public int getItemCount()
    {
        return mProductList.size();
    }
}
