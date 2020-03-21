package com.one4ll.womansafety_womansafety;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

public class ArduinoBluetoothActivity extends AppCompatActivity {
    private static final String TAG = "ArduinoBluetoothActivit";
    private String macAddress;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arduino_bluetooth);
        Intent intent = getIntent();
        macAddress = intent.getStringExtra(BluetoothDevicesListActivity.EXTRA_ADDRESS);
        Log.d(TAG, "onCreate: mac address " + macAddress);
        new ConnectBluetooth().execute();
        try {
             int number = bluetoothSocket.getInputStream().read();
             if (number == 1){
                 //TODO SEND MESSAGE TO EMERGENCY CONTACT
             }else{
                 // WAIT FOR THE 1 😁
             }
            bluetoothSocket.getOutputStream().write("1".toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    private class ConnectBluetooth extends AsyncTask<Void,Void,Void>{
        private boolean connectionSuccess = true;
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(macAddress);
                bluetoothSocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(MY_UUID);
                bluetoothSocket.connect();

            } catch (IOException e) {
                connectionSuccess = false;
                e.printStackTrace();
            }


        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (!connectionSuccess){
                Log.d(TAG, "onPostExecute: failed to connect");
            }else {
                Log.d(TAG, "onPostExecute: success");
            }

        }
    }
}
