package com.bizsoft.pos.dataobject;

import android.app.Dialog;
import android.widget.TextView;

import com.bizsoft.pos.adapter.BillingItemsAdapter;
import com.bizsoft.pos.adapter.PurchaseFullViewAdapter;

/**
 * Created by shri on 28/6/17.
 */

public class BizViewStore {

    private static BizViewStore instance = null;


    public TextView subTotal;
    public TextView gst;
    public TextView grandTotal;
    public TextView itemPrice;
    public int currentOrderedItemPositionTemp;
    public TextView billingCustomerName;
    public TextView billingContactNumber;
    public Dialog billingCustomerDialog;
    public BillingItemsAdapter addedItemAdapter;
    public TextView billingSubTotal;
    public TextView billingGst;
    public TextView billingGrandTotal;
    public Dialog itemShowDialog;
    public float grandTotalValue;
    public float discountAmount;

    private BizViewStore() {
        // Exists only to defeat instantiation.
    }

    public static BizViewStore getInstance() {
        if(instance == null) {
            instance = new BizViewStore();
        }
        return instance;
    }
}
