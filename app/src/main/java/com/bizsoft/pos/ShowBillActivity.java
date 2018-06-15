package com.bizsoft.pos;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
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

import com.bizsoft.pos.adapter.ShowBillAdapter;
import com.bizsoft.pos.dataobject.Bill;
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
import java.util.List;

import me.bendik.simplerangeview.SimpleRangeView;

import static com.bizsoft.pos.service.BizUtils.isExternalStorageAvailable;
import static com.bizsoft.pos.service.BizUtils.isExternalStorageReadOnly;

public class ShowBillActivity extends AppCompatActivity {

    private static File filePath;
    ListView listView;
    FloatingActionButton search, save;
    private Dialog searchDialog;
    private String fromDateValue;
    private String toDateValue;
    private ImageButton fromDateButton;
    private ImageButton toDateButton;
    private EditText fromDate;
    private EditText toDate;
    private Spinner paymentMode;
    private EditText salesID;
    private EditText customerContactNumber;
    private Button searchSales;
    private int startParams;
    private int endParams;
    private String FLAG_DATE;
    private TextView range;
    private int startAmountFixed, endAmountFixed, startAmount, endAmount, count;
    private SimpleRangeView amountRange;
    private int year, month, day;
    private String paymentModeValue;
    private EditText customerID;
    BizUtils bizUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_bill);
        listView = (ListView) findViewById(R.id.listview);
        search = (FloatingActionButton) findViewById(R.id.search);

        save = (FloatingActionButton) findViewById(R.id.generate_report);

        getSupportActionBar().setTitle("Bill list");

        new GetBillDetails(ShowBillActivity.this).execute();

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchDialog();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GenerateReport(ShowBillActivity.this).execute();
            }
        });
        bizUtils = new BizUtils();
    }

    public void searchDialog() {
        searchDialog = new Dialog(ShowBillActivity.this);

        searchDialog.setContentView(R.layout.sales_search_dialog);

        fromDateValue = null;
        toDateValue = null;
        searchDialog.setTitle("Filter Purchase");
        fromDateButton = (ImageButton) searchDialog.findViewById(R.id.from_date_button);
        toDateButton = (ImageButton) searchDialog.findViewById(R.id.to_date_button);
        fromDate = (EditText) searchDialog.findViewById(R.id.from_date);
        toDate = (EditText) searchDialog.findViewById(R.id.to_date);
        paymentMode = (Spinner) searchDialog.findViewById(R.id.payment_mode_spinner);

        range = (TextView) searchDialog.findViewById(R.id.range);
        amountRange = (SimpleRangeView) searchDialog.findViewById(R.id.amount_range);
        salesID = (EditText) searchDialog.findViewById(R.id.sales_id);
        customerContactNumber = (EditText) searchDialog.findViewById(R.id.customer_contact_number);
        searchSales = (Button) searchDialog.findViewById(R.id.search);
        customerID =(EditText) searchDialog.findViewById(R.id.customer_id);



        new MyBackgroundTask().execute();

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


        searchSales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                new SearchBill(ShowBillActivity.this).execute();


            }
        });

        searchDialog.show();

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
            String date = arg3 + "/" + (arg2 + 1) + "/" + arg1;
            if (FLAG_DATE.compareToIgnoreCase("fromdate") == 0) {
                fromDate.setText(String.valueOf(date));

                fromDateValue = String.valueOf(date);
            } else {
                toDate.setText(String.valueOf(date));
                toDateValue = String.valueOf(date);
            }
            Toast.makeText(ShowBillActivity.this, date, Toast.LENGTH_SHORT).show();
        }
    };

    class MyBackgroundTask extends AsyncTask {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object[] params) {

            if (Store.getInstance().billList.size() > 0) {
                startAmountFixed = (int) Store.getInstance().billList.get(0).getMin();
                endAmountFixed = (int) Store.getInstance().billList.get(0).getMax();
                startAmount = (int) Store.getInstance().billList.get(0).getMin();
                endAmount = (int) Store.getInstance().billList.get(0).getMax();
                count = (int) Store.getInstance().billList.get(0).getMax();

                System.out.println("min ==== " + (int) Store.getInstance().billList.get(0).getMin());
                System.out.println("max ==== " + (int) Store.getInstance().billList.get(0).getMax());
            } else {
                System.out.println("billList  empty size 0");
            }


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
            endParams = endAmount;
            range.setText(String.valueOf(startParams + " - " + endParams));
            System.out.println(startParams + "==========" + endParams);
            amountRange.setOnRangeLabelsListener(new SimpleRangeView.OnRangeLabelsListener() {
                @Nullable
                @Override
                public String getLabelTextForPosition(@NotNull SimpleRangeView rangeView, int pos, @NotNull SimpleRangeView.State state) {
                    if (pos == startParams || pos == endParams - 1) {
                        return String.valueOf((pos + 1));
                    } else {
                        return "";
                    }
                }
            });

            amountRange.setOnTrackRangeListener(new SimpleRangeView.OnTrackRangeListener() {
                @Override
                public void onStartRangeChanged(@NotNull SimpleRangeView rangeView, int start) {

                    startParams = start;
                    range.setText(String.valueOf(startParams + " - " + endParams));
                    System.out.println(startParams + "==========" + endParams);

                }

                @Override
                public void onEndRangeChanged(@NotNull SimpleRangeView rangeView, int end) {
                    endParams = end;
                    range.setText(String.valueOf(startParams + " - " + endParams));
                    System.out.println(startParams + "==========" + endParams);
                }
            });

            amountRange.setOnChangeRangeListener(new SimpleRangeView.OnChangeRangeListener() {
                @Override
                public void onRangeChanged(@NotNull SimpleRangeView rangeView, int start, int end) {
                    startParams = start;
                    endParams = end;
                    range.setText(String.valueOf(startParams + " - " + endParams));
                    System.out.println(startParams + "==========" + endParams);
                }
            });
            setPaymentModeList();
        }


    }

    class GetBillDetails extends AsyncTask {

        Context context;
        String url;
        HashMap<String, String> params;
        String jsonStr = null;

        public GetBillDetails(Context context) {
            this.context = context;
            params = new HashMap<String, String>();
            this.url = "bill/getAll";

        }

        @Override

        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected Object doInBackground(Object[] params) {
            HttpHandler httpHandler = new HttpHandler();
            jsonStr = httpHandler.makeServiceCall(this.url, this.params);
            return true;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            System.out.println("Response : " + jsonStr);

            try {
                System.out.println("json : " + jsonStr);
                Log.e("JSON :", jsonStr);


                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.setDateFormat("M/d/yy hh:mm a");
                Gson gson = gsonBuilder.create();


                Type collectionType = new TypeToken<Collection<Bill>>() {
                }.getType();

                Collection<Bill> billList = gson.fromJson(jsonStr, collectionType);

                Store.getInstance().billList = (ArrayList<Bill>) billList;

                Store.getInstance().billListAdapter = new ShowBillAdapter(context);
                listView.setAdapter(Store.getInstance().billListAdapter);


            } catch (Exception e) {

            }
        }


    }

    class SearchBill extends AsyncTask {

        Context context;
        String url;
        HashMap<String, String> params;
        String jsonStr = null;

        public SearchBill(Context context) {
            this.context = context;
            params = new HashMap<String, String>();
            this.url = "bill/getAll";



        }

        @Override

        protected void onPreExecute() {
            super.onPreExecute();

            boolean sID = TextUtils.isEmpty(salesID.getText().toString());

            boolean customerContact = TextUtils.isEmpty(customerContactNumber.getText().toString());

            boolean fromDate = TextUtils.isEmpty(fromDateValue);

            boolean toDate = TextUtils.isEmpty(toDateValue);

            boolean paymentMode = TextUtils.isEmpty(paymentModeValue);

            boolean customerI = TextUtils.isEmpty(customerID.getText().toString());

            if(!sID && salesID!=null)
            {
                params.put("bill_id",salesID.getText().toString());
            }
            if(!customerContact && customerContactNumber!=null)
            {
                params.put("customer_contact",customerContactNumber.getText().toString());
            }
            if(!fromDate && fromDateValue!=null)
            {
                params.put("from_date", fromDateValue);
            }
            if(!toDate && toDateValue!=null)
            {
                params.put("to_date", toDateValue);
            }
            if(String.valueOf(startParams)!=null) {
                params.put("bill_amount_from", String.valueOf(startParams));
            }
            if(String.valueOf(endParams)!=null) {
                params.put("bill_amount_to", String.valueOf(endParams));
            }

            if(!paymentMode && String.valueOf(paymentModeValue)!=null) {
                params.put("payment_mode", String.valueOf(paymentModeValue));
            }
            if(!customerI && String.valueOf(customerID.getText().toString())!=null) {
                params.put("customer_id", String.valueOf(customerID.getText().toString()));
            }
        }

        @Override
        protected Object doInBackground(Object[] params) {
            HttpHandler httpHandler = new HttpHandler();
            jsonStr = httpHandler.makeServiceCall(this.url, this.params);
            return true;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            System.out.println("Response : " + jsonStr);

            try {
                System.out.println("json : " + jsonStr);
                Log.e("JSON 2 ---- :", jsonStr);


                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.setDateFormat("M/d/yy hh:mm a");
                Gson gson = gsonBuilder.create();


                Type collectionType = new TypeToken<Collection<Bill>>() {
                }.getType();

                Collection<Bill> billList = gson.fromJson(jsonStr, collectionType);






                Store.getInstance().billList = (ArrayList<Bill>) billList;
                searchDialog.dismiss();


                System.out.println("Size ===="+Store.getInstance().billList.size());


                Store.getInstance().billListAdapter.billList = Store.getInstance().billList;
                Store.getInstance().billListAdapter.notifyDataSetChanged();
                System.out.println("Size ===="+Store.getInstance().billListAdapter.getCount());


            } catch (Exception e) {

            }
        }


    }

    public void setPaymentModeList() {

        final List<String> categoryList = new ArrayList<String>();

        categoryList.add("");
        categoryList.add("Cash");
        categoryList.add("Card");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ShowBillActivity.this, android.R.layout.simple_spinner_item, categoryList);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        paymentMode.setAdapter(dataAdapter);
        paymentMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                paymentModeValue = categoryList.get(position);


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                try {
                    paymentModeValue = categoryList.get(0);

                } catch (Exception e) {

                }
            }
        });


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
            dialog = new Dialog(ShowBillActivity.this);
            dialog.setContentView(R.layout.spinner);

            dialog.show();

        }
        @Override
        protected Object doInBackground(Object[] params) {
            String fileName = "sales_report_"+bizUtils.getCurrentDateTime()+".xls";
            status =  saveExcelFile(ShowBillActivity.this,fileName);
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

                                    bizUtils.openFile(ShowBillActivity.this, filePath, "application/vnd.ms-excel");
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


                Toast.makeText(ShowBillActivity.this, "File saved", Toast.LENGTH_SHORT).show();

            }
            else {
                Toast.makeText(ShowBillActivity.this, "File not saved", Toast.LENGTH_SHORT).show();
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
        c.setCellValue("Bill ID");
        c.setCellStyle(cs);

        c = row.createCell(1);
        c.setCellValue("Bill Date");
        c.setCellStyle(cs);

        c = row.createCell(2);
        c.setCellValue("Payment Mode");
        c.setCellStyle(cs);


        c = row.createCell(3);
        c.setCellValue("Customer ID");
        c.setCellStyle(cs);

        c = row.createCell(4);
        c.setCellValue("Customer Name");
        c.setCellStyle(cs);

        c = row.createCell(5);
        c.setCellValue("Customer Mobile");
        c.setCellStyle(cs);

        c = row.createCell(6);
        c.setCellValue("Customer Address");
        c.setCellStyle(cs);

        c = row.createCell(7);
        c.setCellValue("Customer Mail");
        c.setCellStyle(cs);


        c = row.createCell(8);
        c.setCellValue("Item Names");
        c.setCellStyle(cs);

        c = row.createCell(9);
        c.setCellValue("Item Quantity");
        c.setCellStyle(cs);

        c = row.createCell(10);
        c.setCellValue("Item Price");
        c.setCellStyle(cs);


        c = row.createCell(11);
        c.setCellValue("Sub Total");
        c.setCellStyle(cs);

        c = row.createCell(12);
        c.setCellValue("GST");
        c.setCellStyle(cs);

        c = row.createCell(13);
        c.setCellValue("Grand Total");
        c.setCellStyle(cs);







        for(int i=0;i<Store.getInstance().billList.size();i++)
        {

            Row row1 = sheet1.createRow(i+1);
            Bill bill= Store.getInstance().billList.get(i);

            c = row1.createCell(0);
            c.setCellValue(String.valueOf(bill.getId()));
            c.setCellStyle(cs1);

            c = row1.createCell(1);
            c.setCellValue(String.valueOf(bill.getDateCreated()));
            c.setCellStyle(cs1);

            c = row1.createCell(2);
            c.setCellValue(String.valueOf(bill.getPaymentMode()));
            c.setCellStyle(cs1);


            c = row1.createCell(3);
            c.setCellValue(String.valueOf(bill.getCustomer().getId()));
            c.setCellStyle(cs1);

            c = row1.createCell(4);
            c.setCellValue(String.valueOf(bill.getCustomer().getFirstName() + bill.getCustomer().getLastName()));
            c.setCellStyle(cs1);

            c = row1.createCell(5);
            c.setCellValue(String.valueOf(bill.getCustomer().getContactNumber()));
            c.setCellStyle(cs1);

            c = row1.createCell(6);
            c.setCellValue(String.valueOf(bill.getCustomer().getAddress()));
            c.setCellStyle(cs1);

            c = row1.createCell(7);
            c.setCellValue(String.valueOf(bill.getCustomer().getMail()));
            c.setCellStyle(cs1);


            c = row1.createCell(8);
            String itemName = "";
            String itemQunatity = "";
            String itemPrice = "";
            for(int j=0;j<bill.getItems().size();j++)
            {

                itemName = itemName + bill.getItems().get(j).getName()+ " - ";
                itemQunatity = itemQunatity + bill.getItems().get(j).getQuantity()+ " - ";
                itemPrice = itemPrice + bill.getItems().get(j).getRetailPrice()+ " - ";
            }

            c.setCellValue(String.valueOf(itemName));
            c.setCellStyle(cs1);


            c = row1.createCell(9);
            c.setCellValue(String.valueOf(itemQunatity));
            c.setCellStyle(cs1);

            c = row1.createCell(10);
            c.setCellValue(String.valueOf(itemPrice));
            c.setCellStyle(cs1);

            c = row1.createCell(11);
            c.setCellValue(String.valueOf(bill.getSubTotal()));
            c.setCellStyle(cs1);

            c = row1.createCell(12);
            c.setCellValue(String.valueOf(bill.getGst()));
            c.setCellStyle(cs1);

            c = row1.createCell(13);
            c.setCellValue(String.valueOf(bill.getGrandTotal()));
            c.setCellStyle(cs1);



        }

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
}
