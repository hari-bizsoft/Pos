package com.bizsoft.pos;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bizsoft.pos.adapter.ItemDialogAdapter;
import com.bizsoft.pos.adapter.ItemOrderedAdapter;
import com.bizsoft.pos.dataobject.BizViewStore;
import com.bizsoft.pos.dataobject.Items;
import com.bizsoft.pos.dataobject.Store;
import com.bizsoft.pos.dataobject.Vendor;
import com.bizsoft.pos.service.HttpHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class CreateOrderActivity extends AppCompatActivity {

    Spinner vendor;
    String vendorID;
    Dialog itemsDialog;
    TextView addItems;
    ListView addedItems;
    TextView subTotal,gst,grandTotal;
    Button save;
    TextView title;
    TextView shippingAddress;
    TextView vendorNameText,titleText,itemsText,shippingAddressText,subTotalText,gstText,grandTotalText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_order);

        getSupportActionBar().setTitle("Create Purchase Order");

        titleText = (TextView) findViewById(R.id.title_textview);
        vendorNameText =(TextView) findViewById(R.id.textView15);
        itemsText=(TextView) findViewById(R.id.textView16);
        subTotalText = (TextView) findViewById(R.id.subtotal_text);
        gstText = (TextView) findViewById(R.id.textView21);
        grandTotalText = (TextView) findViewById(R.id.grand_total_text);
        shippingAddressText = (TextView) findViewById(R.id.textView17);

        addItems = (TextView) findViewById(R.id.add_items);
        addedItems = (ListView) findViewById(R.id.item_list);

        subTotal = (TextView) findViewById(R.id.sub_total);
        gst = (TextView) findViewById(R.id.gst);
        grandTotal = (TextView) findViewById(R.id.total);
        save = (Button) findViewById(R.id.save);
        title = (TextView) findViewById(R.id.title_edittext);
        shippingAddress = (TextView) findViewById(R.id.address);

        BizViewStore.getInstance().subTotal = subTotal;
        BizViewStore.getInstance().gst = gst;
        BizViewStore.getInstance().grandTotal = grandTotal;
        if(Store.getInstance().screenSize.equals(Store.getInstance().LARGE_SCREEN)) {

            titleText.setTextSize(Store.getInstance().textSizeLarge);
            vendorNameText.setTextSize(Store.getInstance().textSizeLarge);
            itemsText.setTextSize(Store.getInstance().textSizeLarge);
            subTotalText.setTextSize(Store.getInstance().textSizeLarge);
            gstText.setTextSize(Store.getInstance().textSizeLarge);
            grandTotalText.setTextSize(Store.getInstance().textSizeLarge);
            shippingAddressText.setTextSize(Store.getInstance().textSizeLarge);
            addItems.setTextSize(Store.getInstance().textSizeLarge);
            subTotal.setTextSize(Store.getInstance().textSizeLarge);
            gst.setTextSize(Store.getInstance().textSizeLarge);
            grandTotal.setTextSize(Store.getInstance().textSizeLarge);

            title.setTextSize(Store.getInstance().textSizeLarge);
            shippingAddress.setTextSize(Store.getInstance().textSizeLarge);
        }




        Store.getInstance().itemAdapterTemp = new ItemOrderedAdapter(getApplicationContext());
        addedItems.setAdapter(Store.getInstance().itemAdapterTemp);


        vendor = (Spinner) findViewById(R.id.vendor);

        new GetSimpleVendorList().execute();

        addItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new GetAllSimpleItems().execute();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean status = true;
                if(TextUtils.isEmpty(title.getText().toString()))
                {
                    title.setError("Please fill");
                    status =  false;
                }

                if(TextUtils.isEmpty(shippingAddress.getText().toString()))
                {
                    shippingAddress.setError("Please fill");
                    status =  false;
                }
                if(Store.getInstance().simpleItemListTemp.size()==0)
                {
                    Toast.makeText(getApplicationContext(), "Please ddd some items", Toast.LENGTH_SHORT).show();
                    status =  false;
                }


                if(status)
                {

                    new SaveOrder(getApplicationContext()).execute();
                }

            }
        });

    }
    class GetSimpleVendorList extends AsyncTask
    {
        String jsonStr = "";

        @Override
        protected Object doInBackground(Object[] params) {
            HttpHandler httpHandler = new HttpHandler();
            jsonStr = httpHandler.makeServiceCall("vendor/getSimpleVendorlist",null);
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if (jsonStr != null) {

                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.setDateFormat("M/d/yy hh:mm a");
                Gson gson = gsonBuilder.create();


                Type collectionType = new TypeToken<Collection<Vendor>>() {
                }.getType();

                Collection<Vendor> Vendor = gson.fromJson(jsonStr, collectionType);


                Store.getInstance().simpleVendorList = (ArrayList<Vendor>) Vendor;

                //bizUtils.display(context, jsonStr);
                Log.d("JSON",jsonStr);

                setVendorList();
                //  finish();
            } else {


            }
        }
    }

    class SaveOrder extends AsyncTask
    {
        Context context;
        String jsonStr;
        String url;
        HashMap<String,String> params;

        public SaveOrder(Context context) {
            this.context = context;
            this.url = "purchase/saveAll";
            this.params = new HashMap<String, String>();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            this.params.put("title",title.getText().toString());
            this.params.put("address",shippingAddress.getText().toString());




            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            Type collectionType = new TypeToken<Collection<Items>>() {
            }.getType();

            String items = gson.toJson(Store.getInstance().simpleItemListTemp);
            Log.d("Json data",items);

            this.params.put("items",items);
            this.params.put("subTotal",subTotal.getText().toString());
            this.params.put("total",grandTotal.getText().toString());
            this.params.put("gst",gst.getText().toString());
            this.params.put("grandTotal",grandTotal.getText().toString());
            this.params.put("vendorID",vendorID);
            this.params.put("status","Pending");

        }

        @Override
        protected Object doInBackground(Object[] params) {
            HttpHandler httpHandler = new HttpHandler();
            jsonStr = httpHandler.makeServiceCall(this.url,this.params);
            return null;
        }
        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            if(jsonStr.contains("true"))
            {
                finish();
                Toast.makeText(context,"Order has been Saved",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CreateOrderActivity.this,PurchaseActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            else
            {
                Toast.makeText(context,"Order not Saved - Has some errors",Toast.LENGTH_SHORT).show();
            }
        }

    }
    public void setVendorList()
    {
        // Spinner Drop down elements
        List<String> categoryList = new ArrayList<String>();

        final ArrayList<Vendor> list = Store.getInstance().simpleVendorList;

        int size = list.size();

        for (int i = 0; i < size;i ++)
        {
            categoryList.add(list.get(i).getName());

            System.out.print("test");

        }

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter <String>(CreateOrderActivity.this, android.R.layout.simple_spinner_item, categoryList);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        vendor.setAdapter(dataAdapter);
        vendor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Toast.makeText(getApplicationContext(),list.get(position).getName(),Toast.LENGTH_SHORT).show();

                vendorID = list.get(position).getId();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                try {
                    vendorID  = list.get(0).getId();
                }
                catch (Exception e)
                {

                }
            }
        });


    }
    public void showItems()
    {



        itemsDialog = new Dialog(CreateOrderActivity.this);

        itemsDialog.setContentView(R.layout.add_item_dialog_main);


        ListView listView = (ListView) itemsDialog.findViewById(R.id.list_view);

        itemsDialog.setTitle("Add Items");

        listView.setAdapter(new ItemDialogAdapter(getApplicationContext()));

        itemsDialog.show();

    }
    class GetAllSimpleItems extends AsyncTask
    {

        String jsonStr = "";
        @Override
        protected Object doInBackground(Object[] params) {
            HttpHandler httpHandler = new HttpHandler();
            jsonStr = httpHandler.makeServiceCall("items/getSimpleItemlist",null);
            return null;
        }
        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if (jsonStr != null) {

                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.setDateFormat("M/d/yy hh:mm a");
                Gson gson = gsonBuilder.create();


                Type collectionType = new TypeToken<Collection<Items>>() {
                }.getType();

                Collection<Items> Items = gson.fromJson(jsonStr, collectionType);


                Store.getInstance().simpleItemList = (ArrayList<Items>) Items;

                //bizUtils.display(context, jsonStr);
                Log.d("JSON",jsonStr);

                showItems();
                //  finish();
            } else {


            }


        }
    }

}
