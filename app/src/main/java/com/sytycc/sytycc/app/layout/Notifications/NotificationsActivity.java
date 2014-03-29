package com.sytycc.sytycc.app.layout.notifications;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.sytycc.sytycc.app.MainActivity;
import com.sytycc.sytycc.app.R;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class NotificationsActivity extends ActionBarActivity {

    private static String TAG = NotificationsActivity.class.getSimpleName();

    private NotificationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        ListView notifications = (ListView) findViewById(R.id.listView);

        /* TODO read notifications from file and load into arraylist */
        loadNotifications();
        notifications.setAdapter(adapter);
        adapter.setNotifyOnChange(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.notifications, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadNotifications(){
        ArrayList<Notification> noteList = new ArrayList<Notification>();
        /* TODO read from internal storage and add */
        noteList.add(new Notification("Title 1","Reuse is nen homo 1"));
        noteList.add(new Notification("Title 2","Reuse is nen homo 2"));
        Notification n1 = new Notification("Title 3","Reuse is nen homo 3");
        Notification n2 = new Notification("Title 4","Reuse is nen homo 4");
        Notification n3 = new Notification("Title 5","Reuse is nen homo 5");
        n1.markAsRead();
        n2.markAsRead();
        n3.markAsRead();
        noteList.add(n1);
        noteList.add(n2);
        noteList.add(n3);
        adapter = new NotificationAdapter(getApplicationContext(),noteList);
    }

    private void checkNotifications(){
        /* TODO pull from server (heartbeat)
        * If there are new notifications
        * - add to adapter
        * - write to file */

        // TODO uncomment
        // writeNotificationsToFile( .. );
     }

    private void writeNotificationsToFile(String notification){
        try {
            FileOutputStream fos = openFileOutput(MainActivity.INTERNAL_STORAGE_FILENAME, Context.MODE_APPEND);
            fos.write(notification.getBytes());
            fos.close();
        } catch (IOException e1){
            Log.e(TAG, "Error writing to " + MainActivity.INTERNAL_STORAGE_FILENAME);
        }
    }
}
