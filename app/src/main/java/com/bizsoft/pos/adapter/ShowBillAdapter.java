package com.bizsoft.pos.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bizsoft.pos.GenerateBillActivity;
import com.bizsoft.pos.R;
import com.bizsoft.pos.dataobject.Bill;
import com.bizsoft.pos.dataobject.Items;
import com.bizsoft.pos.dataobject.Store;
import com.bizsoft.pos.service.HttpHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by shri on 11/7/17.
 */

public class ShowBillAdapter extends BaseAdapter {
    Context context;
    private LayoutInflater inflater;

    public ShowBillAdapter(Context context) {
        this.context = context;
        this.billList = Store.getInstance().billList;
        this.inflater = LayoutInflater.from(context);
    }

    public ArrayList<Bill> billList;


    public void clear()
    {
        this.billList.clear();

    }
    @Override
    public int getCount() {
        return this.billList.size();
    }
    @Override
    public Object getItem(int position) {
        return this.billList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(this.billList.get(position).getId());
    }
    class  Holder
    {
        TextView billID,itemSize,date,grandTotal,user,itemsText;


    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        final Bill bill = (Bill) getItem(position);
        convertView = inflater.inflate(R.layout.show_bill_single_item, parent, false);

        holder.billID = (TextView) convertView.findViewById(R.id.bill_id);
        holder.itemSize = (TextView) convertView.findViewById(R.id.item_size);
        holder.date = (TextView) convertView.findViewById(R.id.date);
        holder.grandTotal = (TextView) convertView.findViewById(R.id.grand_total);
        holder.user  = (TextView) convertView.findViewById(R.id.user);
        holder.itemsText  = (TextView) convertView.findViewById(R.id.items_text);


        holder.billID.setText("Bill ID : "+String.valueOf(bill.getId()));
        holder.itemSize.setText(String.valueOf(bill.getItems().size()));
        holder.date.setText(String.valueOf(bill.getDateCreated()));
        holder.grandTotal.setText(String.valueOf(bill.getGrandTotal()));
        holder.user.setText(String.valueOf(bill.getUsername()));

        if(Store.getInstance().screenSize.equals(Store.getInstance().LARGE_SCREEN)) {
            holder.billID.setTextSize(Store.getInstance().textSizeLarge);
            holder.itemSize.setTextSize(Store.getInstance().textSizeLarge);
            holder.date.setTextSize(Store.getInstance().textSizeLarge);
            holder.grandTotal.setTextSize(Store.getInstance().textSizeLarge);
            holder.user.setTextSize(Store.getInstance().textSizeLarge);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Store.getInstance().currentCustomer = bill.getCustomer();
                Store.getInstance().bill = new Bill();
                Store.getInstance().bill = bill;
                Store.getInstance().currentCustomerID = bill.getCustomerID();
                Store.getInstance().currentBillPosition = position;

                new GetBillDetails(context).execute();
            }
        });


        return convertView;
    }
    class GetBillDetails extends AsyncTask
    {

        Context context;
        String url;
        HashMap<String,String> params;
        String jsonStr = null;

        public GetBillDetails(Context context) {
            this.context = context;
            params = new HashMap<String, String>();
            this.url = "bill/getAllDetails";
            this.params.put("customer_id", Store.getInstance().currentCustomerID);
        }

        @Override

        protected void onPreExecute() {
            super.onPreExecute();


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

            Log.d("JSON :",jsonStr);
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setDateFormat("M/d/yy hh:mm a");
            Gson gson = gsonBuilder.create();


            Type collectionType = new TypeToken<Bill>() {
            }.getType();

            Bill bill = gson.fromJson(jsonStr, collectionType);

            Store.getInstance().bill = (Bill) bill;


            Intent intent = new Intent(context,GenerateBillActivity.class);

            intent.putExtra("from","incart");
            context.startActivity(intent);
        }


    }
}
