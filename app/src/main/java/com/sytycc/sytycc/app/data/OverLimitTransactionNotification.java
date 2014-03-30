package com.sytycc.sytycc.app.data;

/**
 * Created by jeknocka on 30/03/14.
 */
public class OverLimitTransactionNotification extends TransactionNotifiable {

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
