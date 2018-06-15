package com.bizsoft.pos.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bizsoft.pos.ItemsActivity;
import com.bizsoft.pos.R;
import com.bizsoft.pos.dataobject.Category;
import com.bizsoft.pos.dataobject.Store;
import com.bizsoft.pos.service.BizUtils;
import com.bumptech.glide.Glide;


/**
 * Created by shri on 14/6/17.
 */

public class CategoryAdapter extends BaseAdapter {
    Context context;
    private static LayoutInflater inflater=null;
    BizUtils bizUtils;

    public CategoryAdapter(Context context) {
        this.context = context;
        inflater = (LayoutInflater)context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        bizUtils = new BizUtils();

    }

    @Override
    public Object getItem(int position) {
        return Store.getInstance().categoryList.get(position);
    }
    @Override
    public int getCount() {
        return Store.getInstance().categoryList.size();
    }
    @Override
    public long getItemId(int position) {
        return Long.parseLong(Store.getInstance().categoryList.get(position).getId());
    }


    public class Holder
    {
        TextView categoryName,subCategoryName,items;
        ImageView categoryIamge;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;

        rowView = inflater.inflate(R.layout.categories_single_item, null);
        holder.categoryName =(TextView) rowView.findViewById(R.id.os_texts);
        holder.categoryIamge =(ImageView) rowView.findViewById(R.id.os_images);
        holder.subCategoryName = (TextView) rowView.findViewById(R.id.sub_category_text);
        holder.items = (TextView) rowView.findViewById(R.id.item_size);


        final Category category = (Category) getItem(position);
        holder.categoryName.setText(String.valueOf(category.getName()));
        holder.subCategoryName.setText("Sub Categories : "+String.valueOf(category.getSubCategory().size()));
        holder.items.setText(String.valueOf(category.getItems().size())+" Items");

        try {
            Glide.with(context).load(Store.getInstance().baseUrl+"category/category_image/"+category.getId()).into(holder.categoryIamge);
            /*

            Bitmap bitmap = BitmapFactory.decodeByteArray(category.getImage(), 0, category.getImage().length);
            holder.categoryIamge.setImageBitmap(bitmap);
            */
        }
        catch (Exception e)
        {
            bizUtils.display(context, String.valueOf(e));
        }
        catch (Error e)
        {
            bizUtils.display(context, String.valueOf(e));
        }

        rowView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                bizUtils.display(context,"sub cat size --------"+category.getSubCategory().size());
              /*  if(category.getSubCategory().size()==0)
                {
                */
                    bizUtils.display(context,"empty");

                Store.getInstance().currentCategory = category;
                    Intent intent = new Intent(context, ItemsActivity.class);
                    intent.putExtra("category","true");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);








               /*    }
                else
                {



                    bizUtils.display(context,"non empty");

                    Store.getInstance().categoryList.clear();
                    Store.getInstance().categoryList = category.getSubCategory();
                    Store.getInstance().categoryGridView.setAdapter(new CategoryAdapter(context));
                }
 */


            }
        });

        return rowView;
    }

}
