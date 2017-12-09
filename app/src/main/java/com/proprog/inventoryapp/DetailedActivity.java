package com.proprog.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.proprog.inventoryapp.data.ProductContract;

import java.io.ByteArrayOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DetailedActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private TextView productTitle_tv;
    private TextView productPrice_tv;
    private TextView productQuantity_tv;

    private EditText productTitle;
    private EditText productPrice;
    private EditText productQuantity;
    private EditText productSupplier;
    private EditText productEmail;

    private FloatingActionButton fab_delete;
    private FloatingActionButton fab_increase;
    private FloatingActionButton fab_decrease;

    private Button btnOrder;
    private ImageView productImage;
    private Uri uriData;
    private Uri uriImageResponse;
    final static int GALLERY_REQUEST_CODE = 1;
    final static int CAMERA_REQUEST_CODE = 2;
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private static final int PRODUCT_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);
        getSupportActionBar().setTitle("Add Item");
        findViewById(R.id.detail_view).setVisibility(View.GONE);
        findViewById(R.id.edit_view).setVisibility(View.VISIBLE);
        //prepare text views for coming data
        productTitle_tv = (TextView) findViewById(R.id.tv_item_detail);
        productPrice_tv = (TextView) findViewById(R.id.tv_price_detail);
        productQuantity_tv = (TextView) findViewById(R.id.tv_quantity_detail);

        //prepare edittexts to store data
        productTitle = (EditText) findViewById(R.id.product_title_edit_text);
        productPrice = (EditText) findViewById(R.id.product_price_edit_text);
        productQuantity = (EditText) findViewById(R.id.product_quantity_edit_text);
        productSupplier = (EditText) findViewById(R.id.product_supplier_edit_text);
        productEmail = (EditText) findViewById(R.id.product_email_edit_text);
        //prepare image view to get image from camera or gallery
        productImage = (ImageView) findViewById(R.id.product_image);
        //prepare fabs
        fab_delete = (FloatingActionButton) findViewById(R.id.fab_delete);
        fab_increase = (FloatingActionButton) findViewById(R.id.fab_increase);
        fab_decrease = (FloatingActionButton) findViewById(R.id.fab_decrease);
        btnOrder = (Button) findViewById(R.id.btn_order);
        fab_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (uriData != null) {
                    final String selection = ProductContract.ProductEntry._ID + "=?";
                    final String[] selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uriData))};
                    AlertDialog.Builder dialog = new AlertDialog.Builder(DetailedActivity.this);
                    dialog.setCancelable(true);
                    dialog.setTitle("Are you sure you want to delete this item? ");
                    dialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            int d = getContentResolver().delete(uriData, selection, selectionArgs);
                            if (d > 0) {
                                Toast.makeText(DetailedActivity.this, "deleted", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(DetailedActivity.this, "Not deleted", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    dialog.create().show();
                }
            }
        });
        fab_increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int q = 1;
                changeQuantity(q, DetailedActivity.this, uriData);
            }
        });
        fab_decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int q = -1;
                changeQuantity(q, DetailedActivity.this, uriData);
            }
        });
        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (uriData != null) {
                    String selection = ProductContract.ProductEntry._ID + "=?";
                    String[] selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uriData))};
                    String[] projection = {
                            ProductContract.ProductEntry.COL_PRODUCT_NAME,
                            ProductContract.ProductEntry.COL_PRODUCT_SUPPLIER_Name,
                            ProductContract.ProductEntry.COL_PRODUCT_SUPPLIER_EMAIL};
                    Cursor cursor = getContentResolver().query(uriData, projection, selection, selectionArgs, null);
                    if (cursor.moveToFirst()) {
                        int name_index = cursor.getColumnIndex(ProductContract.ProductEntry.COL_PRODUCT_NAME);
                        int supplier_index = cursor.getColumnIndex(ProductContract.ProductEntry.COL_PRODUCT_SUPPLIER_Name);
                        int email_index = cursor.getColumnIndex(ProductContract.ProductEntry.COL_PRODUCT_SUPPLIER_EMAIL);
                        String name = cursor.getString(name_index);
                        String supplier = cursor.getString(supplier_index);
                        String email = cursor.getString(email_index);
                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setData(Uri.parse("mailto:" + email));
                        intent.putExtra(Intent.EXTRA_EMAIL, email);
                        intent.putExtra(Intent.EXTRA_SUBJECT, name);
                        String body = "Dear " + supplier + ", \n" + "can you provide me with " + name;
                        intent.putExtra(Intent.EXTRA_TEXT, body);
                        startActivity(Intent.createChooser(intent, "send email to "));
                    }
                }
            }
        });
        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(DetailedActivity.this);
                dialog.setCancelable(true);
                dialog.setTitle("Choose Image ");
                dialog.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, CAMERA_REQUEST_CODE);
                    }
                });
                dialog.setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, GALLERY_REQUEST_CODE);
                    }
                });
                dialog.create().show();
            }
        });
        Intent intent = getIntent();
        uriData = intent.getData();
        if (uriData != null) {
            getSupportActionBar().setTitle("Item Details");
            getSupportLoaderManager().initLoader(PRODUCT_LOADER, null, DetailedActivity.this);

            findViewById(R.id.edit_view).setVisibility(View.GONE);
            findViewById(R.id.detail_view).setVisibility(View.VISIBLE);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (uriData == null) {
            getMenuInflater().inflate(R.menu.edit_product_menu, menu);
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_done:
                saveProduct();
                break;
            default:
                finish();
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAMERA_REQUEST_CODE:
                    Toast.makeText(this, "I Got IT", Toast.LENGTH_SHORT).show();
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    productImage.setImageBitmap(bitmap);
                    break;
                case GALLERY_REQUEST_CODE:
                    uriImageResponse = data.getData();
                    Toast.makeText(this, "I Got IT " + uriImageResponse.getPath(), Toast.LENGTH_LONG).show();
                    Glide.with(DetailedActivity.this).asBitmap().load(uriImageResponse).into(productImage);
                    break;
                default:
                    break;
            }
        }
    }

    private void saveProduct() {
        String title = productTitle.getText().toString().trim();
        String price = productPrice.getText().toString().trim();
        String quantity = productQuantity.getText().toString().trim();
        String supplier = productSupplier.getText().toString().trim();
        String email = productEmail.getText().toString().trim();
        BitmapDrawable img_drawable = (BitmapDrawable) productImage.getDrawable();
        if (uriData == null &&
                TextUtils.isEmpty(title) && TextUtils.isEmpty(price) &&
                TextUtils.isEmpty(quantity) && TextUtils.isEmpty(supplier) &&
                TextUtils.isEmpty(email) && img_drawable == null) {
            return;
        }
        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, "please provide a valid product name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(price)) {
            Toast.makeText(this, "please provide a valid product price", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(quantity)) {
            Toast.makeText(this, "please provide a valid product quantity", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(supplier)) {
            Toast.makeText(this, "please provide a valid product supplier name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isEmailValid(email)) {
            Toast.makeText(this, "please provide a valid e-mail", Toast.LENGTH_SHORT).show();
            return;
        }
        Bitmap bitmap = img_drawable.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageBytes = stream.toByteArray();
        ContentValues values = new ContentValues();
        values.put(ProductContract.ProductEntry.COL_PRODUCT_NAME, title);
        values.put(ProductContract.ProductEntry.COL_PRODUCT_PRICE, price);
        values.put(ProductContract.ProductEntry.COL_PRODUCT_QUANTITY, quantity);
        values.put(ProductContract.ProductEntry.COL_PRODUCT_SUPPLIER_Name, supplier);
        values.put(ProductContract.ProductEntry.COL_PRODUCT_SUPPLIER_EMAIL, email);
        values.put(ProductContract.ProductEntry.COL_PRODUCT_Image, imageBytes);
        if (uriData == null) {
            getContentResolver().insert(ProductContract.ProductEntry.CONTENT_URI, values);
            finish();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ProductContract.ProductEntry._ID,
                ProductContract.ProductEntry.COL_PRODUCT_NAME,
                ProductContract.ProductEntry.COL_PRODUCT_QUANTITY,
                ProductContract.ProductEntry.COL_PRODUCT_PRICE,
                ProductContract.ProductEntry.COL_PRODUCT_SUPPLIER_Name,
                ProductContract.ProductEntry.COL_PRODUCT_SUPPLIER_EMAIL};

        return new CursorLoader(this,
                uriData,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() < 1) {
            return;
        }
        if (data.moveToFirst()) {
            int name_index = data.getColumnIndex(ProductContract.ProductEntry.COL_PRODUCT_NAME);
            int price_index = data.getColumnIndex(ProductContract.ProductEntry.COL_PRODUCT_PRICE);
            int quantity_index = data.getColumnIndex(ProductContract.ProductEntry.COL_PRODUCT_QUANTITY);
            String name = data.getString(name_index);
            int price = data.getInt(price_index);
            int quantity = data.getInt(quantity_index);
            productTitle_tv.setText(name);
            productPrice_tv.setText("Price : " + String.valueOf(price) + "$");
            productQuantity_tv.setText("Quantity : " + String.valueOf(quantity));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        productTitle_tv.setText("");
        productQuantity_tv.setText("");
        productPrice_tv.setText("");
    }

    public void changeQuantity(int q, Context context, Uri uri) {
        if (uri != null) {
            String selection = ProductContract.ProductEntry._ID + "=?";
            String[] selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
            String[] projection = new String[]{ProductContract.ProductEntry.COL_PRODUCT_QUANTITY};
            Cursor c = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (c.moveToFirst()) {
                int quantity = c.getInt(c.getColumnIndex(ProductContract.ProductEntry.COL_PRODUCT_QUANTITY));
                quantity += q;
                if (quantity < 0) {
                    Toast.makeText(context, "Quantity Can't Be decreased any more", Toast.LENGTH_SHORT).show();
                    return;
                }
                ContentValues values = new ContentValues();
                values.put(ProductContract.ProductEntry.COL_PRODUCT_QUANTITY, quantity);
                int u = context.getContentResolver().update(uri, values, selection, selectionArgs);
                if (u > 0) {
                    Toast.makeText(context, "updated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "not updated", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public static boolean isEmailValid(String email) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return matcher.find();
    }
}
