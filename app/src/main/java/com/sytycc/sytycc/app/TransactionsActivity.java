package com.sytycc.sytycc.app;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.sytycc.sytycc.app.data.Product;
import com.sytycc.sytycc.app.data.Transaction;
import com.sytycc.sytycc.app.layout.transactions.TransactionsAdapter;

import java.util.ArrayList;
import java.util.List;


public class TransactionsActivity extends ActionBarActivity {

    private Product product;
    private TextView nameText;
    private TextView ibanText;
    private TextView newText;
    private TextView amountText;


    private ListView transactionsListView;
    private TransactionsAdapter transactionsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        RelativeLayout includeView = (RelativeLayout)findViewById(R.id.productListItem);
        nameText = (TextView) includeView.findViewById(R.id.productNameText);
        ibanText = (TextView) includeView.findViewById(R.id.ibanText);
        newText = (TextView) includeView.findViewById(R.id.newText);
        amountText = (TextView) includeView.findViewById(R.id.amountText);

        System.out.println(savedInstanceState);
        System.out.println(getIntent().getExtras().keySet());

        product = (Product) getIntent().getExtras().getSerializable(Product.TAG);
        System.out.println(product);
        System.out.println(product.getName());

        nameText.setText(product.getName());
        ibanText.setText(product.getIban());
        newText.setText("0");
        amountText.setText(""+product.getBalance());

        List<Transaction> transactionList = new ArrayList<Transaction>();
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
