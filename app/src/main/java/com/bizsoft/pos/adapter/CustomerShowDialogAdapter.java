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

import com.bizsoft.pos.R;
import com.bizsoft.pos.dataobject.BizViewStore;
import com.bizsoft.pos.dataobject.Items;
import com.bizsoft.pos.dataobject.Store;
import com.bizsoft.pos.dataobject.User;
import com.bizsoft.pos.service.BizUtils;
import com.bizsoft.pos.service.HttpHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Created by shri on 6/7/17.
 */

public class CustomerShowDialogAdapter extends BaseAdapter {


    Context context;
    private static LayoutInflater inflater=null;
    BizUtils bizUtils;
    ArrayList<User> customer;
    public CustomerShowDialogAdapter(Context context) {
        this.context = context;
        inflater = (LayoutInflater)context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        bizUtils = new BizUtils();
        customer = Store.getInstance().customerList;
    }
    @Override
    public int getCount() {
        return Store.getInstance().customerList.size();
    }

    @Override
    public Object getItem(int position) {
        return Store.getInstance().customerList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(Store.getInstance().customerList.get(position).getId());
    }
    class Holder
    {


        TextView firstName,lastName,contactNumber;
        Button add;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder=new Holder();
        View rowView = null;
        final User customer = (User) getItem(position);


        rowView = inflater.inflate(R.layout.show_customer_list_dialog_single, null);
        holder.firstName = (TextView) rowView.findViewById(R.id.first_name);
        holder.lastName= (TextView) rowView.findViewById(R.id.last_name);
        holder.contactNumber= (TextView) rowView.findViewById(R.id.phone_number);

        holder.firstName.setText(String.valueOf(customer.getFirstName()));
        holder.lastName.setText(String.valueOf(customer.getLastName()));
        holder.contactNumber.setText("Ph :"+String.valueOf(customer.getContactNumber()));


        holder.add = (Button) rowView.findViewById(R.id.add);

        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Store.getInstance().currentCustomer = customer;

                Store.getInstance().currentCustomerID =  customer.getId();

                new FullList(context,Store.getInstance().currentCustomerID).execute();

                BizViewStore.getInstance().billingCustomerName.setText(String.valueOf(customer.getFirstName()));
                BizViewStore.getInstance().billingContactNumber.setText(String.valueOf(customer.getContactNumber()));
                BizViewStore.getInstance().billingCustomerDialog.cancel();
            }
        });
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Store.getInstance().currentCustomer = customer;

                Store.getInstance().currentCustomerID =  customer.getId();

                new FullList(context,Store.getInstance().currentCustomerID).execute();

                BizViewStore.getInstance().billingCustomerName.setText(String.valueOf(customer.getFirstName()));
                BizViewStore.getInstance().billingContactNumber.setText(String.valueOf(customer.getContactNumber()));
                BizViewStore.getInstance().billingCustomerDialog.cancel();
            }
        });
        return rowView;

    }
    class FullList extends AsyncTask
    {
        Context context;
        String url;
        String jsonStr;
        String customerID;
        HashMap<String,String> params;



        public FullList(Context context, String currentCustomerID) {
            this.context = context;
            this.url = "cart/fullList";
            this.jsonStr = null;
            this.params = new HashMap<String, String>();
            this.customerID =  currentCustomerID;


        }


        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            this.params.put("customer_id",this.customerID);


        }
        @Override
        protected Object doInBackground(Object[] params) {

            System.out.println("getting full list from cart....");

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
            }
            catch (Exception e)
            {

            }


        }
    }
}
