package com.cuoka.cuoka.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.cuoka.cuoka.Beans.DescubreShop;
import com.cuoka.cuoka.Properties.Properties;
import com.cuoka.cuoka.R;
import com.cuoka.cuoka.Utils.Utils;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Adapter para mostrar las tiendas de Descubre.
 * Created by Daniel Mancebo Aldea on 05/11/2017.
 */

public class DescubreAdapter extends RecyclerView.Adapter<DescubreAdapter.DescubreShopViewHolder>
{
    /* Context */
    private Context mContext;

    /* Data */
    private List<DescubreShop> mShopList;

    public class DescubreShopViewHolder extends RecyclerView.ViewHolder
    {
        /* Views */
        private CircleImageView mShopLogoImageView;
        private ImageView mShopBannerImageView;
        private TextView mShopNameTextView;
        private TextView mShopDescriptionTextView;

        private Target mBannerTarget;
        private Target mLogoTarget;

        public DescubreShopViewHolder(View itemView)
        {
            super(itemView);

            mShopBannerImageView = (ImageView) itemView.findViewById(R.id.descubre_shop_banner);
            mShopLogoImageView   = (CircleImageView) itemView.findViewById(R.id.descubre_shop_logo);

            mShopNameTextView        = (TextView) itemView.findViewById(R.id.descubre_shop_name);
            mShopDescriptionTextView = (TextView) itemView.findViewById(R.id.descubre_shop_description);
        }

        /**
         * Metodo que inicializa las vistas con la tienda correspondiente.
         * @param shop: tienda a mostrar.
         */
        public void bindShop(final DescubreShop shop)
        {
            String fixedUrlLogo = Utils.fixUrl(
                Properties.SERVER_URL + Properties.LOGOS_PATH + shop.getName() + "-logo.jpg");
            String fixedUrlBanner = Utils.fixUrl(
                    Properties.SERVER_URL + Properties.BANNERS_PATH + shop.getUrlBanner() + "-banner.jpg");

            mShopNameTextView.setText(shop.getName());
            mShopDescriptionTextView.setText(shop.getDescription());

            mBannerTarget = new Target()
            {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
                {
                    mShopBannerImageView.setImageBitmap(bitmap);

                    // Reestablecemos la opacidad y el valor de la altura a WRAP_CONTENT, eliminamos el color de fondo.
                    mShopBannerImageView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    mShopBannerImageView.setBackgroundColor(-1);
                    mShopBannerImageView.setAlpha(1.0f);

                    Animation fadeOut = new AlphaAnimation(0, 1);
                    fadeOut.setInterpolator(new AccelerateInterpolator());
                    fadeOut.setDuration(250);
                    mShopBannerImageView.startAnimation(fadeOut);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable)
                {
                    mShopBannerImageView.setBackgroundColor(
                            mContext.getResources().getColor(android.R.color.holo_red_dark));

                    mShopBannerImageView.setAlpha(0.2f);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable)
                {
                    mShopBannerImageView.setImageBitmap(null);

                    // Establecemos la altura usando el AspectRatio del banner.
                    mShopBannerImageView.getLayoutParams().height =
                            (int) (mShopBannerImageView.getWidth() * shop.getAspectRatio());

                    // Establecemos un color de fondo aleatorio y un 25% de opacidad.
                    mShopBannerImageView.setBackgroundColor(
                            mContext.getResources().getColor(R.color.colorAccent));
                    mShopBannerImageView.setAlpha(0.25f);
                }
            };

            mLogoTarget = new Target()
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
                    mShopLogoImageView.setBackgroundColor(
                            mContext.getResources().getColor(android.R.color.holo_red_dark));

                    mShopLogoImageView.setAlpha(0.2f);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable)
                {
                    mShopLogoImageView.setImageBitmap(null);
                }
            };

            Picasso.with(mContext)
                   .load(fixedUrlBanner)
                   .noFade()
                   .into(mBannerTarget);

            Picasso.with(mContext)
                   .load(fixedUrlLogo)
                   .noFade()
                   .into(mLogoTarget);
        }
    }

    /**
     * Constructor del adapter de tiendas de Descubre.
     * @param context: contexto de la aplicación.
     * @param shops: lista de tiendas de Descubre.
     */
    public DescubreAdapter(final Context context, List<DescubreShop> shops)
    {
        mContext = context;

        mShopList = shops;
    }

    @Override
    public DescubreShopViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.shop_descubre
                    , parent
                    , false );

        return new DescubreShopViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DescubreShopViewHolder holder, int position)
    {
        holder.bindShop(mShopList.get(position));
    }

    @Override
    public int getItemCount()
    {
        return mShopList.size();
    }
}
