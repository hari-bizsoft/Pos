package com.bizsoft.pos.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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


public class ItemOrderedAdapter extends BaseAdapter {

    ArrayList<Items> itemsList ;
    Context context;
    private static LayoutInflater inflater=null;
    BizUtils bizUtils;
    Holder holder;

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        Store.getInstance().subTotal =0;
        Store.getInstance().gst = 0;
        float subTotal =0;


        for(int i=0;i<getCount();i++)
        {

            subTotal  = itemsList.get(i).getPurchaseRate() * itemsList.get(i).getQuantity();

            Store.getInstance().subTotal = Store.getInstance().subTotal + subTotal;


            System.out.println("Sub Total : "+Store.getInstance().subTotal);
        }


        Store.getInstance().gst = Store.getInstance().subTotal * 0.06;
        Store.getInstance().grandTotal = Store.getInstance().subTotal +  Store.getInstance().gst;







        BizViewStore.getInstance().subTotal.setText(String.valueOf( Store.getInstance().subTotal));
        BizViewStore.getInstance().gst.setText(String.valueOf(Store.getInstance().gst));
        BizViewStore.getInstance().grandTotal.setText(String.valueOf(Store.getInstance().grandTotal));

    }

    public ItemOrderedAdapter(Context context) {
        inflater = (LayoutInflater)context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        itemsList = Store.getInstance().simpleItemListTemp;

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
        TextView itemName,ItemPrice,ItemQuantity,itemUnit,totalCost;

    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

         holder=new Holder();
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
            holder.add  = (ImageView) rowView.findViewById(R.id.add);

            holder.add.setVisibility(View.INVISIBLE);
            BizViewStore.getInstance().itemPrice =  holder.ItemPrice;
            BizViewStore.getInstance().currentOrderedItemPositionTemp = position;
            holder.itemName.setText(String.valueOf(items.getName()));
            holder.ItemQuantity.setText(String.valueOf(items.getQuantity()));
            holder.ItemPrice.setText(String.valueOf(items.getPurchaseRate()));
            holder.itemUnit.setText(String.valueOf(items.getMeasuringUnit()));

            items.setTotalCost(items.getPurchaseRate() * items.getQuantity());
            holder.totalCost.setText(String.valueOf(items.getTotalCost()));


            Glide.with(context).load(Store.getInstance().baseUrl + "items/item_image/" + items.getId()).into(holder.image);




            Bitmap bitmap = ((BitmapDrawable)holder.image.getDrawable()).getBitmap();

            Palette palette  = Palette.from(bitmap).generate();
            Palette.Swatch swatch = palette.getVibrantSwatch();
            holder.itemName.setBackgroundColor(swatch.getRgb());

            holder.plus.setVisibility(View.GONE);
            holder.minus.setVisibility(View.GONE);

            holder.plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    bizUtils.display("---test--");

                    int quantity = Store.getInstance().simpleItemListTemp.get(position).getQuantity();

                    quantity++;


                    float amount = items.getQuantity() * items.getPurchaseRate();

                    Store.getInstance().simpleItemListTemp.get(position).setQuantity(quantity);
                    //Store.getInstance().simpleItemListTemp.get(position).setPurchaseRate(amount);
                    Store.getInstance().itemAdapterTemp.notifyDataSetChanged();


                }
            });
            holder.minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    bizUtils.display("---test--");

                    int quantity = Store.getInstance().simpleItemListTemp.get(position).getQuantity();

                    quantity--;

                    float amount = items.getQuantity() * items.getPurchaseRate();

                    Store.getInstance().simpleItemListTemp.get(position).setQuantity(quantity);
                    //Store.getInstance().simpleItemListTemp.get(position).setPurchaseRate(amount);
                    Store.getInstance().itemAdapterTemp.notifyDataSetChanged();


                }
            });
            holder.itemName.setVisibility(View.VISIBLE);
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Store.getInstance().simpleItemListTemp.remove(position);
                    Store.getInstance().itemAdapterTemp.notifyDataSetChanged();


                }
            });

        }catch (Exception e)
        {
            bizUtils.display("--Exception : " +e);
        }




        return rowView;
    }
}
