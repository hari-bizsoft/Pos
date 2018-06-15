package com.bizsoft.pos;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bizsoft.pos.adapter.CategoryAdapter;
import com.bizsoft.pos.dataobject.Category;
import com.bizsoft.pos.dataobject.Store;
import com.bizsoft.pos.service.BizUtils;
import com.bizsoft.pos.service.HttpHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {
    GridView gridview;
    BizUtils bizUtils;
    FloatingActionButton fab;
    int PICK_IMAGE_REQUEST = 1;
    static final int REQUEST_IMAGE_CAPTURE = 2;

    byte[] catImage;
    String currentCatId;
    Button add;
    String catImageBase64;

    public ImageView pickImage;
    public EditText name,code;
    Button pickFromGallery,pickFromCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_category);
        bizUtils = new BizUtils();

        new GetAll(getApplicationContext()).execute();



        fab = (FloatingActionButton) findViewById(R.id.fab);



        // add listener to button
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                new GetSimpleCategory(getApplicationContext()).execute();
     }

        });

    }
    public void  addCategory()
    {

        final Dialog dialog = new Dialog(CategoryActivity.this);

        dialog.setContentView(R.layout.add_category_activity);

        dialog.setTitle("Create Category");

        Spinner spinner = (Spinner) dialog.findViewById(R.id.categories);

        pickImage = (ImageView) dialog.findViewById(R.id.pick_image);

        add = (Button) dialog.findViewById(R.id.add);

        name = (EditText) dialog.findViewById(R.id.name);
        code = (EditText) dialog.findViewById(R.id.code);
        pickFromGallery = (Button) dialog.findViewById(R.id.choose_from_gallery);
        pickFromCamera = (Button) dialog.findViewById(R.id.choose_from_camera);

        pickFromCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }

            }
        });
        pickFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();

                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });
        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();

        int size = Store.getInstance().simpleCategoryList.size();

        final ArrayList<Category> list = Store.getInstance().simpleCategoryList;

        for (int i = 0; i < size;i ++)
        {
            categories.add(list.get(i).getName());

        }


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Toast.makeText(getApplicationContext(),list.get(position).getName(),Toast.LENGTH_SHORT).show();
                currentCatId = list.get(position).getId();



            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                try {
                    currentCatId = list.get(0).getId();
                }
                catch (Exception e)
                {

                }
            }
        });


     // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter <String>(CategoryActivity.this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

        dialog.show();



        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.cancel();

                new  AddCategory(CategoryActivity.this).execute();

            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();


            bizUtils.display(getApplicationContext(),"uri : "+ String.valueOf(uri));
            try {


                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);


               // ImageView imageView = (ImageView) findViewById(R.id.imageView);
                pickImage.setImageBitmap(bitmap);


                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 70, stream);
                byte[] byteArray = stream.toByteArray();





                bizUtils.display(getApplicationContext(),"bitmap : "+bitmap.getByteCount());



                String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);



                Log.d("BITMAP", String.valueOf(encodedImage));


                catImageBase64 = encodedImage;


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            pickImage.setImageBitmap(imageBitmap);


            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 70, stream);
            byte[] byteArray = stream.toByteArray();





            bizUtils.display(getApplicationContext(),"bitmap : "+imageBitmap.getByteCount());



            String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);



            Log.d("BITMAP", String.valueOf(encodedImage));


            catImageBase64 = encodedImage;
        }

    }

    public class GetAll extends AsyncTask<Void, Void, Boolean> {

        private final String  url;

        String jsonStr;
        Context context;



        GetAll(Context context) {

            this.url = "category/getAllCategory";;

            this.context = context;


        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            // Simulate network access.
            bizUtils.display(context,"do in background");

            HttpHandler httpHandler = new HttpHandler();
            jsonStr = httpHandler.makeServiceCall(this.url,null);


            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            bizUtils.display(context,"do in post");

           // showProgress(false);


            try {


                if (jsonStr != null) {

                    GsonBuilder gsonBuilder = new GsonBuilder();
                    gsonBuilder.setDateFormat("M/d/yy hh:mm a");
                    Gson gson = gsonBuilder.create();


                    Type collectionType = new TypeToken<Collection<Category>>() {
                    }.getType();

                    Collection<Category> categories = gson.fromJson(jsonStr, collectionType);


                    Store.getInstance().categoryList = (ArrayList<Category>) categories;


                    bizUtils.display(context, jsonStr);


                    gridview = (GridView) findViewById(R.id.customgrid);
                    Store.getInstance().categoryGridView = gridview;
                    gridview.setAdapter(new CategoryAdapter(context));
                    //  finish();
                } else {

                }
            }
            catch (Exception e)
            {
                bizUtils.display(context, String.valueOf(e));
            }
        }

        @Override
        protected void onCancelled() {
            //mAuthTask = null;
            //showProgress(false);
        }
    }
    public class GetSimpleCategory extends AsyncTask<Void, Void, Boolean> {

        private final String  url;

        String jsonStr;
        Context context;



        GetSimpleCategory(Context context) {

            this.url = "category/getSimpleCategoryList";;

            this.context = context;


        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            // Simulate network access.

            HttpHandler httpHandler = new HttpHandler();
            jsonStr = httpHandler.makeServiceCall(this.url,null);


            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {



            // showProgress(false);


            try {


                if (jsonStr != null) {

                    GsonBuilder gsonBuilder = new GsonBuilder();
                    gsonBuilder.setDateFormat("M/d/yy hh:mm a");
                    Gson gson = gsonBuilder.create();


                    Type collectionType = new TypeToken<Collection<Category>>() {
                    }.getType();

                    Collection<Category> categories = gson.fromJson(jsonStr, collectionType);

                    Store.getInstance().simpleCategoryList = (ArrayList<Category>) categories;

                    bizUtils.display(context, jsonStr);
                    addCategory();

                    //  finish();
                } else {

                }
            }
            catch (Exception e)
            {
                bizUtils.display(context, String.valueOf(e));
            }
        }

        @Override
        protected void onCancelled() {
            //mAuthTask = null;
            //showProgress(false);
        }
    }
    public class AddCategory extends AsyncTask<Void, Void, Boolean> {

        private final String  url;

        String jsonStr;
        Context context;
        HashMap<String,String> map ;



        AddCategory(Context context) {

            this.url = "category/addCategory";;

            this.context = context;
            this.map= new HashMap<String, String>();
            map.put("root",currentCatId);
            map.put("name",String.valueOf(name.getText().toString()));
            map.put("code",String.valueOf(code.getText().toString()));
            map.put("image", String.valueOf(catImageBase64));

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            // Simulate network access.

            HttpHandler httpHandler = new HttpHandler();
            jsonStr = httpHandler.makeServiceCall(this.url,this.map);


            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {



            // showProgress(false);


            try {


                if (jsonStr != null) {



                    bizUtils.display(context, jsonStr);



                    //  finish();
                } else {

                }
            }
            catch (Exception e)
            {
                bizUtils.display(context, String.valueOf(e));
            }
        }

        @Override
        protected void onCancelled() {
            //mAuthTask = null;
            //showProgress(false);
        }
    }
}
