package com.wallakoala.wallakoala.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.wallakoala.wallakoala.Activities.ProductUI;
import com.wallakoala.wallakoala.Beans.Product;
import com.wallakoala.wallakoala.R;
import com.wallakoala.wallakoala.Utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
    protected static final String SERVER_URL = "http://cuoka-ws.cloudapp.net";
    protected static final String IMAGES_PATH = "/images/products/";

    /* Context */
    private static Context mContext;

    /* Container Views */
    private static CoordinatorLayout mCoordinatorLayout;

    /* Data */
    private static List<Product> mProductList;
    private static double[] mProductBitmapArray;
    private static ProductHolder mProductClicked;

    /**
     * ViewHolder del producto con todos los componentes graficos necesarios
     */
    public static class ProductHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private boolean ERROR;
        private boolean LOADED;

        private Product mProduct;
        private Target mTarget;
        private Bitmap mBitmap;
        private String mBitmapFileName;

        private ImageView mProductImageView;
        private ImageButton mProductFavoriteImageButton;
        private View mProductFooterView, mProductFooterExtraView, mProductFooterMainView;
        private TextView mTitleTextView, mSubtitleTextView, mNameTextView, mPriceTextView;

        private Animation scaleUp, scaleDownFooterExtra, scaleDownFooter;

        public ProductHolder(View itemView)
        {
            super(itemView);

            mProductImageView = (ImageView)itemView.findViewById(R.id.grid_image);
            mTitleTextView    = (TextView)itemView.findViewById(R.id.footer_title);
            mSubtitleTextView = (TextView)itemView.findViewById(R.id.footer_subtitle);
            mNameTextView     = (TextView)itemView.findViewById(R.id.name);
            mPriceTextView    = (TextView)itemView.findViewById(R.id.footer_price);

            mProductFavoriteImageButton = (ImageButton)itemView.findViewById(R.id.product_item_favorite);

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
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mProductFooterExtraView.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
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

                    /* Sacamos las coordenadas de la imagen y del corazon */
                    int[] imageScreenLocation = new int[2];
                    mProductImageView.getLocationInWindow(imageScreenLocation);

                    int[] favoriteScreenLocation = new int[2];
                    mProductFavoriteImageButton.getLocationOnScreen(favoriteScreenLocation);

                    /* Creamos el intent */
                    Intent intent = new Intent(mContext, ProductUI.class);

                    /* Enviamos toda la informacion necesaria para que la siguiente activity
                    * realice la animacion */
                    intent.putExtra(PACKAGE + ".Beans.Product", mProduct)
                          .putExtra(PACKAGE + ".bitmap", mBitmapFileName)
                          .putExtra(PACKAGE + ".leftFav", favoriteScreenLocation[0])
                          .putExtra(PACKAGE + ".topFav", favoriteScreenLocation[1])
                          .putExtra(PACKAGE + ".widthFav", mProductFavoriteImageButton.getWidth())
                          .putExtra(PACKAGE + ".heightFav", mProductFavoriteImageButton.getHeight())
                          .putExtra(PACKAGE + ".left", imageScreenLocation[0])
                          .putExtra(PACKAGE + ".top", imageScreenLocation[1])
                          .putExtra(PACKAGE + ".width", mProductImageView.getWidth())
                          .putExtra(PACKAGE + ".height", mProductImageView.getHeight());

                    /* Reseteamos el nombre del fichero */
                    mBitmapFileName = null;

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

            mProductImageView.setImageBitmap(null);
            ERROR = false;
            LOADED = false;

            /* Inicializamos los TextViews */
            mTitleTextView.setText(product.getSection());
            mSubtitleTextView.setText(product.getShop());
            mNameTextView.setText(product.getName());
            mPriceTextView.setText(Utils.priceToString(product.getPrice()));

            /* Ocultamos la info, IMPORTANTE. Cosas malas pasan si no se pone */
            mProductFooterExtraView.setVisibility(View.GONE);
            mProductFooterMainView.setVisibility(View.GONE);
            mProductFavoriteImageButton.setVisibility(View.GONE);

            /* Cargamos la imagen usando Picasso */
            mTarget = new Target()
            {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
                {
                    LOADED = true;

                    // Mostramos el pie de foto y el boton de favorito
                    mProductFooterMainView.setVisibility(View.VISIBLE);
                    mProductFavoriteImageButton.setVisibility(View.VISIBLE);

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
                    ERROR = true;

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

            String imageFile = product.getShop() + "_"
                        + product.getSection() + "_"
                        + product.getColors().get(0).getReference() + "_"
                        + product.getColors().get(0).getColorName() + "_0_Small.jpg";

            String url = Utils.fixUrl(SERVER_URL + IMAGES_PATH + product.getShop() + "/" + imageFile);

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
                if (!ERROR && LOADED && mProductClicked == null)
                {
                    // Guardamos el bitmap antes de iniciar la animacion, ya que es una operacion pesada
                    // y ralentiza la animacion
                    mBitmapFileName = Utils.saveImage(mContext, mBitmap, getAdapterPosition(), TAG);

                    if (mBitmapFileName != null)
                    {
                        // Guardamos que producto se ha pinchado para reestablecer despues el pie de foto
                        mProductClicked = this;

                        mProductFooterView.startAnimation(scaleDownFooter);

                    } else {
                        Snackbar.make(mCoordinatorLayout, "Ops, algo ha ido mal", Snackbar.LENGTH_SHORT).show();
                    }
                }
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

    } /* [END] ViewHolder */

    /**
     * Constructor del Adapter
     * @param context: contexto (ProductsUI)
     * @param productList: lista de productos
     * @param total: total de productos, necesario para inicializar el array de ratios
     * @param coordinatorLayout: layout necesario para animar la SnackBar
     */
    public ProductsGridAdapter(Context context, List<Product> productList, int total, CoordinatorLayout coordinatorLayout)
    {
        mContext = context;
        mProductList = productList;

        mCoordinatorLayout = coordinatorLayout;

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

        if (mProductList.size() > mProductBitmapArray.length)
        {
            double[] aux = new double[mProductList.size()];
            for (int i = 0; i < mProductBitmapArray.length; i++)
                aux[i] = mProductBitmapArray[i];

            for (int i = mProductBitmapArray.length; i < aux.length; i++)
                aux[i] = 0.0f;

            mProductBitmapArray = aux;
        }
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

    /**
     * Metodo que indica si se ha clickado en algun producto
     * @return true si se ha clickado en algun producto
     */
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
