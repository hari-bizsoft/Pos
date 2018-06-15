package com.bizsoft.pos;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.bizsoft.pos.adapter.CartItemAdapter;
import com.bizsoft.pos.dataobject.Cart;
import com.bizsoft.pos.dataobject.Store;

public class CartItemActivity extends AppCompatActivity {

    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_item);
        listView = (ListView) findViewById(R.id.listview);

        Cart cart = Store.getInstance().cartList.get(Store.getInstance().currentCartPosition);

        getSupportActionBar().setTitle("Cart ID : "+cart.getId());

        System.out.println("Cart list  ===== "+Store.getInstance().cartList.get(Store.getInstance().currentCartPosition).getItems().size());



            listView.setAdapter(new CartItemAdapter(CartItemActivity.this, Store.getInstance().cartList.get(Store.getInstance().currentCartPosition).getItems()));



    }
}
