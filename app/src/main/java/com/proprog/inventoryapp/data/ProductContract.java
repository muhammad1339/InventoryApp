package com.proprog.inventoryapp.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.UriMatcher;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by mohamedAHMED on 2017-12-01.
 */

public class ProductContract {
    public static final String CONTENT_AUTHORITY = "com.proprog.inventoryapp";
    public static final String PATH = "products";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public ProductContract() {
    }

    public static final class ProductEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH);
        public static final String CONTENT_LIST =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                        + CONTENT_AUTHORITY + "/"
                        + PATH;
        public static final String CONTENT_ITEM =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
                        + CONTENT_AUTHORITY + "/"
                        + PATH;
        //table name
        public static final String TABLE_NAME = "products";

        //columns name
        public final static String _ID = BaseColumns._ID;
        public static final String COL_PRODUCT_NAME = "name";
        public static final String COL_PRODUCT_SUPPLIER_EMAIL = "email";
        public static final String COL_PRODUCT_SUPPLIER_Name = "supplier";
        public static final String COL_PRODUCT_Image = "image";
        public static final String COL_PRODUCT_QUANTITY = "quantity";
        public static final String COL_PRODUCT_PRICE = "price";


    }
}
