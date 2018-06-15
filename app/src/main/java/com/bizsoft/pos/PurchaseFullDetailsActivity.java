package com.bizsoft.pos;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bizsoft.pos.adapter.PurchaseFullViewAdapter;
import com.bizsoft.pos.dataobject.BizViewStore;
import com.bizsoft.pos.dataobject.Items;
import com.bizsoft.pos.dataobject.Purchase;
import com.bizsoft.pos.dataobject.Store;
import com.bizsoft.pos.service.HttpHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class PurchaseFullDetailsActivity extends AppCompatActivity {

    TextView purchaseId,purchaseTitle,purchaseDate,purchaseStatus;
    TextView vendorID,vendorName,vendorTinNumber,vendorContact,vendorAddress,vendorMail;
    TextView subTotal,gst,grandTotal,shippingAddress;
    ListView listView;
    Button accept,reject,moveItems,save;
    TextView note;
    TextView purchaseIDText,purchaseTitleText,purchaseStatusText;
    TextView vendorIdText,vendorNameText,vendorTinNumberText,vendorAddressText,vendorMailText;
    TextView subTotalText,gstText,grandTotalText;
    TextView shippingAddressText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_full_details);

        purchaseIDText = (TextView) findViewById(R.id.purchase_id_text);
        purchaseTitleText = (TextView) findViewById(R.id.title_text);
        purchaseStatusText = (TextView) findViewById(R.id.status_text);

        vendorIdText = (TextView) findViewById(R.id.vendor_id_text);
        vendorNameText = (TextView) findViewById(R.id.vendor_name_text);
        vendorTinNumberText = (TextView) findViewById(R.id.tim_number_text);
        vendorAddressText = (TextView) findViewById(R.id.textView36);
        vendorMailText = (TextView) findViewById(R.id.textView20);

        subTotalText = (TextView) findViewById(R.id.subtotal_text);
        gstText = (TextView) findViewById(R.id.gst_text);
        grandTotalText = (TextView) findViewById(R.id.grand_total_text);

        shippingAddressText = (TextView) findViewById(R.id.textView26);


        purchaseId = (TextView) findViewById(R.id.purchase_id);
        purchaseTitle = (TextView) findViewById(R.id.purchase_title);
        purchaseDate = (TextView) findViewById(R.id.bill_date);
        purchaseStatus = (TextView) findViewById(R.id.status);

        vendorID = (TextView) findViewById(R.id.vendor_id);
        vendorName = (TextView) findViewById(R.id.vendor_name);
        vendorContact = (TextView) findViewById(R.id.vendor_contact_number);
        vendorTinNumber =  (TextView) findViewById(R.id.tin_number);
        vendorAddress = (TextView) findViewById(R.id.vendor_address);
        vendorMail = (TextView) findViewById(R.id.customer_mail);

        subTotal = (TextView) findViewById(R.id.sub_total);
        gst= (TextView) findViewById(R.id.gst);
        grandTotal  = (TextView) findViewById(R.id.total);
        shippingAddress = (TextView) findViewById(R.id.shipping_address);

        listView = (ListView) findViewById(R.id.listview);
        note = (TextView) findViewById(R.id.note);

        Purchase purchase = Store.getInstance().currentPurchaseOrder;
        purchaseId.setText(String.valueOf(purchase.getId()));
        purchaseDate.setText(String.valueOf(purchase.getDateCreated()));
        purchaseTitle.setText(String.valueOf(purchase.getName()));
        purchaseStatus.setText(String.valueOf(purchase.getStatus()));

        vendorID.setText(String.valueOf(purchase.getVendor().getId()));
        vendorName.setText(String.valueOf(purchase.getVendor().getName()));
        vendorTinNumber.setText(String.valueOf(purchase.getVendor().getTinNumber()));
        vendorContact.setText(String.valueOf(purchase.getVendor().getContactNumber()));
        vendorAddress.setText(String.valueOf(purchase.getVendor().getAddress()));
        vendorMail.setText(String.valueOf(purchase.getVendor().getEmail()));

        System.out.println("Sub Total : "+purchase.getSubTotal());
        subTotal.setText(String.valueOf(purchase.getSubTotal()));
        gst.setText(String.valueOf(purchase.getGst()));
        grandTotal.setText(String.valueOf(purchase.getGrandTotal()));

        BizViewStore.getInstance().subTotal = subTotal;
        BizViewStore.getInstance().gst = gst;
        BizViewStore.getInstance().grandTotal = grandTotal;

        shippingAddress.setText(String.valueOf(purchase.getShippingAddress()));

        getSupportActionBar().setTitle("Purchase Order - "+purchase.getId());
        listView.setAdapter(new PurchaseFullViewAdapter(PurchaseFullDetailsActivity.this));


        accept = (Button) findViewById(R.id.accept);
        reject = (Button) findViewById(R.id.reject);
        moveItems = (Button) findViewById(R.id.move_items);
        save = (Button) findViewById(R.id.save);

        System.out.println("Purchase Order Status : "+purchase.getStatus());


        if(Store.getInstance().screenSize.equals(Store.getInstance().LARGE_SCREEN)) {

            purchaseIDText.setTextSize(Store.getInstance().textSizeLarge);

            purchaseTitleText.setTextSize(Store.getInstance().textSizeLarge);
            purchaseStatusText.setTextSize(Store.getInstance().textSizeLarge);

            vendorIdText.setTextSize(Store.getInstance().textSizeLarge);
            vendorNameText.setTextSize(Store.getInstance().textSizeLarge);
            vendorTinNumberText.setTextSize(Store.getInstance().textSizeLarge);
            vendorAddressText.setTextSize(Store.getInstance().textSizeLarge);
            vendorMailText.setTextSize(Store.getInstance().textSizeLarge);

            subTotalText.setTextSize(Store.getInstance().textSizeLarge);
            gstText.setTextSize(Store.getInstance().textSizeLarge);
            grandTotalText.setTextSize(Store.getInstance().textSizeLarge);

            shippingAddressText.setTextSize(Store.getInstance().textSizeLarge);


            purchaseId.setTextSize(Store.getInstance().textSizeLarge);
            purchaseTitle.setTextSize(Store.getInstance().textSizeLarge);
            purchaseDate.setTextSize(Store.getInstance().textSizeLarge);
            purchaseStatus.setTextSize(Store.getInstance().textSizeLarge);

            vendorID.setTextSize(Store.getInstance().textSizeLarge);
            vendorName.setTextSize(Store.getInstance().textSizeLarge);
            vendorContact.setTextSize(Store.getInstance().textSizeLarge);
            vendorTinNumber.setTextSize(Store.getInstance().textSizeLarge);
            vendorAddress.setTextSize(Store.getInstance().textSizeLarge);
            vendorMail.setTextSize(Store.getInstance().textSizeLarge);

            subTotal.setTextSize(Store.getInstance().textSizeLarge);;
            gst.setTextSize(Store.getInstance().textSizeLarge);
            grandTotal.setTextSize(Store.getInstance().textSizeLarge);
            shippingAddress.setTextSize(Store.getInstance().textSizeLarge);


            note.setTextSize(Store.getInstance().textSizeLarge);

        }


        if(purchase.getStatus().equals("Pending"))
        {
            moveItems.setVisibility(View.INVISIBLE);
            note.setText("Purchase order is in pending state");

        }
        else  if(purchase.getStatus().equals("Approved"))
        {
            accept.setVisibility(View.GONE);
            reject.setVisibility(View.GONE);
            note.setText("Purchase order has been approved");

        }
        else if(purchase.getStatus().equals("Moved"))
        {
            accept.setVisibility(View.GONE);
            reject.setVisibility(View.GONE);
            moveItems.setVisibility(View.INVISIBLE);
            note.setText("Items has been moved to stock");
        }
        else
        {
            note.setText("Purchase order has been Rejected");
            moveItems.setVisibility(View.GONE);
            accept.setVisibility(View.GONE);
            reject.setVisibility(View.GONE);
        }
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new UpdateStatus(getApplicationContext(),"Approved").execute();

            }
        });
        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UpdateStatus(getApplicationContext(),"Rejected").execute();
            }
        });
        moveItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new MoveItemsToStock(getApplicationContext()).execute();



            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new EditPurchaseItems(getApplicationContext()).execute();
            }
        });


    }
    class UpdateStatus extends AsyncTask
    {

        Context context;
        String url;
        HashMap<String,String> params;
        String jsonStr = null;
        public UpdateStatus(Context context,String status) {
            this.context = context;
            params = new HashMap<String, String>();
            this.params.put("status",status);
            this.params.put("purchase_id",Store.getInstance().currentPurchaseOrder.getId());
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.url = "purchase/updateStatus";
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

            Toast.makeText(context,jsonStr,Toast.LENGTH_SHORT).show();

            accept.setVisibility(View.GONE);
            reject.setVisibility(View.GONE);
            moveItems.setVisibility(View.VISIBLE);
        }

    }
    class EditPurchaseItems extends AsyncTask
    {

        Context context;
        String url;
        HashMap<String,String> params;
        String jsonStr = null;
        ArrayList<Items> itemList;
        public EditPurchaseItems(Context context) {
            this.context = context;
            params = new HashMap<String, String>();
            this.itemList = Store.getInstance().purchaseList.get(Store.getInstance().currentPurchaseOrderPosition).getItems();
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.params.put("purchase_id",Store.getInstance().currentPurchaseOrder.getId());
            this.url = "purchase/editItems";

            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            Type collectionType = new TypeToken<Collection<Items>>() {
            }.getType();

            String items = gson.toJson(itemList);
            Log.d("Json data",items);

            this.params.put("items",items);
            this.params.put("subTotal",subTotal.getText().toString());
            this.params.put("total",grandTotal.getText().toString());
            this.params.put("gst",gst.getText().toString());
            this.params.put("grandTotal",grandTotal.getText().toString());

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

            if(jsonStr.contains("true")) {

                finish();
                Intent intent = new Intent(context, PurchaseActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(intent);

                Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(context, "Not saved", Toast.LENGTH_SHORT).show();
            }
        }

    }
    class MoveItemsToStock extends AsyncTask
    {

        Context context;
        String url;
        HashMap<String,String> params;
        String jsonStr = null;
        public MoveItemsToStock(Context context) {
            this.context = context;
            params = new HashMap<String, String>();
            this.params.put("status","Moved");
            this.params.put("purchase_id",Store.getInstance().currentPurchaseOrder.getId());
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.url = "purchase/moveItemsToStock";
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


            finish();
            Intent intent = new Intent(context, PurchaseActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);

            Toast.makeText(context,jsonStr,Toast.LENGTH_SHORT).show();
        }

    }
}
