package com.example.zhehui.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.zhehui.inventoryapp.data.InventoryItemContract;

public class InventoryItemCursorAdapter extends CursorAdapter {

    private final static String PRICE_TEXT = "Price: $";
    private final static String STOCK_TEXT = "Stock: ";

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
    public void bindView(final View view, Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout.
        final TextView nameTextView = (TextView) view.findViewById(R.id.item_name);
        final TextView priceTextView = (TextView) view.findViewById(R.id.item_price);
        final TextView quantityTextView = (TextView) view.findViewById(R.id.item_quantity);
        Button sellButton = (Button) view.findViewById(R.id.item_sell);
        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantityString = quantityTextView.getText().toString();
                quantityString = quantityString.replace(STOCK_TEXT, "");
                int quantity = Integer.parseInt(quantityString);
                if (quantity > 0) {
                    quantity -= 1;
                }
                quantityString = STOCK_TEXT + Integer.toString(quantity);
                quantityTextView.setText(quantityString);
            }
        });

        // Find the columns of item attributes that we're interested in.
        int nameColumnIndex = cursor.getColumnIndex(
                InventoryItemContract.InventoryItemEntry.COLUMN_ITEM_NAME);
        int priceColumnIndex = cursor.getColumnIndex(
                InventoryItemContract.InventoryItemEntry.COLUMN_ITEM_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(
                InventoryItemContract.InventoryItemEntry.COLUMN_ITEM_QUANTITY);

        // Read the item attributes from the Cursor for the current item.
        String itemName = "<" + cursor.getString(nameColumnIndex) + ">";
        Float itemPriceFloat = cursor.getFloat(priceColumnIndex);
        String itemPrice = PRICE_TEXT + itemPriceFloat.toString();
        Integer itemQuantityInteger = cursor.getInt(quantityColumnIndex);
        String itemQuantity = STOCK_TEXT + itemQuantityInteger.toString();

        // Update the TextViews with the attributes for the current item.
        nameTextView.setText(itemName);
        priceTextView.setText(itemPrice);
        quantityTextView.setText(itemQuantity);
    }

}
