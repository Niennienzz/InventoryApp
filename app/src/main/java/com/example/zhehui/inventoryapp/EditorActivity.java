package com.example.zhehui.inventoryapp;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.example.zhehui.inventoryapp.data.InventoryItemContract;
import com.example.zhehui.inventoryapp.data.InventoryItemContract.InventoryItemEntry;

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the item data loader.
     */
    private static final int EXISTING_ITEM_LOADER = 0;

    /**
     * Content URI for the existing item (null if it's a new item).
     */
    private Uri mCurrentItemUri;

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
    private EditText mQuantityEditText;

    /**
     * Boolean flag that keeps track of whether the item has been edited (true) or not (false)
     */
    private boolean mItemHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View,
     * implying that they are modifying the view,
     * and we change the mItemHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mItemHasChanged = true;
            return false;
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
        mNameEditText = (EditText) findViewById(R.id.edit_item_name);
        mPriceEditText = (EditText) findViewById(R.id.edit_item_price);
        mQuantityEditText = (EditText) findViewById(R.id.edit_item_quantity);

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all item attributes, define a projection that contains
        // all columns from the item table.
        String[] projection = {
                InventoryItemEntry._ID,
                InventoryItemEntry.COLUMN_ITEM_NAME,
                InventoryItemEntry.COLUMN_ITEM_PRICE,
                InventoryItemEntry.COLUMN_ITEM_QUANTITY};

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
        // Bail early if the cursor is null or there is less than 1 row in the cursor.
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it.
        // This should be the only row in the cursor.
        if (cursor.moveToFirst()) {
            // Find the columns of item attributes that we're interested in.
            int nameColumnIndex = cursor.getColumnIndex(
                    InventoryItemContract.InventoryItemEntry.COLUMN_ITEM_NAME);
            int priceColumnIndex = cursor.getColumnIndex(
                    InventoryItemContract.InventoryItemEntry.COLUMN_ITEM_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(
                    InventoryItemContract.InventoryItemEntry.COLUMN_ITEM_QUANTITY);

            // Read the item attributes from the Cursor for the current item.
            String name = cursor.getString(nameColumnIndex);
            Float price = cursor.getFloat(priceColumnIndex);
            Integer quantity = cursor.getInt(quantityColumnIndex);

            // Update the views on the screen with the values from the database.
            mNameEditText.setText(name);
            mPriceEditText.setText(Float.toString(price));
            mQuantityEditText.setText(Integer.toString(quantity));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
    }

}
