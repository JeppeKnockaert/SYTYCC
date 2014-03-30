package com.sytycc.sytycc.app.data;

import android.content.Context;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Stack;

/**
 * Created by jeknocka on 30/03/14.
 */
public class Notification {

    private static final String filename = "notifications";

    private String title;

    private String message;
    private Category category;
    private String owner;

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

    public static Stack<Notification> fetchNotificationsFromStorage(Context context){
        ObjectInputStream ois = null;
        Stack<Notification> transactions = null;

        try {
            ois = new ObjectInputStream(context.openFileInput(filename));
            transactions = (Stack<Notification>)ois.readObject();
            ois.close();
        } catch (FileNotFoundException e) {
            System.out.println("189: "+e);
        } catch (IOException e) {
            System.out.println("191: " + e);
        } catch (ClassNotFoundException e) {
            System.out.println("193: " + e);
        }
        return transactions;
    }

    public static void addNotification(Notification notification, Context context){
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        Stack<Notification> transactions = null;

        try {
            ois = new ObjectInputStream(context.openFileInput(filename));
            oos = new ObjectOutputStream(context.openFileOutput(filename, Context.MODE_PRIVATE));
            transactions = (Stack<Notification>)ois.readObject();
            transactions.push(notification);
            oos.writeObject(transactions);
            ois.close();
            oos.close();
        } catch (FileNotFoundException e) {
            System.out.println("189: "+e);
        } catch (IOException e) {
            System.out.println("191: " + e);
        } catch (ClassNotFoundException e) {
            System.out.println("193: " + e);
        }
    }
}
