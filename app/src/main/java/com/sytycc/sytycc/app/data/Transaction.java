package com.sytycc.sytycc.app.data;

/**
 * Created by Roel on 29/03/14.
 */
public class Transaction {
    private String description;
    private double amount;
    private String effectiveDate;
    private String accountFrom;
    private String accountTo;
    private String tranCode;
    private String typeDesc;
    private char movementType;

    public Transaction(){

    }

    public Transaction(String description, double amount, String effectiveDate, String accountFrom, String accountTo, String tranCode, String typeDesc, char movementType) {
        this.description = description;
        this.amount = amount;
        this.effectiveDate = effectiveDate;
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.tranCode = tranCode;
        this.typeDesc = typeDesc;
        this.movementType = movementType;
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

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getAccountFrom() {
        return accountFrom;
    }

    public void setAccountFrom(String accountFrom) {
        this.accountFrom = accountFrom;
    }

    public String getAccountTo() {
        return accountTo;
    }

    public void setAccountTo(String accountTo) {
        this.accountTo = accountTo;
    }

    public String getTranCode() {
        return tranCode;
    }

    public void setTranCode(String tranCode) {
        this.tranCode = tranCode;
    }

    public String getTypeDesc() {
        return typeDesc;
    }

    public void setTypeDesc(String typeDesc) {
        this.typeDesc = typeDesc;
    }

    public char getMovementType() {
        return movementType;
    }

    public void setMovementType(char movementType) {
        this.movementType = movementType;
    }
}
