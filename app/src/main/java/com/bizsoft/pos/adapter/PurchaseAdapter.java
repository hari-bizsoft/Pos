package com.bizsoft.pos.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bizsoft.pos.PurchaseFullDetailsActivity;
import com.bizsoft.pos.R;
import com.bizsoft.pos.dataobject.Purchase;
import com.bizsoft.pos.dataobject.Store;

import java.util.ArrayList;

/**
 * Created by shri on 1/7/17.
 */

public class PurchaseAdapter extends BaseAdapter {
    Context context;
    private LayoutInflater inflater;
    ArrayList<Purchase> purchaseList;
    public PurchaseAdapter(Context context) {
        this.context = context;

        this.inflater = LayoutInflater.from(context);
        this.purchaseList = Store.getInstance().purchaseList;
    }

    @Override
    public int getCount() {
        return Store.getInstance().purchaseList.size();

    }

    @Override
    public Object getItem(int position) {
        return Store.getInstance().purchaseList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(Store.getInstance().purchaseList.get(position).getId());
    }

    class Holder
    {
        TextView purchaseID,purchaseNote,itemCount,status,totalAmount,vendor;
        TextView purchaseIDtext,purchaseNoteText,itemCountText,statusText,totalAmountText,vendorText;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        final Purchase purchase = (Purchase) getItem(position);
        convertView = inflater.inflate(R.layout.purchase_single_item, parent, false);


        holder.purchaseID = (TextView) convertView.findViewById(R.id.purchase_id);
        holder.purchaseNote = (TextView) convertView.findViewById(R.id.purchase_note);
        holder.itemCount = (TextView) convertView.findViewById(R.id.item_count);
        holder.status =  (TextView) convertView.findViewById(R.id.status);
        holder.totalAmount =  (TextView) convertView.findViewById(R.id.total_amount);
        holder.vendor = (TextView) convertView.findViewById(R.id.vendor);

        holder.purchaseIDtext = (TextView) convertView.findViewById(R.id.purchase_id_text);
        holder.itemCountText = (TextView) convertView.findViewById(R.id.total_items);
        holder.statusText= (TextView) convertView.findViewById(R.id.status_text);
        holder.totalAmountText= (TextView) convertView.findViewById(R.id.total_amount_text);
        holder.vendorText= (TextView) convertView.findViewById(R.id.to);


        holder.purchaseID.setText(String.valueOf(purchase.getId()));
        holder.purchaseNote.setText(String.valueOf(purchase.getName()));
        holder.status.setText(String.valueOf(purchase.getStatus()));
        holder.itemCount.setText(String.valueOf(purchase.getItems().size()));
        holder.itemCount.setText(String.valueOf(purchase.getItems().size()));
        holder.totalAmount.setText(String.valueOf(purchase.getGrandTotal()));
        holder.vendor.setText(String.valueOf(purchase.getVendor().getName()));
        if(Store.getInstance().screenSize.equals(Store.getInstance().LARGE_SCREEN))
        {
            holder.purchaseID.setTextSize(Store.getInstance().textSizeLarge);
            holder.purchaseNote.setTextSize(Store.getInstance().textSizeLarge);
            holder.status.setTextSize(Store.getInstance().textSizeLarge);
            holder.itemCount.setTextSize(Store.getInstance().textSizeLarge);
            holder.totalAmount.setTextSize(Store.getInstance().textSizeLarge);
            holder.vendor.setTextSize(Store.getInstance().textSizeLarge);
            holder.purchaseIDtext.setTextSize(Store.getInstance().textSizeLarge);
            holder.itemCountText.setTextSize(Store.getInstance().textSizeLarge);
            holder.statusText.setTextSize(Store.getInstance().textSizeLarge);
            holder.totalAmountText.setTextSize(Store.getInstance().textSizeLarge);
            holder.vendorText.setTextSize(Store.getInstance().textSizeLarge);


        }

        if(purchase.getStatus().equals("Approved"))
        {
            holder.status.setTextColor(Color.GREEN);

        }
        else  if(purchase.getStatus().equals("Rejected"))
        {
            holder.status.setTextColor(Color.RED);
        }
        else if(purchase.getStatus().equals("Pending"))
        {
            holder.status.setTextColor(Color.rgb(255,165,0));
        }
        else if(purchase.getStatus().equals("Moved"))
        {
            holder.status.setTextColor(Color.BLUE);
        }


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Store.getInstance().currentPurchaseOrder = purchase;
                Store.getInstance().currentPurchaseOrderPosition = position;
                Intent intent = new Intent(context, PurchaseFullDetailsActivity.class);
                context.startActivity(intent);
            }
        });
        return convertView;
    }
}
