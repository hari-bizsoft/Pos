package com.bizsoft.pos;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bizsoft.pos.dataobject.Category;
import com.bizsoft.pos.dataobject.Items;
import com.bizsoft.pos.dataobject.Store;
import com.bizsoft.pos.dataobject.gst;
import com.bizsoft.pos.service.BizUtils;
import com.bizsoft.pos.service.HttpHandler;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;

import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;

import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    HorizontalBarChart chart ;
    ArrayList<BarEntry> BARENTRY ;
    ArrayList<String> BarEntryLabels ;
    BarDataSet Bardataset ;
    BarData BARDATA ;
    PieChart pieChart;
    ArrayList<Entry> entries;
    ArrayList<String> pieChartLabels ;
    FloatingActionButton refresh;
    BizUtils bizUtils;
    private HashMap<String, Integer> colorCode;
    TextView purchaseGST,billedGST,plStatus,amount,status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        chart = (HorizontalBarChart) findViewById(R.id.chart);
         pieChart = (PieChart) findViewById(R.id.piechart);
        refresh = (FloatingActionButton) findViewById(R.id.fab);
        purchaseGST = (TextView) findViewById(R.id.purchase_gst_value);
        billedGST= (TextView) findViewById(R.id.bill_gst_value);
        plStatus = (TextView) findViewById(R.id.pl_status_value);
        amount = (TextView) findViewById(R.id.status_value);
        status = (TextView) findViewById(R.id.status);
        refresh.bringToFront();
        getScreenSize();



        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new GetAllCategory(MainActivity.this).execute();

                pieChart.invalidate();
                chart.invalidate();
                Snackbar.make(v, "Refreshing...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        bizUtils = new BizUtils();






        try {
            new GetGSTEstimate(MainActivity.this).execute();
        }catch (Exception e)
        {
            Log.e("GST ERROR","ERROR");
        }
        try {
            new GetAllCategory(MainActivity.this).execute();
        }
        catch (Exception e)
        {
            Log.e("CAT","ERROR");
        }





        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Dashboard");




        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        View header=navigationView.getHeaderView(0);



        ImageView displayPic = (ImageView) header.findViewById(R.id.displayPic);
        TextView name = (TextView)header.findViewById(R.id.username);
        TextView role  = (TextView)header.findViewById(R.id.role);
        name.setText(String.valueOf(Store.getInstance().user.getUsername()));
        role.setText(String.valueOf(Store.getInstance().user.getRole()));


    }
    public void getScreenSize()
    {
        int screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;


        String toastMsg;
        switch(screenSize) {
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                toastMsg = Store.getInstance().LARGE_SCREEN;
                break;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                toastMsg = Store.getInstance().MEDIUM_SCREEN;
                break;
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                toastMsg = Store.getInstance().SMALL_SCREEN;
                break;
            default:
                toastMsg = "Screen size is neither large, normal or small";
        }
        Store.getInstance().screenSize = toastMsg;
       // Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
    }
    public void AddValuesToBARENTRY(){

        for(int i=0;i<Store.getInstance().itemListChart.size();i++)
        {

            Items items = Store.getInstance().itemListChart.get(i);
            BARENTRY.add(new BarEntry(items.getQuantity(),i));
        }




    }

    public void AddValuesToBarEntryLabels(){

        for(int i=0;i<Store.getInstance().itemListChart.size();i++)
        {

            Items items = Store.getInstance().itemListChart.get(i);
            BarEntryLabels.add(items.getName());
        }



    }
    public void addValuesToPieChart()
    {
         entries = new ArrayList<>();

         for(int i = 0;i<Store.getInstance().categoryListChart.size();i++)
         {
             Category category = Store.getInstance().categoryListChart.get(i);
             System.out.println("-----"+category.getItems().size());
             for (int x=0;x<category.getItems().size();x++)
             {
                 System.out.println("-----"+category.getItems().get(x).getType());
             }

             entries.add(new Entry(category.getItems().size(),i));
         }
    }
    public void addLabelsToPieChart()
    {
        pieChartLabels = new ArrayList<String>();
        for(int i = 0;i<Store.getInstance().categoryListChart.size();i++)
        {
            Category category = Store.getInstance().categoryListChart.get(i);
            System.out.println("===="+category.getName());
            pieChartLabels.add(category.getName());
        }
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);



        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        switch (item.getItemId()) {

            case R.id.category: {
                //do somthing
                Intent intent = new Intent(getApplicationContext(),CategoryActivity.class);
                startActivity(intent);

                break;
            }
            case R.id.items: {
                //do somthing
                Intent intent = new Intent(getApplicationContext(),ItemsActivity.class);
                startActivity(intent);

                break;
            }
            case R.id.stock: {
                //do somthing
                Intent intent = new Intent(getApplicationContext(),StockActivity.class);
                startActivity(intent);

                break;
            }
            case R.id.purchase: {
                //do somthing
                Intent intent = new Intent(getApplicationContext(),PurchaseActivity.class);
                startActivity(intent);

                break;
            }
            case R.id.customer: {
                //do somthing
                Intent intent = new Intent(getApplicationContext(),CustomerActivity.class);
                startActivity(intent);

                break;
            }
            case R.id.billing: {
                //do somthing
                Intent intent = new Intent(getApplicationContext(),BillingActivity.class);
                startActivity(intent);

                break;
            }
            case R.id.cart: {
                //do somthing
                Intent intent = new Intent(getApplicationContext(),CartActivity.class);
                startActivity(intent);

                break;
            }
            case R.id.sales: {
                //do somthing
                Intent intent = new Intent(getApplicationContext(),ShowBillActivity.class);
                startActivity(intent);

                break;
            }
            case R.id.settings: {
                //do somthing
                Intent intent = new Intent(getApplicationContext(),CompanySettingsAcitivity.class);
                startActivity(intent);

                break;
            }
            case R.id.vendor: {
                //do somthing
                Intent intent = new Intent(getApplicationContext(),VendorActivity.class);
                startActivity(intent);

                break;
            }
            case R.id.reports: {
                //do somthing
                Intent intent = new Intent(getApplicationContext(),ReportMenuActivity.class);
                startActivity(intent);

                break;
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);





        return true;
    }

    public class GetAllCategory extends AsyncTask<Void, Void, Boolean> {

        private final String  url;

        String jsonStr;
        Context context;



        GetAllCategory(Context context) {

            this.url = "category/getAllCategory";;

            this.context = context;


        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            // Simulate network access.


            HttpHandler httpHandler = new HttpHandler();
            jsonStr = httpHandler.makeServiceCall(this.url,null);


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


                    Type collectionType = new TypeToken<Collection<Category>>() {
                    }.getType();

                    Collection<Category> categories = gson.fromJson(jsonStr, collectionType);


                    Store.getInstance().categoryListChart = (ArrayList<Category>) categories;



                    addValuesToPieChart();

                    PieDataSet dataSet = new PieDataSet(entries, "..# ");
                    addLabelsToPieChart();
                    PieData data = new PieData(pieChartLabels, dataSet);

                    pieChart.setData(data);
                    pieChart.setDescription("");
                    pieChart.setDrawSlicesUnderHole(true);
                    pieChart.setDrawHoleEnabled(false);
                    pieChart.setTransparentCircleRadius(38f);

                    pieChart.setHoleRadius(58f);


                     int[] colors = ColorTemplate.COLORFUL_COLORS;



                     colorCode = new HashMap<>();

                    pieChart.getLegend().setEnabled(false);
                    dataSet.setColors(colors);

                    for(int i=0;i<dataSet.getEntryCount();i++)
                    {

                        bizUtils.display("=============COLOR CODE======"+dataSet.getColor(i));
                        colorCode.put(dataSet.getLabel(),dataSet.getColor(i));

                    }

                    data.setValueTextSize(13f);
                    data.setValueTextColor(Color.DKGRAY);
                    pieChart.invalidate();

                    new GetAll(MainActivity.this).execute();


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


                    Store.getInstance().itemListChart = (ArrayList<Items>) items;



                    //bizUtils.display(context, jsonStr);
                    Log.d("JSON",jsonStr);

                    BARENTRY = new ArrayList<>();

                    BarEntryLabels = new ArrayList<String>();

                    AddValuesToBARENTRY();

                    AddValuesToBarEntryLabels();

                    Bardataset = new MyBarDataSet(BARENTRY, "Items");

                    BARDATA = new BarData(BarEntryLabels, Bardataset);
                    BARDATA.setGroupSpace(200f);



                    Bardataset.setColors(new int[]{ContextCompat.getColor(context, R.color.green),
                            ContextCompat.getColor(context, R.color.orange),
                            ContextCompat.getColor(context, R.color.red)});


                    chart.setData(BARDATA);

                    chart.animateY(3000);
                    new GetGSTEstimate(MainActivity.this).execute();
                    //  finish();
                } else {


                }
            }
            catch (Exception e)
            {
                System.out.println(e);
            }
        }

        @Override
        protected void onCancelled() {
            //mAuthTask = null;
            //showProgress(false);


        }
    }

    public class GetGSTEstimate extends AsyncTask<Void, Void, Boolean> {

        private final String  url;

        String jsonStr;
        Context context;



        GetGSTEstimate(Context context) {

            this.url = "purchase/estimateGST";;

            this.context = context;


        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            // Simulate network access.


            HttpHandler httpHandler = new HttpHandler();
            jsonStr = httpHandler.makeServiceCall(this.url,null);


            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            System.out.println("JSON RESPONSE "+jsonStr);



            // showProgress(false);


            try {


                if (jsonStr != null) {

                    GsonBuilder gsonBuilder = new GsonBuilder();
                    gsonBuilder.setDateFormat("M/d/yy hh:mm a");
                    Gson gson = gsonBuilder.create();


                    Type collectionType = new TypeToken<gst>() {
                    }.getType();

                    gst gst = gson.fromJson(jsonStr, collectionType);


                    Store.getInstance().gstDO = (gst) gst;



                    gst gst1 =  Store.getInstance().gstDO;
                    purchaseGST.setText(String.valueOf(gst1.getTotalPurchaseGST())+" RM");
                    billedGST.setText(String.valueOf(gst1.getTotalBillGST())+ " RM");
                    plStatus.setText(String.valueOf(gst1.getStatus()));

                    if(gst1.getStatus().equals("profit"))
                    {
                        plStatus.setTextColor(Color.GREEN);
                        status.setText("Profit = ");
                    }
                    else
                    {
                        plStatus.setTextColor(Color.RED);
                        status.setText("Loss = ");
                    }
                    amount.setText(String.valueOf(gst1.getAmount())+" RM");




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

    public class MyBarDataSet extends BarDataSet {


        public MyBarDataSet(List<BarEntry> yVals, String label) {
            super(yVals, label);
        }

        @Override
        public int getColor(int index) {
            bizUtils.display("=======>"+Store.getInstance().itemListChart.get(index).getReorderLevel());



            if(getEntryForXIndex(index).getVal() < Store.getInstance().itemListChart.get(index).getReorderLevel()) {


                // less than 30 red

                return mColors.get(2);
            }
            else if(getEntryForXIndex(index).getVal() < (Store.getInstance().itemListChart.get(index).getReorderLevel() +5) ) // less than 100 orange
                return mColors.get(1);
            else // greater or equal than 100 red
                return mColors.get(0);
        }

    }
}
