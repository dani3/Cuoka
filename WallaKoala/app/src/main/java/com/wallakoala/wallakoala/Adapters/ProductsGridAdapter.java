package com.wallakoala.wallakoala.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.wallakoala.wallakoala.Activities.ProductUI;
import com.wallakoala.wallakoala.Beans.Product;
import com.wallakoala.wallakoala.Properties.Properties;
import com.wallakoala.wallakoala.R;
import com.wallakoala.wallakoala.Singletons.RestClientSingleton;
import com.wallakoala.wallakoala.Utils.SharedPreferencesManager;
import com.wallakoala.wallakoala.Utils.Utils;
import com.wallakoala.wallakoala.Views.LikeButtonView;

import java.util.List;

/**
 * Adapter para el grid de productos.
 * Created by Daniel Mancebo Aldea on 10/11/2015.
 */

public class ProductsGridAdapter extends RecyclerView.Adapter<ProductsGridAdapter.ProductHolder>
{
    /* Context */
    private Context mContext;

    /* Container Views */
    private FrameLayout mFrameLayout;

    /* Data */
    private List<Product> mProductList;
    private ProductHolder mProductClicked;

    /* SharedPreferences */
    private SharedPreferencesManager mSharedPreferencesManager;

    /**
     * ViewHolder del producto con todos los componentes graficos necesarios
     */
    public class ProductHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private boolean ERROR;
        private boolean LOADED;

        private Product mProduct;
        private Target mTarget;
        private Bitmap mBitmap;
        private String mBitmapFileName;

        private ImageView mProductImageView;
        private LikeButtonView mProductFavoriteImageButton;
        private View mProductFooterView, mProductFooterMainView;
        private TextView mTitleTextView, mSubtitleTextView, mPriceTextView;

        private Animation scaleUp, scaleDownFooter;

