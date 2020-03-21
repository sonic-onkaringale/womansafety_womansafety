package com.one4ll.womansafety_womansafety;

import android.telephony.SmsManager;

public class MessageUtil {
    public static   void sendEmergencyMessage(String message,String phoneNumber){
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber,null, message, null,null);
    }
}
