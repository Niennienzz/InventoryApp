package com.example.zhehui.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.zhehui.inventoryapp.data.InventoryItemContract.InventoryItemEntry;

/**
 * {@link ContentProvider} for InventoryApp.
 */
public class InventoryItemProvider extends ContentProvider {

    /**
     * Tag for log messages.
     */
    public static final String LOG_TAG = InventoryItemProvider.class.getName();

    /**
     * URI matcher codes for content URIs for the inventoryItems table.
     */
    private static final int ITEMS = 100;
    private static final int ITEM_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        sUriMatcher.addURI(
                InventoryItemContract.CONTENT_AUTHORITY,
                InventoryItemContract.PATH_ITEMS,
                ITEMS);

        sUriMatcher.addURI(
                InventoryItemContract.CONTENT_AUTHORITY,
                InventoryItemContract.PATH_ITEMS + "/#",
                ITEM_ID);
    }

    /**
     * Database helper object.
     */
    private InventoryItemDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new InventoryItemDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database.
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query.
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code.
        int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                // Example "content://com.example.zhehui.inventory/items"
                cursor = database.query(InventoryItemEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case ITEM_ID:
                // Example "content://com.example.zhehui.inventory/items/3"
                selection = InventoryItemEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(InventoryItemEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the cursor.
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                return InventoryItemEntry.CONTENT_LIST_TYPE;
            case ITEM_ID:
                return InventoryItemEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                return insertItem(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert an item into the database with the given content values.
     * Return the new content URI for that specific row in the database.
     */
    private Uri insertItem(Uri uri, ContentValues values) {
        // Sanity checks.
        String name = values.getAsString(InventoryItemEntry.COLUMN_ITEM_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Item requires a name.");
        }

        Float price = values.getAsFloat(InventoryItemEntry.COLUMN_ITEM_PRICE);
        if (price == null) {
            throw new IllegalArgumentException("Item requires a price.");
        }

        Integer quantity = values.getAsInteger(InventoryItemEntry.COLUMN_ITEM_QUANTITY);
        if (quantity == null) {
            throw new IllegalArgumentException("Item requires a quantity.");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(InventoryItemEntry.TABLE_NAME, null, values);

        // Insertion fail.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert to row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the item content URI.
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end.
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                return updateItem(uri, contentValues, selection, selectionArgs);
            case ITEM_ID:
                selection = InventoryItemEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateItem(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateItem(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // Sanity checks.
        if (values.containsKey(InventoryItemEntry.COLUMN_ITEM_NAME)) {
            String name = values.getAsString(InventoryItemEntry.COLUMN_ITEM_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Item requires a name.");
            }
        }

        if (values.containsKey(InventoryItemEntry.COLUMN_ITEM_PRICE)) {
            Float price = values.getAsFloat(InventoryItemEntry.COLUMN_ITEM_PRICE);
            if (price == null) {
                throw new IllegalArgumentException("Item requires a price.");
            }
        }

        if (values.containsKey(InventoryItemEntry.COLUMN_ITEM_QUANTITY)) {
            Integer quantity = values.getAsInteger(InventoryItemEntry.COLUMN_ITEM_QUANTITY);
            if (quantity == null) {
                throw new IllegalArgumentException("Item requires a quantity.");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(InventoryItemEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed.
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated.
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                rowsDeleted = database.delete(InventoryItemEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case ITEM_ID:
                selection = InventoryItemEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(InventoryItemEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed.
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted.
        return rowsDeleted;
    }

}
