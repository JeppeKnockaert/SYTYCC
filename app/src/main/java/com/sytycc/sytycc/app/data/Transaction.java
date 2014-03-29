package com.sytycc.sytycc.app.data;

import java.util.Date;

/**
 * Created by Roel on 29/03/14.
 */
public class Transaction {

    private String bankName;
    private String description;
    private double amount;
    private Date effectiveDate;
    private Date valDate;

    public Transaction(String bankName, String description, double amount, Date effectiveDate, Date valDate) {
        this.bankName = bankName;
        this.description = description;
        this.amount = amount;
        this.effectiveDate = effectiveDate;
        this.valDate = valDate;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public Date getValDate() {
        return valDate;
    }

    public void setValDate(Date valDate) {
        this.valDate = valDate;
    }
}
