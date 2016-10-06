package com.wallakoala.wallakoala.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.wallakoala.wallakoala.Activities.ProductUI;
import com.wallakoala.wallakoala.Beans.Product;
import com.wallakoala.wallakoala.Properties.Properties;
import com.wallakoala.wallakoala.R;
import com.wallakoala.wallakoala.Utils.SharedPreferencesManager;
import com.wallakoala.wallakoala.Utils.Utils;
import com.wallakoala.wallakoala.Views.LikeButtonView;

import java.util.List;

/**
 * Adapter para el grid de productos recomendados.
 * Created by Daniel Mancebo on 25/06/2016.
 */

public class RecommendedListAdapter extends RecyclerView.Adapter<RecommendedListAdapter.ProductHolder>
{
    /* Context */
    private static Context mContext;

    /* Container Views */
    private static FrameLayout mFrameLayout;

    /* SharedPreferences */
    private static SharedPreferencesManager mSharedPreferencesManager;

    /* Data */
    private static List<Product> mProductList;

    /**
     * ViewHolder del producto con todos los componentes graficos necesarios
     */
    public static class ProductHolder extends RecyclerView.ViewHolder
    {
        private boolean ERROR;
        private boolean LOADED;

        private Product mProduct;
        private Target mTarget;
        private Bitmap mBitmap;
        private String mBitmapFileName;

        private ImageView mProductImageView;

        private TextView mNameTextView, mShopTextView;

        private LikeButtonView mProductFavoriteImageButton;

        public ProductHolder(View itemView)
        {
            super(itemView);

            mProductImageView = (ImageView)itemView.findViewById(R.id.recommended_image);
            mShopTextView     = (TextView)itemView.findViewById(R.id.recommended_shop);
            mNameTextView     = (TextView)itemView.findViewById(R.id.recommended_name);

            mProductFavoriteImageButton = (LikeButtonView)itemView.findViewById(R.id.recommended_item_favorite);

            mProductFavoriteImageButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    mProductFavoriteImageButton.startAnimation();
                }
            });
        }

        /**
         * Metodo que inicializa las vistas con los datos del producto recibido, se llama cada vez que se visualiza el item.
         * @param product: producto con el que se inicializa un item.
         */
        public void bindProduct(Product product)
        {
            mProduct = product;

            mProductImageView.setImageBitmap(null);

            /* Inicializamos los TextViews */
            mNameTextView.setText(product.getName().toUpperCase());
            mShopTextView.setText(product.getShop().toUpperCase());


            /* Inicializamos el boton de favorito */
            mProductFavoriteImageButton.changeIcon(
                    mSharedPreferencesManager.retreiveUser().getFavoriteProducts().contains(mProduct.getId()));

            /* Cargamos la imagen usando Picasso */
            mTarget = new Target()
            {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
                {
                    LOADED = true;

                    // Reestablecemos la opacidad y el valor de la altura a WRAP_CONTENT, eliminamos el color de fondo.
                    mProductImageView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    mProductImageView.setBackgroundColor(-1);
                    mProductImageView.setAlpha(1.0f);

                    // Guardamos el bitmap, para asi pasarlo a ProductUI.
                    mBitmap = bitmap;

                    // Por ultimo, cargamos el Bitmap en la ImageView
                    mProductImageView.setImageBitmap(bitmap);

                    Animation fadeOut = new AlphaAnimation(0, 1);
                    fadeOut.setInterpolator(new AccelerateInterpolator());
                    fadeOut.setDuration(250);
                    mProductImageView.startAnimation(fadeOut);
                }

                @Override
                @SuppressWarnings("deprecation")
                public void onBitmapFailed(Drawable errorDrawable)
                {
                    ERROR = true;

                    mProductImageView.setBackgroundColor(mContext.getResources()
                                                            .getColor(android.R.color.holo_red_dark));
                    mProductImageView.setAlpha(0.2f);
                }

                @Override
                @SuppressWarnings("deprecation")
                public void onPrepareLoad(Drawable placeHolderDrawable)
                {
                    // Debido a que los ViewHolder se reciclan, eliminamos el Bitmap antiguo
                    mProductImageView.setImageBitmap(null);

                    // Establecemos la altura usando el AspectRatio del producto.
                    mProductImageView.getLayoutParams().height =
                            (int) (mProductImageView.getWidth() * mProduct.getAspectRatio());

                    // Establecemos un color de fondo y un 10% de opacidad.
                    mProductImageView.setBackgroundColor(mContext.getResources().getColor(R.color.colorText));
                    mProductImageView.setAlpha(0.1f);
                }
            };

            mProductImageView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if (!ERROR && LOADED)
                    {
                        // Guardamos el bitmap antes de iniciar la animacion, ya que es una operacion pesada
                        // y ralentiza la animacion
                        mBitmapFileName = Utils.saveImage(mContext, mBitmap, getAdapterPosition(), Properties.TAG);

                        if (mBitmapFileName != null)
                        {
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

                            /* Reseteamos el nombre del fichero */
                            mBitmapFileName = null;

                            mContext.startActivity(intent);

                            /* Desactivamos las transiciones por defecto */
                            activity.overridePendingTransition(0, 0);

                        } else {
                            Snackbar.make(mFrameLayout, "Ops, algo ha ido mal", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                }
            });

            String imageFile = product.getShop() + "_"
                    + product.getSection() + "_"
                    + product.getColors().get(0).getReference() + "_"
                    + product.getColors().get(0).getColorName() + "_0_Small.jpg";

            String url = Utils.fixUrl(Properties.SERVER_URL + Properties.IMAGES_PATH + product.getShop() + "/" + imageFile);

            Picasso.with(mContext)
                    .load(url)
                    .into(mTarget);

            mProduct = product;
        }

    }/* [END] ViewHolder */

    /**
     * Constructor del Adapter
     * @param context: contexto (ProductsUI)
     * @param productList: lista de productos
     * @param frameLayout: layout necesario para animar la SnackBar
     */
    public RecommendedListAdapter(Context context, List<Product> productList, FrameLayout frameLayout)
    {
        mContext = context;
        mProductList = productList;

        mFrameLayout = frameLayout;

        mSharedPreferencesManager = new SharedPreferencesManager(mContext);
    }

    public void updateProductList(List<Product> productList)
    {
        mProductList = productList;
    }

    @Override
    public ProductHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.product_recommended
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
