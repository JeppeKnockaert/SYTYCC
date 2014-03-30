package com.sytycc.sytycc.app.data;

/**
 * Created by jeknocka on 30/03/14.
 */
public abstract class TransactionNotifiable extends Notifiable{

    private Transaction transaction;

    public TransactionNotifiable(Transaction transaction){
        this.transaction = transaction;
    }

    public Transaction getTransaction(){
        return transaction;
    };
}
