package com.sytycc.sytycc.app.layout.notifications;

import android.content.Context;
import android.os.AsyncTask;

import com.sytycc.sytycc.app.APIListener;
import com.sytycc.sytycc.app.AccessAPI;
import com.sytycc.sytycc.app.SessionListener;
import com.sytycc.sytycc.app.data.Notifiable;
import com.sytycc.sytycc.app.data.Product;
import com.sytycc.sytycc.app.data.Transaction;
import com.sytycc.sytycc.app.data.TransactionNotifiable;
import com.sytycc.sytycc.app.utilities.IOManager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Created by jeknocka on 30/03/14.
 */
public class NotificationFetcher extends AsyncTask<APIListener, Void, Void> {

    private final Context context;

    public NotificationFetcher(Context context){
        this.context = context;
    }

    @Override
    protected Void doInBackground(final APIListener... listener) {
        final AccessAPI api = AccessAPI.getInstance();
        api.init(context,new SessionListener() {
            @Override
            public void sessionReady() {
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
                                        listener[0].receiveAnswer(toadd);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        return null;
    }



    private Stack<Transaction> retrieveNewTransactions(Map<String, List<Transaction>> transactions){
        Stack<Notifiable> notifications = IOManager.fetchNotificationsFromStorage(context);
        Stack<Transaction> toadd = new Stack<Transaction>();
        if (notifications == null || notifications.isEmpty()){
            Stack<Transaction> transactionstack = new Stack<Transaction>();
            for (Map.Entry<String, List<Transaction>> entry : transactions.entrySet()){
                for (Transaction transaction : entry.getValue()){
                    transactionstack.push(transaction);
                }
            }
            return transactionstack;
        }
        else {
            for (Map.Entry<String, List<Transaction>> entry : transactions.entrySet()) {
                Transaction mostrecentnotification = null;
                List<Transaction> transactionlist = entry.getValue();
                Iterator<Notifiable> it = notifications.iterator();
                while (it.hasNext()) {
                    Notifiable notification = it.next();
                    if (notification instanceof TransactionNotifiable) {
                        TransactionNotifiable transnotification = (TransactionNotifiable) notification;
                        if (transnotification.getTransaction().getProductUuid().equals(entry.getKey())) {
                            mostrecentnotification = transnotification.getTransaction();
                        }
                    }
                }
                int i = 0;
                boolean found = false;
                while (i < transactionlist.size() && !found) {
                    Transaction currtransaction = transactionlist.get(i);
                    if (!mostrecentnotification.equals(currtransaction)) {
                        toadd.push(currtransaction);
                    } else {
                        found = true;
                    }
                }
            }
            return toadd;
        }
    }
}
