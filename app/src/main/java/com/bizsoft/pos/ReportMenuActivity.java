package com.bizsoft.pos;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

public class ReportMenuActivity extends AppCompatActivity {

    Button purchaseReport,salesReport,StockReport;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_menu);
        getSupportActionBar().setTitle("Reports");
        purchaseReport = (Button) findViewById(R.id.purchase_report);
        salesReport = (Button) findViewById(R.id.sales_report);
        StockReport = (Button) findViewById(R.id.stock_report);

        context = ReportMenuActivity.this;
        Log.d("Files", "Path: " +   context.getExternalFilesDir(null));
        String path = String.valueOf(context.getExternalFilesDir(null));
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        Log.d("Files", "Size: "+ files.length);


        if(files.length==0)
        {
            Toast.makeText(context, "No reports generated", Toast.LENGTH_SHORT).show();
        }

            int purchase_count = 0,stock_count =0,sales_count =0;
            for(int i=0;i<files.length;i++)
            {


                if (files[i].getName().contains("purchase_report")) {
                    purchase_count++;
                }
                else if (files[i].getName().contains("stock_report"))
                {
                    stock_count++;
                }
                else
                if (files[i].getName().contains("sales_report"))
                {
                    sales_count++;
                }
            }


            final int finalPurchase_count = purchase_count;
            purchaseReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(finalPurchase_count >0)
                    {
                        Intent intent = new Intent(ReportMenuActivity.this,ReportListActivity.class);
                        intent.putExtra("from","purchase");
                        startActivity(intent);
                    }
                    else
                    {
                        Toast.makeText(context, "No Purchase Report Generated", Toast.LENGTH_SHORT).show();
                    }


                }
            });


            final int finalStock_count = stock_count;
            StockReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    System.out.println("test = onclick");
                    if(finalStock_count >0)
                    {
                        Intent intent = new Intent(ReportMenuActivity.this,ReportListActivity.class);
                        intent.putExtra("from","stock");
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(context, "No Stock Report Generated", Toast.LENGTH_SHORT).show();
                    }

                }
            });
            final int finalSales_count = sales_count;
            salesReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    System.out.println("test = onclick");
                    if(finalSales_count >0)
                    {
                        Intent intent = new Intent(ReportMenuActivity.this,ReportListActivity.class);
                        intent.putExtra("from","sales");
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(context, "No Stock Report Generated", Toast.LENGTH_SHORT).show();
                    }

                }
            });




    }
}

