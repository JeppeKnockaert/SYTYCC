package com.sytycc.sytycc.app;

import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
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

import com.sytycc.sytycc.app.data.Notification;
import com.sytycc.sytycc.app.data.Product;
import com.sytycc.sytycc.app.data.Transaction;
import com.sytycc.sytycc.app.layout.notifications.NotificationAdapter;
import com.sytycc.sytycc.app.layout.notifications.NotificationService;
import com.sytycc.sytycc.app.layout.products.ProductsAdapter;

import java.util.HashMap;
import java.util.Iterator;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;


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
        if(getIntent().getExtras().getInt("TAB") == 2){
            tabHost.setCurrentTab(2);
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
            startActivity(new Intent(this, SettingsActivity.class));
            // Testing Purposes
            showNotification(R.drawable.notification,getString(R.string.notification_example_title),getString(R.string.notification_example_text));
            return true;
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
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);

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

    private class notificationFetcher extends AsyncTask<String, Void, Void>{

        private int done = 0;

        @Override
        protected Void doInBackground(String... strings) {
            final AccessAPI api = AccessAPI.getInstance();
            api.init(MainActivity.this,new SessionListener() {
                @Override
                public void sessionReady(final String sessionid) {
                    api.getProducts(new APIListener() {
                        @Override
                        public void receiveAnswer(Object obj) {
                            final List<Product> productList = (List<Product>) obj;
                            final Map<String, List<Transaction>> transactionList = new HashMap<String, List<Transaction>>();
                            for (final Product product : productList){
                                api.getProductTransactions(product.getUuid(),new APIListener() {
                                    @Override
                                    public void receiveAnswer(Object obj) {
                                        transactionList.put(product.getUuid(), (List<Transaction>) obj);
                                        if (transactionList.size() == productList.size()){
                                            Stack<Transaction> toadd = retrieveNewTransactions(transactionList);
                                        }
                                    }
                                },sessionid);
                            }
                        }
                    },sessionid);
                }
            });
            return null;
        }

        private Stack<Transaction> retrieveNewTransactions(Map<String, List<Transaction>> transactions){
            Stack<Notification> notifications = Notification.fetchNotificationsFromStorage(MainActivity.this);
            Stack<Transaction> toadd = new Stack<Transaction>();
            for (Map.Entry<String, List<Transaction>> entry : transactions.entrySet()){
                Transaction mostrecentnotification = null;
                List<Transaction> transactionlist = entry.getValue();
                Iterator<Notification> it = notifications.iterator();
                while (it.hasNext()){
                    Notification notification = it.next();
                    if (notification.getCategory() == Notification.Category.TRANSACTION && notification.getOwner().equals(entry.getKey())){
                        mostrecentnotification = notification.getTransaction();
                    }
                }
                int i = 0;
                boolean found = false;
                while (i < transactionlist.size() && !found){
                    Transaction currtransaction = transactionlist.get(i);
                    if (!mostrecentnotification.equals(currtransaction)){
                       toadd.push(currtransaction);
                    }
                    else{
                        found = true;
                    }
                }
            }
            return toadd;
        }
    }

}
