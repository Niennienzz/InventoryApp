package com.example.zhehui.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * API Contract for InventoryApp.
 */
public class InventoryItemContract {

    /**
     * Constants for API contract.
     */
    public static final String CONTENT_AUTHORITY = "com.example.zhehui.inventory";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_ITEMS = "items";

    /**
     * A private empty constructor preventing someone from
     * accidentally instantiating the contract class.
     */
    private InventoryItemContract() {
    }

    /**
     * Inner class that defines constant values for the inventory database table.
     * Each entry in the table represents a single inventory item.
     */
    public static final class InventoryItemEntry implements BaseColumns {

        /**
         * The content URI to access the item data in the provider.
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ITEMS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of items.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single item.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;

        /**
         * Name of database table for inventory items.
         */
        public final static String TABLE_NAME = "inventoryItems";

        /**
         * Unique ID number for the item (only for use in the database table).
         * SQLite Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the item.
         * SQLite Type: TEXT
         */
        public final static String COLUMN_ITEM_NAME = "name";

        /**
         * Current quantity of the item.
         * SQLite Type: INTEGER
         */
        public final static String COLUMN_ITEM_QUANTITY = "quantity";

        /**
         * Price of the item.
         * SQLite Type: REAL
         */
        public final static String COLUMN_ITEM_PRICE = "price";

        /**
         * Information of the item.
         * SQLite Type: TEXT
         */
        public final static String COLUMN_ITEM_INFO = "info";

        /**
         * Supplier email of the item.
         * SQLite Type: TEXT
         */
        public final static String COLUMN_ITEM_EMAIL = "email";

        /**
         * Picture the item.
         * SQLite Type: BLOB
         */
        public final static String COLUMN_ITEM_PICTURE = "picture";

    }

}
