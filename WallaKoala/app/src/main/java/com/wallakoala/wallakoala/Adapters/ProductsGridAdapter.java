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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.wallakoala.wallakoala.Activities.ProductUI;
import com.wallakoala.wallakoala.Beans.Product;
import com.wallakoala.wallakoala.Beans.User;
import com.wallakoala.wallakoala.Properties.Properties;
import com.wallakoala.wallakoala.R;
import com.wallakoala.wallakoala.Singletons.VolleySingleton;
import com.wallakoala.wallakoala.Utils.SharedPreferencesManager;
import com.wallakoala.wallakoala.Utils.Utils;
import com.wallakoala.wallakoala.Views.LikeButtonView;

import java.util.List;
import java.util.Set;

/**
 * @class Adapter para el grid de productos.
 * Created by Daniel Mancebo Aldea on 10/11/2015.
 */

public class ProductsGridAdapter extends RecyclerView.Adapter<ProductsGridAdapter.ProductHolder>
{
    /* Context */
    private static Context mContext;

    /* Container Views */
    private static FrameLayout mFrameLayout;

    /* Data */
    private static List<Product> mProductList;
    private static double[] mProductBitmapArray;
    private static ProductHolder mProductClicked;

    /* SharedPreferences */
    private static SharedPreferencesManager mSharedPreferencesManager;

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
        private LikeButtonView mProductFavoriteImageButton;
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

            mProductFavoriteImageButton = (LikeButtonView)itemView.findViewById(R.id.product_item_favorite);

            mProductFooterView      = itemView.findViewById(R.id.footer);
            mProductFooterExtraView = itemView.findViewById(R.id.extraInfo);
            mProductFooterMainView  = itemView.findViewById(R.id.mainFooter);

            mProductFooterView.setOnClickListener(this);
            mProductImageView.setOnClickListener(this);

            scaleUp              = AnimationUtils.loadAnimation(mContext, R.anim.scale_up_animation);
            scaleDownFooterExtra = AnimationUtils.loadAnimation(mContext, R.anim.scale_down_animation);
            scaleDownFooter      = AnimationUtils.loadAnimation(mContext, R.anim.scale_down_animation);

            mProductFavoriteImageButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    final User user = mSharedPreferencesManager.retreiveUser();
                    final long id = user.getId();

                    final String fixedURL = Utils.fixUrl(Properties.SERVER_URL + ":" + Properties.SERVER_SPRING_PORT
                                    + "/users/" + id + "/" + mProduct.getId() + "/" + Properties.ACTION_FAVORITE);

                    Log.d(Properties.TAG, "Conectando con: " + fixedURL + " para anadir/quitar un producto de favoritos");

