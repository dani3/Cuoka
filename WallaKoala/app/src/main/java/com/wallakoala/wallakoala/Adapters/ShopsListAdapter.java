package com.wallakoala.wallakoala.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        public void bindShop(Shop shop)
        {
            String logoFile = shop.getName() + "-logo.jpg";
            String fixedUrl = Utils.fixUrl(Properties.SERVER_URL + Properties.LOGOS_PATH + logoFile);

            boolean isFavorite = false;
            for (String name : mMyShopsList)
            {
                if (shop.getName().equals(name))
                {
                    isFavorite = true;
                    break;
                }
            }

            mShopLogoSelectedImageView.setVisibility((isFavorite) ? View.VISIBLE : View.INVISIBLE);

            mActionImageButton.setImageDrawable((isFavorite) ? mTrashDrawable : mAddDrawable);
            mFavOrNumberImageView.setImageDrawable((isFavorite) ? mFavoriteDrawable : mClotheDrawable);

            mNumberTextView.setText((isFavorite) ? "12" : Integer.toString(shop.getProducts()));

            // Aplicamos un tinte al icono
            Drawable drawable = mActionImageButton.getDrawable();
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable, mContext.getResources().getColor(R.color.colorMediumText));

            DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN);

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
