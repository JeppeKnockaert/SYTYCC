package com.sytycc.sytycc.app;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TabHost;

import com.sytycc.sytycc.app.data.Transaction;
import com.sytycc.sytycc.app.layout.transactions.TransactionsAdapter;

import java.util.ArrayList;
import java.util.List;


public class TransactionsActivity extends ActionBarActivity {

    private TabHost tabHost;
    private ListView transactionsListView;
    private TransactionsAdapter transactionsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        List<Transaction> transactionList;
        transactionList = new ArrayList<Transaction>();
        transactionList.add(new Transaction("Traspaso emitido",0.5,"11/10/2013","14650100911708338319","14650100952025956295","TFR","TFR",'G'));

        transactionsAdapter = new TransactionsAdapter(this,transactionList);

        transactionsListView = (ListView) findViewById(R.id.transactionList);
        transactionsListView.setAdapter(transactionsAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.transaction, menu);
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

}
