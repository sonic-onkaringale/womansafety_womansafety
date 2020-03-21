package com.one4ll.womansafety_womansafety;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class BluetoothDevicesListActivity extends AppCompatActivity {
    private static final String TAG = "BluetoothDevicesListAct";
    public static final String EXTRA_ADDRESS = "device address";
    private BluetoothAdapter bluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> arrayList;
    private String macAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_devices_list);
        listView = findViewById(R.id.list_view);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        arrayList = new ArrayList<>();
        if (bluetoothAdapter == null) {
            Log.d(TAG, "onCreate: bluetooth is not activate");
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent,1);
        } else {
            searchBluetoothDevice();
        }
        listView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String subString = macAddress.substring(macAddress.length() -17);
                Intent intent = new Intent(BluetoothDevicesListActivity.this,ArduinoBluetoothActivity.class);
                intent.putExtra(EXTRA_ADDRESS, subString);
            }
        });
    }

    private void searchBluetoothDevice() {
        pairedDevices = bluetoothAdapter.getBondedDevices();
//        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//        registerReceiver(broadcastReceiver, intentFilter);
        if (pairedDevices.size() > 0){
            for (BluetoothDevice bluetoothDevice : pairedDevices){

                arrayList.add("Name "+ bluetoothDevice.getName()+"\nMAC address " +bluetoothDevice.getAddress());
                macAddress = bluetoothDevice.getAddress();

            }
            arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,arrayList);
            listView.setAdapter(arrayAdapter);

        }else{
            Toast.makeText(this, "Please Pair with Arduino", Toast.LENGTH_LONG).show();
        }

    }
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "onReceive: ");
            if (BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d(TAG, "onReceive: bluetooth device name "+bluetoothDevice.getName());
                Log.d(TAG, "onReceive: bluetooth device address " + bluetoothDevice.getAddress());
            }
        }
    };

    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }
}
