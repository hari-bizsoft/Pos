package com.bizsoft.pos;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bizsoft.pos.dataobject.Category;
import com.bizsoft.pos.dataobject.StockHome;
import com.bizsoft.pos.dataobject.Store;
import com.bizsoft.pos.dataobject.SupportDetaills;
import com.bizsoft.pos.dataobject.Vendor;
import com.bizsoft.pos.service.BizUtils;
import com.bizsoft.pos.service.HttpHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class AddItemsActivity extends AppCompatActivity {

    Spinner categories,vendor,stockHome,itemType;
    EditText name,quantity,code,purchaseRate,retailPrice,wholeSalePrice,details,measuringUnit,rewardPoint;
    public ImageView pickImage;
    int PICK_IMAGE_REQUEST = 1;
    static final int REQUEST_IMAGE_CAPTURE = 2;
    String catImageBase64;

    Button pickFromGallery,pickFromCamera;
    private String currentCatId;
    private String vendorID;
    private String stockHomeID;
    private String measuring_Unit;
    Button add;
    private String ItemType;
    ImageView QRimage,barcode;
    Bitmap bitmap,bitmap2 ;
    public final static int QRcodeWidth = 500 ;
    private String QRimageBase64;
    private String barCodeimageBase64;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_item_activity);


        pickFromGallery = (Button)findViewById(R.id.choose_from_gallery);
        pickFromCamera = (Button) findViewById(R.id.choose_from_camera);
        categories = (Spinner) findViewById(R.id.categories);
        pickImage = (ImageView) findViewById(R.id.pick_image);
        vendor = (Spinner) findViewById(R.id.vendor);
        stockHome = (Spinner) findViewById(R.id.stock_home);
        measuringUnit =  (EditText) findViewById(R.id.measuring_unit);
        name = (EditText) findViewById(R.id.name);
        code = (EditText) findViewById(R.id.code);
        quantity = (EditText) findViewById(R.id.item_quantity);
        purchaseRate = (EditText) findViewById(R.id.purchase_rate);
        retailPrice= (EditText) findViewById(R.id.retail_price);
        wholeSalePrice = (EditText) findViewById(R.id.whole_sale_price);
        details  = (EditText) findViewById(R.id.details);
        add = (Button) findViewById(R.id.add);
        itemType = (Spinner) findViewById(R.id.item_type);
        QRimage = (ImageView) findViewById(R.id.qr_image);
        barcode = (ImageView) findViewById(R.id.barcode_image);
        rewardPoint = (EditText) findViewById(R.id.reward_point_editText);

        measuringUnit.setText("Nos");
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                new GenerateCode(code.getText().toString()).execute();


                //new AddItems(AddItemsActivity.this).execute();
            }
        });


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







        new GetSupportDetails(AddItemsActivity.this).execute();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();



            try {


                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);


                // ImageView imageView = (ImageView) findViewById(R.id.imageView);
                pickImage.setImageBitmap(bitmap);


                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 70, stream);
                byte[] byteArray = stream.toByteArray();









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









            String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);



            Log.d("BITMAP", String.valueOf(encodedImage));


            catImageBase64 = encodedImage;
        }

    }

    class GenerateCode extends AsyncTask
    {
        String code;

        public GenerateCode(String code) {
            this.code = code;
        }

        @Override
        protected void onPreExecute() {


            super.onPreExecute();

            add.setText("Generating QR code");
        }
        @Override
        protected Object doInBackground(Object[] params) {
            try {
                System.out.println("QR  CODE : "+code);
                bitmap = TextToImageEncode(code);



            } catch (WriterException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            QRimage.setImageBitmap(bitmap);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 70, stream);
            byte[] byteArray = stream.toByteArray();
            String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
            Log.d("QR BITMAP", String.valueOf(encodedImage));
            QRimageBase64 = encodedImage;



            add.setText("Finished generating QR code");
            new GenerateBarCode(code,250,500).execute();
        }


    }
    class GenerateBarCode extends AsyncTask
    {
        String code;
        int height;
        int width;

        public GenerateBarCode(String code,int height,int width) {
            this.code = code;
            this.height = height;
            this.width = width;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            add.setText("Generating Bar code");
        }
        @Override
        protected Object doInBackground(Object[] params) {
            try {
                bitmap2 = createBarcodeBitmap(this.code,this.width,this.height);



            } catch (WriterException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            add.setText("Finished generating Bar code");
            barcode.setImageBitmap(bitmap2);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap2.compress(Bitmap.CompressFormat.PNG, 70, stream);
            byte[] byteArray = stream.toByteArray();
            String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
            Log.d("BARCODE BITMAP", String.valueOf(encodedImage));
            barCodeimageBase64 = encodedImage;

            add.setText("Done");
            new AddItems(AddItemsActivity.this).execute();
        }


    }
    class AddItems extends AsyncTask
    {
        Context context;
        HashMap<String, String> params = new HashMap<String, String>();
        String url = "items/addItems";
        String jsonStr ="";
        BizUtils bizUtils;

        public AddItems(Context context) {
            this.context = context;

            this.bizUtils = new BizUtils();

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            measuring_Unit =  measuringUnit.getText().toString();
            this.params.put("image",catImageBase64);
            this.params.put("qr_image",QRimageBase64);
            this.params.put("barcode_image",barCodeimageBase64);
            this.params.put("currentCatId",currentCatId);
            this.params.put("name",name.getText().toString());
            this.params.put("code",code.getText().toString());
            this.params.put("quantity",quantity.getText().toString());
            this.params.put("details",details.getText().toString());
            this.params.put("purchaseRate",purchaseRate.getText().toString());
            this.params.put("wholeSalePrice",wholeSalePrice.getText().toString());
            this.params.put("retailPrice",retailPrice.getText().toString());
            this.params.put("vendorID",vendorID);
            this.params.put("stockHomeID",stockHomeID);
            this.params.put("measuring_Unit",measuring_Unit);
            this.params.put("item_type",ItemType);
            this.params.put("totalCost","0");
            this.params.put("reward_point",rewardPoint.getText().toString());
        }



        @Override
        protected Object doInBackground(Object[] params) {
            HttpHandler httpHandler = new HttpHandler();
            jsonStr = httpHandler.makeServiceCall(this.url, this.params);
            return true;
        }
        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);




            if(jsonStr==null || jsonStr.contains("false"))
            {
                Toast.makeText(context,"Item Not Added",Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(context,"Item Added",Toast.LENGTH_SHORT).show();


                Intent intent = new Intent(context,ItemsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }

            bizUtils.display(context,jsonStr);
        }
    }
    class GetSupportDetails extends AsyncTask {
        Context context;
        HashMap<String, String> params = new HashMap<String, String>();
        String url = "items/getSupportDetails";
        String jsonStr;
        BizUtils bizUtils;

        public GetSupportDetails(Context context) {
            this.context = context;
            bizUtils = new BizUtils();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object[] params) {

            HttpHandler httpHandler = new HttpHandler();
            jsonStr = httpHandler.makeServiceCall(this.url, null);

            return true;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setDateFormat("M/d/yy hh:mm a");
            Gson gson = gsonBuilder.create();


            Type collectionType = new TypeToken<SupportDetaills>() {
            }.getType();

            SupportDetaills supportDetaills= gson.fromJson(jsonStr, collectionType);


            Store.getInstance().supportDetaills = (SupportDetaills) supportDetaills;

            bizUtils.display(context, String.valueOf(Store.getInstance().supportDetaills));



            bizUtils.display(context, String.valueOf(Store.getInstance().supportDetaills.getCategoryList().size()));

            setCategoryList();
            setVendorList();
            setStockHomeList();
            setMeasuringUnitList();
            setItemType();

        }
    }
    
    public void setCategoryList()
    {
        // Spinner Drop down elements
        List<String> categoryList = new ArrayList<String>();

        final ArrayList<Category> list = Store.getInstance().supportDetaills.getCategoryList();

        int size = list.size();

        for (int i = 0; i < size;i ++)
        {
            categoryList.add(list.get(i).getName());

        }

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter <String>(AddItemsActivity.this, android.R.layout.simple_spinner_item, categoryList);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        categories.setAdapter(dataAdapter);
        categories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        
        
    }
        public void setVendorList()
        {
            // Spinner Drop down elements
            List<String> categoryList = new ArrayList<String>();

            final ArrayList<Vendor> list = Store.getInstance().supportDetaills.getVendorList();

            int size = list.size();

            for (int i = 0; i < size;i ++)
            {
                categoryList.add(list.get(i).getName());

            }

            // Creating adapter for spinner
            ArrayAdapter<String> dataAdapter = new ArrayAdapter <String>(AddItemsActivity.this, android.R.layout.simple_spinner_item, categoryList);

            // Drop down layout style - list view with radio button
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // attaching data adapter to spinner
            vendor.setAdapter(dataAdapter);
            vendor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    Toast.makeText(getApplicationContext(),list.get(position).getName(),Toast.LENGTH_SHORT).show();

                    vendorID = list.get(position).getId();

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                    try {
                        vendorID  = list.get(0).getId();
                    }
                    catch (Exception e)
                    {

                    }
                }
            });


        }


    public void setStockHomeList()
    {
        // Spinner Drop down elements
        List<String> categoryList = new ArrayList<String>();

        final ArrayList<StockHome> list = Store.getInstance().supportDetaills.getStockHomeList();

        int size = list.size();

        for (int i = 0; i < size;i ++)
        {
            categoryList.add(list.get(i).getName());

        }

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter <String>(AddItemsActivity.this, android.R.layout.simple_spinner_item, categoryList);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        stockHome.setAdapter(dataAdapter);
        stockHome.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Toast.makeText(getApplicationContext(),list.get(position).getName(),Toast.LENGTH_SHORT).show();

                stockHomeID = list.get(position).getId();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                try {
                    stockHomeID  = list.get(0).getId();
                }
                catch (Exception e)
                {

                }
            }
        });



    }

    public void setMeasuringUnitList()
    {
        // Spinner Drop down elements
        final List<String> unitList = new ArrayList<String>();
        unitList.add("Kg");
        unitList.add("Ltr");
        unitList.add("Nos");







        measuring_Unit =  measuringUnit.getText().toString();

    }

    public void setItemType()
    {
        // Spinner Drop down elements
        final List<String> typeList = new ArrayList<String>();
        typeList.add("forsale");
        typeList.add("fororder");



        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter <String>(AddItemsActivity.this, android.R.layout.simple_spinner_item, typeList);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        itemType.setAdapter(dataAdapter);
        itemType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Toast.makeText(getApplicationContext(),typeList.get(position),Toast.LENGTH_SHORT).show();

                ItemType = typeList.get(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                try {
                    ItemType   = "forsale";
                }
                catch (Exception e)
                {

                }
            }
        });


    }
    Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.QRCodeBlackColor):getResources().getColor(R.color.QRCodeWhiteColor);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }

    private Bitmap createBarcodeBitmap(String data, int width, int height) throws WriterException {
        MultiFormatWriter writer = new MultiFormatWriter();
        String finalData = Uri.encode(data);

        // Use 1 as the height of the matrix as this is a 1D Barcode.
        BitMatrix bm = writer.encode(finalData, BarcodeFormat.CODE_128, width, 1);
        int bmWidth = bm.getWidth();

        Bitmap imageBitmap = Bitmap.createBitmap(bmWidth, height, Bitmap.Config.ARGB_8888);

        for (int i = 0; i < bmWidth; i++) {
            // Paint columns of width 1
            int[] column = new int[height];
            Arrays.fill(column, bm.get(i, 0) ? Color.BLACK : Color.WHITE);
            imageBitmap.setPixels(column, 0, 1, i, 0, 1, height);
        }

        return imageBitmap;
    }

}




