package com.bizsoft.pos.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bizsoft.pos.EditItems;
import com.bizsoft.pos.R;
import com.bizsoft.pos.dataobject.Items;
import com.bizsoft.pos.dataobject.Store;
import com.bizsoft.pos.service.BizUtils;
import com.bizsoft.pos.service.HttpHandler;
import com.bumptech.glide.Glide;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;

/**
 * Created by shri on 15/6/17.
 */

public class ItemsAdapter extends BaseAdapter implements Filterable{

    ArrayList<Items> itemsList ;
    Context context;
    private static LayoutInflater inflater=null;
    BizUtils bizUtils;
    private ItemFilter mFilter = new ItemFilter();
    ArrayList<Items> filteredList;

    public ItemsAdapter(Context context) {
        inflater = (LayoutInflater)context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        itemsList = Store.getInstance().itemList;
        filteredList = Store.getInstance().itemList;
        this.context = context;
        bizUtils = new BizUtils();
    }

    @Override
    public int getCount() {
        return this.filteredList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.filteredList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(this.filteredList.get(position).getId());
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    public class Holder
    {
        TextView itemName,wholeSalePrice,retailPrice,quantity,stockHome,vendor,measuringUnit,code;
        ImageView itemImage;
        ImageView delete_i,edit_i;
        TextView delete_t,edit_t;

    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder=new Holder();
        View rowView;
        final Items items = (Items) getItem(position);

        rowView = inflater.inflate(R.layout.items_single_item, null);

        holder.itemName = (TextView) rowView.findViewById(R.id.item_name);
        holder.itemImage = (ImageView) rowView.findViewById(R.id.item_image);
        holder.wholeSalePrice = (TextView) rowView.findViewById(R.id.whole_sale_price);
        holder.retailPrice =  (TextView) rowView.findViewById(R.id.retail_price);
        holder.measuringUnit =   (TextView) rowView.findViewById(R.id.measuring_unit);
        holder.quantity =  (TextView) rowView.findViewById(R.id.quantity1);
        holder.code =  (TextView) rowView.findViewById(R.id.bar_code);
        holder.stockHome =  (TextView) rowView.findViewById(R.id.stock_home);
        holder.vendor =  (TextView) rowView.findViewById(R.id.vendor);
        holder.edit_t = (TextView) rowView.findViewById(R.id.edit_t);
        holder.edit_i = (ImageView) rowView.findViewById(R.id.edit_i);
        holder.delete_i = (ImageView) rowView.findViewById(R.id.delete_i);
        holder.delete_t = (TextView) rowView.findViewById(R.id.delete_t);



        holder.itemName.setText(items.getName());
        holder.retailPrice.setText(String.valueOf(items.getRetailPrice()));
        holder.wholeSalePrice.setText(String.valueOf(items.getWholeSalePrice()));
        holder.measuringUnit.setText(String.valueOf(items.getMeasuringUnit()));
        holder.quantity.setText("x"+String.valueOf(items.getQuantity()));
        holder.code.setText(String.valueOf(items.getCode()));
        holder.stockHome.setText(String.valueOf(items.getStock().getName()));
        holder.stockHome.setText(String.valueOf(items.getVendor().getName()));
        holder.edit_t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Store.getInstance().currentItem = items;
                Intent intent = new Intent(context, EditItems.class);
                intent.putExtra("position",position);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        holder.edit_i.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditItems.class);
                intent.putExtra("position",position);
                Store.getInstance().currentItem = items;
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        holder.delete_t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                deleteAlert(items.getId(),v);


            }
        });
        holder.delete_i.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAlert(items.getId(), v);
            }
        });
        try {

            bizUtils.display(context,"id : "+items.getId());
            Glide.with(context).load(Store.getInstance().baseUrl+"items/item_image/"+items.getId()).into(holder.itemImage);





            /*Bitmap bitmap = BitmapFactory.decodeByteArray(items.getImage(), 0, items.getImage().length);
            holder.itemImage.setImageBitmap(bitmap);*/
        }
        catch (Exception e)
        {
            bizUtils.display(context, String.valueOf(e));
        }



        return rowView;
    }
    public void deleteAlert(final String id, View v)
    {

        try             {

                final AlertDialog.Builder alert = new AlertDialog.Builder(
                        v.getRootView().getContext());
                alert.setTitle("Delete Item");
                alert.setMessage("Are you sure to delete ?");
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        new Delete(context,id).execute();

                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {



                    }
                });
                alert.show();
            }
            catch(Exception e)
            {
                bizUtils.display(context,"Exception  : "+e.toString());

            }

    }
    class Delete extends AsyncTask
    {
        String id;
        HashMap<String,String> params = new HashMap<String,String>();
        public Delete(Context context,String id) {
            this.context = context;
            this.url = "items/deleteItems";
            this.params.put("item_id",id);
            this.id = id;
        }

        Context context;
        String url;
        String jsonStr;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }




        @Override
        protected Object doInBackground(Object[] params) {
            HttpHandler httpHandler = new HttpHandler();
            jsonStr = httpHandler.makeServiceCall(this.url, this.params);
            return null;
        }
        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            bizUtils.display(context,"status : "+jsonStr);
            if(jsonStr.contains("true"))
            {
                Toast.makeText(context,"Item deleted",Toast.LENGTH_SHORT).show();


                        for (ListIterator<Items> iter = Store.getInstance().itemList.listIterator(); iter.hasNext(); ) {
                            Items a = iter.next();
                if (a.getId().equals(id)) {
                    iter.remove();
                }
            }

              notifyDataSetChanged();

            }


        }
    }
    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final ArrayList<Items> list = itemsList;

            int count = list.size();

            final ArrayList<Items> nlist = new ArrayList<Items>(count);

            String filterableString;
            bizUtils.display("---------FIltering");
            for (int i = 0; i < count; i++) {
                filterableString = list.get(i).getName();
                bizUtils.display(filterableString+"================"+list.get(i).getName());
                if (filterableString.toLowerCase().contains(filterString)) {
                    nlist.add(list.get(i));
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            bizUtils.display("-----------"+results.values);
            filteredList = (ArrayList<Items>) results.values;
            notifyDataSetChanged();
        }
    }



    }
