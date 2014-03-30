package com.sytycc.sytycc.app.layout.notifications;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.sytycc.sytycc.app.MainActivity;

/**
 * Created by Michaël on 30/03/14.
 *
 * Takes care of sending notifications if a task running in the background is completed
 */
public class NotificationService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /* Moet aangeroepen worden waar nodig */
    //showNotification(R.drawable.notification_white,getString(R.string.notification_example_title),getString(R.string.notification_example_text));

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void showNotification(int imageId, String title, String text) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(imageId)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setAutoCancel(true);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.putExtra("TAB",2);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.

        PendingIntent resultPendingIntent;
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(0, mBuilder.build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
        /* TODO start pulling from the server */
    }

}
