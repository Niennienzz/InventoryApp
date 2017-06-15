package com.example.zhehui.inventoryapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zhehui.inventoryapp.data.InventoryItemContract.InventoryItemEntry;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = EditorActivity.class.getName();
    private static final int PICK_IMAGE_REQUEST = 0;

    /**
     * Identifier for the item data loader.
     */
    private static final int EXISTING_ITEM_LOADER = 0;

    /**
     * Content URI for the existing item (null if it's a new item).
     */
    private Uri mCurrentItemUri;

    /**
     * Image view for them item's name.
     */
    private ImageView mImageView;

    /**
     * EditText field to enter the item's name.
     */
    private EditText mNameEditText;

    /**
     * EditText field to enter the item's price.
     */
    private EditText mPriceEditText;

    /**
     * EditText field to enter the item's quantity.
     */
    private TextView mQuantityTextView;

    /**
     * EditText field to enter the item's information.
     */
    private EditText mInfoEditText;

    /**
     * EditText field to enter the item's supplier email.
     */
    private EditText mEmailEditText;

    /**
     * Gallery URI of the picture of the item.
     */
    private Uri mImageUri;

    /**
     * Boolean flag that keeps track of whether the item has been edited (true) or not (false).
     */
    private boolean mItemHasChanged = false;

    /**
     * TextWatcher to monitor text fields.
     */
    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            mItemHasChanged = true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new item or editing an existing one.
        Intent intent = getIntent();
        mCurrentItemUri = intent.getData();

        // If the intent DOES NOT contain a item content URI,
        // then we know that we are creating a new item.
        if (mCurrentItemUri == null) {
            // This is a new item, so change the app bar to say "Add a Item"
            setTitle(getString(R.string.editor_activity_title_new_item));

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // It doesn't make sense to delete a item that hasn't been created yet.
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing item, so change app bar to say "Edit Item".
            setTitle(getString(R.string.editor_activity_title_edit_item));

            // Initialize a loader to read the item data from the database
            // and display the current values in the editor.
            getLoaderManager().initLoader(EXISTING_ITEM_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from.
        mImageView = (ImageView) findViewById(R.id.editor_item_pic);
        mNameEditText = (EditText) findViewById(R.id.editor_item_name);
        mPriceEditText = (EditText) findViewById(R.id.editor_item_price);
        mQuantityTextView = (TextView) findViewById(R.id.editor_item_quantity);
        mInfoEditText = (EditText) findViewById(R.id.editor_item_info);
        mEmailEditText = (EditText) findViewById(R.id.editor_item_email);

        // Setup TextWatchers on all the input fields.
        mNameEditText.addTextChangedListener(mTextWatcher);
        mPriceEditText.addTextChangedListener(mTextWatcher);
        mQuantityTextView.addTextChangedListener(mTextWatcher);
        mInfoEditText.addTextChangedListener(mTextWatcher);
        mEmailEditText.addTextChangedListener(mTextWatcher);

        // Setup the button listener for add & update.
        Button buttonConfirm = (Button) findViewById(R.id.editor_save_item);
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveItem();
            }
        });

        // Setup the button listener for delete.
        Button buttonDelete = (Button) findViewById(R.id.editor_delete);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });

        // Setup the button listener for pick picture button
        Button buttonPickPic = (Button) findViewById(R.id.editor_pick_picture);
        buttonPickPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (Build.VERSION.SDK_INT < 19) {
                    intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("*/*");
                    startActivityForResult(intent, PICK_IMAGE_REQUEST);
                } else {
                    intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("*/*");
                    startActivityForResult(intent, PICK_IMAGE_REQUEST);
                }
            }
        });

        // Setup the button listener for order more from supplier.
        Button buttonOrderMore = (Button) findViewById(R.id.editor_order);
        buttonOrderMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_EMAIL, mEmailEditText.getText().toString());
                intent.putExtra(Intent.EXTRA_SUBJECT, "Order");
                intent.putExtra(Intent.EXTRA_TEXT, mInfoEditText.getText().toString());
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        // Setup the button listener for sell.
        Button buttonSell = (Button) findViewById(R.id.editor_item_sell);
        buttonSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantityString = mQuantityTextView.getText().toString();
                int quantity = Integer.parseInt(quantityString);
                if (quantity > 0) {
                    quantity -= 1;
                }
                mQuantityTextView.setText(Integer.toString(quantity));
            }
        });

        // Setup the button listener for receive.
        Button buttonReceive = (Button) findViewById(R.id.editor_item_receive);
        buttonReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantityString = mQuantityTextView.getText().toString();
                int quantity = Integer.parseInt(quantityString);
                quantity += 1;
                mQuantityTextView.setText(Integer.toString(quantity));
            }
        });
    }

    private void saveItem() {
        // All fields are up-to-date.
        mItemHasChanged = false;

        // Read from input fields.
        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityTextView.getText().toString().trim();
        String infoString = mInfoEditText.getText().toString().trim();
        String emailString = mEmailEditText.getText().toString().trim();

        // Check if a picture is picked.
        String imageString = "";
        if (mImageUri != null) {
            imageString = mImageUri.toString().trim();
        }

        // Check if this is supposed to be a new item
        // and check if all the fields in the editor are blank.
        if (mCurrentItemUri == null &&
                TextUtils.isEmpty(nameString) &&
                TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(quantityString) &&
                TextUtils.isEmpty(infoString) &&
                TextUtils.isEmpty(emailString)) {
            Snackbar.make(
                    findViewById(R.id.editor_coordinator),
                    R.string.user_fields_empty,
                    Snackbar.LENGTH_SHORT)
                    .show();
            return;
        }

        // Validate input strings, cannot be empty.
        if (TextUtils.isEmpty(nameString)) {
            Snackbar.make(
                    findViewById(R.id.editor_coordinator),
                    R.string.user_item_name_empty,
                    Snackbar.LENGTH_SHORT)
                    .show();
            return;
        }
        if (TextUtils.isEmpty(infoString)) {
            Snackbar.make(
                    findViewById(R.id.editor_coordinator),
                    R.string.user_item_info_empty,
                    Snackbar.LENGTH_SHORT)
                    .show();
            return;
        }
        if (TextUtils.isEmpty(emailString)) {
            Snackbar.make(
                    findViewById(R.id.editor_coordinator),
                    R.string.user_item_email_empty,
                    Snackbar.LENGTH_SHORT)
                    .show();
            return;
        }

        // Validate input format, Float for price and Integer for quantity.
        Float priceFloat;
        Integer quantityInteger;
        try {
            priceFloat = Float.parseFloat(priceString);
        } catch (NumberFormatException e) {
            Log.e(LOG_TAG, "Error parsing price value, require 'Float'.");
            Snackbar.make(
                    findViewById(R.id.editor_coordinator),
                    R.string.user_item_price_invalid,
                    Snackbar.LENGTH_SHORT)
                    .show();
            return;
        }

        try {
            quantityInteger = Integer.parseInt(quantityString);
        } catch (NumberFormatException e) {
            Log.e(LOG_TAG, "Error parsing quantity value, require 'Integer'.");
            Snackbar.make(
                    findViewById(R.id.editor_coordinator),
                    R.string.user_item_quantity_invalid,
                    Snackbar.LENGTH_SHORT)
                    .show();
            return;
        }

        // Get values.
        ContentValues values = new ContentValues();
        values.put(InventoryItemEntry.COLUMN_ITEM_NAME, nameString);
        values.put(InventoryItemEntry.COLUMN_ITEM_PRICE, priceFloat);
        values.put(InventoryItemEntry.COLUMN_ITEM_QUANTITY, quantityInteger);
        values.put(InventoryItemEntry.COLUMN_ITEM_INFO, infoString);
        values.put(InventoryItemEntry.COLUMN_ITEM_EMAIL, emailString);
        values.put(InventoryItemEntry.COLUMN_ITEM_PICTURE, imageString);
        Log.i(LOG_TAG, "Saving Image URI String: " + imageString);

        // Determine adding new item or update existing item.
        if (mCurrentItemUri == null) {
            Uri newUri = getContentResolver().insert(InventoryItemEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_item_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_item_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentItemUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_update_item_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_update_item_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    private void deleteItem() {
        // Only perform the delete if this is an existing item.
        if (mCurrentItemUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentItemUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_item_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_item_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity.
        finish();
    }

    // Satisfy the Cursor Loader interface.
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all item attributes, define a projection that contains
        // all columns from the item table.
        String[] projection = {
                InventoryItemEntry._ID,
                InventoryItemEntry.COLUMN_ITEM_NAME,
                InventoryItemEntry.COLUMN_ITEM_PRICE,
                InventoryItemEntry.COLUMN_ITEM_QUANTITY,
                InventoryItemEntry.COLUMN_ITEM_INFO,
                InventoryItemEntry.COLUMN_ITEM_EMAIL,
                InventoryItemEntry.COLUMN_ITEM_PICTURE};

        // This loader will execute the ContentProvider's query method on a background thread.
        return new CursorLoader(this,   // Parent activity context.
                mCurrentItemUri,        // Query the content URI for the current item.
                projection,             // Columns to include in the resulting Cursor.
                null,                   // No selection clause.
                null,                   // No selection arguments.
                null);                  // Default sort order.
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Fail early if the cursor is null or there is less than 1 row in the cursor.
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it.
        // There should be the only row in the cursor.
        if (cursor.moveToFirst()) {
            // Find the columns of item attributes that we're interested in.
            int nameColumnIndex = cursor.getColumnIndex(
                    InventoryItemEntry.COLUMN_ITEM_NAME);
            int priceColumnIndex = cursor.getColumnIndex(
                    InventoryItemEntry.COLUMN_ITEM_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(
                    InventoryItemEntry.COLUMN_ITEM_QUANTITY);
            int infoColumnIndex = cursor.getColumnIndex(
                    InventoryItemEntry.COLUMN_ITEM_INFO);
            int emailColumnIndex = cursor.getColumnIndex(
                    InventoryItemEntry.COLUMN_ITEM_EMAIL);
            int imageColumnIndex = cursor.getColumnIndex(
                    InventoryItemEntry.COLUMN_ITEM_PICTURE);

            // Read the item attributes from the Cursor for the current item.
            String name = cursor.getString(nameColumnIndex);
            Float price = cursor.getFloat(priceColumnIndex);
            Integer quantity = cursor.getInt(quantityColumnIndex);
            String info = cursor.getString(infoColumnIndex);
            String email = cursor.getString(emailColumnIndex);
            String imageString = cursor.getString(imageColumnIndex);

            // Update the views on the screen with the values from the database.
            mNameEditText.setText(name);
            mPriceEditText.setText(Float.toString(price));
            mQuantityTextView.setText(Integer.toString(quantity));
            mInfoEditText.setText(info);
            mEmailEditText.setText(email);

            if (!TextUtils.isEmpty(imageString)) {
                Log.i(LOG_TAG, "Setting image URI from String: " + imageString);
                mImageUri = Uri.parse(imageString);
                if (mImageUri != null) {
                    mImageView.setImageBitmap(getBitmapFromUri(mImageUri));
                }
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mImageView.setImageBitmap(getBitmapFromUri(null));
        mNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityTextView.setText("");
        mInfoEditText.setText("");
        mEmailEditText.setText("");
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the item hasn't changed, continue with handling back button press.
        if (!mItemHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes.
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    /**
     * This method is called when the 'up-back' button is pressed.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // If the item hasn't changed, continue with handling back button press.
                if (!mItemHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Prompt the user to leave that fields of current item has changed.
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Prompt the user to confirm that they want to delete this item.
     */
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the item.
                deleteItem();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (resultData != null) {
                mImageUri = resultData.getData();
                Log.i(LOG_TAG, "Uri: " + mImageUri.toString());
                mImageView.setImageBitmap(getBitmapFromUri(mImageUri));
            }
        }
    }

    /**
     * Get Bitmap from image content URI.
     * Reference: http://github.com/crlsndrsjmnz/MyShareImageExample
     */
    public Bitmap getBitmapFromUri(Uri uri) {
        if (uri == null || uri.toString().isEmpty())
            return null;

        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        InputStream input = null;
        try {
            input = this.getContentResolver().openInputStream(uri);
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null, bmOptions);
            input.close();

            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;
            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            input = this.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(input, null, bmOptions);
            input.close();
            return bitmap;

        } catch (FileNotFoundException fne) {
            Log.e(LOG_TAG, "Failed to load image, file not found.", fne);
            return null;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to load image.", e);
            return null;
        } finally {
            try {
                input.close();
            } catch (IOException ioe) {
                Log.e(LOG_TAG, "Failed to close input stream.", ioe);
                return null;
            }
        }
    }

}
