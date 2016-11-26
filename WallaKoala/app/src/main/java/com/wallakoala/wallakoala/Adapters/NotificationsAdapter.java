package com.wallakoala.wallakoala.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.wallakoala.wallakoala.Beans.Notification;
import com.wallakoala.wallakoala.Properties.Properties;
import com.wallakoala.wallakoala.R;
import com.wallakoala.wallakoala.Utils.Utils;

import org.w3c.dom.Text;

import java.util.List;

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

    /**
     * Notificacion de nueva tienda.
     */
    public class NewShopNotificationHolder extends RecyclerView.ViewHolder
    {
        private CardView mCardView;

        private CircleImageView mIconImageView;
        private CircleImageView mShopLogoImageView;

        private TextView mTitle;
        private TextView mBody;
        private TextView mOffset;
        private TextView mActionButton;

        private Target mTarget;

        public NewShopNotificationHolder(View itemView)
        {
            super(itemView);

            mCardView = (CardView) itemView.findViewById(R.id.notification);

            mIconImageView     = (CircleImageView) itemView.findViewById(R.id.notification_icon);
            mShopLogoImageView = (CircleImageView) itemView.findViewById(R.id.notification_shop_logo);

            mTitle        = (TextView) itemView.findViewById(R.id.notification_title);
            mBody         = (TextView) itemView.findViewById(R.id.notification_body);
            mOffset       = (TextView) itemView.findViewById(R.id.notification_offset);
            mActionButton = (TextView) itemView.findViewById(R.id.notification_button);
        }

        @SuppressWarnings("deprecation")
        public void bindNotification(Notification notification)
        {
            mTitle.setText(notification.getTitle());
            mBody.setText(notification.getText());
            mOffset.setText(Utils.getMessageFromDaysOffset(notification.getOffset()));

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
                   .load(fixedUrl)
                   .noFade()
                   .into(mTarget);
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
    }

    @Override
    public int getItemViewType(int position)
    {
        return mNotificationsList.get(position).getAction();
    }

    @Override
    public NewShopNotificationHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        switch (viewType)
        {
            case (Properties.NEW_SHOP_NOTIFICATION):
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.notification_new_shop
                                , parent
                                , false );

                return new NewShopNotificationHolder(itemView);
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
        }
    }

    @Override
    public int getItemCount()
    {
        return mNotificationsList.size();
    }
}
