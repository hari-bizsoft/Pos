package com.bizsoft.pos.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bizsoft.pos.CartItemActivity;
import com.bizsoft.pos.R;
import com.bizsoft.pos.dataobject.Cart;
import com.bizsoft.pos.dataobject.Items;
import com.bizsoft.pos.dataobject.Store;

import java.util.ArrayList;

/**
 * Created by shri on 11/7/17.
 */

public class CartAdapter extends BaseAdapter {

    Context context;
    private LayoutInflater inflater;
    ArrayList<Cart> cartList;

    public CartAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.cartList = Store.getInstance().cartList;
    }

    @Override
    public int getCount() {
        return this.cartList.size();

    }

    @Override
    public Object getItem(int position) {
        return this.cartList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(this.cartList.get(position).getId());
    }
    class Holder
    {
            TextView username,cartSize;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        final Cart cart= (Cart) getItem(position);
        convertView = inflater.inflate(R.layout.cart_single_item, parent, false);
        holder.username = (TextView) convertView.findViewById(R.id.user);
        holder.cartSize = (TextView) convertView.findViewById(R.id.size);



        if(Store.getInstance().screenSize.equals(Store.getInstance().LARGE_SCREEN)) {

            holder.username.setTextSize(Store.getInstance().textSizeLarge);
            holder.cartSize.setTextSize(Store.getInstance().textSizeLarge);
        }

        holder.username.setText(String.valueOf(cart.getUser()));
        holder.cartSize.setText(String.valueOf(cart.getSize()));

        final int pos = position;
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Store.getInstance().currentCartPosition = pos;


                if(Integer.parseInt(cart.getSize())==0)
                {
                    Toast.makeText(context, "Sorry,Cart has no items to show", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Intent intent = new Intent(context, CartItemActivity.class);
                    context.startActivity(intent);
                }


            }
        });
        return convertView;
    }
}
