package com.bizsoft.pos;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;

import com.bizsoft.pos.adapter.CategoryAdapter;
import com.bizsoft.pos.adapter.ItemsAdapter;
import com.bizsoft.pos.dataobject.Category;
import com.bizsoft.pos.dataobject.Items;
import com.bizsoft.pos.dataobject.Store;
import com.bizsoft.pos.service.BizUtils;
import com.bizsoft.pos.service.HttpHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class ItemsActivity extends AppCompatActivity {
    BizUtils bizUtils;
    ListView itemListView;
    FloatingActionButton fab;
    boolean isCategory;
    EditText searchBar;
    private ItemsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        itemListView = (ListView) findViewById(R.id.items_list);
        bizUtils = new BizUtils();
        fab = (FloatingActionButton) findViewById(R.id.fab);
        searchBar = (EditText) findViewById(R.id.search_bar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                bizUtils.display("on Text Changed --------");
                String key = searchBar.getText().toString();
                new GetAll(getApplicationContext(),key).execute();

            }

            @Override
            public void afterTextChanged(Editable s) {

                adapter.notifyDataSetChanged();

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(ItemsActivity.this,AddItemsActivity.class);
                startActivity(intent);

                //

            }

        });
        getSupportActionBar().setTitle("All Items");

        try
        {
            Intent intent = getIntent();

            String cat = intent.getStringExtra("category");
            if(cat!=null)
            {
                getSupportActionBar().setTitle(Store.getInstance().currentCategory.getName());
                isCategory = true;
            }


        new GetAll(getApplicationContext()).execute();
    }
    catch (Exception e)
    {
    bizUtils.display(ItemsActivity.this,"Exception : "+e.getMessage());
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


            if(isCategory) {
                this.params.put("category_id", Store.getInstance().currentCategory.getId());
            }
            this.context = context;



        }

        public GetAll(Context context, String key) {
            this.url = "items/getAllItems";
            this.params = new HashMap<String, String>();
            params.put("search_key",key);

            if(isCategory) {
                this.params.put("category_id", Store.getInstance().currentCategory.getId());
            }
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


        Store.getInstance().itemList = (ArrayList<Items>) items;



        //bizUtils.display(context, jsonStr);
        Log.d("JSON",jsonStr);

        adapter = new ItemsAdapter(context);
        itemListView.setAdapter(new ItemsAdapter(context));
        //  finish();
    } else {


    }
}
catch (Exception e)
{
    bizUtils.display(context, String.valueOf(e));
}
        }

        @Override
        protected void onCancelled() {
            //mAuthTask = null;
            //showProgress(false);


        }
    }
}
