package com.bizsoft.pos;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.bizsoft.pos.adapter.CartAdapter;
import com.bizsoft.pos.dataobject.BizViewStore;
import com.bizsoft.pos.dataobject.Cart;
import com.bizsoft.pos.dataobject.Items;
import com.bizsoft.pos.dataobject.Store;
import com.bizsoft.pos.service.HttpHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class CartActivity extends AppCompatActivity {

    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        listView = (ListView) findViewById(R.id.listview);
        getSupportActionBar().setTitle("Cart list");

        new GetCartDetails(CartActivity.this).execute();
    }
    class GetCartDetails extends AsyncTask
    {
        Context context;
        String url;
        String jsonStr;
        HashMap<String,String> params;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        public GetCartDetails(Context context) {
            this.context = context;
            this.url = "cart/getAllCartDetails";
            this.jsonStr = null;
            this.params = new HashMap<String, String>();
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
            try {
                System.out.println("json : " + jsonStr);
                Log.e("JSON :", jsonStr);


                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.setDateFormat("M/d/yy hh:mm a");
                Gson gson = gsonBuilder.create();


                Type collectionType = new TypeToken<Collection<Cart>>() {
                }.getType();

                Collection<Cart> cartList = gson.fromJson(jsonStr, collectionType);

                Store.getInstance().cartList = (ArrayList<Cart>) cartList;

                listView.setAdapter(new CartAdapter(context));



            }
            catch (Exception e)
            {

            }
        }


    }
}
