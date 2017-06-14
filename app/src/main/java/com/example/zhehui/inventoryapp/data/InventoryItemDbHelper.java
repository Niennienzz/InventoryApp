package com.example.zhehui.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.zhehui.inventoryapp.data.InventoryItemContract.InventoryItemEntry;

/**
 * Database helper for InventoryApp.
 * This class manages database creation and version management.
 */
public class InventoryItemDbHelper extends SQLiteOpenHelper {

    /**
     * Tag for log messages.
     */
    public static final String LOG_TAG = InventoryItemDbHelper.class.getName();

    /**
     * Name of the database file.
     */
    private static final String DATABASE_NAME = "inventory.db";

    /**
     * Database version.
     * Increment of the database version upon the database schema change.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link InventoryItemDbHelper}.
     *
     * @param context of the app
     */
    public InventoryItemDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the items table
        String SQL_CREATE_TABLE = "CREATE TABLE " + InventoryItemEntry.TABLE_NAME + " ("
                + InventoryItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + InventoryItemEntry.COLUMN_ITEM_NAME + " TEXT NOT NULL, "
                + InventoryItemEntry.COLUMN_ITEM_QUANTITY + " INTEGER NOT NULL, "
                + InventoryItemEntry.COLUMN_ITEM_PRICE + " REAL NOT NULL, "
                + InventoryItemEntry.COLUMN_ITEM_INFO + " TEXT NOT NULL, "
                + InventoryItemEntry.COLUMN_ITEM_EMAIL + " TEXT NOT NULL, "
                + InventoryItemEntry.COLUMN_ITEM_PICTURE + " TEXT);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }

}
