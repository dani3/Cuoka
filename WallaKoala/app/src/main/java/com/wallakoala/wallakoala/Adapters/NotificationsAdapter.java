package com.wallakoala.wallakoala.Adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
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
import com.wallakoala.wallakoala.Activities.ShopsUI;
import com.wallakoala.wallakoala.Beans.Notification;
import com.wallakoala.wallakoala.Properties.Properties;
import com.wallakoala.wallakoala.R;
import com.wallakoala.wallakoala.Singletons.RestClientSingleton;
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
    /* Constants */
    private static final int MANAGE_SHOPS_REQUEST = 0;

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
        private long notificationId;
        private Notification mNotification;

        private boolean marked;

        private CardView mCardView;
        private LinearLayout mBackground;

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
            // Guardamos el id de la notificacion.
            notificationId = notification.getId();
            mNotification = notification;

            // Establecemos la cabecera, el body y la diferencia de dias.
            mTitle.setText(notification.getTitle());
            mBody.setText(notification.getText());
            mOffset.setText(Utils.getMessageFromDaysOffset(notification.getOffset()));

            // Cargamos el logo del icono.
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

                    try
                    {
                        if (marked)
                        {
                            mShopLogoImageView.setImageBitmap(
                                    Utils.toGrayscale(((BitmapDrawable) mShopLogoImageView.getDrawable()).getBitmap()));
                        }

                    } catch (Exception e) {
                        Log.e(Properties.TAG, e.getMessage());
                    }
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

            marked = mNotificationsReadList.contains(notification.getId());

            Picasso.with(mContext)
                   .load(fixedUrl)
                   .noFade()
                   .into(mTarget);

            if (mNotificationsReadList.contains(notification.getId()))
            {
                _markNotification(false, notificationId);
            }
        }

        @Override
        public void onClick(View v)
        {
            if (!marked)
            {
                if (v.getId() == mCardView.getId())
                {
                    _markNotification(true, notificationId);

                } else if (v.getId() == mActionButton.getId()) {
                    Intent intent = new Intent(mContext, ShopsUI.class);

                    intent.putExtra("shop", mNotification.getExtraInfo());

                    // Iniciamos la activity ShopsUI
                    ((Activity) mContext).startActivityForResult(intent, MANAGE_SHOPS_REQUEST);

                    // Animacion de transicion para pasar de una activity a otra.
                    ((Activity) mContext).overridePendingTransition(R.anim.right_in_animation, R.anim.right_out_animation);
                }
            }
        }

        /**
         * Metodo que marca la notificacion como leida y la sombrea.
         */
        @SuppressWarnings("deprecation")
        private void _markNotification(boolean connect, long id)
        {
            // Llamamos al servidor para marcar la notificacion como leida.
            if (connect)
            {
                new MarkNotificationAsyncTask().execute(id);

                try
                {
                    mShopLogoImageView.setImageBitmap(
                            Utils.toGrayscale(((BitmapDrawable) mShopLogoImageView.getDrawable()).getBitmap()));

                } catch (Exception e) {
                    Log.e(Properties.TAG, e.getMessage());
                }
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
     * Notificacion de rebajas generales.
     */
    public class SalesNotificationHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private long notificationId;

        private boolean marked;

        private CardView mCardView;
        private LinearLayout mBackground;

        private CircleImageView mIconImageView;
        private CircleImageView mSalesImageView;

        private TextView mTitle;
        private TextView mBody;
        private TextView mOffset;

        private Target mTarget;

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
            // Guardamos el id de la notificacion.
            notificationId = notification.getId();

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

                    try
                    {
                        if (marked)
                        {
                            mSalesImageView.setImageBitmap(
                                    Utils.toGrayscale(((BitmapDrawable) mSalesImageView.getDrawable()).getBitmap()));
                        }

                    } catch (Exception e) {
                        Log.e(Properties.TAG, e.getMessage());
                    }
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
                }
            };

            marked = mNotificationsReadList.contains(notification.getId());

            Picasso.with(mContext)
                   .load(fixedUrl)
                   .noFade()
                   .into(mTarget);

            if (mNotificationsReadList.contains(notification.getId()))
            {
                _markNotification(false, notificationId);
            }
        }

        @Override
        public void onClick(View v)
        {
            if (!marked)
            {
                if (v.getId() == mCardView.getId())
                {
                    _markNotification(true, notificationId);
                }
            }
        }

        /**
         * Metodo que marca la notificacion como leida y la sombrea.
         */
        @SuppressWarnings("deprecation")
        private void _markNotification(boolean connect, long id)
        {
            // Llamamos al servidor para marcar la notificacion como leida.
            if (connect)
            {
                new MarkNotificationAsyncTask().execute(id);

                try
                {
                    mSalesImageView.setImageBitmap(
                            Utils.toGrayscale(((BitmapDrawable) mSalesImageView.getDrawable()).getBitmap()));

                } catch (Exception e) {
                    Log.e(Properties.TAG, e.getMessage());
                }
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
     * Notificacion de descuentos especiales en una tienda.
     */
    public class ShopDiscountNotificationHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private long notificationId;

        private boolean marked;

        private CardView mCardView;
        private LinearLayout mBackground;

        private CircleImageView mIconImageView;
        private CircleImageView mShopLogoImageView;

        private TextView mTitle;
        private TextView mBody;
        private TextView mOffset;

        private Target mTarget;

        public ShopDiscountNotificationHolder(View itemView)
        {
            super(itemView);

            mCardView   = (CardView) itemView.findViewById(R.id.notification);
            mBackground = (LinearLayout) itemView.findViewById(R.id.notification_background);

            mIconImageView     = (CircleImageView) itemView.findViewById(R.id.notification_icon);
            mShopLogoImageView = (CircleImageView) itemView.findViewById(R.id.notification_shop_discount_icon);

            mTitle  = (TextView) itemView.findViewById(R.id.notification_title);
            mBody   = (TextView) itemView.findViewById(R.id.notification_body);
            mOffset = (TextView) itemView.findViewById(R.id.notification_offset);

            mCardView.setOnClickListener(this);
        }

        @SuppressWarnings("deprecation")
        public void bindNotification(Notification notification)
        {
            // Guardamos el id de la notificacion.
            notificationId = notification.getId();

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

                    try
                    {
                        if (marked)
                        {
                            mShopLogoImageView.setImageBitmap(
                                    Utils.toGrayscale(((BitmapDrawable) mShopLogoImageView.getDrawable()).getBitmap()));
                        }

                    } catch (Exception e) {
                        Log.e(Properties.TAG, e.getMessage());
                    }
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

            marked = mNotificationsReadList.contains(notification.getId());

            Picasso.with(mContext)
                   .load(fixedUrl)
                   .noFade()
                   .into(mTarget);

            if (mNotificationsReadList.contains(notification.getId()))
            {
                _markNotification(false, notificationId);
            }
        }

        @Override
        public void onClick(View v)
        {
            if (!marked)
            {
                if (v.getId() == mCardView.getId())
                {
                    _markNotification(true, notificationId);
                }
            }
        }

        /**
         * Metodo que marca la notificacion como leida y la sombrea.
         */
        @SuppressWarnings("deprecation")
        private void _markNotification(boolean connect, long id)
        {
            // Llamamos al servidor para marcar la notificacion como leida.
            if (connect)
            {
                new MarkNotificationAsyncTask().execute(id);

                try
                {
                    mShopLogoImageView.setImageBitmap(
                            Utils.toGrayscale(((BitmapDrawable) mShopLogoImageView.getDrawable()).getBitmap()));

                } catch (Exception e) {
                    Log.e(Properties.TAG, e.getMessage());
                }
            }

            // Sombreamos la CardView y quitamos la elevacion.
            mBackground.setBackgroundColor(mContext.getResources().getColor(R.color.colorLight));
            mIconImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_notification_discount_bw));

            mCardView.setCardElevation(0.0f);

            // Ponemos el mismo color en todos los textos.
            mTitle.setTextColor(mContext.getResources().getColor(R.color.colorText));
            mBody.setTextColor(mContext.getResources().getColor(R.color.colorText));
            mOffset.setTextColor(mContext.getResources().getColor(R.color.colorText));
        }
    }

    /**
     * Notificacion de nueva actualizacion.
     */
    public class UpdateNotificationHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private long notificationId;

        private boolean marked;

        private CardView mCardView;
        private LinearLayout mBackground;

        private CircleImageView mIconImageView;
        private CircleImageView mUpdateImageView;

        private TextView mTitle;
        private TextView mBody;
        private TextView mOffset;
        private TextView mActionButton;

        private Target mTarget;

        public UpdateNotificationHolder(View itemView)
        {
            super(itemView);

            mCardView   = (CardView) itemView.findViewById(R.id.notification);
            mBackground = (LinearLayout) itemView.findViewById(R.id.notification_background);

            mIconImageView   = (CircleImageView) itemView.findViewById(R.id.notification_icon);
            mUpdateImageView = (CircleImageView) itemView.findViewById(R.id.notification_update_logo);

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
            // Guardamos el id de la notificacion.
            notificationId = notification.getId();

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
                    mUpdateImageView.setImageBitmap(bitmap);

                    Animation fadeOut = new AlphaAnimation(0, 1);
                    fadeOut.setInterpolator(new AccelerateInterpolator());
                    fadeOut.setDuration(250);
                    mUpdateImageView.startAnimation(fadeOut);

                    try
                    {
                        if (marked)
                        {
                            mUpdateImageView.setImageBitmap(
                                    Utils.toGrayscale(((BitmapDrawable) mUpdateImageView.getDrawable()).getBitmap()));
                        }

                    } catch (Exception e) {
                        Log.e(Properties.TAG, e.getMessage());
                    }
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable)
                {
                    mUpdateImageView.setBackgroundColor(
                            mContext.getResources().getColor(android.R.color.holo_red_dark));

                    mUpdateImageView.setAlpha(0.2f);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable)
                {
                    mUpdateImageView.setImageBitmap(null);
                }
            };

            marked = mNotificationsReadList.contains(notification.getId());

            Picasso.with(mContext)
                   .load(fixedUrl)
                   .noFade()
                   .into(mTarget);

            if (mNotificationsReadList.contains(notification.getId()))
            {
                _markNotification(false, notificationId);
            }
        }

        @Override
        public void onClick(View v)
        {
            if (!marked)
            {
                if (v.getId() == mCardView.getId())
                {
                    _markNotification(true, notificationId);

                } else if (v.getId() == mActionButton.getId()) {
                    // TODO llevar al PlayStore
                }
            }
        }

        /**
         * Metodo que marca la notificacion como leida y la sombrea.
         */
        @SuppressWarnings("deprecation")
        private void _markNotification(boolean connect, long id)
        {
            // Llamamos al servidor para marcar la notificacion como leida.
            if (connect)
            {
                new MarkNotificationAsyncTask().execute(id);

                try
                {
                    mUpdateImageView.setImageBitmap(
                            Utils.toGrayscale(((BitmapDrawable) mUpdateImageView.getDrawable()).getBitmap()));

                } catch (Exception e) {
                    Log.e(Properties.TAG, e.getMessage());
                }
            }

            // Sombreamos la CardView y quitamos la elevacion.
            mBackground.setBackgroundColor(mContext.getResources().getColor(R.color.colorLight));
            mIconImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_notification_update_bw));

            mCardView.setCardElevation(0.0f);

            // Ponemos el mismo color en todos los textos.
            mTitle.setTextColor(mContext.getResources().getColor(R.color.colorText));
            mBody.setTextColor(mContext.getResources().getColor(R.color.colorText));
            mOffset.setTextColor(mContext.getResources().getColor(R.color.colorText));
            mActionButton.setTextColor(mContext.getResources().getColor(R.color.colorText));
        }
    }

    /**
     * Tarea en segundo plano que marca la notificacion como leida.
     */
    private class MarkNotificationAsyncTask extends AsyncTask<Long, Void, Void>
    {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute()
        {
            progressDialog = new ProgressDialog(mContext, R.style.MyDialogTheme);
            progressDialog.setTitle("");
            progressDialog.setMessage("Realizando cambios...");
            progressDialog.setIndeterminate(true);

            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Long... id)
        {
            RestClientSingleton.markNotificationAsRead(mContext, id[0]);

            mNotificationsReadList.add(id[0]);

            return null;
        }

        @Override
        protected void onPostExecute(Void unused)
        {
            progressDialog.dismiss();
        }
    }

    /**
     * Notificacion de nueva recomendacion.
     */
    public class RecommendedNotificationHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private long notificationId;

        private boolean marked;

        private CardView mCardView;
        private LinearLayout mBackground;

        private CircleImageView mIconImageView;
        private CircleImageView mShopLogoImageView;

        private TextView mTitle;
        private TextView mBody;
        private TextView mOffset;
        private TextView mActionButton;

        private Target mTarget;

        public RecommendedNotificationHolder(View itemView)
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
            // Guardamos el id de la notificacion.
            notificationId = notification.getId();

            // Establecemos la cabecera, el body y la diferencia de dias.
            mTitle.setText(notification.getTitle());
            mBody.setText(notification.getText());
            mOffset.setText(Utils.getMessageFromDaysOffset(notification.getOffset()));

            // Cargamos el logo del icono.
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

                    try
                    {
                        if (marked)
                        {
                            mShopLogoImageView.setImageBitmap(
                                    Utils.toGrayscale(((BitmapDrawable) mShopLogoImageView.getDrawable()).getBitmap()));
                        }

                    } catch (Exception e) {
                        Log.e(Properties.TAG, e.getMessage());
                    }
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

            marked = mNotificationsReadList.contains(notification.getId());

            Picasso.with(mContext)
                   .load(fixedUrl)
                   .noFade()
                   .into(mTarget);

            if (mNotificationsReadList.contains(notification.getId()))
            {
                _markNotification(false, notificationId);
            }
        }

        @Override
        public void onClick(View v)
        {
            if (!marked)
            {
                if (v.getId() == mCardView.getId())
                {
                    _markNotification(true, notificationId);

                } else if (v.getId() == mActionButton.getId()) {

                }
            }
        }

        /**
         * Metodo que marca la notificacion como leida y la sombrea.
         */
        @SuppressWarnings("deprecation")
        private void _markNotification(boolean connect, long id)
        {
            // Llamamos al servidor para marcar la notificacion como leida.
            if (connect)
            {
                new MarkNotificationAsyncTask().execute(id);

                try
                {
                    mShopLogoImageView.setImageBitmap(
                            Utils.toGrayscale(((BitmapDrawable) mShopLogoImageView.getDrawable()).getBitmap()));

                } catch (Exception e) {
                    Log.e(Properties.TAG, e.getMessage());
                }
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
    }

    /**
     * Metodo que comprueba si todas las notificaciones se han marcado como leidas.
     * @return true si se han leido todas las notificaciones.
     */
    public boolean isEveryNotificationRead()
    {
        for (Notification notification : mNotificationsList)
        {
            if (!mNotificationsReadList.contains(notification.getId()))
            {
                return false;
            }
        }

        return true;
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
                                    , false);

                return new NewShopNotificationHolder(itemView);

            case (Properties.SALES_NOTIFICATION):
                itemView = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.notification_sales
                                    , parent
                                    , false);

                return new SalesNotificationHolder(itemView);

            case (Properties.SHOP_DISCOUNT_NOTIFICATION):
                itemView = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.notification_shop_discount
                                    , parent
                                    , false);

                return new ShopDiscountNotificationHolder(itemView);

            case (Properties.UPDATE_NOTIFICATION):
                itemView = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.notification_update
                                    , parent
                                    , false);

                return new UpdateNotificationHolder(itemView);

            case (Properties.RECOMMENDED_NOTIFICATION):
                itemView = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.notification_update
                                    , parent
                                    , false);

                return new RecommendedNotificationHolder(itemView);
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

            case (Properties.SHOP_DISCOUNT_NOTIFICATION):
                ((ShopDiscountNotificationHolder) holder).bindNotification(mNotificationsList.get(position));
                break;

            case (Properties.UPDATE_NOTIFICATION):
                ((UpdateNotificationHolder) holder).bindNotification(mNotificationsList.get(position));
                break;

            case (Properties.RECOMMENDED_NOTIFICATION):
                ((RecommendedNotificationHolder) holder).bindNotification(mNotificationsList.get(position));
                break;
        }
    }

    @Override
    public int getItemCount()
    {
        return mNotificationsList.size();
    }
}
