package com.cuoka.cuoka.Services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.cuoka.cuoka.R;

/**
 *
 * Created by Daniel Mancebo on 09/08/2017.
 */

public class NotificationService extends IntentService
{
    public NotificationService()
    {
        super("Notification service");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent)
    {
        /*NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_logo)
                        .setContentTitle("Novedades")
                        .setContentText("¡Tienes nuevos productos en CUOKA!");

        // Sets an ID for the notification
        int mNotificationId = 001;
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());*/
    }
}
