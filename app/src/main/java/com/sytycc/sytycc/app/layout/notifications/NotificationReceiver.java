package com.sytycc.sytycc.app.layout.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Michaël on 30/03/14.
 */
public class NotificationReceiver extends BroadcastReceiver {

    public static int REQUEST_CODE = 12345;

    // Triggered by the Alarm periodically (starts the service to run task)
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, NotificationService.class);
        i.putExtra("CHECK",true);
        context.startService(i);
    }
}