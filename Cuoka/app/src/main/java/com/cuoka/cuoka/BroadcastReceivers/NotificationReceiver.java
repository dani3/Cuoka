package com.cuoka.cuoka.BroadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cuoka.cuoka.Services.NotificationService;

/**
 *
 * Created by Daniel Mancebo on 09/08/2017.
 */

public class NotificationReceiver extends BroadcastReceiver
{
    public static final int REQUEST_CODE = 12345;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Intent i = new Intent(context, NotificationService.class);

        context.startService(i);
    }
}
