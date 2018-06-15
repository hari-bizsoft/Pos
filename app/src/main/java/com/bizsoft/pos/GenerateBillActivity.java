package com.bizsoft.pos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bizsoft.pos.BTLib.BTDeviceList;
import com.bizsoft.pos.BTLib.BTPrint;
import com.bizsoft.pos.adapter.BillingFullViewAdapter;
import com.bizsoft.pos.adapter.BillingItemsAdapter;
import com.bizsoft.pos.dataobject.Bill;
import com.bizsoft.pos.dataobject.BizViewStore;
import com.bizsoft.pos.dataobject.Items;
import com.bizsoft.pos.dataobject.Store;
import com.bizsoft.pos.service.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

public class GenerateBillActivity extends AppCompatActivity {


    TextView billId,billDate,paymentMode;
    TextView customerID,customerName,customerMobile,customerAddress,customerMail,customerScore;
    TextView subTotal,gst,grandTotal;
    TextView shippingAddress;
    ListView listView;
    String from;
    Bill bill;
    Button print;
    Context context;
    SharedPreferences sharedPref;
    final static  int BLUETOOTH_FLAG = 619;
    TextView gst_Text;
    TextView billIDText,billPaymentModeText;
    TextView vendorIdText,vendorNameText,vendorTinNumberText,vendorAddressText,vendorMailText,customerDetailsText;
    TextView subTotalText,gstText,grandTotalText;
    TextView shippingAddressText;
    TextView discountText,discount;
    private String discountType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_bill);




        billIDText = (TextView) findViewById(R.id.purchase_id_text);
        billPaymentModeText = (TextView) findViewById(R.id.textView47);
        discountText = (TextView) findViewById(R.id.discount_text);
        discount= (TextView) findViewById(R.id.discount_value);


        customerDetailsText = (TextView) findViewById(R.id.vendor_details_text);
        vendorIdText =  (TextView) findViewById(R.id.vendor_id_text);
        vendorNameText = (TextView) findViewById(R.id.vendor_name_text);
        vendorTinNumberText = (TextView) findViewById(R.id.tim_number_text);
        vendorAddressText = (TextView) findViewById(R.id.textView36);
        vendorMailText = (TextView) findViewById(R.id.textView20);

        subTotalText = (TextView) findViewById(R.id.subtotal_text);
        gstText = (TextView) findViewById(R.id.gst_text);
        grandTotalText = (TextView) findViewById(R.id.grand_total_text);

        shippingAddressText = (TextView) findViewById(R.id.textView26);




        billId = (TextView) findViewById(R.id.bill_id);
        billDate = (TextView) findViewById(R.id.bill_date);
        customerID  = (TextView) findViewById(R.id.customer_id);
        customerName= (TextView) findViewById(R.id.customer_name);
        customerMobile = (TextView) findViewById(R.id.mobile_number);
        customerMail = (TextView) findViewById(R.id.customer_mail);
        customerScore  = (TextView) findViewById(R.id.credit_score);
        customerAddress = (TextView) findViewById(R.id.customer_address);
        subTotal = (TextView) findViewById(R.id.sub_total);
        gst= (TextView) findViewById(R.id.gst);
        grandTotal  = (TextView) findViewById(R.id.total);
        shippingAddress = (TextView) findViewById(R.id.shipping_address);
        listView = (ListView) findViewById(R.id.listview);
        print = (Button) findViewById(R.id.print);
        paymentMode = (TextView) findViewById(R.id.payment_mode);
        gst_Text = (TextView) findViewById(R.id.gst_text);
        getSupportActionBar().setTitle("Bill Preview");


        if(Store.getInstance().screenSize.equals(Store.getInstance().LARGE_SCREEN)) {



            billIDText.setTextSize(Store.getInstance().textSizeLarge);
            billPaymentModeText.setTextSize(Store.getInstance().textSizeLarge);


            customerDetailsText.setTextSize(Store.getInstance().textSizeLarge);
            vendorIdText.setTextSize(Store.getInstance().textSizeLarge);
            vendorNameText.setTextSize(Store.getInstance().textSizeLarge);
            vendorTinNumberText.setTextSize(Store.getInstance().textSizeLarge);
            vendorAddressText.setTextSize(Store.getInstance().textSizeLarge);
            vendorMailText.setTextSize(Store.getInstance().textSizeLarge);

            subTotalText.setTextSize(Store.getInstance().textSizeLarge);
            gstText.setTextSize(Store.getInstance().textSizeLarge);
            grandTotalText.setTextSize(Store.getInstance().textSizeLarge);

            shippingAddressText.setTextSize(Store.getInstance().textSizeLarge);




            billId.setTextSize(Store.getInstance().textSizeLarge);
            billDate.setTextSize(Store.getInstance().textSizeLarge);
            customerID.setTextSize(Store.getInstance().textSizeLarge);
            customerName.setTextSize(Store.getInstance().textSizeLarge);
            customerMobile.setTextSize(Store.getInstance().textSizeLarge);
            customerMail.setTextSize(Store.getInstance().textSizeLarge);
            customerScore.setTextSize(Store.getInstance().textSizeLarge);
            customerAddress.setTextSize(Store.getInstance().textSizeLarge);
            subTotal.setTextSize(Store.getInstance().textSizeLarge);
            gst.setTextSize(Store.getInstance().textSizeLarge);
            grandTotal.setTextSize(Store.getInstance().textSizeLarge);
            shippingAddress.setTextSize(Store.getInstance().textSizeLarge);

            paymentMode.setTextSize(Store.getInstance().textSizeLarge);
            gst_Text.setTextSize(Store.getInstance().textSizeLarge);
            discount.setTextSize(Store.getInstance().textSizeLarge);
            discountText.setTextSize(Store.getInstance().textSizeLarge);

        }


        context = GenerateBillActivity.this;
        sharedPref = context.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        final Intent intent = getIntent();
        if(intent.getStringExtra("from")!=null)
        {
            from = intent.getStringExtra("from");
            if(from.equals("incart"))
            {
                bill = Store.getInstance().billList.get(Store.getInstance().currentBillPosition);
                Toast.makeText(this, "current bill : "+bill.getId(), Toast.LENGTH_SHORT).show();

            }
            else
            {
                bill = Store.getInstance().bill;
            }

        }
        else
        {
            bill = Store.getInstance().bill;

        }
        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               try
               {
                   Toast.makeText(GenerateBillActivity.this, "test print", Toast.LENGTH_SHORT).show();
                   if(BTPrint.btsocket==null)
                   {
                       Intent intent = new Intent(GenerateBillActivity.this,BTDeviceList.class);

                       startActivityForResult(intent,BLUETOOTH_FLAG);

                       Toast.makeText(GenerateBillActivity.this, "new socket", Toast.LENGTH_SHORT).show();


                   }
                   else {
                       Toast.makeText(GenerateBillActivity.this, "old socket", Toast.LENGTH_SHORT).show();
                       String company_name = sharedPref.getString("company_name", "POS");
                       String company_tin = sharedPref.getString("company_tin_number","1234567");
                       String company_contact_number = sharedPref.getString("company_contact_number","65012345");
                       String company_mail_address = sharedPref.getString("company_mail_address","a@a.com");
                       String company_address = sharedPref.getString("company_address","123, Rio street,Kulalampur,Malaysia");

                       BTPrint.SetAlign(Paint.Align.CENTER);
                       BTPrint.PrintTextLine("-----POS-----");
                       BTPrint.SetAlign(Paint.Align.CENTER);
                       BTPrint.PrintTextLine("Company name: "+company_name);
                       BTPrint.PrintTextLine("Ph: "+company_contact_number);
                       BTPrint.PrintTextLine("------------------------------");
                       BTPrint.PrintTextLine("Bill ID: "+bill.getId());
                       BTPrint.PrintTextLine("Date : "+bill.getDateCreated());
                       BTPrint.PrintTextLine("------------------------------");
                       BTPrint.PrintTextLine("Customer ID: "+Store.getInstance().currentCustomer.getId());
                       BTPrint.PrintTextLine("Customer Name :"+Store.getInstance().currentCustomer.getFirstName()+ " "+Store.getInstance().currentCustomer.getLastName());
                       BTPrint.PrintTextLine("------------------------------");
                       BTPrint.SetAlign(Paint.Align.CENTER);
                       BTPrint.PrintTextLine("***ITEM DETAILS***");
                       BTPrint.PrintTextLine("------------------------------");
                       BTPrint.SetAlign(Paint.Align.LEFT);
                       BTPrint.PrintTextLine("NAME     QTY   RATE     AMOUNT ");
                       String gstSpace = "";
                       for(int i=0;i<bill.getItems().size();i++) {
                           Items item = bill.getItems().get(i);
                           String in;
                           String iq = "";
                           String ip = "";
                           String ir = "";

                           String itemnameSpace = "";
                           String itemquantitySpace = "";
                           String itempriceSpace = "";
                           String itemrateSpace = "";
                           int spaceLength = 0;
                           int itemQspaceL = 0;
                           int itemPspaceL = 0;
                           int itemRspaceL = 0;

                           if(item.getName().length()>=10)
                           {
                               in = item.getName().substring(0,9);
                               itemnameSpace = " ";

                           }
                           else
                           {
                               int total = item.getName().length();
                               spaceLength = 10 - total;

                               for(int x=0;x<spaceLength;x++)
                               {
                                   itemnameSpace = itemnameSpace + " ";
                               }



                               in = item.getName();

                           }

                           if(String.valueOf(item.getQuantity()).length()>=6)
                           {
                               iq = String.valueOf(item.getQuantity()).substring(0,4);
                               iq = iq + "..";
                           }
                           else
                           {
                               int total = String.valueOf(item.getQuantity()).length();
                               itemQspaceL = 6 - total;

                               for(int x=0;x<itemQspaceL;x++)
                               {
                                   itemquantitySpace = itemquantitySpace+ " ";
                               }

                               iq = String.valueOf(item.getQuantity());
                           }
                        String itemP = String.valueOf(item.getRetailPrice());


                           if(itemP.length()>=8)
                           {
                               in = itemP.substring(0,7);
                               itemrateSpace = " ";

                           }
                           else
                           {
                               int total =itemP.length();
                               itemPspaceL = 8 - total;

                               for(int x=0;x<itemPspaceL;x++)
                               {
                                   itempriceSpace = itempriceSpace + " ";
                               }



                               ip = itemP;

                           }

                           String subTotal = String.valueOf(bill.getSubTotal());
                           int subTotalLength = subTotal.length();

                           String gst = String.valueOf(bill.getGst());
                           int gstLength = gst.length();


                            gstSpace = "";

                           int c = subTotalLength - gstLength;

                           for(int f=0;f<c;f++)
                           {
                               gstSpace = gstSpace + " ";
                           }




                           BTPrint.PrintTextLine(in+itemnameSpace+iq+itemquantitySpace+ip+itempriceSpace+item.getTotalCost());
                       }
                       BTPrint.PrintTextLine("------------------------------");
                       BTPrint.SetAlign(Paint.Align.RIGHT);
                       BTPrint.PrintTextLine("Sub total = "+bill.getSubTotal()+" RM");
                       BTPrint.PrintTextLine("GST = "+gstSpace+bill.getGst()+" RM");
                       BTPrint.PrintTextLine("------------------------------");
                       BTPrint.PrintTextLine("Discount("+Store.getInstance().bill.getDiscountRate()+" "+discountType+") = -"+gstSpace+Store.getInstance().bill.getDiscount()+" RM");
                       BTPrint.PrintTextLine("------------------------------");
                       BTPrint.PrintTextLine("------------------------------");
                       BTPrint.PrintTextLine("Grand total = "+bill.getGrandTotal()+" RM");
                       BTPrint.PrintTextLine("------------------------------");
                       BTPrint.SetAlign(Paint.Align.CENTER);
                       BTPrint.PrintTextLine("*****THANK YOU*****");
                       BTPrint.printLineFeed();

                       finish();
                       Intent intent = new Intent(context, BillingActivity.class);
                       intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                       intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                       Store.getInstance().addedItemList.clear();
                       BizViewStore.getInstance().addedItemAdapter.notifyDataSetChanged();
                       startActivity(intent);





                   }

               }
               catch (Exception e)
               {

               }



            }
        });






        billId.setText(String.valueOf(bill.getId()));

        billDate.setText(String.valueOf(bill.getDateCreated()));
        paymentMode.setText(String.valueOf(bill.getPaymentMode()));

        customerID.setText(String.valueOf(Store.getInstance().currentCustomer.getId()));
        customerName.setText(String.valueOf(Store.getInstance().currentCustomer.getFirstName()));
        customerMobile.setText(String.valueOf(Store.getInstance().currentCustomer.getContactNumber()));
        customerMail.setText(String.valueOf(Store.getInstance().currentCustomer.getMail()));
        customerAddress.setText(String.valueOf(Store.getInstance().currentCustomer.getAddress()));
        shippingAddress.setText(String.valueOf(Store.getInstance().currentCustomer.getAddress()));

        subTotal.setText(String.valueOf(bill.getSubTotal()));
        gst.setText(String.valueOf(bill.getGst()));
        grandTotal.setText(String.valueOf(bill.getGrandTotal()));

        System.out.println("Bill  = "+Store.getInstance().bill.getDiscount());
        System.out.println("Bill  = "+bill.getDiscount());
        discount.setText("-"+String.valueOf(Store.getInstance().bill.getDiscount()));

        if(Store.getInstance().bill.getDiscountType().contains("Percentage")) {
            discountType = "%";

        }
        else
        {
            discountType = "RM";

        }
        discountText.setText("Discount(" + String.valueOf(Store.getInstance().bill.getDiscountRate()) + String.valueOf(discountType) + ")");


        listView.setAdapter(new BillingFullViewAdapter(GenerateBillActivity.this,bill.getItems()));




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("Result code === "+resultCode);
        System.out.println("requestCode === "+requestCode);
        System.out.println("data === "+data);



        try {


            BTPrint.btsocket = BTDeviceList.getSocket();
            if (BTPrint.btsocket != null) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                OutputStream opstream = null;
                try {
                    opstream = BTPrint.btsocket.getOutputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                BTPrint.btoutputstream = opstream;
            }

        }
        catch (Exception e)
        {

        }

    }
}
