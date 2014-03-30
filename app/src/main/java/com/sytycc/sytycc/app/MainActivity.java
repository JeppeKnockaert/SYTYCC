package com.sytycc.sytycc.app;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import com.sytycc.sytycc.app.data.InfoNotification;
import com.sytycc.sytycc.app.data.Notifiable;
import com.sytycc.sytycc.app.data.OverLimitTransactionNotification;
import com.sytycc.sytycc.app.data.Product;
import com.sytycc.sytycc.app.data.Transaction;
import com.sytycc.sytycc.app.data.TransactionNotifiable;
import com.sytycc.sytycc.app.layout.notifications.NotificationAdapter;
import com.sytycc.sytycc.app.layout.notifications.NotificationReceiver;
import com.sytycc.sytycc.app.layout.notifications.NotificationService;
import com.sytycc.sytycc.app.layout.products.ProductsAdapter;
import com.sytycc.sytycc.app.utilities.IOManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


public class MainActivity extends ActionBarActivity {

    public TabHost tabHost;
    private ListView productsListView;
    private ListView notificationsListView;
    private ProductsAdapter productsAdapter;
    private NotificationAdapter notificationAdapter;

    private static String TAG = MainActivity.class.getSimpleName();
    public static String INTERNAL_STORAGE_FILENAME = "ModifyINGnotifications";

    private static int PIN_LENGTH = 6;
    private static int SERVER_CHECK_INTERVAL = 5000; // Millisecs, time between server pulls

    private boolean updateNotificationsAdapter = false;
    private static MainActivity instance;
    private boolean pulling = false;
    private int notificationAmount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;

         /* Start service to pull from server and send notifications when the app is
                     * running in the background */
        Intent intent = new Intent(this, NotificationService.class);
        startService(intent);

        PreferenceManager.setDefaultValues(this, R.xml.preferences_account, true);
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
        spec1.setIndicator("",getResources().getDrawable(R.drawable.home_white));

        TabHost.TabSpec spec2=tabHost.newTabSpec("Notifications");
        /*ImageView notifyIcon = new ImageView(this);
        notifyIcon.setImageResource(R.drawable.warning);
        notifyIcon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);*/
        spec2.setIndicator("",getResources().getDrawable(R.drawable.warning_white));
        //spec2.setIndicator(getString(R.string.notifications_tab));
        spec2.setContent(R.id.tab2);

        TabHost.TabSpec spec3=tabHost.newTabSpec("Settings");
        spec3.setContent(R.id.tab3);
        spec3.setIndicator("",getResources().getDrawable(R.drawable.settings_white));

        /* show settings fragment in settings tab */
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        NotificationSettingsFragment fragment = new NotificationSettingsFragment();
        fragmentTransaction.add(R.id.tab3, fragment);
        fragmentTransaction.commit();