        public ProductHolder(View itemView)
        {
            super(itemView);

            mProductImageView = (ImageView)itemView.findViewById(R.id.grid_image);
            mTitleTextView    = (TextView)itemView.findViewById(R.id.footer_title);
            mSubtitleTextView = (TextView)itemView.findViewById(R.id.footer_subtitle);
            mPriceTextView    = (TextView)itemView.findViewById(R.id.footer_price);

            mProductFavoriteImageButton = (LikeButtonView)itemView.findViewById(R.id.product_item_favorite);

            mProductFooterView      = itemView.findViewById(R.id.footer);
            mProductFooterMainView  = itemView.findViewById(R.id.mainFooter);

            mProductFooterView.setOnClickListener(this);
            mProductImageView.setOnClickListener(this);

            scaleUp         = AnimationUtils.loadAnimation(mContext, R.anim.scale_up_animation);
            scaleDownFooter = AnimationUtils.loadAnimation(mContext, R.anim.scale_down_animation);

            mProductFavoriteImageButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (!mProductFavoriteImageButton.ANIMATING)
                    {
                        Log.d(Properties.TAG, "[PRODUCTS_GRID_ADAPTER] Se hace click -> Favorito");

                        RestClientSingleton.sendFavoriteProduct(mContext, mProduct);

                        mProductFavoriteImageButton.startAnimation();
                    }
                }
            });

            scaleDownFooter.setAnimationListener(new Animation.AnimationListener()
            {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation)
                {
                    Log.d(Properties.TAG, "[PRODUCTS_GRID_ADAPTER] Se crea la activity -> ProductUI");

                    mProductFooterView.setVisibility(View.GONE);

                    Activity activity = (Activity) mContext;

                    // Sacamos las coordenadas de la imagen y del corazon
                    int[] imageScreenLocation = new int[2];
                    mProductImageView.getLocationInWindow(imageScreenLocation);

                    int[] favoriteScreenLocation = new int[2];
                    mProductFavoriteImageButton.getLocationOnScreen(favoriteScreenLocation);

                    // Creamos el intent
                    Intent intent = new Intent(mContext, ProductUI.class);

                    // Enviamos toda la informacion necesaria para que la siguiente activity realice la animacion
                    intent.putExtra(Properties.PACKAGE + ".Beans.Product", mProduct)
                          .putExtra(Properties.PACKAGE + ".bitmap", mBitmapFileName)
                          .putExtra(Properties.PACKAGE + ".leftFav", favoriteScreenLocation[0])
                          .putExtra(Properties.PACKAGE + ".topFav", favoriteScreenLocation[1])
                          .putExtra(Properties.PACKAGE + ".widthFav", mProductFavoriteImageButton.getWidth())
                          .putExtra(Properties.PACKAGE + ".heightFav", mProductFavoriteImageButton.getHeight())
                          .putExtra(Properties.PACKAGE + ".left", imageScreenLocation[0])
                          .putExtra(Properties.PACKAGE + ".top", imageScreenLocation[1])
                          .putExtra(Properties.PACKAGE + ".width", mProductImageView.getWidth())
                          .putExtra(Properties.PACKAGE + ".height", mProductImageView.getHeight());

                    // Reseteamos el nombre del fichero
                    mBitmapFileName = null;

                    mContext.startActivity(intent);

                    // Desactivamos las transiciones por defecto
                    activity.overridePendingTransition(0, 0);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
        }

        /**
         * Metodo que inicializa las vistas con los datos del producto recibido, se llama cada vez que se visualiza el item.
         * @param product: producto con el que se inicializa un item.
         */
        @SuppressWarnings("deprecation")
        public void bindProduct(final Product product)
        {
            mProduct = product;

            ERROR = false;
            LOADED = false;

            // Inicializamos los TextViews.
            String name = product.getName().substring(0, 1) + product.getName().split(" ")[0].substring(1).toLowerCase();
            mTitleTextView.setText(name);
            mSubtitleTextView.setText(product.getShop());
            mPriceTextView.setText(Utils.priceToString(product.getPrice()));

            // Ocultamos la info, IMPORTANTE. Cosas malas pasan si no se pone.
            mProductFooterMainView.setVisibility(View.GONE);
            mProductFavoriteImageButton.setVisibility(View.GONE);

            // Inicializamos el boton de favorito.
            mProductFavoriteImageButton.changeIcon(
                    mSharedPreferencesManager.retreiveUser().getFavoriteProducts().contains(mProduct.getId()));

            // Cargamos la imagen usando Picasso.
            mTarget = new Target()
            {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
                {
                    LOADED = true;

                    // Mostramos el pie de foto y el boton de favorito
                    mProductFooterMainView.setVisibility(View.VISIBLE);
                    mProductFavoriteImageButton.setVisibility(View.VISIBLE);

                    // Guardamos el bitmap, para asi pasarlo a ProductUI.
                    mBitmap = bitmap;

                    // Por ultimo, cargamos el Bitmap en la ImageView.
                    mProductImageView.setImageBitmap(bitmap);

                    // Reestablecemos la opacidad y el valor de la altura a WRAP_CONTENT, eliminamos el color de fondo.
                    mProductImageView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    mProductImageView.setBackgroundColor(-1);
                    mProductImageView.setAlpha(1.0f);

                    Animation fadeOut = new AlphaAnimation(0, 1);
                    fadeOut.setInterpolator(new AccelerateInterpolator());
                    fadeOut.setDuration(250);
                    mProductImageView.startAnimation(fadeOut);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable)
                {
                    ERROR = true;

                    Log.e(Properties.TAG, "[PRODUCTS_GRID_ADAPTER] Error cargando imagen: "
                            + product.getShop() + " " + product.getSection() + " " + product.getColors().get(0).getReference());

                    mProductImageView.setBackgroundColor(
                            mContext.getResources().getColor(android.R.color.holo_red_dark));
                    mProductImageView.setAlpha(0.2f);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable)
                {
                    // Debido a que los ViewHolder se reciclan, eliminamos el Bitmap antiguo.
                    mProductImageView.setImageBitmap(null);

                    // Establecemos la altura usando el AspectRatio del producto.
                    mProductImageView.getLayoutParams().height =
                            (int) (mProductImageView.getWidth() * mProduct.getAspectRatio());

                    // Establecemos un color de fondo aleatorio y un 25% de opacidad.
                    mProductImageView.setBackgroundColor(
                            mContext.getResources().getColor(R.color.colorAccent));
                    mProductImageView.setAlpha(0.25f);
                }
            };

            final String imageFile = product.getShop() + "_"
                        + product.getSection() + "_"
                        + product.getColors().get(0).getReference() + "_"
                        + product.getColors().get(0).getColorName() + "_0_Small.jpg";

            final String url = Utils.fixUrl(
                    Properties.SERVER_URL + Properties.IMAGES_PATH + product.getShop() + "/" + imageFile);

            Picasso.with(mContext)
                   .load(url)
                   .noFade()
                   .into(mTarget);
        }

        @Override
        public void onClick(final View view)
        {
            if (!ERROR && LOADED && mProductClicked == null)
            {
                Log.d(Properties.TAG, "[PRODUCTS_GRID_ADAPTER] Se hace click -> Imagen");

                // Guardamos el bitmap antes de iniciar la animacion, ya que es una operacion pesada
                // y puede ralentizar la animacion.
                mBitmapFileName = Utils.saveImage(mContext, mBitmap, getAdapterPosition());

                if (mBitmapFileName != null)
                {
                    // Guardamos que producto se ha pinchado para reestablecer despues el pie de foto
                    mProductClicked = this;

                    mProductFooterView.startAnimation(scaleDownFooter);

                } else {
                    Snackbar.make(mFrameLayout
                            , mContext.getResources().getString(R.string.error_message)
                            , Snackbar.LENGTH_SHORT).show();
                }
            }
        }

        /**
         * Metodo que inicia una animacion para restaurar el pie de foto
         */
        private void restoreFooter()
        {
            mProductFooterView.setVisibility(View.VISIBLE);

            mProductFooterView.startAnimation(scaleUp);
        }

        /**
         * Metodo que cambia el icono de favoritos si es necesario.
         */
        private void notifyFavoriteChanged()
        {
            mProductFavoriteImageButton.changeIcon(
                    mSharedPreferencesManager.retreiveUser().getFavoriteProducts().contains(mProduct.getId()));
        }

    } /* [END] ViewHolder */

    /**
     * Constructor del Adapter
     * @param context: contexto (ProductsUI)
     * @param productList: lista de productos
     * @param frameLayout: layout necesario para animar la SnackBar
     */
    public ProductsGridAdapter(Context context
                        , List<Product> productList
                        , FrameLayout frameLayout)
    {
        mContext = context;
        mProductList = productList;

        mFrameLayout = frameLayout;

        mSharedPreferencesManager = new SharedPreferencesManager(mContext);

        mProductClicked = null;
    }

    /**
     * Metodo que actualiza la lista de productos del adapter
     * @param productList: lista con todos los productos
     */
    public void updateProductList(final List<Product> productList)
    {
        mProductList = productList;
    }

    /**
     * Metodo para restaurar el pie de foto, comprobando que se haya clickado en algun producto.
     * Tambien comprueba si hay que cambiar el icono de favoritos (por si se cambia desde ProductUI)
     */
    public void restore()
    {
        if (mProductClicked != null)
        {
            mProductClicked.restoreFooter();
            mProductClicked.notifyFavoriteChanged();

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
    public ProductHolder onCreateViewHolder(final ViewGroup viewGroup, final int viewType)
    {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                                      .inflate(R.layout.product_item_grid
                                                    , viewGroup
                                                    , false );

        return new ProductHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ProductHolder productHolder, final int pos)
    {
        productHolder.bindProduct(mProductList.get(pos));
    }

    @Override
    public int getItemCount()
    {
        return mProductList.size();
    }
}
