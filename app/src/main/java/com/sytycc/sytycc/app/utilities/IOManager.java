package com.sytycc.sytycc.app.utilities;

import android.content.Context;

import com.sytycc.sytycc.app.data.Notification;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Stack;

/**
 * Created by jeknocka on 30/03/14.
 */
public class IOManager {

    private static final String filename = "notifications";

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

    public static void addNotificationToStorage(Notification notification, Context context){
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
