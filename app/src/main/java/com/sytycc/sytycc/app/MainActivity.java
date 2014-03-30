package com.sytycc.sytycc.app;

import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import com.sytycc.sytycc.app.layout.notifications.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;

import com.sytycc.sytycc.app.data.Product;
import com.sytycc.sytycc.app.layout.notifications.NotificationAdapter;
import com.sytycc.sytycc.app.layout.notifications.NotificationService;
import com.sytycc.sytycc.app.layout.products.ProductsAdapter;

import java.io.FileOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private TabHost tabHost;
    private ListView productsListView;
    private ProductsAdapter productsAdapter;
    private NotificationAdapter notificationAdapter;

    private static String TAG = MainActivity.class.getSimpleName();
    public static String INTERNAL_STORAGE_FILENAME = "ModifyINGnotifications";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Start service to pull from server and send notifications when the app is
        * running in the background */
        Intent intent = new Intent(this, NotificationService.class);
        startService(intent);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, true);
        setContentView(R.layout.activity_main);

        productsListView = (ListView) findViewById(R.id.productsListView);
        productsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent= new Intent(MainActivity.this, TransactionsActivity.class);
                intent.putExtra(Product.TAG,(Product)productsAdapter.getItem(i));
                startActivity(intent);
            }
        });
        new LoadProducts().execute("");
        tabHost=(TabHost)findViewById(R.id.tabHost);
        tabHost.setup();

        TabHost.TabSpec spec1=tabHost.newTabSpec("Home");
        spec1.setContent(R.id.tab1);
        spec1.setIndicator(getString(R.string.home_tab));

        TabHost.TabSpec spec2=tabHost.newTabSpec("Notifications");
        spec2.setIndicator(getString(R.string.notifications_tab));
        spec2.setContent(R.id.tab2);

        TabHost.TabSpec spec3=tabHost.newTabSpec("Settings");
        spec3.setContent(R.id.tab3);
        spec3.setIndicator(getString(R.string.settings_tab));

        /* show settings fragment in settings tab */
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        SettingsFragment fragment = new SettingsFragment();
        fragmentTransaction.add(R.id.tab3, fragment);
        fragmentTransaction.commit();

        /* Init notifications
        * TODO afwerken */
        loadNotifications();
        ListView notificationsListView = (ListView) findViewById(R.id.notificationsListView);
        notificationsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                /* Ask for pin code to show notification details */
            }
        });
        notificationsListView.setAdapter(notificationAdapter);
        notificationAdapter.setNotifyOnChange(true);

        tabHost.addTab(spec1);
        tabHost.addTab(spec2);
        tabHost.addTab(spec3);

        /* If user arrived here cause of notification, open 2nd tab (notifications) */
        if(getIntent().hasExtra("TAB")){
            tabHost.setCurrentTab(getIntent().getExtras().getInt("TAB"));
        }
    }

    private class LoadProducts extends AsyncTask<String, Void, Void>{
        @Override
        protected Void doInBackground(String... strings) {
            final AccessAPI api = AccessAPI.getInstance();
            api.init(MainActivity.this,new SessionListener() {
                @Override
                public void sessionReady(final String sessionid) {
                    api.getProducts(new APIListener() {
                        @Override
                        public void receiveAnswer(Object obj) {
                            ((LinearLayout)productsListView.getParent()).removeView(findViewById(R.id.loadingProductsText));
                            List<Product> productList = (List<Product>) obj;
                            productsAdapter = new ProductsAdapter(MainActivity.this, productList);
                            productsListView.setAdapter(productsAdapter);
                        }
                    },sessionid);
                }
            });
            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
        }else if(id == R.id.action_cardstop){
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:0495789995"));
            startActivity(callIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt("currentTabNr", tabHost.getCurrentTab());
    }

    protected void onRestoreInstanceState(Bundle bundle){
        super.onRestoreInstanceState(bundle);
        tabHost.setCurrentTab(bundle.getInt("currentTabNr"));
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
        notificationAdapter = new NotificationAdapter(getApplicationContext(),noteList);
        /*
         * Pull from server
         * If in background, call showNotification
         * If not in background, show new (unread notifications) in menubar
         */

        /* Write notification(s) to file */
        String notification = "title///text";
        writeNotificationsToFile(notification);
    }

    private void writeNotificationsToFile(String notification){
        try {
            FileOutputStream fos = openFileOutput(INTERNAL_STORAGE_FILENAME, Context.MODE_APPEND);
            fos.write(notification.getBytes());
            fos.close();
        } catch (IOException e1){
            Log.e(TAG, "Error writing to " + INTERNAL_STORAGE_FILENAME);
        }
    }
}

