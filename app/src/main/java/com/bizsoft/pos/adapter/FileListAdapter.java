package com.bizsoft.pos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bizsoft.pos.R;
import com.bizsoft.pos.dataobject.Items;
import com.bizsoft.pos.dataobject.Store;

import java.util.ArrayList;

/**
 * Created by shri on 21/7/17.
 */

public class FileListAdapter extends BaseAdapter {
    Context context;
    ArrayList<String> fileList;
    private LayoutInflater inflater;
    public FileListAdapter(Context context,ArrayList<String> fileList) {
        this.context = context;
        this.fileList = new ArrayList<>();
        this.fileList = fileList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return fileList.size();
    }

    @Override
    public Object getItem(int position) {
        return fileList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class Holder
    {
        TextView fileName;

    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();

        String item = (String) getItem(position);
        convertView = inflater.inflate(R.layout.file_list_single_item, parent, false);


        holder.fileName = (TextView) convertView.findViewById(R.id.file_name);
        holder.fileName.setText(String.valueOf(item));


        if(Store.getInstance().screenSize.equals(Store.getInstance().LARGE_SCREEN)) {

            holder.fileName.setTextSize(Store.getInstance().textSizeLarge);

        }

            return convertView;
    }
}
