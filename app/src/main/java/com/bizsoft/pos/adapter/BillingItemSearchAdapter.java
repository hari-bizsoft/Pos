package com.bizsoft.pos.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bizsoft.pos.R;
import com.bizsoft.pos.dataobject.BizViewStore;
import com.bizsoft.pos.dataobject.Category;
import com.bizsoft.pos.dataobject.Items;
import com.bizsoft.pos.dataobject.Store;
import com.bizsoft.pos.service.BizUtils;
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
 * Created by shri on 4/7/17.
 */

public class BillingItemSearchAdapter extends BaseAdapter {
    Context context;
    private static LayoutInflater inflater=null;
    BizUtils bizUtils;
    ArrayList<Items> itemList;

    public BillingItemSearchAdapter(Context context,ArrayList<Items> itemList) {
        this.context = context;
        inflater = (LayoutInflater)context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        bizUtils = new BizUtils();
        this.itemList  = itemList;
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

    public class Holder
    {
        TextView itemName,itemPrice,quantity,measuringUnit,quantityPrice,totalQuantity;
        ImageView itemImage,plus,minus;
        Button add;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Items items = (Items) getItem(position);
        final Holder holder=new Holder();
        View rowView;





        float tqp = items.getQuantity() * items.getRetailPrice();


        items.setTotalItemPrice(tqp);

        rowView = inflater.inflate(R.layout.billing_items_single, null);

        holder.itemImage = (ImageView) rowView.findViewById(R.id.item_image);
        holder.itemName = (TextView) rowView.findViewById(R.id.item_name);
        holder.itemPrice = (TextView) rowView.findViewById(R.id.item_price);
        holder.quantity = (TextView) rowView.findViewById(R.id.items_quantity);
        holder.measuringUnit = (TextView) rowView.findViewById(R.id.measuring_unit);
        holder.plus = (ImageView) rowView.findViewById(R.id.plus);
        holder.minus = (ImageView) rowView.findViewById(R.id.minus);
        holder.totalQuantity = (TextView) rowView.findViewById(R.id.testview);




        holder.totalQuantity.setText("("+String.valueOf(items.getRefTQ())+")");
        holder.quantityPrice = (TextView) rowView.findViewById(R.id.item_quantity_price);


        holder.itemName.setText(String.valueOf(items.getName()));
        holder.itemPrice.setText(String.valueOf(items.getRetailPrice()));



        bizUtils.display("Message ==== : "+items.getQuantity());
        holder.quantity.setText(String.valueOf(items.getQuantity()));
        holder.measuringUnit.setText(String.valueOf(items.getMeasuringUnit()));
        Glide.with(context).load(Store.getInstance().baseUrl+"items/item_image/"+items.getId()).into(holder.itemImage);

        holder.quantityPrice.setText(String.valueOf(items.getTotalItemPrice()));
        holder.add = (Button) rowView.findViewById(R.id.add);



        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("called add items to cart");


                boolean status = true;
                ArrayList<Items> itemsA = Store.getInstance().addedItemList;

                for (int i=0;i<itemsA.size();i++)
                {
                    if( Integer.parseInt(itemsA.get(i).getId()) == Integer.parseInt(items.getId()))
                    {
                        status = false;
                        Toast.makeText(context, "Item already added", Toast.LENGTH_SHORT).show();


                    }
                    else
                    {
                        status = true;

                    }


                }

                if(status)
                {
                    //Store.getInstance().addedItemList.add(items);

                   // BizViewStore.getInstance().addedItemAdapter.notifyDataSetChanged();
                    new AddToCart(context,items).execute();
                }

                BizViewStore.getInstance().itemShowDialog.cancel();




            }
        });
        holder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                int q = items.getQuantity();

                bizUtils.display("REF Q ==== : "+q);

                if(q > 0 ) {
                    q--;
                    holder.quantity.setText(String.valueOf(q));
                    items.setQuantity(q);

                    notifyDataSetChanged();
                }
                else
                {
                    Toast.makeText(context,"Invalid Quantity",Toast.LENGTH_SHORT).show();
                }


            }
        });
        holder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int q =  items.getQuantity();

                if(q < items.getRefTQ()) {
                    q++;
                    holder.quantity.setText(String.valueOf(q));
                    items.setQuantity(q);
                    notifyDataSetChanged();
                }
                else
                {
                    Toast.makeText(context,"Invalid Quantity",Toast.LENGTH_SHORT).show();
                }
            }
        });



        return rowView ;
    }

    class AddToCart extends AsyncTask
    {
        Context context;
        String url;
        String jsonStr;
        HashMap<String,String> params;
        Items items;


        public AddToCart(Context context, Items items) {
            this.context = context;
            this.url = "cart/addItems";
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

            System.out.println("Adding item to cart....");

            HttpHandler httpHandler = new HttpHandler();
            jsonStr = httpHandler.makeServiceCall(this.url,this.params);

            return true;
        }
        @Override
        protected void onPostExecute(Object o) {

              super.onPostExecute(o);
            System.out.println("json : "+jsonStr);
            Log.e("JSON :",jsonStr);

            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setDateFormat("M/d/yy hh:mm a");
            Gson gson = gsonBuilder.create();


            Type collectionType = new TypeToken<Collection<Items>>() {
            }.getType();

            Collection<Items> items = gson.fromJson(jsonStr, collectionType);

            Store.getInstance().addedItemList.clear();

            ArrayList<Items> tempList = new ArrayList<Items>();

            tempList =  (ArrayList<Items>) items;

            Store.getInstance().addedItemList.addAll(tempList);

             BizViewStore.getInstance().addedItemAdapter.notifyDataSetChanged();


        }
    }
}
