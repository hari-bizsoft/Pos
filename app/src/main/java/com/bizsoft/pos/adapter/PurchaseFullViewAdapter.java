package com.bizsoft.pos.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bizsoft.pos.R;
import com.bizsoft.pos.dataobject.BizViewStore;
import com.bizsoft.pos.dataobject.Items;
import com.bizsoft.pos.dataobject.Store;
import com.bizsoft.pos.service.HttpHandler;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by shri on 1/7/17.
 */

public class PurchaseFullViewAdapter extends BaseAdapter {
    Context context;
    private LayoutInflater inflater;
    ArrayList<Items> itemList;
    SharedPreferences sharedPref;
    float gstValue;

    public PurchaseFullViewAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.itemList = Store.getInstance().purchaseList.get(Store.getInstance().currentPurchaseOrderPosition).getItems();
        sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String gstV = sharedPref.getString("gst","6.0");
        gstValue = Float.parseFloat(gstV);
    }
    public PurchaseFullViewAdapter(Context context,ArrayList<Items> addedList) {
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
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();

        float gst = 0;
        float subTotal = 0;
        float grandTotal = 0;
        for(int i=0;i<this.itemList.size();i++)
        {
            subTotal = subTotal +this.itemList.get(i).getTotalCost();

        }


        gst = subTotal * (gstValue/100);
        grandTotal = subTotal + gst;

        BizViewStore.getInstance().gst.setText(String.valueOf(gst));
        BizViewStore.getInstance().grandTotal.setText(String.valueOf(grandTotal));
        BizViewStore.getInstance().subTotal.setText(String.valueOf(subTotal));
    }

    @Override
    public View getView(final int position, View rowView, ViewGroup parent) {
        final Holder holder = new Holder();
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
            holder.ItemPrice.setText(String.valueOf(items.getPurchaseRate()));
            holder.itemUnit.setText(String.valueOf(items.getMeasuringUnit()));
            holder.totalCost.setText(String.valueOf(items.getTotalCost()));

            holder.minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    int q = items.getQuantity();



                    q--;
                    holder.ItemQuantity.setText(String.valueOf(q));
                    items.setQuantity(q);
                    float total = q * items.getPurchaseRate();

                    holder.totalCost.setText(String.valueOf(total));
                    items.setTotalCost(total);
                    notifyDataSetChanged();



                }
            });
            holder.plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int q = items.getQuantity();


                    q++;
                    holder.ItemQuantity.setText(String.valueOf(q));
                    items.setQuantity(q);
                    float total = q * items.getPurchaseRate();
                    holder.totalCost.setText(String.valueOf(total));
                    items.setTotalCost(total);
                    notifyDataSetChanged();

                }
            });
            final int pos = position;

            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    itemList.remove(pos);
                    notifyDataSetChanged();
                    new RemoveItem(context,items.getId()).execute();



                }
            });

            Glide.with(context).load(Store.getInstance().baseUrl + "items/item_image/" + items.getId()).into(holder.image);
        }catch (Exception e)
        {

        }

        return rowView;
    }
    class RemoveItem extends AsyncTask
    {

        Context context;
        String url;
        HashMap<String,String> params;
        String jsonStr = null;
        public RemoveItem(Context context, String id) {
            this.context = context;
            params = new HashMap<String, String>();

            this.params.put("purchase_id",Store.getInstance().currentPurchaseOrder.getId());
            this.params.put("item_id", id);
            this.params.put("subTotal",BizViewStore.getInstance().subTotal.getText().toString());
            this.params.put("grandTotal",BizViewStore.getInstance().grandTotal.getText().toString());
            this.params.put("gst",BizViewStore.getInstance().gst.getText().toString());
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.url = "purchase/removeItem";
        }


        @Override

        protected Object doInBackground(Object[] params) {
            HttpHandler httpHandler = new HttpHandler();
            jsonStr = httpHandler.makeServiceCall(this.url,this.params);
            return true;
        }
        @Override
        protected void onPostExecute(Object o) {
             super.onPostExecute(o);
             System.out.println("Response : "+jsonStr);

            if(jsonStr.contains("true"))
            {

                Toast.makeText(context,"Item Removed",Toast.LENGTH_SHORT).show();
            }


        }

    }
}