        /* Init notifications*/
        loadNotifications();
        notificationsListView = (ListView) findViewById(R.id.notificationsListView);
        notificationsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                Notifiable n = notificationAdapter.getItem(i);
                n.markAsRead();
                showNotificationDetails(n);
                notificationAdapter.notifyDataSetChanged();
            }
        });
        notificationsListView.setAdapter(notificationAdapter);
        notificationAdapter.setNotifyOnChange(true);

        tabHost.addTab(spec1);
        tabHost.addTab(spec2);
        tabHost.addTab(spec3);

        System.out.println(getIntent().getIntExtra("TAG",1));
        /* If user arrived here cause of notification, open 2nd tab (notifications) */
        if((getIntent() != null) && (getIntent().getExtras() != null) && (getIntent().getExtras().getInt("TAB") == 1)){
            tabHost.setCurrentTab(1);
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(prefs.getBoolean("pref_key_notifications_enabled", false)){
            /* Start periodic checks */
            schedulePulls();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(updateNotificationsAdapter){
            updateNotificationsAdapter = false;
        }
    }

    private void showNotificationDetails(Notifiable notification){
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle("Notification details");
        TextView text = new TextView(MainActivity.this);
        alert.setView(text);

        Dialog d = new Dialog(MainActivity.this);

        d.setTitle("Notification details");

        if(notification instanceof InfoNotification){
            d.setContentView(R.layout.notification_detailed_view);
            ((TextView)d.findViewById(R.id.notification_title)).setText(notification.getTitle());
            ((TextView)d.findViewById(R.id.notification_text)).setText(notification.getMessage());
        } else if (notification instanceof OverLimitTransactionNotification){
            d.setContentView(R.layout.transaction_detailed);
            Transaction t = ((TransactionNotifiable) notification).getTransaction();
            ((TextView)d.findViewById(R.id.transaction_description)).setText(t.getDescription());
            ((TextView)d.findViewById(R.id.transaction_val_date)).setText(t.getValDate().toString());
            ((TextView)d.findViewById(R.id.transaction_effective_date)).setText(t.getEffectiveDate().toString());
            ((TextView)d.findViewById(R.id.transaction_bank_name)).setText(t.getBankName().toString());
            ((TextView)d.findViewById(R.id.transaction_type)).setText(t.getTypeDesc());
            ((TextView)d.findViewById(R.id.transaction_product_uuid)).setText(t.getProductUuid());
            ((TextView)d.findViewById(R.id.transaction_amount)).setText("" + t.getAmount());
        }
        d.show();
        updateNotificationsAdapter = true;
    }

    private class LoadProducts extends AsyncTask<String, Void, Void>{
        @Override
        protected Void doInBackground(String... strings) {
            final AccessAPI api = AccessAPI.getInstance();
            api.init(MainActivity.this,new SessionListener() {
                @Override
                public void sessionReady() {
                    // Fetch products
                    api.getProducts(new APIListener() {
                        @Override
                        public void receiveAnswer(Object obj) {
                            ((LinearLayout)productsListView.getParent()).removeView(findViewById(R.id.loadingProductsText));
                            List<Product> productList = (List<Product>) obj;
                            productsAdapter = new ProductsAdapter(MainActivity.this, productList);
                            productsListView.setAdapter(productsAdapter);
                        }
                    });
                    api.destroySession();
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
            startActivity(new Intent(this, AccountSettingsActivity.class));
            return true;
        }else if(id == R.id.action_cardstop){
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:0495789995"));
            startActivity(callIntent);
        }else if(id == R.id.action_transfer){
            transfer();
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt("currentTabNr", tabHost.getCurrentTab());
        bundle.putInt("notificationAmount", notificationAmount);
    }

    protected void onRestoreInstanceState(Bundle bundle){
        super.onRestoreInstanceState(bundle);
        tabHost.setCurrentTab(bundle.getInt("currentTabNr"));
        setNotificationAmount(bundle.getInt("notificationAmount"));
    }

    public void loadNotifications(){
        ArrayList<Notifiable> noteList = new ArrayList<Notifiable>();
        notificationAdapter = new NotificationAdapter(getApplicationContext(),noteList);

        Stack<Notifiable> notifications = IOManager.fetchNotificationsFromStorage(this);
        if (notifications != null){
            for (Notifiable notification : notifications){
                notificationAdapter.add(notification);
            }
        }

        notificationAdapter.add(new InfoNotification("Info","New message from ING"));
        notificationAdapter.add(new InfoNotification("Salary received","Salary received"));

        /*
         * Pull from server
         * If in background, call showNotification
         * If not in background, show new (unread notifications) in menubar
         */

        /* Write notification(s) to file */

        //writeNotificationsToFile(notification);
    }

    public void schedulePulls() {
        if(!pulling){
            Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);
            // Create a PendingIntent to be triggered when the alarm goes off
            final PendingIntent pIntent = PendingIntent.getBroadcast(this, NotificationReceiver.REQUEST_CODE,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);

            long firstMillis = System.currentTimeMillis();
            int intervalMillis = SERVER_CHECK_INTERVAL;
            AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis, intervalMillis, pIntent);
            pulling = true;
        }
    }

    public void cancelPulls() {
        if(pulling){
            Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);
            final PendingIntent pIntent = PendingIntent.getBroadcast(this, NotificationReceiver.REQUEST_CODE,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            alarm.cancel(pIntent);
            pulling = false;
        }

    }

    public static MainActivity getInstance(){
        return instance;
    }

    public void setNotificationAmount(int amount){
        this.notificationAmount = amount;
        TextView title = (TextView) tabHost.getTabWidget().getChildAt(1).findViewById(android.R.id.title);
        title.setText("" + (amount==0?"":amount));
    }


    public void transfer(){
        System.out.println("transfer");
    }

    public int getNotificationAmount(){
        return notificationAmount;
    }
}
