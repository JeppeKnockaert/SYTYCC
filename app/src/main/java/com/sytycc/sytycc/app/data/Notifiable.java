package com.sytycc.sytycc.app.data;

/**
 * Created by jeknocka on 30/03/14.
 */
public abstract class Notifiable {

    private boolean read;

    public Notifiable(){
        read = false;
    }

    public abstract String getMessage();
    public abstract String getTitle();

    public boolean isRead() {
        return read;
    }
    public void markAsRead() {
        read = true;
    }

}
