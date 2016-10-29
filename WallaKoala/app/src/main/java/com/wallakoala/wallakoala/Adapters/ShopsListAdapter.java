package com.wallakoala.wallakoala.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.wallakoala.wallakoala.Beans.Product;
import com.wallakoala.wallakoala.Beans.Shop;
import com.wallakoala.wallakoala.Properties.Properties;
import com.wallakoala.wallakoala.R;
import com.wallakoala.wallakoala.Singletons.TypeFaceSingleton;
import com.wallakoala.wallakoala.Utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Adapter con para mostrar la lista de tiendas.
 * Created by Daniel Mancebo Aldea on 23/10/2016.
 */

public class ShopsListAdapter extends RecyclerView.Adapter<ShopsListAdapter.ShopHolder>
{
    /* Constants */
    private static final int ACTION_SHOP_ADDED   = 1;
    private static final int ACTION_SHOP_DELETED = 2;

    /* Context */
    private static Context mContext;

    /* Data */
    private static List<Shop> mAllShopsList;
    private static List<String> mMyShopsList;
    private static Map<String, Integer> mFavoriteMap;

    private static Drawable mRoundedAccent;
    private static Drawable mRoundedGrey;
    private static Drawable mFavoriteDrawable;
    private static Drawable mClotheDrawable;

    /* [BEGIN ViewHolder] */
    public static class ShopHolder extends RecyclerView.ViewHolder
    {
        private CircleImageView mShopLogoImageView;
        private Button mActionButton;
        private ImageView mFavOrNumberImageView;

        private TextView mNameTextView;
        private TextView mNumberTextView;

        private Target mTarget;
        private boolean mFavorite;

        @SuppressWarnings("deprecation")
        public ShopHolder(View itemView)
        {
            super(itemView);

            mShopLogoImageView    = (CircleImageView)itemView.findViewById(R.id.shops_logo);
            mActionButton         = (Button)itemView.findViewById(R.id.shops_action_button);
            mFavOrNumberImageView = (ImageView)itemView.findViewById(R.id.shops_icon);
            mNameTextView         = (TextView)itemView.findViewById(R.id.shops_name);
            mNumberTextView       = (TextView)itemView.findViewById(R.id.shops_number);

            mActionButton.setTypeface(TypeFaceSingleton.getTypeFace(mContext, "Existence-StencilLight.otf"));
        }

