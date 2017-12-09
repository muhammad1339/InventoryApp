package com.proprog.inventoryapp;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.proprog.inventoryapp.data.ProductContract;

import java.text.NumberFormat;

/**
 * Created by mohamedAHMED on 2017-12-04.
 */

public class ProductCursorAdapter extends CursorAdapter {
    private Context mContext ;
    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
        mContext=context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        MyViewHolder viewHolder = new MyViewHolder(view);
        int id_index = cursor.getColumnIndex(ProductContract.ProductEntry._ID);
        int item_index = cursor.getColumnIndex(ProductContract.ProductEntry.COL_PRODUCT_NAME);
        int quantity_index = cursor.getColumnIndex(ProductContract.ProductEntry.COL_PRODUCT_QUANTITY);
        int price_index = cursor.getColumnIndex(ProductContract.ProductEntry.COL_PRODUCT_PRICE);

        String item = cursor.getString(item_index);
        int quantity = cursor.getInt(quantity_index);
        int price = cursor.getInt(price_index);
        final int id = cursor.getInt(id_index);
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance();
        String priceMoneyFormat = numberFormat.format(price);
        viewHolder.tv_item.setText(item);
        viewHolder.tv_quantity.setText(String.valueOf(quantity) + " item");
        viewHolder.tv_price.setText(priceMoneyFormat);
        viewHolder.btnSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri currentUri = ContentUris.withAppendedId(ProductContract.ProductEntry.CONTENT_URI, id);
                new DetailedActivity().changeQuantity(-1,context,currentUri);
            }
        });
    }

    private class MyViewHolder {
        private TextView tv_item;
        private TextView tv_quantity;
        private TextView tv_price;
        private Button btnSale;

        public MyViewHolder(View view) {
            tv_item = view.findViewById(R.id.tv_item);
            tv_quantity = view.findViewById(R.id.tv_quantity);
            tv_price = view.findViewById(R.id.tv_price);
            btnSale = view.findViewById(R.id.btn_sale);
        }
    }

}
