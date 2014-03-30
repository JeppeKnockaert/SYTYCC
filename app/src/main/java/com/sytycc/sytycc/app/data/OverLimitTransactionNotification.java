package com.sytycc.sytycc.app.data;

import java.io.Serializable;

/**
 * Created by jeknocka on 30/03/14.
 */
public class OverLimitTransactionNotification extends TransactionNotifiable implements Serializable {

    public OverLimitTransactionNotification(Transaction transaction){
        super(transaction);
    }

    @Override
    public String getMessage() {
        return "A payment was made from your account that surpasses the limit you set.";
    }

    @Override
    public String getTitle() {
        return "Payment surpasses limit";
    }
}
