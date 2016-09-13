package com.inbeacon.cordova;


import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.inbeacon.sdk.InbeaconManager;
/**
 * Main application need to start the inbeacon manager instance
 */
public class CordovaInbeaconApplication extends Application {
    private static final String TAG = "CordovaInbeaconApplication";
    @Override
    public void onCreate() {
        super.onCreate();
        ApplicationInfo appliInfo = null;
        super.onCreate();
        try {
            appliInfo = this.getPackageManager().getApplicationInfo(this.getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        // get info from manifest, which is generated from fetch.json variables
        String clientId = appliInfo.metaData.getString("com.inbeacon.android.CLIENTID");
        String clientSecret = appliInfo.metaData.getString("com.inbeacon.android.SECRET");

        if (clientId == null     || clientId.equals("your-clientid") ||
                clientSecret == null || clientSecret.equals("your-secret"))
        {
            Log.e(TAG, "Invalid clientId and/or clientSecret !!!!!! YOU NEED TO SET INBEACON CREDENTIALS in your plugin variables (fetch.json)");
            return;
        }
        // initialize the shared instance on application level
        InbeaconManager.initialize(this, clientId, clientSecret);
    }
}
