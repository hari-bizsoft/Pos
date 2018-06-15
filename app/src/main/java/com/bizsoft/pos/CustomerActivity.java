package com.bizsoft.pos;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.bizsoft.pos.adapter.CustomerAdapter;
import com.bizsoft.pos.dataobject.Store;
import com.bizsoft.pos.dataobject.User;
import com.bizsoft.pos.service.HttpHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

public class CustomerActivity extends AppCompatActivity {

    ListView listView;
    FloatingActionButton add;
    private Dialog createCustomerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);
        getSupportActionBar().setTitle("Customer List");
        listView = (ListView) findViewById(R.id.listview);
        add = (FloatingActionButton) findViewById(R.id.add);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createCustomer();
            }
        });
        new GetCustomerDetails(CustomerActivity.this,listView).execute();
    }
    public void createCustomer()
    {

        Intent intent = new Intent(CustomerActivity.this,CreateCustomerActivity.class);
        startActivity(intent);
    }

}
class GetCustomerDetails extends AsyncTask
{
    Context context;
    String jsonStr;
    String url;
    ListView listView;

    public GetCustomerDetails(Context context, ListView listView) {
        this.context = context;
        this.url="user/getCustomerList";
        this.listView = listView;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }



    @Override
    protected Object doInBackground(Object[] params) {
        HttpHandler httpHandler = new HttpHandler();
        jsonStr = httpHandler.makeServiceCall(this.url,null);
        return true;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);

        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setDateFormat("M/d/yy hh:mm a");
            Gson gson = gsonBuilder.create();


            Type collectionType = new TypeToken<Collection<User>>() {
            }.getType();

            Collection<User> customers = gson.fromJson(jsonStr, collectionType);

            Store.getInstance().customerList = (ArrayList<User>) customers;


            listView.setAdapter(new CustomerAdapter(context));
        }
        catch (Exception e)
        {

        }
    }

}
