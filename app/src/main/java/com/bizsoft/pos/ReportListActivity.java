package com.bizsoft.pos;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.bizsoft.pos.adapter.FileListAdapter;
import com.bizsoft.pos.service.BizUtils;

import java.io.File;
import java.util.ArrayList;


public class ReportListActivity extends AppCompatActivity {

    ListView listview;
    TextView title;
    ArrayList<String> fileList;
    Context context;
    BizUtils bizUtils;
    static File filePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_list);
        getSupportActionBar().setTitle("Reports");
        title = (TextView) findViewById(R.id.title);
        listview = (ListView) findViewById(R.id.listview);
        bizUtils = new BizUtils();
        fileList =  new ArrayList<String>();

        context = ReportListActivity.this;


        Log.d("Files", "Path: " +   context.getExternalFilesDir(null));
        String path = String.valueOf(context.getExternalFilesDir(null));
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        Log.d("Files", "Size: "+ files.length);
        Intent intent  = getIntent();


        for (int i = 0; i < files.length; i++)
        {
            Log.d("Files", "FileName:" + files[i].getName());

            String from = intent.getStringExtra("from");
            if(from.contains("purchase"))
            {
                if (files[i].getName().contains("purchase_report")) {
                    fileList.add(files[i].getName());

                }
                title.setText("Purchase reports");
                getSupportActionBar().setTitle("Purchase reports");
            }
            else
            if(from.contains("stock"))
            {
                if (files[i].getName().contains("stock_report")) {
                    fileList.add(files[i].getName());
                }
                title.setText("Stock reports");
                getSupportActionBar().setTitle("Stock reports");
            }
            else
            if(from.contains("sales"))
            {
                if (files[i].getName().contains("sales_report")) {
                    fileList.add(files[i].getName());
                }
                getSupportActionBar().setTitle("Sales reports");
            }
        }


    listview.setAdapter(new FileListAdapter(ReportListActivity.this,fileList));
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                File file = new File(context.getExternalFilesDir(null), fileList.get(position));
                try {
                    bizUtils.openFile(ReportListActivity.this, file, "application/vnd.ms-excel");
                }
                catch (Exception e)
                {
                    Log.d("Task Failed", String.valueOf(e));
                }
            }
        });
    }
}
