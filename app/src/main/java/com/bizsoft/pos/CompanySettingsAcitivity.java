package com.bizsoft.pos;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;

public class CompanySettingsAcitivity extends AppCompatActivity {

    EditText companyName,companyAddress,companyMail,companyTin,companyContact;
    EditText gstE;
    Context context;
    SharedPreferences sharedPref;
    Button save;
    HashMap<String,String> uim = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_settings_acitivity);

        companyName = (EditText) findViewById(R.id.company_name);
        companyAddress= (EditText) findViewById(R.id.address);
        companyMail = (EditText) findViewById(R.id.mail_address);
        companyTin  = (EditText) findViewById(R.id.tin_number);
        companyContact = (EditText) findViewById(R.id.contact_number);
        save = (Button) findViewById(R.id.save);
        getSupportActionBar().setTitle("Company Settings");


        gstE = (EditText) findViewById(R.id.gst);

         context = CompanySettingsAcitivity.this;
         sharedPref = context.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);



        String company_name = sharedPref.getString("company_name", "POS");
        String company_tin = sharedPref.getString("company_tin_number","1234567");
        String company_contact_number = sharedPref.getString("company_contact_number","65012345");
        String company_mail_address = sharedPref.getString("company_mail_address","a@a.com");
        String company_address = sharedPref.getString("company_address","123, Rio street,Kulalampur,Malaysia");

        String gst = sharedPref.getString("gst","6.0");


        System.out.println("company name : "+company_name);

        companyName.setText(company_name);
        companyAddress.setText(company_address);
        companyContact.setText(company_contact_number);
        companyMail.setText(company_mail_address);
        companyTin.setText(company_tin);

        this.gstE.setText(gst);


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
            }
        });

    }

    public void update()
    {
        String cn =  companyName.getText().toString();
        String ca = companyAddress.getText().toString();
        String cm = companyMail.getText().toString();
        String ct = companyTin.getText().toString();
        String cp = companyContact.getText().toString();

        String gst = gstE.getText().toString();

        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString("company_name",cn);
        editor.putString("company_tin_number",ct);
        editor.putString("company_contact_number",cp);
        editor.putString("company_mail_address",cm);
        editor.putString("company_address",ca);

        editor.putString("gst",gst);

        boolean status = true;

        if(cn.isEmpty() )
        {
            status = false;

            companyName.setError("Field cannot be empty");

        }
        if( cm.isEmpty() )
        {
            status = false;
            companyMail.setError("Field cannot be empty");


        }
        if(  ca.isEmpty() )
        {
            status = false;
            companyAddress.setError("Field cannot be empty");


        }
        if( cp.isEmpty()  )
        {
            status = false;
            companyContact.setError("Field cannot be empty");


        }if( ct.isEmpty() )
    {
        status = false;
        companyTin.setError("Field cannot be empty");


    }

        if( gst.isEmpty() )
        {
            status = false;
            gstE.setError("Field cannot be empty");


        }
        if(gst!=null )
        {
            try {
                float gst1 = Float.parseFloat(gst);
            }
            catch (NumberFormatException e)
            {
                status = false;
            }
            if(!status)
            {
                gstE.setError("Not a valid gst value");
            }

        }


        if(status) {
            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();

            editor.commit();
            finish();
        }
        else
        {
            Toast.makeText(context, "Not saved", Toast.LENGTH_SHORT).show();
        }




    }
}
