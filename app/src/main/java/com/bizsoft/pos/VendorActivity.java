package com.bizsoft.pos;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bizsoft.pos.service.HttpHandler;

import java.util.HashMap;

public class VendorActivity extends AppCompatActivity {

    EditText vendorName,vendorContactNumber,vendorMail,vendorAddress,vendorTinNumber;
    Button create;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor);

        vendorName = (EditText) findViewById(R.id.vendor_name);
        vendorContactNumber= (EditText) findViewById(R.id.contact_number);
        vendorMail = (EditText) findViewById(R.id.mail_address);
        vendorAddress = (EditText) findViewById(R.id.address);
        vendorTinNumber = (EditText) findViewById(R.id.tin_number);

        create = (Button) findViewById(R.id.create);
        getSupportActionBar().setTitle("Create VendorThiruvengadam ");

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validate();
            }
        });

    }
    public void validate()
    {
        boolean status = true;
        String vn = vendorName.getText().toString();
        String vc = vendorContactNumber.getText().toString();
        String vm = vendorMail.getText().toString();
        String vt = vendorTinNumber.getText().toString();
        String va = vendorAddress.getText().toString();

        if(vn.isEmpty())
        {
            vendorName.setError("Field cannot be empty");
            status = false;
        }
        if(vc.isEmpty())
        {
            vendorContactNumber.setError("Field cannot be empty");
            status = false;
        }
        if(vm.isEmpty())
        {
            vendorMail.setError("Field cannot be empty");
            status = false;
        }
        if(vt.isEmpty())
        {
            vendorTinNumber.setError("Field cannot be empty");
            status = false;
        }
        if(va.isEmpty())
        {
            vendorAddress.setError("Field cannot be empty");
            status = false;
        }

        if(status)
        {
            new CreateVendor(VendorActivity.this).execute();
        }
        else
        {
            Toast.makeText(this, "Validation error", Toast.LENGTH_SHORT).show();
        }




    }
    class CreateVendor extends AsyncTask
    {


        Context context;
        String url;
        HashMap<String,String> params;
        String jsonStr;
        public CreateVendor(Context context) {
            this.context = context;
            this.params = new HashMap<String, String>();
            this.url = "vendor/createVendor";
            this.jsonStr = null;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.params.put("name",vendorName.getText().toString());
            this.params.put("address",vendorAddress.getText().toString());
            this.params.put("email",vendorMail.getText().toString());
            this.params.put("contactNumber",vendorContactNumber.getText().toString());
            this.params.put("tinNumber",vendorTinNumber.getText().toString());
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

            try
            {
            if(jsonStr.contains("true"))
            {
                Toast.makeText(context, "Vendor added "+jsonStr, Toast.LENGTH_SHORT).show();
                finish();
            }
            }
            catch (Exception e)
            {
                Toast.makeText(context, "Vendor not added "+jsonStr, Toast.LENGTH_SHORT).show();
            }

        }


    }
}
