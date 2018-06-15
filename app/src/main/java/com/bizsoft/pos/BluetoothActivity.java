package com.bizsoft.pos;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.bizsoft.pos.BTLib.BTDeviceList;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BluetoothActivity extends AppCompatActivity {
    public static int REQUEST_BLUETOOTH = 1;
    private static final String TAG = "BLUETOOTH ACTIVITY" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        Intent intent = new Intent(BluetoothActivity.this,BTDeviceList.class);
        startActivity(intent);

     /*   BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        List<String> s = new ArrayList<String>();
        for(BluetoothDevice bt : pairedDevices) {
            s.add(bt.getName());
        }
        System.out.println("=========PAIRED DEVICES======");
        for(String bt : s) {

            System.out.println("========="+bt);
        }
        System.out.println("=========Available DEVICES======");

      //  Intent intent =  new Intent(BluetoothActivity.this, BTDeviceList.class);
       // startActivity(intent);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);

         final BroadcastReceiver mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // A Bluetooth device was found
                    // Getting device information from the intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    Log.i(TAG, "Device found: " + device.getName() + "; MAC " + device.getAddress());
                }
            }
        };


        registerReceiver(mReceiver, filter);
        if (mBluetoothAdapter.isDiscovering()) {
            // Bluetooth is already in modo discovery mode, we cancel to restart it again
            mBluetoothAdapter.cancelDiscovery();
        }
        mBluetoothAdapter.startDiscovery();
        */
    }

}
