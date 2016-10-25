package com.wallakoala.wallakoala.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.wallakoala.wallakoala.Beans.Shop;
import com.wallakoala.wallakoala.Properties.Properties;
import com.wallakoala.wallakoala.R;
import com.wallakoala.wallakoala.Utils.Utils;

import java.util.List;

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

    private static Drawable mTrashDrawable;
    private static Drawable mAddDrawable;
    private static Drawable mFavoriteDrawable;
    private static Drawable mClotheDrawable;

    /* [BEGIN ViewHolder] */
    public static class ShopHolder extends RecyclerView.ViewHolder
    {
        private CircleImageView mShopLogoImageView;
        private CircleImageView mShopLogoSelectedImageView;
        private ImageButton mActionImageButton;
        private ImageView mFavOrNumberImageView;

        private TextView mNameTextView;
        private TextView mNumberTextView;

        private Target mTarget;
        private boolean mFavorite;

        @SuppressWarnings("deprecation")
        public ShopHolder(View itemView)
        {
            super(itemView);

            mShopLogoImageView         = (CircleImageView)itemView.findViewById(R.id.shops_logo);
            mShopLogoSelectedImageView = (CircleImageView)itemView.findViewById(R.id.shops_logo_selected);
            mActionImageButton         = (ImageButton)itemView.findViewById(R.id.shops_action_button);
            mFavOrNumberImageView      = (ImageView)itemView.findViewById(R.id.shops_icon);
            mNameTextView              = (TextView)itemView.findViewById(R.id.shops_name);
            mNumberTextView            = (TextView)itemView.findViewById(R.id.shops_number);
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

            mActionImageButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    _actionPerformed((mFavorite) ? ACTION_SHOP_DELETED : ACTION_SHOP_ADDED, shop);
                }
            });

            // Mostramos/ocultamos el aro de seleccionamos
            mShopLogoSelectedImageView.setVisibility((mFavorite) ? View.VISIBLE : View.INVISIBLE);
            // Mostramos el icono de añadir/eliminar
            mActionImageButton.setImageDrawable((mFavorite) ? mTrashDrawable : mAddDrawable);
            // Mostramos el icono de la seccion/corazon
            mFavOrNumberImageView.setImageDrawable((mFavorite) ? mFavoriteDrawable : mClotheDrawable);
            // Mostramos el numero de favoritos/total de productos de la tienda
            mNumberTextView.setText((mFavorite) ? "12" : Integer.toString(shop.getProducts()));

            // Aplicamos un tinte al icono
            Drawable drawable = mActionImageButton.getDrawable();
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable, mContext.getResources().getColor(R.color.colorMediumText));
            DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN);

            // Establecemos el nombre de la tienda
            mNameTextView.setText(shop.getName().toUpperCase());

            mTarget = new Target()
            {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
                {
                    mShopLogoImageView.setImageBitmap(bitmap);
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
                   .into(mTarget);
        }

        /**
         * Metodo que borra/añade una tienda y realiza las animaciones.
         * @param action: accion realizada por el usuario.
         */
        private void _actionPerformed(int action, final Shop shop)
        {
            final boolean actionDeleted = (action == ACTION_SHOP_DELETED);

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
    public ShopsListAdapter(Context context, List<Shop> allShopsList, List<String> myShopsList)
    {
        mContext = context;
        mAllShopsList = allShopsList;
        mMyShopsList = myShopsList;

        mTrashDrawable    = mContext.getResources().getDrawable(R.drawable.ic_trash);
        mAddDrawable      = mContext.getResources().getDrawable(R.drawable.ic_add_grey);
        mFavoriteDrawable = mContext.getResources().getDrawable(R.drawable.ic_favorite_grey);
        mClotheDrawable   = mContext.getResources().getDrawable(R.drawable.ic_shirt);
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
