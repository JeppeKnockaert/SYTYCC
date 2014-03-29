package com.sytycc.sytycc.app.data;

/**
 * Created by Roel on 29/03/14.
 */
public class Product implements Serializable{
    public static String TAG = Product.class.getName();

    private String name;
    private String productNumber;
    private int bank;
    private String iban;
    private String bic;
    private String openingDate;
    private int type;
    private int subtype;
    private double availableBalance;
    private double balance;
    private String uuid;

    public Product(String name, String productNumber, int bank, String iban, String bic, String openingDate, int type, int subtype, double availableBalance, double balance, String uuid) {
        this.name = name;
        this.productNumber = productNumber;
        this.bank = bank;
        this.iban = iban;
        this.bic = bic;
        this.openingDate = openingDate;
        this.type = type;
        this.subtype = subtype;
        this.availableBalance = availableBalance;
        this.balance = balance;
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProductNumber() {
        return productNumber;
    }

    public void setProductNumber(String productNumber) {
        this.productNumber = productNumber;
    }

    public int getBank() {
        return bank;
    }

    public void setBank(int bank) {
        this.bank = bank;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getBic() {
        return bic;
    }

    public void setBic(String bic) {
        this.bic = bic;
    }

    public String getOpeningDate() {
        return openingDate;
    }

    public void setOpeningDate(String openingDate) {
        this.openingDate = openingDate;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSubtype() {
        return subtype;
    }

    public void setSubtype(int subtype) {
        this.subtype = subtype;
    }

    public double getAvailableBalance() {
        return availableBalance;
    }

    public void setAvailableBalance(double availableBalance) {
        this.availableBalance = availableBalance;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getUuid(){ return uuid; }

    public void setUuid(String uuid) {this.uuid = uuid; }
}
