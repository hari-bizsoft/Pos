package com.bizsoft.pos.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bizsoft.pos.CartItemActivity;
import com.bizsoft.pos.R;
import com.bizsoft.pos.dataobject.Items;
import com.bizsoft.pos.dataobject.Store;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by shri on 1/7/17.
 */

public class CartItemAdapter extends BaseAdapter {
    Context context;
    private LayoutInflater inflater;
    ArrayList<Items> itemList;


    public CartItemAdapter(Context context, ArrayList<Items> addedList) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.itemList = addedList;
    }
    @Override
    public int getCount() {

        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(itemList.get(position).getId());
    }
    class  Holder
    {
        ImageView image,plus,minus,delete;
        TextView itemName,ItemPrice,ItemQuantity,itemUnit,cross;

        public TextView totalCost;

    }
    @Override
    public View getView(final int position, View rowView, ViewGroup parent) {
        Holder holder = new Holder();
        final Items items= (Items) getItem(position);
        rowView = inflater.inflate(R.layout.purchase_order_single_item, parent, false);

        try {


            holder.itemName = (TextView) rowView.findViewById(R.id.item_name);
            holder.image = (ImageView) rowView.findViewById(R.id.item_image);
            holder.ItemQuantity = (TextView) rowView.findViewById(R.id.item_quantity);
            holder.ItemPrice = (TextView) rowView.findViewById(R.id.price);
            holder.itemUnit = (TextView) rowView.findViewById(R.id.unit);
            holder.plus = (ImageView) rowView.findViewById(R.id.plus);
            holder.minus = (ImageView) rowView.findViewById(R.id.minus);
            holder.delete = (ImageView) rowView.findViewById(R.id.delete);
            holder.totalCost =  (TextView) rowView.findViewById(R.id.total_cost);
            holder.cross =   (TextView) rowView.findViewById(R.id.cross);


            holder.itemName.setText(String.valueOf(items.getName()));
            holder.ItemQuantity.setText("X "+String.valueOf(items.getQuantity()));
            holder.ItemPrice.setText(String.valueOf(items.getRetailPrice()));
            holder.itemUnit.setText(String.valueOf(items.getMeasuringUnit()));
            holder.totalCost.setText(String.valueOf(items.getTotalCost()));
            holder.plus.setVisibility(View.GONE);
            holder.minus.setVisibility(View.GONE);
            holder.delete.setVisibility(View.GONE);

            holder.cross.setVisibility(View.GONE);

            Glide.with(context).load(Store.getInstance().baseUrl + "items/item_image/" + items.getId()).into(holder.image);

        }catch (Exception e)
        {

        }

        return rowView;
    }
}
