package com.sytycc.sytycc.app.layout.notificationss;

/**
 * Created by Michaël on 29/03/14.
 */
public class Notification {

    private String title;
    private String text;
    private boolean read;

    public Notification(String title, String text){
        this.title = title;
        this.text = text;
        read = false;
    }

    public void markAsRead(){
        read = true;
    }

    public String getTitle(){
        return title;
    }

    public String getText(){
        return text;
    }

    public boolean isRead(){
        return read;
    }
}
