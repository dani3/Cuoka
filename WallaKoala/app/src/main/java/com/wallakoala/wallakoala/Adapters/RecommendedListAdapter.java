package com.wallakoala.wallakoala.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.wallakoala.wallakoala.Activities.ProductUI;
import com.wallakoala.wallakoala.Beans.ColorVariant;
import com.wallakoala.wallakoala.Beans.Product;
import com.wallakoala.wallakoala.Properties.Properties;
import com.wallakoala.wallakoala.R;
import com.wallakoala.wallakoala.Singletons.RestClientSingleton;
import com.wallakoala.wallakoala.Utils.SharedPreferencesManager;
import com.wallakoala.wallakoala.Utils.Utils;
import com.wallakoala.wallakoala.Views.FlipLayout;
import com.wallakoala.wallakoala.Views.LikeButtonView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Adapter para el grid de productos recomendados.
 * Created by Daniel Mancebo on 25/06/2016.
 */

public class RecommendedListAdapter extends RecyclerView.Adapter<RecommendedListAdapter.ProductHolder>
{
    /* Context */
    private Context mContext;

    /* Container Views */
    private FrameLayout mFrameLayout;

    /* SharedPreferences */
    private SharedPreferencesManager mSharedPreferencesManager;

    /* Data */
    private List<Product> mProductList;
    private ProductHolder mProductClicked;
    private boolean[] mItemsFlipped;

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

        private ViewGroup mIconList;
        private CircleImageView[] mIconViews;

        private FlipLayout mFlippableView;

        private ImageView mProductImageView;
        private View mImageContainer;

        private TextView mNameTextView, mShopTextView, mPriceTextView, mDescriptionTextView;

        private LikeButtonView mProductFavoriteImageButton;

