package com.bizsoft.pos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bizsoft.pos.R;
import com.bizsoft.pos.dataobject.Items;
import com.bizsoft.pos.dataobject.Store;
import com.bizsoft.pos.dataobject.User;
import com.bizsoft.pos.service.BizUtils;

import java.util.ArrayList;

/**
 * Created by shri on 3/7/17.
 */

public class CustomerAdapter extends BaseAdapter {
    Context context;
    private static LayoutInflater inflater=null;
    BizUtils bizUtils;
    ArrayList<User> customer;

    public CustomerAdapter(Context context) {
        this.context = context;
        inflater = (LayoutInflater)context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        bizUtils = new BizUtils();
        customer = Store.getInstance().customerList;
    }

    @Override
    public int getCount() {

        return Store.getInstance().customerList.size();
    }

    @Override
    public Object getItem(int position) {
        return Store.getInstance().customerList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(Store.getInstance().customerList.get(position).getId());
    }
    class Holder
    {

        ImageView image;
        TextView firstName,lastName,contactNumber,mail,rewardPoint,cartSize;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder=new Holder();
        View rowView;
         User customer = (User) getItem(position);

        rowView = inflater.inflate(R.layout.customer_single_item, null);
        holder.image = (ImageView) rowView.findViewById(R.id.customer_image);
        holder.firstName = (TextView) rowView.findViewById(R.id.customer_fname);
        holder.lastName = (TextView) rowView.findViewById(R.id.customer_lname);
        holder.contactNumber = (TextView) rowView.findViewById(R.id.contact_number);
        holder.mail = (TextView) rowView.findViewById(R.id.customer_mail);
        holder.rewardPoint = (TextView) rowView.findViewById(R.id.credit_points);
        holder.cartSize = (TextView) rowView.findViewById(R.id.cart_size);

        if(customer.getGender().compareToIgnoreCase("male")==0)
        {
            holder.image.setBackgroundResource(R.drawable.avatar_male);
        }
        else
        {
            holder.image.setBackgroundResource(R.drawable.avatar_girl);
        }
        holder.firstName.setText(String.valueOf(customer.getFirstName()));
        holder.lastName.setText(String.valueOf(customer.getLastName()));
        holder.mail.setText(String.valueOf(customer.getMail()));
        holder.contactNumber.setText(String.valueOf(customer.getContactNumber()));
        holder.rewardPoint.setText(String.valueOf(customer.getCreditPoints()));
        holder.cartSize.setText(String.valueOf(customer.getCartSize()));
        return rowView;
    }
}
