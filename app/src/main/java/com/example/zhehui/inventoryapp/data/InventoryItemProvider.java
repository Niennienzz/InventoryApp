package com.example.zhehui.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

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
    private static final int ITEM_NAME = 102;

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

        sUriMatcher.addURI(
                InventoryItemContract.CONTENT_AUTHORITY,
                InventoryItemContract.PATH_ITEM_NAME + "/*",
                ITEM_NAME);
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
                // Example "content://com.example.zhehui.inventory/items/3"
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
            case ITEM_NAME:
                // Example "content://com.example.zhehui.inventory/itemNames/HeadPhone"
                selection = InventoryItemEntry.COLUMN_ITEM_NAME + "=?";
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
            case ITEM_NAME:
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
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
            case ITEM_ID:
            case ITEM_NAME:
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
            case ITEM_ID:
            case ITEM_NAME:
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

//        if (rowsDeleted != 0) {
//            getContext().getContentResolver().notifyChange(uri, null);
//        }
//        return rowsDeleted;
    }

}
