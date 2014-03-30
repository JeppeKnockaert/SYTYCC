package com.sytycc.sytycc.app.utilities;

import android.content.Context;

import com.sytycc.sytycc.app.data.Notifiable;

import java.io.EOFException;
import java.io.File;
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

    private static void testExistance(Context context){
        File notificationfile = new File(context.getFilesDir().getPath().toString()+"/"+filename);
        if (!notificationfile.exists()){
            try {
                ObjectOutputStream oos = new ObjectOutputStream(context.openFileOutput(filename, Context.MODE_PRIVATE));
                Stack<Notifiable> emptystack = new Stack<Notifiable>();
                oos.writeObject(emptystack);
                oos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Stack<Notifiable> stack = fetchNotificationsFromStorage(context);
        }
    }

    public static Stack<Notifiable> fetchNotificationsFromStorage(Context context){
        ObjectInputStream ois = null;
        Stack<Notifiable> transactions = null;
        testExistance(context);
        try {
            ois = new ObjectInputStream(context.openFileInput(filename));
            transactions = (Stack<Notifiable>)ois.readObject();
            ois.close();
        } catch (FileNotFoundException e) {
            System.out.println("189: "+e);
        } catch (IOException e) {
            if (e instanceof EOFException){
                System.out.println("EOF");
                return null;
            }
            else{
                System.out.println("191: " + e);
            }
        } catch (ClassNotFoundException e) {
            System.out.println("193: " + e);
        }
        return transactions;
    }

    public static void addNotificationToStorage(Notifiable notification, Context context){
        ObjectOutputStream oos = null;
        Stack<Notifiable> transactions = null;
        testExistance(context);
        try {
            transactions = fetchNotificationsFromStorage(context);
            transactions.push(notification);

            oos = new ObjectOutputStream(context.openFileOutput(filename, Context.MODE_PRIVATE));
            oos.writeObject(transactions);
            oos.close();
        } catch (FileNotFoundException e) {
            System.out.println("189: "+e);
        } catch (IOException e) {
            System.out.println("191: " + e);
        }
    }
}
