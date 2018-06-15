package com.bizsoft.pos;

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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.bizsoft.pos.service.HttpHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CreateCustomerActivity extends AppCompatActivity {

    EditText firstName,lastName,nickName,contactNumber,mail,address;
    Spinner gender;
    Button add;
    private String vgender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_customer);
        getSupportActionBar().setTitle("Create Customer");

        firstName = (EditText) findViewById(R.id.firstName);
        lastName = (EditText) findViewById(R.id.lastName);
        nickName = (EditText) findViewById(R.id.nickName);
        gender = (Spinner) findViewById(R.id.gender);
        contactNumber = (EditText) findViewById(R.id.contact_number);
        mail = (EditText) findViewById(R.id.mail);
        address = (EditText) findViewById(R.id.address);
        add  = (Button) findViewById(R.id.add);

        setGenderDetails();
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validate();

            }
        });
    }
    public void validate()
    {
        boolean status = true;
        if(TextUtils.isEmpty(firstName.getText().toString()))
        {
            firstName.setError("Please Fill");
            status =  false;
        }
        if(TextUtils.isEmpty(lastName.getText().toString()))
        {
            lastName.setError("Please Fill");
            status =  false;
        }
        if(TextUtils.isEmpty(nickName.getText().toString()))
        {
            nickName.setError("Please Fill");
            status =  false;
        }
        if(TextUtils.isEmpty(contactNumber.getText().toString()))
        {
            contactNumber.setError("Please Fill");
            status =  false;
        }
        if(TextUtils.isEmpty(mail.getText().toString()))
        {
            mail.setError("Please Fill");
            status =  false;
        }
        if(TextUtils.isEmpty(address.getText().toString()))
        {
            address.setError("Please Fill");
            status =  false;
        }
        if(status)
        {
            new CreateCustomer(CreateCustomerActivity.this).execute();
        }
    }
    public void setGenderDetails()
    {
        final List<String> genderList = new ArrayList<String>();
        genderList.add("male");
        genderList.add("female");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter <String>(CreateCustomerActivity.this, android.R.layout.simple_spinner_item, genderList);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(dataAdapter);
        gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(),genderList.get(position),Toast.LENGTH_SHORT).show();
                vgender = genderList.get(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                vgender = genderList.get(0);


            }


        });



    }
    class CreateCustomer extends AsyncTask
    {


        Context context;
        String jsonStr;
        HashMap<String,String> params;
        String url;
        public CreateCustomer(Context context) {
            this.context = context;
            this.url = "user/createCustomer";
            this.params = new HashMap<String, String>();
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            this.params.put("firstName",firstName.getText().toString());
            this.params.put("lastName",lastName.getText().toString());
            this.params.put("nickName",nickName.getText().toString());
            this.params.put("contactNumber",contactNumber.getText().toString());
            this.params.put("email",mail.getText().toString());
            this.params.put("address",address.getText().toString());
            this.params.put("gender",vgender);



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

            Log.d("Response : " ,jsonStr);


            if(jsonStr.contains("true"))
            {
                Intent intent = new Intent(CreateCustomerActivity.this,CustomerActivity.class);
                startActivity(intent);

                Toast.makeText(CreateCustomerActivity.this,"Customer Created",Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(CreateCustomerActivity.this,"Customer not created",Toast.LENGTH_SHORT).show();
            }
        }


    }
}
