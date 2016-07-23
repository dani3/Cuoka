package com.wallakoala.wallakoala.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.wallakoala.wallakoala.Properties.Properties;
import com.wallakoala.wallakoala.R;
import com.wallakoala.wallakoala.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @class Adapter para mostrar la lista de tiendas.
 * Created by Daniel Mancebo Aldea on 06/07/2016.
 */

public class ShopLogoAdapter extends RecyclerView.Adapter<ShopLogoAdapter.ShopHolder>
{
    /* Context */
    private static Context mContext;

    /* Data */
    private static List<String> mShopList;
    private static boolean[] mShopsCheckedArray;

    /* [BEGIN] ViewHolder */
    public static class ShopHolder extends RecyclerView.ViewHolder
    {
        private CircleImageView mShopLogoSelectedImageView;
        private CircleImageView mShopLogoImageView;
        private Target mTarget;

        public ShopHolder(View itemView)
        {
            super(itemView);

            mShopLogoSelectedImageView = (CircleImageView) itemView.findViewById(R.id.shop_logo_selected);
            mShopLogoSelectedImageView.setAlpha(0.0f);

            mShopLogoImageView = (CircleImageView) itemView.findViewById(R.id.shop_logo);
        }

        /**
         * Metodo llamado cuando se va a mostrar el item.
         * @param shop: nombre de la tienda.
         */
        public void bindShop(final String shop)
        {
            mShopLogoImageView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (mShopLogoSelectedImageView.getAlpha() == 0.0f)
                    {
                        mShopLogoSelectedImageView.animate().setDuration(250)
                                                            .alpha(1.0f)
                                                            .setInterpolator(new OvershootInterpolator());

                        _shopCheckChanged(shop, true);

                    } else {
                        mShopLogoSelectedImageView.animate().setDuration(250)
                                                            .alpha(0.0f)
                                                            .setInterpolator(new OvershootInterpolator());

                        _shopCheckChanged(shop, false);
                    }
                }
            });

            final String logoFile = shop + "-logo.jpg";

            final String fixedUrl = Utils.fixUrl(Properties.SERVER_URL + Properties.LOGOS_PATH + logoFile);

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

    } /* [END] ViewHolder */

    public ShopLogoAdapter(List<String> shops, Context context)
    {
        mShopList = shops;
        mContext = context;

        mShopsCheckedArray = new boolean[mShopList.size()];
    }

    /**
     * Metodo que actualiza el array de tiendas marcadas.
     * @param shop: tienda que ha cambiado su estado.
     * @param isChecked: true si se ha marcado.
     */
    private static void _shopCheckChanged(String shop, boolean isChecked)
    {
        final int pos = mShopList.indexOf(shop);

        mShopsCheckedArray[pos] = isChecked;
    }

    /**
     * Metodo que devuelve solo las tiendas marcadas.
     * @return lista de tiendas marcadas.
     */
    public List<String> getShopsChecked()
    {
        List<String> checkedShops = new ArrayList<>();
        for (int i = 0; i < mShopsCheckedArray.length; i++)
        {
            if (mShopsCheckedArray[i])
            {
                checkedShops.add(mShopList.get(i));
            }
        }

        return checkedShops;
    }

    @Override
    public ShopHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                                      .inflate(R.layout.shop_logo_item
                                            , viewGroup
                                            , false );

        return new ShopHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ShopHolder shopHolder, int pos)
    {
        shopHolder.bindShop(mShopList.get(pos));
    }

    @Override
    public int getItemCount()
    {
        return mShopList.size();
    }
}
