package com.example.zhehui.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.zhehui.inventoryapp.data.InventoryItemContract.InventoryItemEntry;

public class CatalogActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the item data loader
     */
    private static final int ITEM_LOADER = 0;

    /**
     * Adapter for the ListView
     */
    InventoryItemCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Find the ListView which will be populated with the item data.
        ListView itemListView = (ListView) findViewById(R.id.catalog_list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        itemListView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of item data in the Cursor.
        // There is no item data yet (until the loader finishes) so pass in null for the Cursor.
        mCursorAdapter = new InventoryItemCursorAdapter(this, null);
        itemListView.setAdapter(mCursorAdapter);

        // Setup the item click listener
        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                Uri currentItemUri = ContentUris.withAppendedId(InventoryItemEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent.
                intent.setData(currentItemUri);

                // Launch the {@link EditorActivity} to display the data for the current item.
                startActivity(intent);
            }
        });

        // Kick off the loader
        getLoaderManager().initLoader(ITEM_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                InventoryItemEntry._ID,
                InventoryItemEntry.COLUMN_ITEM_NAME,
                InventoryItemEntry.COLUMN_ITEM_PRICE,
                InventoryItemEntry.COLUMN_ITEM_QUANTITY};

        // This loader will execute the ContentProvider's query method on a background thread.
        return new CursorLoader(this,               // Parent activity context.
                InventoryItemEntry.CONTENT_URI,     // Provider content URI to query.
                projection,                         // Columns to include in the resulting Cursor.
                null,                               // No selection clause.
                null,                               // No selection arguments.
                null);                              // Default sort order.
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
