package com.bizsoft.pos;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bizsoft.pos.adapter.BillingItemSearchAdapter;
import com.bizsoft.pos.adapter.BillingItemsAdapter;
import com.bizsoft.pos.adapter.CustomerAdapter;
import com.bizsoft.pos.adapter.CustomerShowDialogAdapter;
import com.bizsoft.pos.adapter.PurchaseFullViewAdapter;
import com.bizsoft.pos.dataobject.Bill;
import com.bizsoft.pos.dataobject.BizViewStore;
import com.bizsoft.pos.dataobject.Category;
import com.bizsoft.pos.dataobject.Items;
import com.bizsoft.pos.dataobject.Store;
import com.bizsoft.pos.dataobject.User;
import com.bizsoft.pos.service.HttpHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.Result;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;

public class BillingActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private EditText searchKey;
    ImageView searchIcon;
    private Dialog createItemDialog;
    ListView listView;

    private String customerID;
    EditText customerSearchBox;
    ImageView customerSearchButton;
    private Dialog dialog;
    private ListView customerListView;
    TextView contactNumber,customerName;
    ListView itemListView;
    private BillingItemsAdapter itemAddedAdapter;
    TextView subTotal,gst,grandTotal;
    Button checkout,clear;
    Spinner paymentMode;
    private String paymentModeV;
    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView mScannerView;
    ImageView scanner;
    SharedPreferences sharedPref;
    Context context;
    TextView gst_Text,paymentModeText,chooseCustomerText,customerNameText,customerContactText,subTotalText,grandTotalText;
    Spinner discountSpinner;
    private String discountValue;
    EditText discount;
    TextView discountAmountText,discount_value_text,discountValueIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing);

        ActionBar actionBar = getSupportActionBar();



        getSupportActionBar().setTitle("Billing Place");

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
            if (checkPermission()) {
                Toast.makeText(getApplicationContext(), "Permission already granted", Toast.LENGTH_LONG).show();

            } else {
                requestPermission();
            }
        }



      //  LayoutInflater inflator = (LayoutInflater) this .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      //  View v = inflator.inflate(R.layout.billing_search_bar, null);
      //  actionBar.setCustomView(v);

        searchKey = (EditText) findViewById(R.id.search_box);
        searchIcon = (ImageView) findViewById(R.id.search_icon);
        scanner = (ImageView) findViewById(R.id.scan);

        customerSearchBox = (EditText) findViewById(R.id.search_edittext);
        customerSearchButton = (ImageView) findViewById(R.id.search_view);
        customerName = (TextView) findViewById(R.id.customer_name);
        contactNumber = (TextView) findViewById(R.id.contact_number);
        itemListView = (ListView) findViewById(R.id.listview);
        subTotal = (TextView) findViewById(R.id.sub_total);
        gst = (TextView) findViewById(R.id.gst);
        grandTotal = (TextView) findViewById(R.id.total);
        clear = (Button) findViewById(R.id.clear);
        checkout = (Button) findViewById(R.id.checkout);
        paymentMode = (Spinner) findViewById(R.id.payment_mode);
        gst_Text = (TextView) findViewById(R.id.gst_text);
        paymentModeText = (TextView) findViewById(R.id.payment_mode_text);
        chooseCustomerText = (TextView) findViewById(R.id.customer_list_textview);
        customerNameText = (TextView) findViewById(R.id.customer_name_text);
        customerContactText = (TextView) findViewById(R.id.customer_number_text);
        subTotalText = (TextView) findViewById(R.id.subtotal_text);
        grandTotalText = (TextView) findViewById(R.id.grand_total_text);
        discountSpinner = (Spinner) findViewById(R.id.discount_spinner);
        discount  = (EditText) findViewById(R.id.discount);
        discountAmountText = (TextView) findViewById(R.id.discount_amount);
        discount_value_text = (TextView) findViewById(R.id.discount_value_text);
        discountValueIn = (TextView) findViewById(R.id.discount_value_in);

        discount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {
                float dt =0;
                float gt = BizViewStore.getInstance().grandTotalValue;
                float discountAmount = 0;



                try {
                     dt = Float.parseFloat(discount.getText().toString());
                }
                catch (Exception e)
                {
                    System.out.println("dt = "+e);
                    Toast.makeText(context, "Invalid value at discount", Toast.LENGTH_SHORT).show();
                }


                try {
                    System.out.print("Discount value = " + discountValue);
                    if (discountValue.contains("Percentage")) {
                        discountAmount = (dt / 100) * gt;
                        discount_value_text.setText("Discount(%) RM");
                    } else {
                        discountAmount = dt;
                        discount_value_text.setText("Discount(RM) RM");
                    }
                    discountAmountText.setText("-" + String.format("%.2f",discountAmount));
                    System.out.println("Discount = " + discountAmount);

                    gt = BizViewStore.getInstance().grandTotalValue - discountAmount;
                    BizViewStore.getInstance().discountAmount = discountAmount;

                    grandTotal.setText(String.format("%.2f",gt));
                }
                catch (Exception e)
                {
                    System.out.println("Exce"+e);
                }

            }
        });

        if(Store.getInstance().screenSize.equals(Store.getInstance().LARGE_SCREEN))
        {
            customerName.setTextSize(Store.getInstance().textSizeLarge);
            contactNumber.setTextSize(Store.getInstance().textSizeLarge);
            subTotal.setTextSize(Store.getInstance().textSizeLarge);
            gst.setTextSize(Store.getInstance().textSizeLarge);
            grandTotal.setTextSize(Store.getInstance().textSizeLarge);
            gst_Text.setTextSize(Store.getInstance().textSizeLarge);
            paymentModeText.setTextSize(Store.getInstance().textSizeLarge);
            chooseCustomerText.setTextSize(Store.getInstance().textSizeLarge);
            customerNameText.setTextSize(Store.getInstance().textSizeLarge);
            customerContactText.setTextSize(Store.getInstance().textSizeLarge);
            subTotalText.setTextSize(Store.getInstance().textSizeLarge);
            grandTotalText.setTextSize(Store.getInstance().textSizeLarge);
            discountValueIn.setTextSize(Store.getInstance().textSizeLarge);
            discountAmountText.setTextSize(Store.getInstance().textSizeLarge);
            discount_value_text.setTextSize(Store.getInstance().textSizeLarge);


        }



        context = BillingActivity.this;
        sharedPref = context.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String gstV = sharedPref.getString("gst","6.0");
        gst_Text.setText("GST("+gstV+"%) RM");
        setPaymentMode();
        setDiscount();
        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(itemAddedAdapter.getCount()!=0) {
                    //checkout
                    //new FlushCart(BillingActivity.this).execute();

                    new CreateBill(BillingActivity.this).execute();
                }else
                {
                    Toast.makeText(BillingActivity.this, "Please add item", Toast.LENGTH_SHORT).show();
                }
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Store.getInstance().addedItemList.clear();

                ArrayList<Items> tempList = new ArrayList<Items>();



                Store.getInstance().addedItemList.addAll(tempList);

                BizViewStore.getInstance().addedItemAdapter.notifyDataSetChanged();

                customerName.setText("");
                contactNumber.setText("");
                discount.setText("0");


            }
        });

        scanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (customerName.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Choose Customer first", Toast.LENGTH_SHORT).show();
                } else {


                    Toast.makeText(BillingActivity.this, "Start Scanning", Toast.LENGTH_SHORT).show();
                    int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                    if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
                        Toast.makeText(BillingActivity.this, "android M", Toast.LENGTH_SHORT).show();

                        if (checkPermission()) {
                            if (mScannerView == null) {

                                Toast.makeText(BillingActivity.this, "Starting", Toast.LENGTH_SHORT).show();
                                dialog = new Dialog(BillingActivity.this);

                                dialog.setContentView(R.layout.show_customer_list_dialog_main);

                                dialog.setTitle("Item Search Result");
                                mScannerView = new ZXingScannerView(BillingActivity.this);
                                dialog.setContentView(mScannerView);
                                dialog.setOnKeyListener(new Dialog.OnKeyListener() {

                                    @Override
                                    public boolean onKey(DialogInterface arg0, int keyCode,
                                                         KeyEvent event) {
                                        // TODO Auto-generated method stub
                                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                                            Toast.makeText(BillingActivity.this, "Back pressed", Toast.LENGTH_SHORT).show();

                                            dialog.dismiss();
                                        }
                                        return true;
                                    }
                                });
                                dialog.show();


                            }
                            mScannerView.setResultHandler(BillingActivity.this);
                            mScannerView.startCamera();
                        } else {
                            requestPermission();
                        }
                    } else {


                        if (mScannerView == null) {

                            Toast.makeText(BillingActivity.this, "Starting", Toast.LENGTH_SHORT).show();
                            System.out.println("opening Camera");
                            dialog = new Dialog(BillingActivity.this);

                            dialog.setContentView(R.layout.show_customer_list_dialog_main);

                            dialog.setTitle("Item Search Result");
                            mScannerView = new ZXingScannerView(BillingActivity.this);
                            dialog.setContentView(mScannerView);
                            dialog.setOnKeyListener(new Dialog.OnKeyListener() {

                                @Override
                                public boolean onKey(DialogInterface arg0, int keyCode,
                                                     KeyEvent event) {
                                    // TODO Auto-generated method stub
                                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                                        Toast.makeText(BillingActivity.this, "Back pressed", Toast.LENGTH_SHORT).show();


                                        dialog.dismiss();
                                    }
                                    return true;
                                }
                            });
                            dialog.show();


                        }
                        mScannerView.setResultHandler(BillingActivity.this);
                        mScannerView.startCamera();
                    }

                }
            }
        });


        BizViewStore.getInstance().billingSubTotal = subTotal;
        BizViewStore.getInstance().billingGst = gst;
        BizViewStore.getInstance().billingGrandTotal= grandTotal;


        itemAddedAdapter = new BillingItemsAdapter(getApplicationContext(),Store.getInstance().addedItemList);
        itemListView.setAdapter(itemAddedAdapter);

        BizViewStore.getInstance().addedItemAdapter = itemAddedAdapter;

        BizViewStore.getInstance().billingCustomerName = customerName;
        BizViewStore.getInstance().billingContactNumber = contactNumber;

        customerSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String temp = customerSearchBox.getText().toString();

                if(customerName.getText().toString().isEmpty())
                {
                    new GetCustomerDetails(BillingActivity.this,temp).execute();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Customer Already Choosed",Toast.LENGTH_SHORT).show();
                }


            }
        });



        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if(customerName.getText().toString().isEmpty())
                {
                    Toast.makeText(getApplicationContext(),"Choose Customer first",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    String temp = searchKey.getText().toString();



                    new GetAll(BillingActivity.this,temp).execute();
                }


            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
            if (checkPermission()) {
                if(mScannerView == null) {
                 //   mScannerView = new ZXingScannerView(this);
                   // setContentView(mScannerView);
                }
                //mScannerView.setResultHandler(this);
               // mScannerView.startCamera();
            } else {
                requestPermission();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mScannerView!=null)
        {
            mScannerView.stopCamera();
        }

    }
    private boolean checkPermission() {
        return ( ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA ) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (grantResults.length > 0) {

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted){
                        Toast.makeText(getApplicationContext(), "Permission Granted, Now you can access camera", Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(getApplicationContext(), "Permission Denied, You cannot access and camera", Toast.LENGTH_LONG).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(CAMERA)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{CAMERA},
                                                            REQUEST_CAMERA);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new android.support.v7.app.AlertDialog.Builder(BillingActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
    public void setPaymentMode()
    {
        final List<String> genderList = new ArrayList<String>();
        genderList.add("Cash");
        genderList.add("Card");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter <String>(BillingActivity.this, android.R.layout.simple_spinner_item, genderList);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paymentMode.setAdapter(dataAdapter);
        paymentMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(),genderList.get(position),Toast.LENGTH_SHORT).show();
                paymentModeV = genderList.get(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                paymentModeV = genderList.get(0);


            }


        });



    }
    public void setDiscount()
    {
        final List<String> genderList = new ArrayList<String>();
        genderList.add("Percentage");
        genderList.add("Amount");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter <String>(BillingActivity.this, android.R.layout.simple_spinner_item, genderList);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        discountSpinner.setAdapter(dataAdapter);
        discountSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(),genderList.get(position),Toast.LENGTH_SHORT).show();
                discountValue = genderList.get(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                discountValue = genderList.get(0);


            }


        });



    }
    public void showItems()
    {
        createItemDialog = new Dialog(BillingActivity.this);

        createItemDialog.setContentView(R.layout.show_items_list);

        createItemDialog.setTitle("Add Items");

        listView = (ListView) createItemDialog.findViewById(R.id.listview);

        listView.setAdapter(new BillingItemSearchAdapter(BillingActivity.this,Store.getInstance().billingItemList));



        createItemDialog.show();


        BizViewStore.getInstance().itemShowDialog = createItemDialog;
    }

    @Override
    public void handleResult(Result result) {
        final String result1 = result.getText();
        Log.d("QRCodeScanner", result.getText());
        Log.d("QRCodeScanner", result.getBarcodeFormat().toString());
        mScannerView.removeAllViews(); //<- here remove all the views, it will make an Activity having no View
        mScannerView.stopCamera();
        dialog.cancel();
        mScannerView = null;


        new GetAllByCode                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    (BillingActivity.this,result.getText()).execute();


    }


    public class GetAll extends AsyncTask<Void, Void, Boolean> {

        private final String url;

        String jsonStr;
        Context context;
        HashMap<String,String> params ;


        GetAll(Context context) {

            this.url = "items/getAllItems";
            this.params = new HashMap<String, String>();

            this.context = context;



        }

        public GetAll(Context context, String key) {
            this.url = "items/getAllItems";
            this.params = new HashMap<String, String>();
            params.put("search_key",key);
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            // Simulate network access.

            HttpHandler httpHandler = new HttpHandler();
            jsonStr = httpHandler.makeServiceCall(this.url, this.params);


            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {


            // showProgress(false);

            try {
                if (jsonStr != null) {

                    GsonBuilder gsonBuilder = new GsonBuilder();
                    gsonBuilder.setDateFormat("M/d/yy hh:mm a");
                    Gson gson = gsonBuilder.create();


                    Type collectionType = new TypeToken<Collection<Items>>() {
                    }.getType();

                    Collection<Items> items = gson.fromJson(jsonStr, collectionType);


                    Store.getInstance().billingItemList = (ArrayList<Items>) items;


                    for(int i=0;i<Store.getInstance().billingItemList.size();i++)
                    {
                        if( Store.getInstance().billingItemList.get(i).getQuantity()!=0) {
                            Store.getInstance().billingItemList.get(i).setQuantity(1);
                        }

                    }



                    //bizUtils.display(context, jsonStr);
                    Log.d("JSON",jsonStr);
                    showItems();

                    //  finish();
                } else {


                }
            }
            catch (Exception e)
            {

            }
        }

        @Override
        protected void onCancelled() {
            //mAuthTask = null;
            //showProgress(false);


        }
    }
    public class GetAllByCode extends AsyncTask<Void, Void, Boolean> {

        private final String url;

        String jsonStr;
        Context context;
        HashMap<String,String> params ;


        GetAllByCode(Context context) {

            this.url = "items/getAllItemsByCode";
            this.params = new HashMap<String, String>();

            this.context = context;



        }

        public GetAllByCode(Context context, String key) {
            this.url = "items/getAllItems";
            this.params = new HashMap<String, String>();
            params.put("search_key",key);
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            // Simulate network access.

            HttpHandler httpHandler = new HttpHandler();
            jsonStr = httpHandler.makeServiceCall(this.url, this.params);


            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {


            // showProgress(false);

            try {
                if (jsonStr != null) {

                    GsonBuilder gsonBuilder = new GsonBuilder();
                    gsonBuilder.setDateFormat("M/d/yy hh:mm a");
                    Gson gson = gsonBuilder.create();


                    Type collectionType = new TypeToken<Collection<Items>>() {
                    }.getType();

                    Collection<Items> items = gson.fromJson(jsonStr, collectionType);


                    Store.getInstance().billingItemList = (ArrayList<Items>) items;



                    //bizUtils.display(context, jsonStr);
                    Log.d("JSON",jsonStr);
                    showItems();

                    //  finish();
                } else {


                }
            }
            catch (Exception e)
            {

            }
        }

        @Override
        protected void onCancelled() {
            //mAuthTask = null;
            //showProgress(false);


        }
    }
    class GetCustomerDetails extends AsyncTask
    {
        Context context;
        String jsonStr;
        String url;
        String searchKey;
        HashMap<String,String> params;

        public GetCustomerDetails(Context context, String searchKey) {
            this.context = context;
            this.url="user/getCustomerList";
            this.searchKey = searchKey;
            this.params = new HashMap<String, String>();

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.params.put("search_key",this.searchKey);


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

            try {
                Log.d("JSON :",jsonStr);
                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.setDateFormat("M/d/yy hh:mm a");
                Gson gson = gsonBuilder.create();


                Type collectionType = new TypeToken<Collection<User>>() {
                }.getType();

                Collection<User> customers = gson.fromJson(jsonStr, collectionType);

                Store.getInstance().customerList = (ArrayList<User>) customers;

                search();

            }
            catch (Exception e)
            {

            }
        }

    }
    class CreateBill extends AsyncTask
    {
        Context context;
        String jsonStr;
        String url;
        String searchKey;
        HashMap<String,String> params;

        public CreateBill(Context context) {
            this.context = context;
            this.url="bill/CreateBills";
            this.searchKey = searchKey;
            this.params = new HashMap<String, String>();

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.params.put("customer_id",Store.getInstance().currentCustomerID);
            this.params.put("subtotal",subTotal.getText().toString());
            this.params.put("gst",gst.getText().toString());
            this.params.put("grand_total",grandTotal.getText().toString());
            this.params.put("payment_mode",paymentModeV);
            this.params.put("discount_type",discountValue);
            this.params.put("discount_amount", String.valueOf(BizViewStore.getInstance().discountAmount));
            this.params.put("discount_rate",discount.getText().toString());


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

            try {
                Log.d("JSON :",jsonStr);
                if(jsonStr.contains("true"))
                {
                    Toast.makeText(context, "Bill generated", Toast.LENGTH_SHORT).show();

                    new FlushCart(context).execute();



                }
                else
                {
                    Toast.makeText(context, "Bill not generated", Toast.LENGTH_SHORT).show();
                }


            }
            catch (Exception e)
            {

            }
        }

    }
    class FlushCart extends AsyncTask
    {
        Context context;
        String jsonStr;
        String url;
        String searchKey;
        HashMap<String,String> params;

        public FlushCart(Context context) {
            this.context = context;
            this.url="cart/flushCart";
            this.searchKey = searchKey;
            this.params = new HashMap<String, String>();

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.params.put("customer_id",Store.getInstance().currentCustomerID);


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

            try {
                Log.d("JSON :",jsonStr);
                if(jsonStr.contains("true"))
                {
                    Toast.makeText(context, "Cart flushed", Toast.LENGTH_SHORT).show();


                    finish();

                    new GetBillDetails(context).execute();

                }
                else
                {
                    Toast.makeText(context, "Cart not flushed", Toast.LENGTH_SHORT).show();
                }


            }
            catch (Exception e)
            {

            }
        }

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
            startActivity(intent);
        }


    }


    public void search()
    {
        dialog = new Dialog(BillingActivity.this);

        dialog.setContentView(R.layout.show_customer_list_dialog_main);

        dialog.setTitle("Customer Search Result");
        
        
        customerListView = (ListView) dialog.findViewById(R.id.listview);

        customerListView.setAdapter(new CustomerShowDialogAdapter(getApplicationContext()));

        dialog.show();

        BizViewStore.getInstance().billingCustomerDialog = dialog;
    }


}
