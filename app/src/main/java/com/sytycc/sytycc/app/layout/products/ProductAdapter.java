package com.sytycc.sytycc.app.layout.products;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sytycc.sytycc.app.R;
import com.sytycc.sytycc.app.data.Product;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Roel on 29/03/14.
 */
public class ProductAdapter extends BaseAdapter {
    private List<Product> productList;
    private Context context;

    public ProductAdapter(Context context, List<Product> productList){
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
        TextView nameText;
        TextView ibanText;
        TextView newText;
        TextView amountText;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        final Product rowItem = (Product) getItem(position);

        if (convertView == null) {
            System.out.println("isnull");
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.product_listitem_layout, null);
            holder = new ViewHolder();
            holder.nameText = (TextView) convertView.findViewById(R.id.productNameText);
            holder.ibanText = (TextView) convertView.findViewById(R.id.ibanText);
            holder.newText = (TextView) convertView.findViewById(R.id.newText);
            holder.amountText = (TextView) convertView.findViewById(R.id.amountText);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.nameText.setText(rowItem.getName());
        holder.ibanText.setText(rowItem.getIban());
        holder.newText.setText("0");
        holder.amountText.setText(""+rowItem.getBalance());

        return convertView;
    }
}
