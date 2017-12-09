package com.proprog.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.proprog.inventoryapp.data.ProductContract.ProductEntry;

/**
 * Created by mohamedAHMED on 2017-12-01.
 */

public class ProductHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "products.db";
    public static final int DB_VERSION = 1;

    public ProductHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql_create_table = "CREATE TABLE " + ProductEntry.TABLE_NAME + "("
                + ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ProductEntry.COL_PRODUCT_NAME + " TEXT NOT NULL,"
                + ProductEntry.COL_PRODUCT_SUPPLIER_Name + " TEXT NOT NULL,"
                + ProductEntry.COL_PRODUCT_SUPPLIER_EMAIL + " TEXT NOT NULL,"
                + ProductEntry.COL_PRODUCT_Image + " BLOB NOT NULL,"
                + ProductEntry.COL_PRODUCT_PRICE + " INTEGER NOT NULL,"
                + ProductEntry.COL_PRODUCT_QUANTITY + " INTEGER NOT NULL"
                + ")";
        sqLiteDatabase.execSQL(sql_create_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
