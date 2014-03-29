package com.sytycc.sytycc.app.layout.products;

import android.content.Context;
import android.widget.Button;
import android.widget.LinearLayout;

import com.sytycc.sytycc.app.data.Product;

import java.util.List;

/**
 * Created by Roel on 29/03/14.
 */
public class ProductsView extends Button{

    private final Product product;

    public ProductsView(Context context, Product product) {
        super(context);
        this.product = product;
        init(product);
    }

    private void init(Product product) {
        setText(product.getName());

    }
}
