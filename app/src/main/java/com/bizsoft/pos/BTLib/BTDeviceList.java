package com.bizsoft.pos.BTLib;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


import com.bizsoft.pos.R;
import com.bizsoft.pos.dataobject.Store;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;



public class BTDeviceList extends ListActivity {

    static public final int REQUEST_CONNECT_BT = 0x2300;

    static private final int REQUEST_ENABLE_BT = 0x1000;

    static  private  final  int BOND_BONDED = 0x0000000c;

    static private BluetoothAdapter mBluetoothAdapter = null;

    static private ArrayAdapter<String> mArrayAdapter = null;

    static private ArrayAdapter<BluetoothDevice> btDevices = null;

    private static final UUID SPP_UUID = UUID
            .fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    // UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    static private BluetoothSocket mbtSocket = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Bluetooth Devices");
        Store.getInstance().bluetoothStatus = false;

        try {
            if (initDevicesList() != 0) {
                this.finish();
                return;
            }

        } catch (Exception ex) {
            this.finish();
            return;
        }



        IntentFilter btIntentFilter = new IntentFilter(
                BluetoothDevice.ACTION_FOUND);
        registerReceiver(mBTReceiver, btIntentFilter);
        mBluetoothAdapter.startDiscovery();
    }
    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        try{
            if(mBTReceiver!=null)
                unregisterReceiver(mBTReceiver);
        }catch(Exception e)
        {

        }


    }


    public static BluetoothSocket getSocket() {
        return mbtSocket;
    }

    private void flushData() {
        try {
            if (mbtSocket != null) {
                mbtSocket.close();
                mbtSocket = null;
            }

            if (mBluetoothAdapter != null) {
                mBluetoothAdapter.cancelDiscovery();
            }

            if (btDevices != null) {
                btDevices.clear();
                btDevices = null;
            }

            if (mArrayAdapter != null) {
                mArrayAdapter.clear();
                mArrayAdapter.notifyDataSetChanged();
                mArrayAdapter.notifyDataSetInvalidated();
                mArrayAdapter = null;
            }

            finalize();

        } catch (Exception ex) {
        } catch (Throwable e) {
        }

    }


    private int initDevicesList() {

        flushData();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(),
                    "Bluetooth not supported!!", Toast.LENGTH_LONG).show();
            return -1;
        }

        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }

        mArrayAdapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.bt_item_list);

        setListAdapter(mArrayAdapter);

        Intent enableBtIntent = new Intent(
                BluetoothAdapter.ACTION_REQUEST_ENABLE);
        try {
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } catch (Exception ex) {
            return -2;
        }

        Toast.makeText(getApplicationContext(),
                "Getting all available Bluetooth Devices", Toast.LENGTH_SHORT)
                .show();

        return 0;

    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent intent) {
        super.onActivityResult(reqCode, resultCode, intent);

        switch (reqCode) {
            case REQUEST_ENABLE_BT:

                if (resultCode == RESULT_OK) {
                    Set<BluetoothDevice> btDeviceList = mBluetoothAdapter.getBondedDevices();
                    try {
                        if (btDeviceList.size() > 0) {

                            for (BluetoothDevice device : btDeviceList) {
                                if (btDeviceList.contains(device) == false) {

                                    btDevices.add(device);

                                    System.out.println("Bluetooth devices = "+device.getName());

                                    mArrayAdapter.add(device.getName() + "\n"
                                            + device.getAddress());
                                    mArrayAdapter.notifyDataSetInvalidated();
                                }
                            }
                        }
                    } catch (Exception ex) {
                    }
                }

                break;
        }

        mBluetoothAdapter.startDiscovery();

    }

    private final BroadcastReceiver mBTReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                System.out.println("found some bluetooth devices ");
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                try {
                    if (btDevices == null) {
                        btDevices = new ArrayAdapter<BluetoothDevice>(getApplicationContext(),R.id.text1);
                    }

                    if (btDevices.getPosition(device) < 0) {
                        btDevices.add(device);
                        System.out.println("Bluetooth devices = "+device.getName());
                        mArrayAdapter.add(device.getName() + "\n"
                                + device.getAddress() + "\n" );
                        mArrayAdapter.notifyDataSetInvalidated();
                    }
                } catch (Exception ex) {
                    // ex.fillInStackTrace();
                }
            }
            else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
            //Device is now connected
                System.out.println("Device is now connected");
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            //Done searching
                System.out.println("Done searching");
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
            //Device is about to disconnect
                System.out.println("Device is about to disconnect");
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
            //Device has disconnected
                System.out.println("Device has disconnected");
            }
        }
    };

    @Override
    protected void onListItemClick(ListView l, View v, final int position,
                                   long id) {
        super.onListItemClick(l, v, position, id);

        try{

            if (mBluetoothAdapter == null) {
                return;
            }

            if (mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.cancelDiscovery();
            }

            Toast.makeText(
                    getApplicationContext(),
                    "Connecting to " + btDevices.getItem(position).getName() + ","
                            + btDevices.getItem(position).getAddress(),
                    Toast.LENGTH_SHORT).show();

            final BluetoothDevice btDevice = btDevices.getItem(position);
            if(btDevice.getBondState()!=BOND_BONDED) BT_Connect(btDevice);

            final Thread connectThread = new Thread(new Runnable() {

                @Override
                public void run() {

                    try {
                        while (btDevice.getBondState()!=BOND_BONDED);

                        int i=0;
                        do{
                            i=i+1;
                            Thread.sleep(500);
                            try{
                                System.out.println("Connected to bluetooth device 1");
                                boolean gotuuid = btDevice.fetchUuidsWithSdp();
                                UUID uuid = btDevice.getUuids()[0]
                                        .getUuid();
                                mbtSocket = btDevice.createRfcommSocketToServiceRecord(uuid);

                                mbtSocket.connect();
                                System.out.println("Connected to bluetooth device 2");

                                Store.getInstance().bluetoothStatus = true;
                                break;

                            }catch (Exception ex){msg(ex.toString());}

                        }while (!mbtSocket.isConnected() && i<=10);

                        System.out.println("called finish");
                        finish();

                    } catch (Exception ex) {
                        runOnUiThread(socketErrorRunnable);
                        try {
                            mbtSocket.close();
                        } catch (IOException e) {
                            // e.printStackTrace();
                        }
                        mbtSocket = null;
                        return;
                    }
                }
            });

            connectThread.start();


        }catch (Exception e){msg(e.toString());}
    }

    public boolean removeBond(BluetoothDevice btDevice)
            throws Exception
    {
        Class btClass = Class.forName("android.bluetooth.BluetoothDevice");
        Method removeBondMethod = btClass.getMethod("removeBond");
        Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }
    private void BT_Connect(final BluetoothDevice bdDevice) {
        Boolean bool = false;
        try {
            msg(" service method is called ");
            Class cl = Class.forName("android.bluetooth.BluetoothDevice");
            Class[] par = {};
            Method method = cl.getMethod("createBond", par);
            Object[] args = {};
            bool = (Boolean) method.invoke(bdDevice);//, args);// this invoke creates the detected devices paired.
            Log.i("Log", "This is: "+bool.booleanValue());
            Log.i("Log", "devicesss: "+bdDevice.getName());
        } catch (Exception e) {
            msg("Inside catch of serviceFromDevice Method");
            e.printStackTrace();
        }



        if (bool.booleanValue()==false ){
            try {
                removeBond(bdDevice);
            } catch (Exception e) {
            }
            BT_ConnetConformation("Could not Connect this device. can re-pair?",bdDevice);
        }
        else{

        }


    };



    private void msg(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    private Runnable socketErrorRunnable = new Runnable() {

        @Override
        public void run() {
            Toast.makeText(getApplicationContext(),
                    "Cannot establish connection", Toast.LENGTH_SHORT).show();
            mBluetoothAdapter.startDiscovery();

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0, Menu.FIRST, Menu.NONE, "Refresh Scanning");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case Menu.FIRST:
                initDevicesList();
                break;
        }

        return true;
    }
    void BT_ConnetConformation(String AlertMessage, final BluetoothDevice bdDevice ){
        try{

            AlertDialog.Builder adb = new AlertDialog.Builder(BTDeviceList.this);
            adb.setTitle(getTitle());
            adb.setIcon(R.drawable.logo_one);
            adb.setMessage(AlertMessage);
            adb.setPositiveButton(R.string.alert_ButtonRetry, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    BT_Connect(bdDevice);
                }
            });
            adb.setNegativeButton(R.string.alert_ButtonCancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    System.out.println("called finish");
                    finish();
                }
            });
            AlertDialog ad = adb.create();
            ad.setIcon(R.drawable.logo_one);
            ad.show();

        }catch (Exception ex){}

    }
}
