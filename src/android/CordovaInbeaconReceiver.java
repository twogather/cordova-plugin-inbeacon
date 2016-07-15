package com.inbeacon.cordova;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class CordovaInbeaconReceiver extends BroadcastReceiver {

    public static final String TAG = "cordova-plugin-inbeacon";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "CordovaInbeaconReceiver says wake up!");
        CordovaInbeaconManager.wakeUp(context);
    }
}
