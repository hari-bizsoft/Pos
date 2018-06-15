package com.bizsoft.pos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bizsoft.pos.R;
import com.bizsoft.pos.dataobject.BizViewStore;
import com.bizsoft.pos.dataobject.Items;
import com.bizsoft.pos.dataobject.Store;
import com.bizsoft.pos.service.BizUtils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by shri on 28/6/17.
 */
public class ItemDialogAdapter extends BaseAdapter {

    ArrayList<Items> itemsList ;
    Context context;
    private static LayoutInflater inflater=null;
    BizUtils bizUtils;


    public ItemDialogAdapter(Context context) {
        inflater = (LayoutInflater)context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        itemsList = Store.getInstance().simpleItemList;

        this.context = context;
        bizUtils = new BizUtils();
    }


    @Override
    public int getCount() {
        return this.itemsList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemsList.get(position);
    }

    @Override
    public long getItemId(int position) {

        return Long.parseLong(itemsList.get(position).getId());
    }

    class Holder
    {
        ImageView image,plus,minus,delete,add;
        TextView itemName,ItemPrice,ItemQuantity,itemUnit,cross;

        public TextView totalCost;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Holder holder=new Holder();
        View rowView;
        final Items items = (Items) getItem(position);
        rowView = inflater.inflate(R.layout.dialog_itemlist, null);
        try {


        holder.itemName = (TextView) rowView.findViewById(R.id.item_name);
        holder.image = (ImageView) rowView.findViewById(R.id.item_image);
        holder.ItemQuantity = (TextView) rowView.findViewById(R.id.item_quantity);
        holder.ItemPrice = (TextView) rowView.findViewById(R.id.price);
        holder.itemUnit = (TextView) rowView.findViewById(R.id.unit);
        holder.plus = (ImageView) rowView.findViewById(R.id.plus);
        holder.minus = (ImageView) rowView.findViewById(R.id.minus);
        holder.delete = (ImageView) rowView.findViewById(R.id.delete);
        holder.totalCost =  (TextView) rowView.findViewById(R.id.total_cost);
        holder.cross =   (TextView) rowView.findViewById(R.id.cross);
        holder.add  = (ImageView) rowView.findViewById(R.id.add);


        holder.itemName.setText(String.valueOf(items.getName()));
        holder.ItemQuantity.setText("X "+String.valueOf(items.getQuantity()));
        holder.ItemPrice.setText(String.valueOf(items.getPurchaseRate()));
        holder.itemUnit.setText(String.valueOf(items.getMeasuringUnit()));



            float total = items.getQuantity() * items.getPurchaseRate();
            items.setTotalCost(total);

            holder.totalCost.setText(String.valueOf(items.getTotalCost()));
            holder.delete.setVisibility(View.INVISIBLE);
            holder.cross.setVisibility(View.GONE);

            Glide.with(context).load(Store.getInstance().baseUrl + "items/item_image/" + items.getId()).into(holder.image);
        }catch (Exception e)
        {
            bizUtils.display("--Exception : " +e);
        }

        holder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("called add items to cart");


                boolean status = true;
                ArrayList<Items> itemsA = Store.getInstance().addedItemList;

                for (int i=0;i<itemsA.size();i++)
                {
                    if( Integer.parseInt(itemsA.get(i).getId()) == Integer.parseInt(items.getId()))
                    {
                        status = false;
                        Toast.makeText(context, "Item already added", Toast.LENGTH_SHORT).show();


                    }
                    else
                    {
                        status = true;

                    }


                }

                if(status)
                {
                    //Store.getInstance().addedItemList.add(items);

                    // BizViewStore.getInstance().addedItemAdapter.notifyDataSetChanged();
                   // new BillingItemSearchAdapter.AddToCart(context,items).execute();
                }

                BizViewStore.getInstance().itemShowDialog.cancel();




            }
        });
        holder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                int q = items.getQuantity();



                    q--;
                    holder.ItemQuantity.setText(String.valueOf(q));
                    items.setQuantity(q);


                float total = items.getQuantity() * items.getPurchaseRate();
                items.setTotalCost(total);
                holder.totalCost.setText(String.valueOf(items.getTotalCost()));
                    notifyDataSetChanged();



            }
        });
        holder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int q = items.getQuantity();


                    q++;
                    holder.ItemQuantity.setText(String.valueOf(q));
                    items.setQuantity(q);
                float total = items.getQuantity() * items.getPurchaseRate();
                items.setTotalCost(total);
                holder.totalCost.setText(String.valueOf(items.getTotalCost()));
                    notifyDataSetChanged();

            }
        });
        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<Items> mItems = Store.getInstance().simpleItemListTemp;


                if(mItems.size()==0)
                {

                    bizUtils.display("size 0");
                    Store.getInstance().simpleItemListTemp.add(items);
                    Store.getInstance().itemAdapterTemp.notifyDataSetChanged();
                }

                boolean status = true;
                for(int i=0;i<mItems.size();i++)
                {
                    int id = Integer.parseInt(mItems.get(i).getId());
                    int id2 = Integer.parseInt(items.getId());

                    int quant1 = mItems.get(i).getQuantity();
                    int quant2 = items.getQuantity();

                    if(id==id2)
                    {
                        Toast.makeText(context,"Item Already Added",Toast.LENGTH_SHORT).show();
                        System.out.println(id+"=====EQ========"+id2);
                        status =  false;

                    }

                }

                if(status)

                {
                    System.out.println("============= NOT EQUAL");
                    Store.getInstance().simpleItemListTemp.add(items);
                    Store.getInstance().itemAdapterTemp.notifyDataSetChanged();
                }







            }
        });
        return rowView;
    }
}
