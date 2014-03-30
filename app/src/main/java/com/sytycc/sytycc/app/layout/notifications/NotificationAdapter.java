package com.sytycc.sytycc.app.layout.notifications;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sytycc.sytycc.app.R;
import com.sytycc.sytycc.app.data.Notifiable;

import java.util.ArrayList;

/**
 * Created by Michaël on 29/03/14.
 */
public class NotificationAdapter extends ArrayAdapter<Notifiable> {

    private final Context context;
    private final ArrayList<Notifiable> notificationsArrayList;

    public NotificationAdapter(Context context, ArrayList<Notifiable> notificationsArrayList) {
        super(context, R.layout.row, notificationsArrayList);
        this.context = context;
        this.notificationsArrayList = notificationsArrayList;
    }

    public void addNotification(Notifiable notification){
        notificationsArrayList.add(notification);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 1. Create inflater
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 2. Get rowView from inflater
        View rowView = inflater.inflate(R.layout.row, parent, false);

        // 3. Get the two text view from the rowView
        TextView labelView = (TextView) rowView.findViewById(R.id.label);
        TextView valueView = (TextView) rowView.findViewById(R.id.value);
        ImageView valueView2 = (ImageView) rowView.findViewById(R.id.value2);

        // 4. Set the text for textView
        labelView.setText(notificationsArrayList.get(position).getTitle());
        valueView.setText(notificationsArrayList.get(position).getMessage());

        Log.i("diomaijeaioef",""+notificationsArrayList.get(position).isRead());
        if(notificationsArrayList.get(position).isRead()){
            valueView2.setVisibility(View.INVISIBLE);
        } else {
            labelView.setTypeface(Typeface.DEFAULT_BOLD);
        }
        rowView.setBackgroundColor(Color.TRANSPARENT);

        // 5. return rowView
        return rowView;
    }
}