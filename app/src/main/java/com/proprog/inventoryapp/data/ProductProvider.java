package com.proprog.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.proprog.inventoryapp.data.ProductContract.ProductEntry;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.sql.Blob;

import static android.R.attr.name;


/**
 * Created by mohamedAHMED on 2017-12-01.
 */

public class ProductProvider extends ContentProvider {

    public static final UriMatcher sURI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    public static final int PRODUCT_TABLE_CODE = 100;
    public static final int PRODUCT_ID_CODE = 101;
    private ProductHelper productHelper;


    static {
        sURI_MATCHER.addURI(ProductContract.CONTENT_AUTHORITY
                , ProductContract.PATH, PRODUCT_TABLE_CODE);
        sURI_MATCHER.addURI(ProductContract.CONTENT_AUTHORITY
                , ProductContract.PATH + "/#", PRODUCT_ID_CODE);
    }

    @Override
    public boolean onCreate() {
        productHelper = new ProductHelper(getContext());
        return true;
    }


    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int row_deleted = 0;
        SQLiteDatabase database = productHelper.getWritableDatabase();
        int match = sURI_MATCHER.match(uri);
        switch (match) {
            case PRODUCT_TABLE_CODE:
                row_deleted = database.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCT_ID_CODE:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                row_deleted = database.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Nothing is deleted from : " + uri.toString());
        }
        if (row_deleted>0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return row_deleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        int row_updated = 0;
        int match = sURI_MATCHER.match(uri);
        switch (match) {
            case PRODUCT_TABLE_CODE:
                row_updated = updateProduct(uri, contentValues, selection, selectionArgs);
                break;
            case PRODUCT_ID_CODE:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                row_updated = updateProduct(uri, contentValues, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Nothing is updated from : " + uri.toString());

        }
        return row_updated;
    }

    public int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int row_updated = 0;
        validateColumns(values);
        if (values.size() == 0) {
            return 0;
        }
        SQLiteDatabase database = productHelper.getWritableDatabase();
        row_updated = database.update(ProductEntry.TABLE_NAME, values, selection, selectionArgs);
        if (row_updated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return row_updated;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sURI_MATCHER.match(uri);
        switch (match) {
            case PRODUCT_TABLE_CODE:
                return ProductEntry.CONTENT_LIST;
            case PRODUCT_ID_CODE:
                return ProductEntry.CONTENT_ITEM;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection
            , @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;
        SQLiteDatabase database = productHelper.getReadableDatabase();

        int match = sURI_MATCHER.match(uri);
        switch (match) {
            case PRODUCT_TABLE_CODE:
                cursor = database.query(ProductEntry.TABLE_NAME, projection
                        , selection, selectionArgs, null, null, sortOrder);
                break;
            case PRODUCT_ID_CODE:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(ProductEntry.TABLE_NAME, projection
                        , selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Can't get the query");
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sURI_MATCHER.match(uri);
        switch (match) {
            case PRODUCT_TABLE_CODE:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    //insert a product
    public Uri insertProduct(Uri uri, ContentValues values) {
        String name = values.getAsString(ProductEntry.COL_PRODUCT_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Name can't be Empty");
        }
        String supplier = values.getAsString(ProductEntry.COL_PRODUCT_SUPPLIER_Name);
        if (supplier == null) {
            throw new IllegalArgumentException("supplier name can't be Empty");
        }

        String email = values.getAsString(ProductEntry.COL_PRODUCT_SUPPLIER_EMAIL);
        if (email == null) {
            throw new IllegalArgumentException("E-Mail can't be Empty");
        }

        byte[] blob = values.getAsByteArray(ProductEntry.COL_PRODUCT_Image);
        if (blob == null) {
            throw new IllegalArgumentException("Image can't be Empty");
        }

        Integer quantity = values.getAsInteger(ProductEntry.COL_PRODUCT_QUANTITY);
        if (quantity == null && quantity < 0) {
            throw new IllegalArgumentException("Quantity can't be negative.");
        }
        Integer price = values.getAsInteger(ProductEntry.COL_PRODUCT_PRICE);
        if (price == null && price < 0) {
            throw new IllegalArgumentException("Price can't be negative.");
        }
        SQLiteDatabase database = productHelper.getWritableDatabase();
        long id = database.insert(ProductEntry.TABLE_NAME, null, values);
        if (id == -1) {
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    private void validateColumns(ContentValues values) {
        if (values.containsKey(ProductEntry.COL_PRODUCT_NAME)) {
            String name = values.getAsString(ProductEntry.COL_PRODUCT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Name can't be Empty");
            }
        }
        if (values.containsKey(ProductEntry.COL_PRODUCT_SUPPLIER_Name)) {
            String supplier = values.getAsString(ProductEntry.COL_PRODUCT_SUPPLIER_Name);
            if (supplier == null) {
                throw new IllegalArgumentException("supplier name can't be Empty");
            }
        }
        if (values.containsKey(ProductEntry.COL_PRODUCT_SUPPLIER_EMAIL)) {
            String email = values.getAsString(ProductEntry.COL_PRODUCT_SUPPLIER_EMAIL);
            if (email == null) {
                throw new IllegalArgumentException("E-Mail can't be Empty");
            }
        }
        if (values.containsKey(ProductEntry.COL_PRODUCT_Image)) {
            byte[] blob = values.getAsByteArray(ProductEntry.COL_PRODUCT_Image);
            if (blob == null) {
                throw new IllegalArgumentException("Image can't be Empty");
            }
        }
        if (values.containsKey(ProductEntry.COL_PRODUCT_QUANTITY)) {
            Integer quantity = values.getAsInteger(ProductEntry.COL_PRODUCT_QUANTITY);
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException("Quantity can't be Negative");
            }
        }
        if (values.containsKey(ProductEntry.COL_PRODUCT_PRICE)) {
            Integer price = values.getAsInteger(ProductEntry.COL_PRODUCT_PRICE);
            if (price != null && price < 0) {
                throw new IllegalArgumentException("Price can't be Negative");
            }
        }
    }
}
