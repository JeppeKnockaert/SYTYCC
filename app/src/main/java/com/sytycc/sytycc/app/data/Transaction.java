package com.sytycc.sytycc.app.data;

import java.util.Date;

/**
 * Created by Roel on 29/03/14.
 */
public class Transaction {

    private String bankName;
    private String description;
    private String typeDesc;
    private double amount;
    private Date effectiveDate;
    private Date valDate;
    private String productUuid;

    public Transaction(String productUuid, String bankName, String description, String typeDesc, double amount, Date effectiveDate, Date valDate) {
        this.bankName = bankName;
        this.description = description;
        this.typeDesc = typeDesc;
        this.amount = amount;
        this.effectiveDate = effectiveDate;
        this.valDate = valDate;
        this.productUuid = productUuid;
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

    public String getTypeDesc() { return typeDesc; }

    public void setTypeDesc(String typeDesc) { this.typeDesc = typeDesc; }

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

    public String getProductUuid() {
        return productUuid;
    }

    public void setProductUuid(String productUuid) {
        this.productUuid = productUuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Transaction that = (Transaction) o;

        if (Double.compare(that.amount, amount) != 0) return false;
        if (bankName != null ? !bankName.equals(that.bankName) : that.bankName != null)
            return false;
        if (!description.equals(that.description)) return false;
        if (!effectiveDate.equals(that.effectiveDate)) return false;
        if (!typeDesc.equals(that.typeDesc)) return false;
        if (!valDate.equals(that.valDate)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = bankName != null ? bankName.hashCode() : 0;
        result = 31 * result + description.hashCode();
        result = 31 * result + typeDesc.hashCode();
        temp = Double.doubleToLongBits(amount);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + effectiveDate.hashCode();
        result = 31 * result + valDate.hashCode();
        return result;
    }
}
