package com.bizsoft.pos.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bizsoft.pos.R;
import com.bizsoft.pos.dataobject.BizViewStore;
import com.bizsoft.pos.dataobject.Items;
import com.bizsoft.pos.dataobject.Store;
import com.bizsoft.pos.service.HttpHandler;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Created by shri on 1/7/17.
 */

public class BillingItemsAdapter extends BaseAdapter {
    Context context;
    private LayoutInflater inflater;
    ArrayList<Items> itemList;

    SharedPreferences sharedPref;


    public BillingItemsAdapter(Context context, ArrayList<Items> addedList) {
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

        float subTotal = 0;
        float gst =0;
        float grandTotal = 0;
        sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        for(int i=0;i<this.itemList.size();i++)
        {

            subTotal = subTotal + (itemList.get(i).getRetailPrice()*itemList.get(i).getQuantity());
        }

        String gstV = sharedPref.getString("gst","6.0");

        float Ogst = Float.parseFloat(gstV);

        Ogst = Ogst/100;

        System.out.println("Ogst = "+Ogst);
         gst = (float) (subTotal *Ogst);

        grandTotal = gst + subTotal;

        BizViewStore.getInstance().billingSubTotal.setText(String.valueOf(subTotal)) ;

        BizViewStore.getInstance().billingGst.setText(String.valueOf(gst)) ;

        BizViewStore.getInstance().billingGrandTotal.setText(String.valueOf(grandTotal)) ;

        BizViewStore.getInstance().grandTotalValue = grandTotal;



    }

    @Override
    public View getView(final int position, View rowView, ViewGroup parent) {
        Holder holder = new Holder();
        final Items items= (Items) getItem(position);
        rowView = inflater.inflate(R.layout.billing_order_single_item, parent, false);

        try {

            float totalPrice = items.getQuantity() * items.getRetailPrice();


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
            holder.ItemPrice.setText(String.format("%.2f",items.getRetailPrice()));
            holder.itemUnit.setText(String.valueOf(items.getMeasuringUnit()));
            holder.totalCost.setText(String.format("%.2f",items.getTotalCost()));
            holder.plus.setVisibility(View.GONE);
            holder.minus.setVisibility(View.GONE);
            holder.totalCost.setText(String.format("%.2f",totalPrice));


            holder.cross.setVisibility(View.GONE);
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {




                    new RemoveFromCart(context,items).execute();

                }
            });



            Glide.with(context).load(Store.getInstance().baseUrl + "items/item_image/" + items.getId()).into(holder.image);
        }catch (Exception e)
        {

        }

        return rowView;
    }
    class RemoveFromCart extends AsyncTask
    {
        Context context;
        String url;
        String jsonStr;
        HashMap<String,String> params;
        Items items;


        public RemoveFromCart(Context context, Items items) {
            this.context = context;
            this.url = "cart/removeItems";
            this.jsonStr = null;
            this.params = new HashMap<String, String>();
            this.items = items;

        }


        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            this.params.put("item_id",this.items.getId());
            this.params.put("item_quantity", String.valueOf(this.items.getQuantity()));
            this.params.put("customer_id",Store.getInstance().currentCustomerID);
            this.params.put("total_price", String.valueOf(items.getTotalItemPrice()));

        }
        @Override
        protected Object doInBackground(Object[] params) {

            System.out.println("Removing items from cart....");

            HttpHandler httpHandler = new HttpHandler();
            jsonStr = httpHandler.makeServiceCall(this.url,this.params);

            return true;
        }
        @Override
        protected void onPostExecute(Object o) {

            super.onPostExecute(o);


          try {
              System.out.println("json : " + jsonStr);
              Log.e("JSON :", jsonStr);


              GsonBuilder gsonBuilder = new GsonBuilder();
              gsonBuilder.setDateFormat("M/d/yy hh:mm a");
              Gson gson = gsonBuilder.create();


              Type collectionType = new TypeToken<Collection<Items>>() {
              }.getType();

              Collection<Items> items = gson.fromJson(jsonStr, collectionType);

              Store.getInstance().addedItemList.clear();

              ArrayList<Items> tempList = new ArrayList<Items>();

              tempList = (ArrayList<Items>) items;

              Store.getInstance().addedItemList.addAll(tempList);


              BizViewStore.getInstance().addedItemAdapter.notifyDataSetChanged();



              System.out.println("test me = "+Store.getInstance().addedItemList.size());

          }
          catch (Exception e)
          {

          }


        }
    }
}
