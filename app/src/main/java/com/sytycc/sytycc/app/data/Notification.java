package com.sytycc.sytycc.app.data;

/**
 * Created by jeknocka on 30/03/14.
 */
public class Notification {

    private String title;

    private String message;
    private Category category;
    private String owner;
    private boolean read;
    private Transaction transaction;

    public enum Category {
        INFO,
        TRANSACTION
    }

    public Notification(String title, String message, Category category, String owner) {
        this.title = title;
        this.message = message;
        this.category = category;
        this.owner = owner;
        this.read = false;
    }

    public boolean isRead() {
        return read;
    }

    public void markAsRead() {
        this.read = true;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
