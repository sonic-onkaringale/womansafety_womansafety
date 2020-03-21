package com.one4ll.womansafety_womansafety;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;

public class ArduinoBluetoothActivity extends AppCompatActivity {
    private String macAddress;
    private BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arduino_bluetooth);
        Intent intent = getIntent();
        macAddress = intent.getStringExtra(BluetoothDevicesListActivity.EXTRA_ADDRESS);


    }
}