        /**
         * Metodo llamado cuando se va a mostrar el item.
         * @param shop: objeto de la tienda.
         */
        @SuppressWarnings("deprecation")
        public void bindShop(final Shop shop)
        {
            String logoFile = shop.getName() + "-logo.jpg";
            String fixedUrl = Utils.fixUrl(Properties.SERVER_URL + Properties.LOGOS_PATH + logoFile);

            // Comprobamos si la tienda es favorita.
            mFavorite = false;
            for (String name : mMyShopsList)
            {
                if (shop.getName().equals(name))
                {
                    mFavorite = true;
                    break;
                }
            }

            // Cambiamos el color y el texto del boton para seguir las tiendas.
            mActionButton.setBackgroundDrawable((mFavorite) ? mRoundedAccent : mRoundedGrey);
            mActionButton.setText((mFavorite) ? Html.fromHtml("<b>Siguiendo</b>") : Html.fromHtml("<b>Seguir</b>"));
            mActionButton.setTextColor((mFavorite)
                    ? mContext.getResources().getColor(R.color.colorAccent) : mContext.getResources().getColor(R.color.colorText));

            mActionButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    _actionPerformed((mFavorite) ? ACTION_SHOP_DELETED : ACTION_SHOP_ADDED, shop);
                }
            });

            // Mostramos el icono de la seccion/corazon.
            mFavOrNumberImageView.setImageDrawable((mFavorite) ? mFavoriteDrawable : mClotheDrawable);
            // Mostramos el numero de favoritos/total de productos de la tienda
            int numberOfFavorites = (mFavoriteMap.get(shop.getName()) == null) ? 0 : mFavoriteMap.get(shop.getName());
            mNumberTextView.setText((mFavorite) ? String.valueOf(numberOfFavorites) : Integer.toString(shop.getProducts()));

            // Establecemos el nombre de la tienda.
            mNameTextView.setText(shop.getName().toUpperCase());

            mTarget = new Target()
            {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
                {
                    mShopLogoImageView.setImageBitmap(bitmap);

                    Animation fadeOut = new AlphaAnimation(0, 1);
                    fadeOut.setInterpolator(new AccelerateInterpolator());
                    fadeOut.setDuration(250);
                    mShopLogoImageView.startAnimation(fadeOut);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable)
                {
                    mShopLogoImageView.setBackgroundColor(mContext.getResources()
                            .getColor(android.R.color.holo_red_dark));

                    mShopLogoImageView.setAlpha(0.2f);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable)
                {
                    mShopLogoImageView.setImageBitmap(null);
                }
            };

            Picasso.with(mContext)
                   .load(fixedUrl)
                   .noFade()
                   .into(mTarget);
        }

        /**
         * Metodo que borra/añade una tienda y realiza las animaciones.
         * @param action: accion realizada por el usuario.
         */
        @SuppressWarnings("deprecation")
        private void _actionPerformed(int action, final Shop shop)
        {
            final boolean actionDeleted = (action == ACTION_SHOP_DELETED);

            Animation implode = AnimationUtils.loadAnimation(mContext, R.anim.implode_animation);
            final Animation explode = AnimationUtils.loadAnimation(mContext, R.anim.explode_animation);

            implode.setAnimationListener(new Animation.AnimationListener()
            {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation)
                {
                    // Cambiamos el color y el texto del boton para seguir las tiendas.
                    mActionButton.setBackgroundDrawable((mFavorite) ? mRoundedAccent : mRoundedGrey);
                    mActionButton.setText((mFavorite) ? Html.fromHtml("<b>Siguiendo</b>") : Html.fromHtml("<b>Seguir</b>"));
                    mActionButton.setTextColor((mFavorite)
                            ? mContext.getResources().getColor(R.color.colorAccent) : mContext.getResources().getColor(R.color.colorText));

                    // Mostramos el icono de la seccion/corazon
                    mFavOrNumberImageView.setImageDrawable((!actionDeleted) ? mFavoriteDrawable : mClotheDrawable);
                    // Mostramos el numero de favoritos/total de productos de la tienda
                    int numberOfFavorites = (mFavoriteMap.get(shop.getName()) == null) ? 0 : mFavoriteMap.get(shop.getName());
                    mNumberTextView.setText((!actionDeleted) ? String.valueOf(numberOfFavorites) : Integer.toString(shop.getProducts()));

                    mActionButton.startAnimation(explode);
                    mFavOrNumberImageView.startAnimation(explode);
                    mNumberTextView.startAnimation(explode);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });

            // Iniciamos todas las animaciones
            mActionButton.startAnimation(implode);
            mNumberTextView.startAnimation(implode);
            mFavOrNumberImageView.startAnimation(implode);

            // Borramos/añadimos el producto a la lista
            if (actionDeleted)
            {
                mMyShopsList.remove(shop.getName());
                mFavorite = false;

            } else {
                mMyShopsList.add(shop.getName());
                mFavorite = true;
            }
        }

    } /* [END ViewHolder] */

    /**
     * Constructor del adapter de tiendas.
     * @param context: contexto.
     * @param allShopsList: lista con todas las tiendas.
     * @param myShopsList: lista de tiendas del usuario.
     */
    @SuppressWarnings("deprecation")
    public ShopsListAdapter(Context context
                    , List<Shop> allShopsList
                    , List<String> myShopsList
                    , List<Product> favoriteList)
    {
        mContext = context;
        mAllShopsList = allShopsList;
        mMyShopsList = myShopsList;

        mFavoriteMap = new HashMap<>();

        // Metemos en un mapa el numero de favoritos que tiene el usuario por tienda.
        for (Product product : favoriteList)
        {
            mFavoriteMap.put(product.getShop()
                    , (mFavoriteMap.containsKey(product.getShop())) ? mFavoriteMap.get(product.getShop()) + 1: 1);
        }

        mRoundedAccent    = mContext.getResources().getDrawable(R.drawable.rounded_button);
        mRoundedGrey      = mContext.getResources().getDrawable(R.drawable.rounded_button_grey);
        mFavoriteDrawable = mContext.getResources().getDrawable(R.drawable.ic_favorite_grey);
        mClotheDrawable   = mContext.getResources().getDrawable(R.drawable.ic_shirt);
    }

    /**
     * Metodo que actualiza la lista de todas las tiendas.
     * @param allShopList: lista con las nuevas tiendas.
     */
    public void updateShopList(List<Shop> allShopList)
    {
        mAllShopsList = allShopList;
    }

    /**
     * Metodo que devuelve la lista tiendas marcadas por el usuario.
     * @return lista de tiendas.
     */
    public List<String> getListOfShops()
    {
        return mMyShopsList;
    }

    @Override
    public ShopHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.shop_item
                        , parent
                        , false );

        return new ShopHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ShopHolder shopHolder, int pos)
    {
        shopHolder.bindShop(mAllShopsList.get(pos));
    }

    @Override
    public int getItemCount()
    {
        return mAllShopsList.size();
    }
}
