package com.sytycc.sytycc.app.layout.transactions;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sytycc.sytycc.app.R;
import com.sytycc.sytycc.app.data.Transaction;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Roel on 29/03/14.
 */
public class TransactionsAdapter extends BaseAdapter{
    private List<Transaction> productList;
    private Context context;

    public TransactionsAdapter(Context context, List<Transaction> productList){
        this.context = context;
        this.productList = productList;
    }

    @Override
    public int getCount() {
        return productList.size();
    }

    @Override
    public Object getItem(int i) {
        return productList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return productList.indexOf(getItem(i));
    }

    /*private view holder class*/
    private class ViewHolder {
        TextView dateText;
        TextView bankNrText;
        TextView amountText;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        final Transaction rowItem = (Transaction) getItem(position);

        if (convertView == null) {
            System.out.println("isnull");
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.transaction_listitem_layout, null);
            holder = new ViewHolder();
            holder.dateText = (TextView) convertView.findViewById(R.id.dateText);
            holder.bankNrText = (TextView) convertView.findViewById(R.id.bankNrText);
            holder.amountText = (TextView) convertView.findViewById(R.id.amountText);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.dateText.setText(rowItem.getEffectiveDate().toString());
        holder.bankNrText.setText(rowItem.getBankName());

        if(rowItem.getAmount() < 0){
            convertView.setBackgroundColor(Color.RED);
        }else{
            convertView.setBackgroundColor(Color.GREEN);
        }
        holder.amountText.setText(""+rowItem.getAmount());

        return convertView;
    }
}
