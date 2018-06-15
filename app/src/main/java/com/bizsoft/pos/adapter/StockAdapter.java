package com.bizsoft.pos.adapter;

import android.app.ExpandableListActivity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bizsoft.pos.R;
import com.bizsoft.pos.dataobject.Items;
import com.bizsoft.pos.dataobject.Stock;
import com.bizsoft.pos.dataobject.Store;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by shri on 20/6/17.
 */

public class StockAdapter extends BaseExpandableListAdapter {

    Context context;
    ArrayList<Stock> stockList;

    @Override
    public void notifyDataSetInvalidated() {
        super.notifyDataSetInvalidated();
    }

    @Override
    public void notifyDataSetChanged() {

        super.notifyDataSetChanged();
    }

    public StockAdapter(Context context, ArrayList<Stock> stockList) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.stockList = stockList;

    }


    public void clear()
    {
        this.stockList.clear();
    }
    public void addAll(ArrayList<Stock> stockList)
    {
        this.stockList = stockList;

    }
    private LayoutInflater inflater;
    @Override
    public int getGroupCount() {
        return stockList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return stockList.get(groupPosition).getItems().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return stockList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return stockList.get(groupPosition).getItems().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return Long.parseLong(stockList.get(groupPosition).getId());
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return Long.parseLong(stockList.get(groupPosition).getItems().get(childPosition).getId());
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        Stock stock = (Stock) getGroup(groupPosition);
        GroupHolder groupHolder = new GroupHolder();

        convertView = inflater.inflate(R.layout.stock_single_item, parent, false);


        groupHolder.stockName = (TextView) convertView.findViewById(R.id.stock_name);
        groupHolder.indicator = (ImageView) convertView.findViewById(R.id.indicator);
        groupHolder.stockSize = (TextView) convertView.findViewById(R.id.stock_size);

        if (isExpanded) {
            groupHolder.indicator.setImageResource(R.drawable.down_icon);
        } else {
            groupHolder.indicator.setImageResource(R.drawable.up_icon);
        }
        groupHolder.stockName.setText(stock.getName());
        groupHolder.stockSize.setText("("+stock.getStockSize()+")");
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {


        Items items = (Items) getChild(groupPosition,childPosition);

        ChildHolder childHolder  = new ChildHolder();
        convertView = inflater.inflate(R.layout.stock_child_item, parent, false);
        childHolder.itemName = (TextView) convertView.findViewById(R.id.item_name);
        childHolder.itemQuantity = (TextView) convertView.findViewById(R.id.item_quantity);
        childHolder.itemImage = (ImageView) convertView.findViewById(R.id.item_image);

        childHolder.itemName.setText(items.getName());
        childHolder.itemQuantity.setText(String.valueOf(items.getQuantity()));

        System.out.println("---------------------"+items.getId());

try
{
    Glide.with(context).load(Store.getInstance().baseUrl+"items/item_image/"+items.getId()).into(childHolder.itemImage);
}
catch (Exception e)
{
    System.out.println("---------------------"+e);
}
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
    class GroupHolder
    {
        TextView stockName;
        ImageView indicator;
        TextView stockSize;

    }
    class ChildHolder
    {
        TextView itemName,itemQuantity;
        ImageView itemImage;


    }
}
