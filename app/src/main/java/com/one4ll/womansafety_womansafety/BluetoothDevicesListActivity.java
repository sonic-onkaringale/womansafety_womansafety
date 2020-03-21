package com.one4ll.womansafety_womansafety;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.Set;

public class BluetoothDevicesListActivity extends AppCompatActivity {
    private static final String TAG = "BluetoothDevicesListAct";
    private BluetoothAdapter bluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_devices_list);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Log.d(TAG, "onCreate: bluetooth is not activate");
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent,1);
        } else {
            searchBluetoothDevice();
        }
    }

    private void searchBluetoothDevice() {
        pairedDevices = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice bluetoothDevice : pairedDevices) {
            Log.d(TAG, "onCreate: name " + bluetoothDevice.getName());
            Log.d(TAG, "onCreate:  address " + bluetoothDevice.getAddress());
        }
    }
}
