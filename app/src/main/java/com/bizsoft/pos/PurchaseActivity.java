package com.bizsoft.pos;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bizsoft.pos.adapter.PurchaseAdapter;
import com.bizsoft.pos.dataobject.Purchase;
import com.bizsoft.pos.dataobject.Store;
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
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import me.bendik.simplerangeview.SimpleRangeView;

public class PurchaseActivity extends AppCompatActivity {

    FloatingActionButton createOrder,search,save;
    ListView listView;
    private Dialog searchDialog;
    ImageButton fromDateButton,toDateButton;
    EditText fromDate,toDate,purchaseID,purchaseName;
    private Calendar calendar;
    private int year,month,day;
    String FLAG_DATE;
    Spinner status,vendor;
    private String status_value;
    private HashMap<String, String> vendorMap;
    private String vendor_value;
    int startParams,endParams;
    private float threshhold =0;
    String vendorID;
    private String vendorId;
    private String fromDateValue;
    private String toDateValue;
    Button searchPurchase;
    static File filePath;
    BizUtils bizUtils;
    private TextView range;
    private SimpleRangeView amountRange;
    private int startAmountFixed, endAmountFixed, startAmount, endAmount, count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);
        getSupportActionBar().setTitle("Purchase Order List");
        createOrder = (FloatingActionButton) findViewById(R.id.add);
        search = (FloatingActionButton) findViewById(R.id.search);
        listView = (ListView) findViewById(R.id.listview);
        save = (FloatingActionButton) findViewById(R.id.generate_report);
        bizUtils = new BizUtils();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new GenerateReport(PurchaseActivity.this).execute();
            }
        });


        createOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(PurchaseActivity.this,CreateOrderActivity.class);
                startActivity(intent);
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                searchDialog();
            }
        });
        new GetAll(PurchaseActivity.this).execute();

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
            dialog = new Dialog(PurchaseActivity.this);
            dialog.setContentView(R.layout.spinner);

            dialog.show();

        }
        @Override
        protected Object doInBackground(Object[] params) {
            String fileName = "purchase_report_"+bizUtils.getCurrentDateTime()+".xls";
            status =  saveExcelFile(PurchaseActivity.this,fileName);
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

                                  openFile(filePath);
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


                Toast.makeText(PurchaseActivity.this, "File saved", Toast.LENGTH_SHORT).show();

            }
            else {
                Toast.makeText(PurchaseActivity.this, "File not saved", Toast.LENGTH_SHORT).show();
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
        sheet1 = wb.createSheet("Purchase Order");

        // Generate column headings




        Row row = sheet1.createRow(0);


        c = row.createCell(0);
        c.setCellValue("Purchase ID");
        c.setCellStyle(cs);

        c = row.createCell(1);
        c.setCellValue("Purchase Title");
        c.setCellStyle(cs);

        c = row.createCell(2);
        c.setCellValue("Purchase Date");
        c.setCellStyle(cs);


        c = row.createCell(3);
        c.setCellValue("Status");
        c.setCellStyle(cs);

        c = row.createCell(4);
        c.setCellValue("Vendor ID");
        c.setCellStyle(cs);

        c = row.createCell(5);
        c.setCellValue("Vendor Name");
        c.setCellStyle(cs);

        c = row.createCell(6);
        c.setCellValue("Vendor Phone Number");
        c.setCellStyle(cs);

        c = row.createCell(7);
        c.setCellValue("Vendor Mail");
        c.setCellStyle(cs);

        c = row.createCell(8);
        c.setCellValue("Vendor TIN");
        c.setCellStyle(cs);

        c = row.createCell(9);
        c.setCellValue("Vendor Address");
        c.setCellStyle(cs);

        c = row.createCell(10);
        c.setCellValue("Item Names");
        c.setCellStyle(cs);

        c = row.createCell(11);
        c.setCellValue("Item Quantity");
        c.setCellStyle(cs);

        c = row.createCell(12);
        c.setCellValue("Item Price");
        c.setCellStyle(cs);


        c = row.createCell(13);
        c.setCellValue("Sub Total");
        c.setCellStyle(cs);

        c = row.createCell(14);
        c.setCellValue("GST");
        c.setCellStyle(cs);

        c = row.createCell(15);
        c.setCellValue("Grand Total");
        c.setCellStyle(cs);





        Row row1 = null;
        float totalGrantTotal = 0;

        for(int i=0;i<Store.getInstance().purchaseList.size();i++)
        {

             row1 = sheet1.createRow(i+1);
            Purchase purchase = Store.getInstance().purchaseList.get(i);

            c = row1.createCell(0);
            c.setCellValue(String.valueOf(purchase.getId()));
            c.setCellStyle(cs1);

            c = row1.createCell(1);
            c.setCellValue(String.valueOf(purchase.getName()));
            c.setCellStyle(cs1);

            c = row1.createCell(2);
            c.setCellValue(String.valueOf(purchase.getDateCreated()));
            c.setCellStyle(cs1);


            c = row1.createCell(3);
            c.setCellValue(String.valueOf(purchase.getStatus()));
            c.setCellStyle(cs1);

            c = row1.createCell(4);
            c.setCellValue(String.valueOf(purchase.getVendor().getId()));
            c.setCellStyle(cs1);

            c = row1.createCell(5);
            c.setCellValue(String.valueOf(purchase.getVendor().getName()));
            c.setCellStyle(cs1);

            c = row1.createCell(6);
            c.setCellValue(String.valueOf(purchase.getVendor().getContactNumber()));
            c.setCellStyle(cs1);

            c = row1.createCell(7);
            c.setCellValue(String.valueOf(purchase.getVendor().getEmail()));
            c.setCellStyle(cs1);

            c = row1.createCell(8);
            c.setCellValue(String.valueOf(purchase.getVendor().getTinNumber()));
            c.setCellStyle(cs1);

            c = row1.createCell(9);
            c.setCellValue(String.valueOf(purchase.getVendor().getAddress()));
            c.setCellStyle(cs1);

            c = row1.createCell(10);
            String itemName = "";
            String itemQunatity = "";
            String itemPrice = "";
            totalGrantTotal = 0;

            for(int j=0;j<purchase.getItems().size();j++)
            {


                itemName = itemName + purchase.getItems().get(j).getName()+ " - ";
                itemQunatity = itemQunatity + purchase.getItems().get(j).getQuantity()+ " - ";
                itemPrice = itemPrice + purchase.getItems().get(j).getRetailPrice()+ " - ";
                totalGrantTotal = totalGrantTotal + purchase.getItems().get(j).getRetailPrice();
            }

            c.setCellValue(String.valueOf(itemName));
            c.setCellStyle(cs1);


            c = row1.createCell(11);
            c.setCellValue(String.valueOf(itemQunatity));
            c.setCellStyle(cs1);

            c = row1.createCell(12);
            c.setCellValue(String.valueOf(itemPrice));
            c.setCellStyle(cs1);

            c = row1.createCell(13);
            c.setCellValue(String.valueOf(purchase.getSubTotal()));
            c.setCellStyle(cs1);

            c = row1.createCell(14);
            c.setCellValue(String.valueOf(purchase.getGst()));
            c.setCellStyle(cs1);

            c = row1.createCell(15);
            c.setCellValue(String.valueOf(purchase.getGrandTotal()));
            c.setCellStyle(cs1);



        }

        c = row1.createCell(16);
        c.setCellValue(String.valueOf(totalGrantTotal));
        c.setCellStyle(cs1);



        sheet1.setColumnWidth(0, (15 * 500));
        sheet1.setColumnWidth(1, (15 * 500));
        sheet1.setColumnWidth(2, (15 * 500));
        sheet1.setColumnWidth(3, (15 * 500));
        sheet1.setColumnWidth(4, (15 * 500));
        sheet1.setColumnWidth(5, (15 * 500));
        sheet1.setColumnWidth(6, (15 * 500));
        sheet1.setColumnWidth(7, (15 * 500));
        sheet1.setColumnWidth(8, (15 * 500));
        sheet1.setColumnWidth(9, (15 * 500));
        sheet1.setColumnWidth(10, (15 * 500));
        sheet1.setColumnWidth(11, (15 * 500));
        sheet1.setColumnWidth(12, (15 * 500));
        sheet1.setColumnWidth(13, (15 * 500));
        sheet1.setColumnWidth(14, (15 * 500));
        sheet1.setColumnWidth(15, (15 * 500));
        sheet1.setColumnWidth(16, (15 * 500));

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
            Toast.makeText(PurchaseActivity.this, date, Toast.LENGTH_SHORT).show();
        }
    };


    public void openFile(File fileName)
    {

        Uri path = Uri.fromFile(fileName);
        Intent pdfOpenintent = new Intent(Intent.ACTION_VIEW);
        pdfOpenintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pdfOpenintent.setDataAndType(path, "application/vnd.ms-excel");
        try {
            startActivity(pdfOpenintent);
        }
        catch (ActivityNotFoundException e) {
            Toast.makeText(PurchaseActivity.this, "No Application Available to View Excel",
                    Toast.LENGTH_SHORT).show();
        }
    }
    public void searchDialog()
    {
        fromDateValue = null;
        toDateValue = null;
        searchDialog = new Dialog(PurchaseActivity.this);

        searchDialog.setContentView(R.layout.purchase_search_dialog);

        searchDialog.setTitle("Filter Purchase");
        fromDateButton = (ImageButton) searchDialog.findViewById(R.id.from_date_button);
        toDateButton= (ImageButton) searchDialog.findViewById(R.id.to_date_button);
        fromDate = (EditText) searchDialog.findViewById(R.id.from_date);
        toDate= (EditText) searchDialog.findViewById(R.id.to_date);
        status = (Spinner) searchDialog.findViewById(R.id.payment_mode_spinner);
        vendor = (Spinner) searchDialog.findViewById(R.id.vendor_spinner);
        range = (TextView) searchDialog.findViewById(R.id.range);
         amountRange= (SimpleRangeView) searchDialog.findViewById(R.id.amount_range);
        purchaseID = (EditText) searchDialog.findViewById(R.id.purchase_id);
        purchaseName = (EditText) searchDialog.findViewById(R.id.purchase_name);
        searchPurchase = (Button) searchDialog.findViewById(R.id.search);


        if(Store.getInstance().purchaseList!=null) {

            new MyBackGroundTask().execute();
        }
        else
        {
            Toast.makeText(this, "No Purchase Done Yet..", Toast.LENGTH_SHORT).show();
        }



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



        searchPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean status =  true;



                new Search(PurchaseActivity.this).execute();
            }
        });
        setVendorList();
        setStatusList();
        searchDialog.show();

    }
    class MyBackGroundTask extends  AsyncTask
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            if(Store.getInstance().purchaseList.size()>0)
            {
                startAmountFixed =  (int) Store.getInstance().purchaseList.get(0).getMin();
                endAmountFixed = (int) Store.getInstance().purchaseList.get(0).getMax();
                startAmount = (int) Store.getInstance().purchaseList.get(0).getMin();
                endAmount = (int) Store.getInstance().purchaseList.get(0).getMax();
                count = (int) Store.getInstance().purchaseList.get(0).getMax();

                System.out.println("min ==== "+(int) Store.getInstance().purchaseList.get(0).getMin());
                System.out.println("max ==== "+(int) Store.getInstance().purchaseList.get(0).getMax());
            }
            else
            {
                System.out.println("Purchase list empty size 0");
            }

        }

        @Override
        protected Object doInBackground(Object[] params) {

            return null;
        }
        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            amountRange.setStartFixed(startAmountFixed);
            amountRange.setEndFixed(endAmountFixed);
            amountRange.setStart(startAmount);
            amountRange.setEnd(endAmount);

            amountRange.setCount(count);
            startParams = startAmount;
            endParams =  endAmount;
            range.setText(String.valueOf(startParams+" - "+endParams));
            System.out.println(startParams+"=========="+endParams);

            amountRange.setOnRangeLabelsListener(new SimpleRangeView.OnRangeLabelsListener() {
                @Nullable
                @Override
                public String getLabelTextForPosition(@NotNull SimpleRangeView rangeView, int pos, @NotNull SimpleRangeView.State state) {
                    if(pos == startParams || pos == endParams-1) {
                        return String.valueOf((pos + 1));
                    }
                    else
                    {
                        return  "";
                    }
                }
            });

            amountRange.setOnTrackRangeListener(new SimpleRangeView.OnTrackRangeListener() {
                @Override
                public void onStartRangeChanged(@NotNull SimpleRangeView rangeView, int start) {

                    startParams = start;
                    range.setText(String.valueOf(startParams+" - "+endParams));
                    System.out.println(startParams+"=========="+endParams);

                }

                @Override
                public void onEndRangeChanged(@NotNull SimpleRangeView rangeView, int end) {
                    endParams = end;
                    range.setText(String.valueOf(startParams+" - "+endParams));
                    System.out.println(startParams+"=========="+endParams);
                }
            });

            amountRange.setOnChangeRangeListener(new SimpleRangeView.OnChangeRangeListener() {
                @Override
                public void onRangeChanged(@NotNull SimpleRangeView rangeView, int start, int end) {
                    startParams = start;
                    endParams = end;
                    range.setText(String.valueOf(startParams+" - "+endParams));
                    System.out.println(startParams+"=========="+endParams);
                }
            });


        }

    }
    public void setStatusList()
    {
        // Spinner Drop down elements
        final List<String> categoryList = new ArrayList<String>();





        categoryList.add("");
        categoryList.add("Pending");
        categoryList.add("Approved");
        categoryList.add("Rejected");
        categoryList.add("Moved");


        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter <String>(PurchaseActivity.this, android.R.layout.simple_spinner_item, categoryList);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        status.setAdapter(dataAdapter);
        status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {



                status_value = categoryList.get(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                try {
                    status_value  = categoryList.get(0);
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
        final List<String> categoryList = new ArrayList<String>();
        final List<String> vendorID = new ArrayList<String>();
        categoryList.add("");
        vendorID.add("");
        Iterator it = vendorMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            System.out.println(pair.getKey() + " = " + pair.getValue());
           categoryList.add((String) pair.getValue());
            vendorID.add((String) pair.getKey());

        }




        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter <String>(PurchaseActivity.this, android.R.layout.simple_spinner_item, categoryList);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        vendor.setAdapter(dataAdapter);
        vendor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {



               vendor_value = categoryList.get(position);
                vendorId = vendorID.get(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                try {
                    vendor_value  = categoryList.get(0);
                    vendorId = vendorID.get(0);
                }
                catch (Exception e)
                {

                }
            }
        });


    }
    class GetAll extends AsyncTask
    {
        Context context;
        String jsonStr;
        String url;
        HashMap<String,String> params;

        public GetAll(Context context) {
            this.context = context;
            this.url = "purchase/getAll";
            this.params = new HashMap<String, String>();
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

           try
           {
               GsonBuilder gsonBuilder = new GsonBuilder();
               gsonBuilder.setDateFormat("M/d/yy hh:mm a");
               Gson gson = gsonBuilder.create();


               Type collectionType = new TypeToken<Collection<Purchase>>() {
               }.getType();

               Collection<Purchase> Purchase = gson.fromJson(jsonStr, collectionType);


               Store.getInstance().purchaseList = (ArrayList<Purchase>) Purchase;

               vendorMap = new HashMap<String, String>();
               for(int i=0;i<Store.getInstance().purchaseList.size();i++)
               {


                    threshhold =  Store.getInstance().purchaseList.get(i).getGrandTotal();

                   vendorMap.put(Store.getInstance().purchaseList.get(i).getVendor().getId(),Store.getInstance().purchaseList.get(i).getVendor().getName());
               }

               //bizUtils.display(context, jsonStr);
               Log.d("JSON",jsonStr);
               Store.getInstance().purchaseListAdapter =new PurchaseAdapter(context);
               listView.setAdapter(Store.getInstance().purchaseListAdapter);
           }
           catch (Exception e)
           {
               Log.d("Exception", String.valueOf(e));
           }


        }


    }
    class Search extends AsyncTask
    {
        Context context;
        String jsonStr;
        String url;
        HashMap<String,String> params;

        public Search(Context context) {
            this.context = context;
            this.url = "purchase/search";
            this.params = new HashMap<String, String>();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            System.out.println("purchase_id = "+purchaseID.getText().toString());

            boolean pID = TextUtils.isEmpty(purchaseID.getText().toString());
            boolean pName = TextUtils.isEmpty(purchaseName.getText().toString());


            if(purchaseID.getText()!=null && !pID ) {
                params.put("purchase_id", purchaseID.getText().toString());
            }
            if(purchaseName.getText()!=null && !pName ) {
                params.put("name", purchaseName.getText().toString());
            }
            if(status_value!=null && status_value.compareTo("")!=0) {
                params.put("status", status_value);
            }
            if(vendorId!=null && vendorId.compareTo("")!=0)
            {
                params.put("vendor_id", vendorId);
            }
                if(fromDateValue!=null) {
                    params.put("from_date", fromDateValue);
                }
                if(toDateValue!=null) {
                    params.put("to_date", toDateValue);
                }
                if(String.valueOf(startParams)!=null) {
                    params.put("order_amount_from", String.valueOf(startParams));
                }
                if(String.valueOf(endParams)!=null) {
                    params.put("order_amount_to", String.valueOf(endParams));
                }
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

            try
            {
                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.setDateFormat("M/d/yy hh:mm a");
                Gson gson = gsonBuilder.create();


                Type collectionType = new TypeToken<Collection<Purchase>>() {
                }.getType();

                Collection<Purchase> Purchase = gson.fromJson(jsonStr, collectionType);


                Store.getInstance().purchaseList = (ArrayList<Purchase>) Purchase;



                //bizUtils.display(context, jsonStr);
                Log.d("JSON",jsonStr);

                searchDialog.cancel();



                Store.getInstance().purchaseListAdapter.notifyDataSetChanged();

            }
            catch (Exception e)
            {
                Log.d("Exception", String.valueOf(e));
            }


        }


    }
}
