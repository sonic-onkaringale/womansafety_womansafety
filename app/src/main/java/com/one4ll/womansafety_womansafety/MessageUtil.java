package com.one4ll.womansafety_womansafety;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.SmsManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MessageUtil {
    public static void sendEmergencyMessage(Context context, String message, String phoneNumber,int requestCode) {
        SmsManager smsManager = SmsManager.getDefault();
        if (Build.VERSION.SDK_INT >= 22) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                //TODO
            } else {
                smsManager.sendTextMessage(phoneNumber, null, message, null, null);

            }
        }
    }
}
