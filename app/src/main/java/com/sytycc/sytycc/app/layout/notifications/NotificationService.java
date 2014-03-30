package com.sytycc.sytycc.app.layout.notifications;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.sytycc.sytycc.app.APIListener;
import com.sytycc.sytycc.app.AccessAPI;
import com.sytycc.sytycc.app.MainActivity;
import com.sytycc.sytycc.app.R;
import com.sytycc.sytycc.app.data.Transaction;

import java.util.Stack;

/**
 * Created by Michaël on 30/03/14.
 *
 * Takes care of sending notifications if a task running in the background is completed
 */
public class NotificationService extends IntentService {

    public NotificationService(){
        super("NotificationService");
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void showNotification(String title, String text) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.notification_white)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setAutoCancel(true);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);
        // God immediately to the notifications tab
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
    protected void onHandleIntent(Intent intent) {
        if (AccessAPI.getInstance().isSessionReady()) {
            new NotificationFetcher(getApplicationContext()).execute(new APIListener() {
                @Override
                public void receiveAnswer(Object obj) {
                    if (obj != null) {
                        Stack<Transaction> transactions = (Stack<Transaction>) obj;
                        // New transactions available
                        if (!transactions.empty()) {
                            if (!getApplicationContext().getPackageName().equalsIgnoreCase(((ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE)).getRunningTasks(1).get(0).topActivity.getPackageName())) {
                                // App is not in the foreground
                                showNotification("Notification", "Reuse is still an homo");
                            } else {
                                // Increment number in notifications tab name
                            }
                        }
                    }
                }
            });
        }
    }

}
