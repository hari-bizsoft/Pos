package com.bizsoft.pos.BTLib;

import android.bluetooth.BluetoothSocket;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by GopiKingMaker on 2/11/2017.
 */

public class BTPrint extends AppCompatActivity {
    private static  int NoOfLine = 32;
    public  static Paint.Align PrintAlign;
    public static BluetoothSocket btsocket;
    public static OutputStream btoutputstream;
    private static byte FONT_TYPE;
    private static  int FONT_SIZE;
    private static  boolean FONT_BOLD;

    public  static  void  SetAlign(Paint.Align align){
        PrintAlign = align;
    }
    public  static  void  SetSize(int size){
        FONT_SIZE = size;
    }

    public  static  void  SetBold(boolean isBold){
        FONT_BOLD = isBold;
    }

    public  static  void ResetPrinter(){
        byte[] printformat = { 0x1b, 0x40 };
        try {
            btoutputstream.write(printformat);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public  static  void PrintTextLine(String Text){
        try{
            byte[] printformat = { 0x1B, 0x21, FONT_TYPE };
            btoutputstream.write(printformat);

            if (PrintAlign == Paint.Align.LEFT){
                btoutputstream.write(new byte[] { 0x1b, 0x61, 0x30 });
            }else if(PrintAlign == Paint.Align.CENTER){
                btoutputstream.write(new byte[] { 0x1b, 0x61, 0x31 });
            }else {
                btoutputstream.write(new byte[] { 0x1b, 0x61, 0x32 });
            }
            if(FONT_SIZE==0){
                btoutputstream.write(new byte[] { 0x1d, 0x21, 0x00 });
            }else if(FONT_SIZE==1){
                btoutputstream.write(new byte[] { 0x1d, 0x21, 0x02 });
            }else {
                btoutputstream.write(new byte[] { 0x1d, 0x21, 0x11 });
            }

            if(FONT_BOLD){
                btoutputstream.write(new byte[] {  0x1b, 0x45, 0x00 });
            }
            else {
                btoutputstream.write(new byte[] {  0x1b, 0x45, 0x01 });
            }
            btoutputstream.write(Text.getBytes());
            btoutputstream.write(0x0D);
            btoutputstream.flush();
        }catch (Exception e){}
    }



    public static void printLineFeed (){
        try{
            byte[] printformat = { 0x1B, 0x21, FONT_TYPE };
            btoutputstream.write(printformat);
            String msg = "\n";
            btoutputstream.write(msg.getBytes());
            btoutputstream.write(0x0D);
            btoutputstream.write(0x0D);
            //btoutputstream.write(0x0D);
            btoutputstream.flush();
        }catch (Exception e){}
    }
}
