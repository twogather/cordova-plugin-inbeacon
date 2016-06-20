/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
*/
package com.inbeacon.cordova;

import com.inbeacon.sdk.InbeaconManager;
import com.inbeacon.sdk.VerifiedCapability;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CordovaInbeaconManager extends CordovaPlugin {

    public static final String TAG = "com.inbeacon.cordova";
    private Activity activity = null;
    private Context context = null;

    public void pluginInitialize() {
        ApplicationInfo appliInfo = null;
        this.activity = this.cordova.getActivity();
        this.context = this.activity.getApplicationContext();

        try {
            appliInfo = this.activity.getPackageManager().getApplicationInfo(activity.getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {}

        // initialize with clientId and secret from plugin parameters.
        String clientId = appliInfo.metaData.getString("com.inbeacon.android.CLIENTID");
        String clientSecret = appliInfo.metaData.getString("com.inbeacon.android.SECRET");

        if (clientId != null && clientSecret != null) {
            InbeaconManager.initialize(context, clientId, clientSecret);
        }
        //
        // If you have user credentials somewhere in your app, you can attach the account
        //        HashMap<String, String> user=new HashMap<String, String>();
        //        user.put("name","Dwight Schulz");                 // example only! don't use these in your own app
        //        user.put("email","dwight@ateam.com");
        //        InbeaconManager.getSharedInstance().attachUser(user);

        // refresh data from server. Call this after attachuser, so everything is updated.
        InbeaconManager.getSharedInstance().refresh();
    }

    /**
     * The final call you receive before your activity is destroyed.
     */
    public void onDestroy() {
        /*
        InBeaconManager.unbindService(this);

        if (broadcastReceiver != null) {
            cordova.getActivity().unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }

        super.onDestroy();
        */
    }

    public void onReset() {}


    //////////////// PLUGIN ENTRY POINT /////////////////////////////

    /**
     * Executes the request and returns PluginResult.
     *
     * @param action          The action to execute.
     * @param args            JSONArray of arguments for the plugin.
     * @param callbackContext The callback id used when calling back into JavaScript.
     * @return                True if the action was valid, false if not.
     */
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if ("initialize".equals(action)) {
            initialize(args, callbackContext);
        } else if ("attachUser".equals(action)) {
            attachUser(args, callbackContext);
        } else if ("detachUser".equals(action)) {
            detachUser(callbackContext);
        } else if ("refresh".equals(action)) {
            refresh(callbackContext);
        } else if ("verifyCapabilities".equals(action)) {
            verifyCapabilities(callbackContext);
        } else if ("askPermissions".equals(action)) {
            askPermission(callbackContext);
        } else {
            return false;
        }
        return true;
    }

    private void initialize(JSONArray args, CallbackContext callbackContext) throws JSONException {
        JSONObject kwargs = args.getJSONObject(0);
        String clientId = kwargs.getString("clientId");
        String clientSecret = kwargs.getString("clientSecret");
        Context context = this.cordova.getActivity().getApplicationContext();
        InbeaconManager.initialize(context, clientId, clientSecret);
        callbackContext.success();
    }

    private void attachUser(JSONArray args, CallbackContext callbackContext) { }

    private void detachUser(CallbackContext callbackContext) { }

    private void refresh(CallbackContext callbackContext) { }

    private void verifyCapabilities(CallbackContext callbackContext) { }

    private void askPermission(CallbackContext callbackContext) { }

}
