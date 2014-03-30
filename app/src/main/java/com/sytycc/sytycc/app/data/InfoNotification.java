package com.sytycc.sytycc.app.data;

/**
 * Created by jeknocka on 30/03/14.
 */
public class InfoNotification extends Notifiable {

    private final String message;
    private final String title;

    public InfoNotification(String title, String message){
        this.title = title;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getTitle() {
        return title;
    }
}
