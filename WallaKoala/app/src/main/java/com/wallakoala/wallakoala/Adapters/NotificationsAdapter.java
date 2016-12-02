package com.wallakoala.wallakoala.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.wallakoala.wallakoala.Beans.Notification;
import com.wallakoala.wallakoala.Properties.Properties;
import com.wallakoala.wallakoala.R;
import com.wallakoala.wallakoala.Utils.SharedPreferencesManager;
import com.wallakoala.wallakoala.Utils.Utils;

import java.util.List;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Adapter que muestra los distintos tipos de notificaciones.
 * Created by Daniel Mancebo Aldea on 25/11/2016.
 */

public class NotificationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    /* Context */
    private Context mContext;

    /* Data */
    private List<Notification> mNotificationsList;
    private Set<Long> mNotificationsReadList;

    /**
     * Notificacion de nueva tienda.
     */
    public class NewShopNotificationHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private CardView mCardView;
        private LinearLayout mBackground;

        private CircleImageView mIconImageView;
        private CircleImageView mShopLogoImageView;

        private TextView mTitle;
        private TextView mBody;
        private TextView mOffset;
        private TextView mActionButton;

        private Target mTarget;

        private boolean loading;

        public NewShopNotificationHolder(View itemView)
        {
            super(itemView);

            mCardView   = (CardView) itemView.findViewById(R.id.notification);
            mBackground = (LinearLayout) itemView.findViewById(R.id.notification_background);

            mIconImageView     = (CircleImageView) itemView.findViewById(R.id.notification_icon);
            mShopLogoImageView = (CircleImageView) itemView.findViewById(R.id.notification_shop_logo);

            mTitle        = (TextView) itemView.findViewById(R.id.notification_title);
            mBody         = (TextView) itemView.findViewById(R.id.notification_body);
            mOffset       = (TextView) itemView.findViewById(R.id.notification_offset);
            mActionButton = (TextView) itemView.findViewById(R.id.notification_button);

            mCardView.setOnClickListener(this);
            mActionButton.setOnClickListener(this);
        }

        @SuppressWarnings("deprecation")
        public void bindNotification(Notification notification)
        {
            // Establecemos la cabecera, el body y la diferencia de dias.
            mTitle.setText(notification.getTitle());
            mBody.setText(notification.getText());
            mOffset.setText(Utils.getMessageFromDaysOffset(notification.getOffset()));

            // Cargamos el logo del icono
            String fixedUrl = Utils.fixUrl(
                    Properties.SERVER_URL + Properties.NOTIFICATION_PATH + notification.getImage());

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

                    loading = false;
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

                    loading = true;
                }
            };

            Picasso.with(mContext)
                   .load(fixedUrl)
                   .noFade()
                   .into(mTarget);

            if (mNotificationsReadList.contains(notification.getId()))
            {
                _markNotification(false);
            }
        }

        @Override
        public void onClick(View v)
        {
            if (v.getId() == mCardView.getId())
            {
                _markNotification(true);

            } else if (v.getId() == mActionButton.getId()) {


            }
        }

        /**
         * Metodo que marca la notificacion como leida y la sombrea.
         */
        @SuppressWarnings("deprecation")
        private void _markNotification(boolean connect)
        {
            // Llamamos al servidor para marcar la notificacion como leida.
            if (connect)
            {

            }

            // Sombreamos la CardView y quitamos la elevacion.
            mBackground.setBackgroundColor(mContext.getResources().getColor(R.color.colorLight));
            mIconImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_notification_shop_bw));

            mCardView.setCardElevation(0.0f);

            // Ponemos el mismo color en todos los textos.
            mTitle.setTextColor(mContext.getResources().getColor(R.color.colorText));
            mBody.setTextColor(mContext.getResources().getColor(R.color.colorText));
            mOffset.setTextColor(mContext.getResources().getColor(R.color.colorText));
            mActionButton.setTextColor(mContext.getResources().getColor(R.color.colorText));
        }
    }

    /**
     * Notificacion de nueva tienda.
     */
    public class SalesNotificationHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private CardView mCardView;
        private LinearLayout mBackground;

        private CircleImageView mIconImageView;
        private CircleImageView mSalesImageView;

        private TextView mTitle;
        private TextView mBody;
        private TextView mOffset;

        private Target mTarget;

        private boolean loading;

        public SalesNotificationHolder(View itemView)
        {
            super(itemView);

            mCardView   = (CardView) itemView.findViewById(R.id.notification);
            mBackground = (LinearLayout) itemView.findViewById(R.id.notification_background);

            mIconImageView  = (CircleImageView) itemView.findViewById(R.id.notification_icon);
            mSalesImageView = (CircleImageView) itemView.findViewById(R.id.notification_sales_icon);

            mTitle  = (TextView) itemView.findViewById(R.id.notification_title);
            mBody   = (TextView) itemView.findViewById(R.id.notification_body);
            mOffset = (TextView) itemView.findViewById(R.id.notification_offset);

            mCardView.setOnClickListener(this);
        }

        @SuppressWarnings("deprecation")
        public void bindNotification(Notification notification)
        {
            // Establecemos la cabecera, el body y la diferencia de dias.
            mTitle.setText(notification.getTitle());
            mBody.setText(notification.getText());
            mOffset.setText(Utils.getMessageFromDaysOffset(notification.getOffset()));

            // Cargamos el logo del icono
            String fixedUrl = Utils.fixUrl(
                    Properties.SERVER_URL + Properties.NOTIFICATION_PATH + notification.getImage());

            mTarget = new Target()
            {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
                {
                    mSalesImageView.setImageBitmap(bitmap);

                    Animation fadeOut = new AlphaAnimation(0, 1);
                    fadeOut.setInterpolator(new AccelerateInterpolator());
                    fadeOut.setDuration(250);
                    mSalesImageView.startAnimation(fadeOut);

                    loading = false;
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable)
                {
                    mSalesImageView.setBackgroundColor(
                            mContext.getResources().getColor(android.R.color.holo_red_dark));

                    mSalesImageView.setAlpha(0.2f);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable)
                {
                    mSalesImageView.setImageBitmap(null);

                    loading = true;
                }
            };

            Picasso.with(mContext)
                   .load(fixedUrl)
                   .noFade()
                   .into(mTarget);

            if (mNotificationsReadList.contains(notification.getId()))
            {
                _markNotification(false);
            }
        }

        @Override
        public void onClick(View v)
        {
            if (v.getId() == mCardView.getId())
            {
                _markNotification(true);
            }
        }

        /**
         * Metodo que marca la notificacion como leida y la sombrea.
         */
        @SuppressWarnings("deprecation")
        private void _markNotification(boolean connect)
        {
            // Llamamos al servidor para marcar la notificacion como leida.
            if (connect)
            {

            }

            try
            {
                mSalesImageView.setImageBitmap(
                        Utils.toGrayscale(((BitmapDrawable)mSalesImageView.getDrawable()).getBitmap()));

            } catch (Exception e) {
                Log.e(Properties.TAG, e.getMessage());
            }

            // Sombreamos la CardView y quitamos la elevacion.
            mBackground.setBackgroundColor(mContext.getResources().getColor(R.color.colorLight));
            mIconImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_notification_sales_bw));

            mCardView.setCardElevation(0.0f);

            // Ponemos el mismo color en todos los textos.
            mTitle.setTextColor(mContext.getResources().getColor(R.color.colorText));
            mBody.setTextColor(mContext.getResources().getColor(R.color.colorText));
            mOffset.setTextColor(mContext.getResources().getColor(R.color.colorText));
        }
    }

    /**
     * Constructor del adapter.
     * @param context: contexto.
     * @param notifications: lista de notificaciones.
     */
    public NotificationsAdapter(Context context, List<Notification> notifications)
    {
        mContext = context;
        mNotificationsList = notifications;

        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(mContext);

        mNotificationsReadList = sharedPreferencesManager.retreiveUser().getNotificationsRead();

        Log.d(Properties.TAG, "Notificaciones leidas: ");
        for (Long id : mNotificationsReadList)
        {
            Log.d(Properties.TAG, " - " + id);
        }
    }

    @Override
    public int getItemViewType(int position)
    {
        return mNotificationsList.get(position).getAction();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView;

        switch (viewType)
        {
            case (Properties.NEW_SHOP_NOTIFICATION):
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.notification_new_shop
                            , parent
                            , false );

                return new NewShopNotificationHolder(itemView);

            case (Properties.SALES_NOTIFICATION):
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.notification_sales
                                , parent
                                , false );

                return new SalesNotificationHolder(itemView);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        switch (holder.getItemViewType())
        {
            case (Properties.NEW_SHOP_NOTIFICATION):
                ((NewShopNotificationHolder) holder).bindNotification(mNotificationsList.get(position));
                break;

            case (Properties.SALES_NOTIFICATION):
                ((SalesNotificationHolder) holder).bindNotification(mNotificationsList.get(position));
                break;
        }
    }

    @Override
    public int getItemCount()
    {
        return mNotificationsList.size();
    }
}
