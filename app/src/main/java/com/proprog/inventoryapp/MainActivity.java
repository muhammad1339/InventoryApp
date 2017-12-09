package com.proprog.inventoryapp;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.proprog.inventoryapp.data.ProductContract;


public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PRODUCT_LOADER = 0;
    private FloatingActionButton fab_add_product;
    private ListView listProduct;
    private ProductCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fab_add_product = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        listProduct = (ListView) findViewById(R.id.list_product);
        fab_add_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DetailedActivity.class);
                startActivity(intent);
            }
        });
        View emptyView = findViewById(R.id.empty_view);
        listProduct.setEmptyView(emptyView);

        mCursorAdapter = new ProductCursorAdapter(MainActivity.this, null);
        listProduct.setAdapter(mCursorAdapter);
        listProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, DetailedActivity.class);
                Log.i("onItemClick","cccc");
                Uri currentUri = ContentUris.withAppendedId(ProductContract.ProductEntry.CONTENT_URI, l);
                intent.setData(currentUri);
                startActivity(intent);
            }
        });
        getSupportLoaderManager().initLoader(PRODUCT_LOADER, null, this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                ProductContract.ProductEntry._ID,
                ProductContract.ProductEntry.COL_PRODUCT_NAME,
                ProductContract.ProductEntry.COL_PRODUCT_QUANTITY,
                ProductContract.ProductEntry.COL_PRODUCT_PRICE,
                ProductContract.ProductEntry.COL_PRODUCT_SUPPLIER_Name,
                ProductContract.ProductEntry.COL_PRODUCT_SUPPLIER_EMAIL};

        return new CursorLoader(this,
                ProductContract.ProductEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
