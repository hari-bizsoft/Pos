package com.bizsoft.pos.service;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import com.bizsoft.pos.PurchaseActivity;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by shri on 13/6/17.
 */

public class BizUtils {

    String TAG = "BizUtiils";

    public void display(Context context, String msg)
    {
        System.out.println(context.getClass().getName()+": "+msg);


    }
    public void display(String msg)
    {
        System.out.println(msg);


    }
    public String getCurrentDateTime()
    {
        String datetime;
        DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        Date date = new Date();
        datetime = dateFormat.format(date);
        System.out.println(datetime); //2016/11/16 12:08:43
        return  datetime;
    }
    public void openFile(Context context,File fileName, String fileType)
    {

        Uri path = Uri.fromFile(fileName);
        Intent pdfOpenintent = new Intent(Intent.ACTION_VIEW);
        pdfOpenintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pdfOpenintent.setDataAndType(path, fileType);
        try {
            context.startActivity(pdfOpenintent);
        }
        catch (ActivityNotFoundException e) {
            Toast.makeText(context, "No Application Available to View Excel..Redirecting to play store",
                    Toast.LENGTH_SHORT).show();

            // getPackageName() from Context or Activity object
            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "cn.wps.moffice_eng&hl=en")));
            } catch (android.content.ActivityNotFoundException anfe) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + "cn.wps.moffice_eng&hl=en")));
            }

        }
    }
    public static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

}
