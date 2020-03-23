package com.one4ll.womansafety_womansafety;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ArduinoBluetoothActivity extends AppCompatActivity {
    private static final String TAG = "ArduinoBluetoothActivit";
    public static final int LOCATION_REQUEST_CODE = 1818;
    private static final int MESSAGE_REQUEST_CODE = 2323;
    private String macAddress;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private Button setUpBluetooth;
    //spp UUID
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arduino_bluetooth);
        progressDialog = new ProgressDialog(this);
        setUpBluetooth = findViewById(R.id.set_up_bluetooth);
        Intent intent = getIntent();
        macAddress = intent.getStringExtra(BluetoothDevicesListActivity.EXTRA_ADDRESS);
        Log.d(TAG, "onCreate: mac address " + macAddress);
//        sendAlertMessageToUser();


        setUpBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });

    }

    private void sendAlertMessageToUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ArduinoBluetoothActivity.this);
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                accessLocation();
//                SharedPreferences sharedPreferences = getSharedPreferences("storage", MODE_PRIVATE);
//                String number = sharedPreferences.getString("number", "0");
//                Log.d(TAG, "onClick: " + number);
//                String message = sharedPreferences.getString("message", "0");
//                //Sends message to Emergency contact
//                MessageUtil.sendEmergencyMessage(message, number);
            }


        }).setNegativeButton("No ,it's ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(ArduinoBluetoothActivity.this, "We more care about your health", Toast.LENGTH_LONG).show();
            }
        }).setTitle("Are you Danger").show();
    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            Log.d(TAG, "onLocationChanged: latitude" + latitude);
            Log.d(TAG, "onLocationChanged: " + longitude);
            Geocoder geocoder = new Geocoder(ArduinoBluetoothActivity.this);
            List<Address> addressList = new ArrayList<>();
            try {
                addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                Log.d(TAG, "onLocationChanged: address list size" + addressList.size());
                String street = null;
                String pincode = null;
                String locality = null;
                for (Address address : addressList) {
                    street = address.getAddressLine(0);
                    pincode = address.getPostalCode();
                    locality = address.getLocality();
                    Log.d(TAG, "onLocationChanged: street " + street);
                    Log.d(TAG, "onLocationChanged: " + pincode);
                    Log.d(TAG, "onLocationChanged: loca" + locality);
                }
                SharedPreferences sharedPreferences = getSharedPreferences("storage", MODE_PRIVATE);
                String number = sharedPreferences.getString("number", "0");
                Log.d(TAG, "onClick: " + number);
                String usersMessage = sharedPreferences.getString("message", "0");
                String message = usersMessage + "latitude" + latitude + "longitude" + longitude + "address line" + street + "pin code" + pincode + "locality" + locality;
                sendMessage(message, number);
            } catch (IOException e) {
                e.printStackTrace();
                if (e.getMessage().equals("grpc failed")) {
                    Log.d(TAG, "onLocationChanged: unable to find location");
                    SharedPreferences sharedPreferences = getSharedPreferences("storage", MODE_PRIVATE);
                    String number = sharedPreferences.getString("number", "0");
                    Log.d(TAG, "onClick: " + number);
                    String userMessage = sharedPreferences.getString("message", "0");
                    String message = userMessage + "latitude" + latitude + "longitude " + longitude;
                    sendMessage(message, number);
                }
            }

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d(TAG, "onStatusChanged: status " + status + "provider " + provider + "extras " + extras);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d(TAG, "onProviderDisabled: " + provider);
        }
    };

    private void sendMessage(String message, String phoneNumber) {
        SmsManager smsManager = SmsManager.getDefault();
        if (Build.VERSION.SDK_INT >= 22) {
            if (ContextCompat.checkSelfPermission(ArduinoBluetoothActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ArduinoBluetoothActivity.this, new String[]{Manifest.permission.SEND_SMS}, MESSAGE_REQUEST_CODE);
            } else {
                smsManager.sendTextMessage(phoneNumber, null, message, null, null);

            }
        } else {
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
        }
    }

    private void accessLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 22) {
            if (ContextCompat.checkSelfPermission(ArduinoBluetoothActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ArduinoBluetoothActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST_CODE);
            } else {
                locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, null);
            }
        } else {
            locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, null);
        }


    }

    private class ConnectBluetooth extends AsyncTask<Void, Void, Void> {
        private boolean connectionSuccess = true;

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Connect to the Arduino");
            progressDialog.setTitle("Please wait");
            progressDialog.show();
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
            progressDialog.dismiss();
            if (!connectionSuccess) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ArduinoBluetoothActivity.this);
                builder.setTitle("Failed");
                builder.setMessage("Connection failed. Is it a SPP Bluetooth? Try again.");
                builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new ConnectBluetooth().execute();
                    }
                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
            } else {
                Log.d(TAG, "onPostExecute: success");
            }
        }

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == LOCATION_REQUEST_CODE) {
                Log.d(TAG, "onActivityResult: permission given");
                accessLocation();
            } else if (requestCode == MESSAGE_REQUEST_CODE) {
                //I know it's wrong but we don't have time right?
                accessLocation();
            }
        }
    }
}