                    final StringRequest stringRequest = new StringRequest(Request.Method.GET
                        , fixedURL
                        , new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response)
                            {
                                Log.d(Properties.TAG, "Respuesta del servidor: " + response);

                                if (!response.equals(Properties.PRODUCT_NOT_FOUND) || !response.equals(Properties.USER_NOT_FOUND))
                                {
                                    // Si contiene el producto, es que lo ha quitado de favoritos.
                                    if (user.getFavoriteProducts().contains(mProduct.getId()))
                                    {
                                        user.getFavoriteProducts().remove(mProduct.getId());

                                    } else {
                                        user.getFavoriteProducts().add(mProduct.getId());
                                    }

                                    mSharedPreferencesManager.insertUser(user);
                                }
                            }
                        }
                        , new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error)
                            {}
                        });

                    VolleySingleton.getInstance(mContext).addToRequestQueue(stringRequest);

                    mProductFavoriteImageButton.startAnimation();
                }
            });

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
        @SuppressWarnings("deprecation")
        public void bindProduct(final Product product, final int pos)
        {
            mProduct = product;

            mProductImageView.setImageBitmap(null);
            ERROR = false;
            LOADED = false;

            /* Inicializamos los TextViews */
            String name = product.getName().substring(0, 1) + product.getName().split(" ")[0].substring(1).toLowerCase();
            mTitleTextView.setText(name);
            mSubtitleTextView.setText(product.getShop());
            mNameTextView.setText(product.getName());
            mPriceTextView.setText(Utils.priceToString(product.getPrice()));

            /* Ocultamos la info, IMPORTANTE. Cosas malas pasan si no se pone */
            mProductFooterExtraView.setVisibility(View.GONE);
            mProductFooterMainView.setVisibility(View.GONE);
            mProductFavoriteImageButton.setVisibility(View.GONE);

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

                    // Mostramos el pie de foto y el boton de favorito
                    mProductFooterMainView.setVisibility(View.VISIBLE);
                    mProductFavoriteImageButton.setVisibility(View.VISIBLE);

                    // Reestablecemos la opacidad y el valor de la altura a WRAP_CONTENT, eliminamos el color de fondo.
                    mProductImageView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    mProductImageView.setBackgroundColor(-1);
                    mProductImageView.setAlpha(1.0f);

                    // Si la imagen es nueva, calculamos el aspect ratio y lo almacenamos el el array en la pos correspondiente.
                    if (mProductBitmapArray[pos] == 0.0f)
                    {
                        mProductBitmapArray[pos] = (double)bitmap.getHeight() / (double)bitmap.getWidth();
                    }

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
                    if (mProductBitmapArray[pos] != 0.0f)
                    {
                        mProductImageView.getLayoutParams().height =
                                (int) (mProductImageView.getWidth() * mProductBitmapArray[pos]);

                    } else{
                        mProductImageView.getLayoutParams().height = 600;
                    }

                    // Establecemos un color de fondo y un 10% de opacidad.
                    mProductImageView.setBackgroundColor(mContext.getResources().getColor(R.color.colorText));
                    mProductImageView.setAlpha(0.1f);
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
                   .into(mTarget);
        }

        @Override
        public void onClick(final View view)
        {
            /* Si se pulsa en el pie de foto */
            if (view.getId() == mProductFooterView.getId())
            {
                /* Abrimos la info extra si esta oculta */
                if (mProductFooterExtraView.getVisibility() == View.GONE)
                {
                    mProductFooterExtraView.setVisibility(View.VISIBLE);
                    mProductFooterExtraView.startAnimation(scaleUp);

                } else {
                    mProductFooterExtraView.startAnimation(scaleDownFooterExtra);
                }
            }

            /* Si se pulsa en la imagen */
            if (view.getId() == mProductImageView.getId())
            {
                if (!ERROR && LOADED && mProductClicked == null)
                {
                    // Guardamos el bitmap antes de iniciar la animacion, ya que es una operacion pesada
                    // y ralentiza la animacion
                    mBitmapFileName = Utils.saveImage(mContext, mBitmap, getAdapterPosition(), Properties.TAG);

                    if (mBitmapFileName != null)
                    {
                        // Guardamos que producto se ha pinchado para reestablecer despues el pie de foto
                        mProductClicked = this;

                        mProductFooterView.startAnimation(scaleDownFooter);

                    } else {
                        Snackbar.make(mFrameLayout, "Ops, algo ha ido mal", Snackbar.LENGTH_SHORT).show();
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
     * @param frameLayout: layout necesario para animar la SnackBar
     */
    public ProductsGridAdapter(final Context context
            , final List<Product> productList
            , final int total
            , final FrameLayout frameLayout)
    {
        mContext = context;
        mProductList = productList;

        mFrameLayout = frameLayout;

        mSharedPreferencesManager = new SharedPreferencesManager(mContext);

        mProductBitmapArray = new double[total];
        for (int i = 0; i < total; i++)
        {
            mProductBitmapArray[i] = 0.0f;
        }

        mProductClicked = null;
    }

    /**
     * Metodo que actualiza la lista de productos del adapter
     * @param productList: lista con todos los productos
     */
    public void updateProductList(final List<Product> productList)
    {
        mProductList = productList;

        if (mProductList.size() > mProductBitmapArray.length)
        {
            double[] aux = new double[mProductList.size()];
            System.arraycopy(mProductBitmapArray, 0, aux, 0, mProductBitmapArray.length);

            for (int i = mProductBitmapArray.length; i < aux.length; i++)
            {
                aux[i] = 0.0f;
            }

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
        productHolder.bindProduct(mProductList.get(pos), pos);
    }

    @Override
    public int getItemCount()
    {
        return mProductList.size();
    }

}
