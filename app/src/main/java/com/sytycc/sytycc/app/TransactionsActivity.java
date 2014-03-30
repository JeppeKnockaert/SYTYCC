package com.sytycc.sytycc.app;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sytycc.sytycc.app.data.Product;
import com.sytycc.sytycc.app.data.Transaction;
import com.sytycc.sytycc.app.layout.transactions.TransactionsAdapter;

import java.text.SimpleDateFormat;
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
        final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        final AccessAPI api = AccessAPI.getInstance();
        api.init(this,new SessionListener() {
            @Override
            public void sessionReady() {
                api.getProductTransactions(product.getUuid(),new APIListener() {
                    @Override
                    public void receiveAnswer(Object obj) {
                        ((LinearLayout)transactionsListView.getParent()).removeView(findViewById(R.id.loadingTransactionsText));
                        List<Transaction> transactionList = (List<Transaction>) obj;
                        transactionsAdapter = new TransactionsAdapter(TransactionsActivity.this,transactionList);
                        transactionsListView.setAdapter(transactionsAdapter);
                    }
                });
            }
        });

        transactionsListView = (ListView) findViewById(R.id.transactionList);
        transactionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Dialog d = new Dialog(TransactionsActivity.this);
                d.setTitle("Transaction details");
                d.setContentView(R.layout.transaction_detailed_view);
                ((TextView)d.findViewById(R.id.dateText)).setText(sdf.format(((Transaction)transactionsAdapter.getItem(i)).getEffectiveDate()));
                ((TextView)d.findViewById(R.id.bankNumberText)).setText(((Transaction)transactionsAdapter.getItem(i)).getBankName());
                ((TextView)d.findViewById(R.id.amountText)).setText(""+((Transaction)transactionsAdapter.getItem(i)).getAmount());
                ((TextView)d.findViewById(R.id.descriptionText)).setText(((Transaction)transactionsAdapter.getItem(i)).getDescription());
                d.show();
            }
        });
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

}
