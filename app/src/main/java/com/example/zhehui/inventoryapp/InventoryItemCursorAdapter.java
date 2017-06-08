package com.example.zhehui.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.zhehui.inventoryapp.data.InventoryItemContract;

public class InventoryItemCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link InventoryItemCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public InventoryItemCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the item data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current item can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout.
        TextView nameTextView = (TextView) view.findViewById(R.id.item_name);
        TextView priceTextView = (TextView) view.findViewById(R.id.item_price);
        TextView quantityTextView = (TextView) view.findViewById(R.id.item_quantity);


        // Find the columns of item attributes that we're interested in.
        int nameColumnIndex = cursor.getColumnIndex(
                InventoryItemContract.InventoryItemEntry.COLUMN_ITEM_NAME);
        int priceColumnIndex = cursor.getColumnIndex(
                InventoryItemContract.InventoryItemEntry.COLUMN_ITEM_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(
                InventoryItemContract.InventoryItemEntry.COLUMN_ITEM_QUANTITY);

        // Read the item attributes from the Cursor for the current item.
        String itemName = cursor.getString(nameColumnIndex);
        Float itemPrice = cursor.getFloat(priceColumnIndex);
        Integer itemQuantity = cursor.getInt(quantityColumnIndex);

        // Update the TextViews with the attributes for the current item.
        nameTextView.setText(itemName);
        priceTextView.setText(itemPrice.toString());
        quantityTextView.setText(itemQuantity.toString());
    }

}