        public ProductHolder(View itemView)
        {
            super(itemView);

            mProductImageView    = (ImageView)itemView.findViewById(R.id.recommended_image);
            mShopTextView        = (TextView)itemView.findViewById(R.id.recommended_shop);
            mNameTextView        = (TextView)itemView.findViewById(R.id.recommended_name);
            mPriceTextView       = (TextView)itemView.findViewById(R.id.recommended_price);
            mDescriptionTextView = (TextView)itemView.findViewById(R.id.recommended_description);
            mIconList            = (ViewGroup)itemView.findViewById(R.id.recommended_icons_list);
            mFlippableView       = (FlipLayout)itemView.findViewById(R.id.flippable_view);

            mImageContainer = itemView.findViewById(R.id.image_container);

            mProductImageView.setOnClickListener(this);
            mFlippableView.setOnClickListener(this);

            mProductFavoriteImageButton = (LikeButtonView)itemView.findViewById(R.id.recommended_item_favorite);

            mProductFavoriteImageButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if (!mProductFavoriteImageButton.ANIMATING)
                    {
                        RestClientSingleton.sendFavoriteProduct(mContext, mProduct);

                        mProductFavoriteImageButton.startAnimation();
                    }
                }
            });
        }

        /**
         * Metodo que inicializa las vistas con los datos del producto recibido, se llama cada vez que se visualiza el item.
         * @param product: producto con el que se inicializa un item.
         */
        @SuppressWarnings("deprecation")
        public void bindProduct(Product product)
        {
            mProduct = product;

            loadColors(product);

            // Reinicializamos el bitmap de la imagen
            mProductImageView.setImageBitmap(null);

            boolean emptyDescription = (mProduct.getDescription() == null || mProduct.getDescription().isEmpty());
            String description = "<b>Descripción: " +  (emptyDescription ? "No disponible" : mProduct.getDescription() + "</b>");

            // Inicializamos los TextViews
            mNameTextView.setText(product.getName().toUpperCase());
            mShopTextView.setText(product.getShop().toUpperCase());
            mPriceTextView.setText(Utils.priceToString(product.getPrice()));
            mDescriptionTextView.setText(Html.fromHtml(description));

            // Inicializamos el boton de favorito
            mProductFavoriteImageButton.changeIcon(
                    mSharedPreferencesManager.retreiveUser().getFavoriteProducts().contains(mProduct.getId()));

            mFlippableView.setFlipped(mItemsFlipped[this.getAdapterPosition()]);

            // Cargamos la imagen usando Picasso
            mTarget = new Target()
            {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
                {
                    LOADED = true;

                    // Guardamos el bitmap, para asi pasarlo a ProductUI.
                    mBitmap = bitmap;

                    // Por ultimo, cargamos el Bitmap en la ImageView
                    mProductImageView.setImageBitmap(bitmap);

                    // Reestablecemos la opacidad y el valor de la altura a WRAP_CONTENT, eliminamos el color de fondo.
                    mImageContainer.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    mProductImageView.setBackgroundColor(-1);
                    mProductImageView.setAlpha(1.0f);

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
                    mImageContainer.getLayoutParams().height =
                            (int) (mProductImageView.getWidth() * mProduct.getAspectRatio());

                    // Establecemos un color de fondo y un 25% de opacidad.
                    mProductImageView.setBackgroundColor(
                            mContext.getResources().getColor(R.color.colorAccent));
                    mProductImageView.setAlpha(0.25f);
                }
            };

            String imageFile = product.getShop() + "_"
                    + product.getSection() + "_"
                    + product.getColors().get(0).getReference() + "_"
                    + product.getColors().get(0).getColorName() + "_0_Small.jpg";

            String url = Utils.fixUrl(
                    Properties.SERVER_URL + Properties.IMAGES_PATH + product.getShop() + "/" + imageFile);

            Picasso.with(mContext)
                   .load(url)
                   .noFade()
                   .into(mTarget);

            mProduct = product;
        }

        /**
         * Metodo que inicializa la lista de colores.
         * @param product: producto.
         */
        @SuppressLint("InflateParams")
        private void loadColors(Product product)
        {
            // Eliminamos todos los iconos anteriores.
            mIconList.removeAllViews();

            // Creamos un array con todos los iconos.
            mIconViews = new CircleImageView[mProduct.getColors().size()];

            // Metemos cada icono en la lista.
            int max = (mProduct.getColors().size() > 3) ? 4 : mProduct.getColors().size();
            for (int i = 0; i < max; i++)
            {
                ColorVariant colorVariant = mProduct.getColors().get(i);

                // Inflamos la vista con el icono.
                LayoutInflater mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                mIconViews[i] = (CircleImageView) mInflater.inflate(
                        R.layout.aux_recommended_color_icon, null).findViewById(R.id.recommended_color_icon);

                // Seteamos los margenes laterales para que no queden pegados unos con otros.
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)mIconViews[i].getLayoutParams();
                params.setMargins(8, params.topMargin, 8, params.bottomMargin);
                mIconViews[i].setLayoutParams(params);

                // Obtenemos la url del icono del color.
                String url = Utils.getColorUrl(colorVariant, product.getShop(), product.getSection());
                Log.d(Properties.TAG, url);

                Picasso.with(mContext)
                        .load(url)
                        .into(mIconViews[i]);

                // Eliminamos el padre de la vista del icono.
                ((ViewGroup)mIconViews[i].getParent()).removeView(mIconViews[i]);
                // Lo añadimos a la lista de iconos.
                mIconList.addView(mIconViews[i]);
            }
        }

        /**
         * Metodo que cambia el icono de favoritos si es necesario.
         */
        private void notifyFavoriteChanged()
        {
            mProductFavoriteImageButton.changeIcon(
                    mSharedPreferencesManager.retreiveUser().getFavoriteProducts().contains(mProduct.getId()));
        }

        @Override
        public void onClick(View v)
        {
            if (v.getId() == mFlippableView.getId())
            {
                mFlippableView.flip();

                mItemsFlipped[this.getAdapterPosition()] = !mItemsFlipped[this.getAdapterPosition()];

            } else if (v.getId() == mProductImageView.getId()) {

                if (!ERROR && LOADED && mProductClicked == null)
                {
                    mProductClicked = this;

                    // Guardamos el bitmap antes de iniciar la animacion, ya que es una operacion pesada
                    // y ralentiza la animacion
                    mBitmapFileName = Utils.saveImage(mContext, mBitmap, getAdapterPosition());

                    if (mBitmapFileName != null)
                    {
                        Activity activity = (Activity) mContext;

                        // Sacamos las coordenadas de la imagen y del corazon
                        int[] imageScreenLocation = new int[2];
                        mProductImageView.getLocationInWindow(imageScreenLocation);

                        int[] favoriteScreenLocation = new int[2];
                        mProductFavoriteImageButton.getLocationOnScreen(favoriteScreenLocation);

                        // Creamos el intent
                        Intent intent = new Intent(mContext, ProductUI.class);

                        // Enviamos toda la informacion necesaria para que la siguiente activity
                        // realice la animacion
                        intent.putExtra(Properties.PACKAGE + ".Beans.Product", mProduct)
                              .putExtra(Properties.PACKAGE + ".bitmap", mBitmapFileName)
                              .putExtra(Properties.PACKAGE + ".leftFav", favoriteScreenLocation[0])
                              .putExtra(Properties.PACKAGE + ".topFav", favoriteScreenLocation[1])
                              .putExtra(Properties.PACKAGE + ".widthFav", 0)
                              .putExtra(Properties.PACKAGE + ".heightFav", 0)
                              .putExtra(Properties.PACKAGE + ".left", imageScreenLocation[0])
                              .putExtra(Properties.PACKAGE + ".top", imageScreenLocation[1])
                              .putExtra(Properties.PACKAGE + ".width", mProductImageView.getWidth())
                              .putExtra(Properties.PACKAGE + ".height", mProductImageView.getHeight());

                        // Reseteamos el nombre del fichero
                        mBitmapFileName = null;

                        mContext.startActivity(intent);

                        // Desactivamos las transiciones por defecto
                        activity.overridePendingTransition(0, 0);

                    } else {
                        Snackbar.make(mFrameLayout
                                , mContext.getResources().getString(R.string.error_message)
                                , Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
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

        mItemsFlipped = new boolean[mProductList.size()];
        for (int i = 0; i < mItemsFlipped.length; i++)
        {
            mItemsFlipped[i] = true;
        }

        mFrameLayout = frameLayout;

        mSharedPreferencesManager = new SharedPreferencesManager(mContext);
    }

    /**
     * Metodo para restaurar el icono de favoritos, comprobando que se haya clickado en algun producto.
     * Tambien comprueba si hay que cambiar el icono de favoritos (por si se cambia desde ProductUI)
     */
    public void restore()
    {
        if (mProductClicked != null)
        {
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
    public ProductHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.product_recommended
                        , viewGroup
                        , false);

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
