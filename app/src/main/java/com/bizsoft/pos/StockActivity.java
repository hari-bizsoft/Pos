package com.bizsoft.pos;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.bizsoft.pos.adapter.StockAdapter;
import com.bizsoft.pos.dataobject.Category;
import com.bizsoft.pos.dataobject.Items;
import com.bizsoft.pos.dataobject.Purchase;
import com.bizsoft.pos.dataobject.Stock;
import com.bizsoft.pos.dataobject.StockHome;
import com.bizsoft.pos.dataobject.Store;
import com.bizsoft.pos.dataobject.SupportDetaills;
import com.bizsoft.pos.dataobject.Vendor;
import com.bizsoft.pos.service.BizUtils;
import com.bizsoft.pos.service.HttpHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class StockActivity extends AppCompatActivity {
    ExpandableListView stocklistview;
    Spinner category,vendor,stock;
    private String vendorID;
    private String stockHomeID;
    private String currentCatId;
    private EditText itemName;
    private StockAdapter stockAdapter;
    private Dialog dialog;
    FloatingActionButton add,search,save;
    ArrayList<Stock> stockList = new ArrayList<Stock>() ;
    EditText searchKey;
    EditText fromDate,toDate;
    ImageButton fromDateButton,toDateButton;
    Dialog createStockDialog;
    private String fromDateValue;
    private String toDateValue;
    private int year,month,day;
    private String FLAG_DATE;
    static File filePath;
    BizUtils bizUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);
        stocklistview = (ExpandableListView) findViewById(R.id.stock_list);
        add = (FloatingActionButton) findViewById(R.id.add);
        search = (FloatingActionButton) findViewById(R.id.search);
        save = (FloatingActionButton) findViewById(R.id.generate_report);
        bizUtils = new BizUtils();



        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("Stock Home");

        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater inflator = (LayoutInflater) this .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.search_bar, null);
        searchKey = (EditText) v.findViewById(R.id.search_box);
        actionBar.setCustomView(v);




        searchKey.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


                System.out.println("before text change");






            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                System.out.println("on text change");

                String text = searchKey.getText().toString();


                HashMap<String,String> map = new HashMap<String, String>();
                map.put("search_bar",text);
                new SearchItems(StockActivity.this,map).execute();


                System.out.println("Stock size ---"+Store.getInstance().stock.size());
               /* for(int i=0;i<Store.getInstance().stock.size();i++)
                {
                    stockList.clear();
                    Stock stock = new Stock();
                    stock  = Store.getInstance().stock.get(i);
                    ArrayList<Items> itemList = stock.getItems();

                    //
                    ArrayList<Items> itemListTemp = new ArrayList<Items>();


                    for(int j=0;j<itemList .size();j++)
                    {
                        String itemName =  itemList.get(j).getName().toLowerCase();
                        System.out.println(" keyword = " + text);
                        System.out.println(" itemname = " + itemName);
                        System.out.println(" status = " + itemName.contains(text));


                        if(itemName.contains(text)) {
                            System.out.println(" j = " + j);
                            itemListTemp.add(itemList.get(j));
                            System.out.println(" item found =========== " + itemName);
                        }

                    }

                    stock.setItems(itemListTemp);

                    stockList.add(stock);







                }
                */


            }

            @Override
            public void afterTextChanged(Editable s) {
                System.out.println("after text change");
                /* new StockAdapter(StockActivity.this,stockList);

                stockAdapter.clear();
                stockAdapter.addAll(stockList);

                stockAdapter.notifyDataSetChanged();
                */

            }
        });




     new StockDetails(StockActivity.this).execute();
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // Intent intent = new Intent(StockActivity.this,AddItemsActivity.class);
              //  startActivity(intent);
                createStockHome();
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetSupportDetails(StockActivity.this).execute();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new GenerateReport(StockActivity.this).execute();
            }
        });

    }

    public void createStockHome()
    {
        createStockDialog = new Dialog(StockActivity.this);

        createStockDialog.setContentView(R.layout.add_stock_activity);

        createStockDialog.setTitle("Create Stock Home");


        final EditText stockName = (EditText) createStockDialog.findViewById(R.id.stock_name);
        Button create = (Button) createStockDialog.findViewById(R.id.create);
        createStockDialog.show();
        boolean status;
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String s = stockName.getText().toString();
                if(TextUtils.isEmpty(s))
                {
                    stockName.setError("Please fill");
                }
                else
                {
                    System.out.println("Called stockhome ");
                    new CreateStock(StockActivity.this,stockName.getText().toString()).execute();
                }
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return super.onCreateOptionsMenu(menu);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {


            default:
                return super.onOptionsItemSelected(item);
        }
    }
    class StockDetails extends AsyncTask
    {
        String  url;
        String jsonStr;
        Context context;
        HashMap<String,String> params;

        public StockDetails(Context context, HashMap<String, String> params) {
            this.context = context;
            this.params = params;
        }

        public StockDetails(Context context) {

            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.url = "stock/getAllStock";;

            this.context = context;
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
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setDateFormat("M/d/yy hh:mm a");
            Gson gson = gsonBuilder.create();


            Type collectionType = new TypeToken<Collection<Stock>>() {
            }.getType();

            Collection<Stock> stockList = gson.fromJson(jsonStr, collectionType);

            Store.getInstance().stock = (ArrayList<Stock>) stockList;




             stockAdapter = new StockAdapter(context,Store.getInstance().stock);
            stocklistview.setAdapter(stockAdapter);



        }

    }

    class SearchItems extends AsyncTask
    {
        String  url;
        String jsonStr;
        Context context;
        HashMap<String,String> params;

        public SearchItems(Context context, HashMap<String, String> params) {
            this.context = context;
            this.params = params;
        }

        public SearchItems(Context context) {

            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.url = "stock/getAllStock";;

            this.context = context;
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

            Log.d("JSON : ",jsonStr);
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setDateFormat("M/d/yy hh:mm a");
            Gson gson = gsonBuilder.create();


            Type collectionType = new TypeToken<Collection<Stock>>() {
            }.getType();

            Collection<Stock> stockList = gson.fromJson(jsonStr, collectionType);

            Store.getInstance().stock = (ArrayList<Stock>) stockList;


            stockAdapter.clear();
            stockAdapter.addAll(Store.getInstance().stock);
            stockAdapter.notifyDataSetChanged();



            if(dialog!=null) {
                dialog.dismiss();

            }


        }

    }
    class GetSupportDetails extends AsyncTask {
        Context context;
        HashMap<String, String> params = new HashMap<String, String>();
        String url = "items/getSupportDetails";
        String jsonStr;
        BizUtils bizUtils;

        public GetSupportDetails(Context context) {
            this.context = context;
            bizUtils = new BizUtils();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object[] params) {

            HttpHandler httpHandler = new HttpHandler();
            jsonStr = httpHandler.makeServiceCall(this.url, null);

            return true;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setDateFormat("M/d/yy hh:mm a");
            Gson gson = gsonBuilder.create();


            Type collectionType = new TypeToken<SupportDetaills>() {
            }.getType();

            SupportDetaills supportDetaills= gson.fromJson(jsonStr, collectionType);


            Store.getInstance().supportDetaills = (SupportDetaills) supportDetaills;

            bizUtils.display(context, String.valueOf(Store.getInstance().supportDetaills));



            bizUtils.display(context, String.valueOf(Store.getInstance().supportDetaills.getCategoryList().size()));


            search();

        }
    }
    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(this, myDateListener, year, month, day);
        }
        return null;
    }



    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // arg1 = year
            // arg2 = month
            // arg3 = day
            String date = arg3+"/"+(arg2+1)+"/"+arg1;
            if(FLAG_DATE.compareToIgnoreCase("fromdate")==0)
            {
                fromDate.setText(String.valueOf(date));

                fromDateValue = String.valueOf(date);
            }
            else
            {
                toDate.setText(String.valueOf(date));
                toDateValue = String.valueOf(date);
            }
            Toast.makeText(StockActivity.this, date, Toast.LENGTH_SHORT).show();
        }
    };
    public void search()
    {
       dialog = new Dialog(StockActivity.this);

        dialog.setContentView(R.layout.search_form);
        dialog.setTitle("Search Items");
        vendor = (Spinner) dialog.findViewById(R.id.vendor_spinner);
        category= (Spinner) dialog.findViewById(R.id.category_spinner);
        stock = (Spinner) dialog.findViewById(R.id.stock_spinner);
        Button search = (Button) dialog.findViewById(R.id.search);
         itemName = (EditText) dialog.findViewById(R.id.search_box);
        fromDateButton = (ImageButton) dialog.findViewById(R.id.from_date_button);
        toDateButton= (ImageButton) dialog.findViewById(R.id.to_date_button);
        fromDate = (EditText) dialog.findViewById(R.id.from_date);
        toDate= (EditText) dialog.findViewById(R.id.to_date);
        toDateValue = null;
        fromDateValue = null;

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean status = true;
                if(TextUtils.isEmpty(itemName.getText().toString()))
                {
                    status = true;

                    itemName.setText(String.valueOf(""));


                }

                if(status)
                {
                    System.out.println("test");
                    HashMap<String,String> params = new HashMap<String, String>();
                    if(fromDateValue!=null) {
                        params.put("from_date", fromDateValue);
                    }
                    if(toDateValue!=null) {
                        params.put("to_date", toDateValue);
                    }
                    params.put("category_id",currentCatId);
                    params.put("vendor_id",vendorID);
                    params.put("stock_id",stockHomeID);
                    params.put("item_name",itemName.getText().toString());

                    Store.getInstance().currentstockHomeID = stockHomeID;

                    new SearchItems(StockActivity.this,params).execute();
                }
                else {
                    itemName.setError("Please Fill..");
                }





            }
        });
        fromDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FLAG_DATE = "fromdate";

                showDialog(999);
            }
        });
        toDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FLAG_DATE = "todate";
                showDialog(999);
            }
        });

        setCategoryList();
        setVendorList();
        setStockHomeList();
        dialog.show();
    }
    private void attemptLogin() {


        // Reset errors.
        itemName.setError(null);


        // Store values at the time of the login attempt.
        String item = itemName.getText().toString();


        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(item)) {
            itemName.setError("invalid item name");
            focusView = itemName;
            cancel = true;
        }


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.

        }
    }
    public void setCategoryList()
    {
        // Spinner Drop down elements
        List<String> categoryList = new ArrayList<String>();

        final ArrayList<Category> list = Store.getInstance().supportDetaills.getCategoryList();

        int size = list.size();

        for (int i = 0; i < size;i ++)
        {
            categoryList.add(list.get(i).getName());

        }

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter <String>(StockActivity.this, android.R.layout.simple_spinner_item, categoryList);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        category.setAdapter(dataAdapter);
        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                currentCatId = list.get(position).getId();


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                try {
                    currentCatId = list.get(0).getId();
                }
                catch (Exception e)
                {

                }
            }
        });


    }
    public void setVendorList()
    {
        // Spinner Drop down elements
        List<String> categoryList = new ArrayList<String>();

        final ArrayList<Vendor> list = Store.getInstance().supportDetaills.getVendorList();

        int size = list.size();

        for (int i = 0; i < size;i ++)
        {
            categoryList.add(list.get(i).getName());

        }

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter <String>(StockActivity.this, android.R.layout.simple_spinner_item, categoryList);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        vendor.setAdapter(dataAdapter);
        vendor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {



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


    public void setStockHomeList()
    {
        // Spinner Drop down elements
        List<String> categoryList = new ArrayList<String>();

        final ArrayList<StockHome> list = Store.getInstance().supportDetaills.getStockHomeList();

        int size = list.size();

        for (int i = 0; i < size;i ++)
        {
            categoryList.add(list.get(i).getName());

        }

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter <String>(StockActivity.this, android.R.layout.simple_spinner_item, categoryList);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        stock.setAdapter(dataAdapter);
        stock.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {



                stockHomeID = list.get(position).getId();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                try {
                    stockHomeID  = list.get(0).getId();
                }
                catch (Exception e)
                {

                }
            }
        });


    }
    class CreateStock extends AsyncTask
    {
        String  url;
        String jsonStr;
        Context context;
        HashMap<String,String> params = new HashMap<String, String>();
        String stockName;

        public CreateStock(Context context, String stockName) {
            this.context = context;
            this.stockName= stockName;
            params.put("name",stockName);
            System.out.println("called ---- constructor");
        }



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.url = "stock/createStock";;
            System.out.println("called ---- pre");


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

            System.out.println("JSON : "+jsonStr);

            if(jsonStr!=null)
            {
                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.setDateFormat("M/d/yy hh:mm a");
                Gson gson = gsonBuilder.create();


                Type collectionType = new TypeToken<Collection<Stock>>() {
                }.getType();

                Collection<Stock> stockList = gson.fromJson(jsonStr, collectionType);

                Store.getInstance().stock = (ArrayList<Stock>) stockList;


                stockAdapter.clear();
                stockAdapter.addAll(Store.getInstance().stock);
                stockAdapter.notifyDataSetChanged();
                createStockDialog.dismiss();
            }


        }

    }
    class GenerateReport extends AsyncTask
    {
        boolean status;
        Dialog dialog;
        Context context;

        public GenerateReport(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new Dialog(StockActivity.this);
            dialog.setContentView(R.layout.spinner);

            dialog.show();

        }
        @Override
        protected Object doInBackground(Object[] params) {
            String fileName = "stock_report_"+bizUtils.getCurrentDateTime()+".xls";
            status =  saveExcelFile(StockActivity.this,fileName);
            return null;
        }


        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            dialog.cancel();
            if(status)
            {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(context);
                }
                builder.setTitle("View Report")
                        .setMessage("Would you like to view report now ?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with view

                                try {

                                    bizUtils.openFile(StockActivity.this, filePath, "application/vnd.ms-excel");

                                }catch (Exception e)
                                {
                                    Log.e("Task Failed", String.valueOf(e));
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_menu_view)
                        .show();


                Toast.makeText(StockActivity.this, "File saved", Toast.LENGTH_SHORT).show();

            }
            else {
                Toast.makeText(StockActivity.this, "File not saved", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private static boolean saveExcelFile(Context context, String fileName) {

        // check if available and not read only
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.e("Storage info", "Storage not available or read only");
            return false;
        }

        boolean success = false;

        //New Workbook
        Workbook wb = new HSSFWorkbook();

        Cell c = null;

        //Cell style for header row
        CellStyle cs = wb.createCellStyle();
        cs.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);

        cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        //Cell style for header row
        CellStyle cs1 = wb.createCellStyle();
        cs1.setFillForegroundColor(HSSFColor.WHITE.index);
        cs1.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        //New Sheet
        Sheet sheet1 = null;
        sheet1 = wb.createSheet("Stock Details");

        // Generate column headings




        Row row = sheet1.createRow(0);


        c = row.createCell(0);
        c.setCellValue("Stock ID");
        c.setCellStyle(cs);

        c = row.createCell(1);
        c.setCellValue("Stock Name");
        c.setCellStyle(cs);

        c = row.createCell(2);
        c.setCellValue("Category");
        c.setCellStyle(cs);


        c = row.createCell(3);
        c.setCellValue("Item ID");
        c.setCellStyle(cs);

        c = row.createCell(4);
        c.setCellValue("Moved Date");
        c.setCellStyle(cs);

        c = row.createCell(5);
        c.setCellValue("Item Name");
        c.setCellStyle(cs);

        c = row.createCell(6);
        c.setCellValue("Quantity");
        c.setCellStyle(cs);

        c = row.createCell(7);
        c.setCellValue("Price");
        c.setCellStyle(cs);







        for(int i=0;i<Store.getInstance().stock.size();i++) {

            Stock stock = Store.getInstance().stock.get(i);

            for (int x = 0; x < stock.getItems().size(); x++) {

                Row row1 = sheet1.createRow(x + 1);
                Items items = stock.getItems().get(x);

                c = row1.createCell(0);
                c.setCellValue(String.valueOf(stock.getId()));
                c.setCellStyle(cs1);

                c = row1.createCell(1);
                c.setCellValue(String.valueOf(stock.getName()));
                c.setCellStyle(cs1);

                c = row1.createCell(2);
                c.setCellValue(String.valueOf(items.getCategory()));
                c.setCellStyle(cs1);


                c = row1.createCell(3);
                c.setCellValue(String.valueOf(items.getId()));
                c.setCellStyle(cs1);

                c = row1.createCell(4);
                c.setCellValue(String.valueOf(items.getMovedDate()));
                c.setCellStyle(cs1);

                c = row1.createCell(5);
                c.setCellValue(String.valueOf(items.getName()));
                c.setCellStyle(cs1);

                c = row1.createCell(6);
                c.setCellValue(String.valueOf(items.getQuantity()));
                c.setCellStyle(cs1);

                c = row1.createCell(7);
                c.setCellValue(String.valueOf(items.getRetailPrice()));
                c.setCellStyle(cs1);






            }
        }

        sheet1.setColumnWidth(0, (15 * 500));
        sheet1.setColumnWidth(1, (15 * 500));
        sheet1.setColumnWidth(2, (15 * 500));
        sheet1.setColumnWidth(3, (15 * 500));
        sheet1.setColumnWidth(4, (15 * 500));
        sheet1.setColumnWidth(5, (15 * 500));
        sheet1.setColumnWidth(6, (15 * 500));
        sheet1.setColumnWidth(7, (15 * 500));


        // Create a path where we will place our List of objects on external storage
        File file = new File(context.getExternalFilesDir(null), fileName);

        filePath = file;
        FileOutputStream os = null;

        try {
            os = new FileOutputStream(file);
            wb.write(os);
            Log.w("FileUtils", "Writing file" + file);
            success = true;
        } catch (IOException e) {
            Log.w("FileUtils", "Error writing " + file, e);
        } catch (Exception e) {
            Log.w("FileUtils", "Failed to save file", e);
        } finally {
            try {
                if (null != os)
                    os.close();
            } catch (Exception ex) {
            }
        }
        return success;
    }
    public static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

}
