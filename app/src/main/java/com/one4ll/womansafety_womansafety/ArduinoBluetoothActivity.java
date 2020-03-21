package com.one4ll.womansafety_womansafety;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

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
            //send message
            if (bluetoothSocket != null) {
                int number = bluetoothSocket.getInputStream().read();
                if (number == 1) {
                    //TODO SEND MESSAGE TO EMERGENCY CONTACT
                    sendAlertMessageToUser();
                }


            } else {
                // WAIT FOR THE 1 😁
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendAlertMessageToUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ArduinoBluetoothActivity.this);
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences sharedPreferences = getSharedPreferences("storage", MODE_PRIVATE);
                String number = sharedPreferences.getString("number", "0");
                Log.d(TAG, "onClick: " + number);
                String message = sharedPreferences.getString("message", "0");
                //Sends message to Emergency contact
                MessageUtil.sendEmergencyMessage(message, number);
            }
        }).setNegativeButton("No ,it's ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(ArduinoBluetoothActivity.this, "We more care about your health", Toast.LENGTH_LONG).show();
            }
        }).setTitle("Are you Danger").show();
    }

    private class ConnectBluetooth extends AsyncTask<Void, Void, Void> {
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
            return null;


        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (!connectionSuccess) {
                Log.d(TAG, "onPostExecute: failed to connect");
            } else {
                Log.d(TAG, "onPostExecute: success");
            }

        }
    }
}
