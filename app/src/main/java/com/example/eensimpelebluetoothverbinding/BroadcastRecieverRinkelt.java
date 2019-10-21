package com.example.eensimpelebluetoothverbinding;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

//public class MyBroadcastReceiver extends BroadcastReceiver

public class BroadcastRecieverRinkelt extends BroadcastReceiver {
    private String TAG = "testflow2";
    public static int erIsOpgehangen=0;
    //private BroadcastReceiver mBroadcastReceiver6 = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            Toast.makeText(context, "Call from: " + incomingNumber, Toast.LENGTH_LONG).show();
            Log.d(TAG, "bellen actief");
            // bytes="r".getBytes(defaultCharset());
            // mBluetoothConnection.write("r".getBytes(defaultCharset()));
        } else if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_IDLE) || intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
            Toast.makeText(context, "Detectes call hangup event", Toast.LENGTH_LONG).show();
            erIsOpgehangen++;
            Log.d(TAG, "bellen is gestopt " + erIsOpgehangen);
        }
    }
}


