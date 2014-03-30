package com.sytycc.sytycc.app.data;

/**
 * Created by jeknocka on 30/03/14.
 */
public abstract class TransactionNotifiable extends Notifiable{
    public abstract Transaction getTransaction();
    public abstract String getProduct();
}
