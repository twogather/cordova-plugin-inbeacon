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

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

public class CordovaInbeaconManager extends CordovaPlugin {

    public static final String TAG = "com.inbeacon.cordova";

    public void pluginInitialize() {
        Activity activity = this.cordova.getActivity();
        ApplicationInfo appliInfo = null;

        try {
            appliInfo = activity.getPackageManager().getApplicationInfo(activity.getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {}

        // initialize with clientId and secret from plugin parameters.
        String clientId = appliInfo.metaData.getString("com.inbeacon.android.CLIENTID");
        String clientSecret = appliInfo.metaData.getString("com.inbeacon.android.SECRET");

        if (clientId != null && clientSecret != null) {
            InbeaconManager.initialize(activity.getApplicationContext(), clientId, clientSecret);
            InbeaconManager.getSharedInstance().askPermissions(activity);
        }

//        InbeaconManager.getSharedInstance().refresh();
    }

    /**
     * The final call you receive before your activity is destroyed.
     */
    public void onDestroy() {
//        InBeaconManager.unbindService(this);

//        if (broadcastReceiver != null) {
//            cordova.getActivity().unregisterReceiver(broadcastReceiver);
//            broadcastReceiver = null;
//        }

        super.onDestroy();
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
        InbeaconManager.getSharedInstance().askPermissions(this.cordova.getActivity());
        callbackContext.success();
    }

    private void attachUser(JSONArray args, CallbackContext callbackContext) throws JSONException {
        JSONObject kwargs = args.getJSONObject(0);

        // kwargs keys can be:
        // name, email, customerid, address, gender, zip, city, country,  birth, phone_mobile,
        // phone_home, phone_work, social_facebook_id, social_twitter_id, social_linkedin_id
        HashMap<String, String> user = new HashMap<String, String>();
        for (Iterator<String> iter = kwargs.keys(); iter.hasNext();) {
            String key = iter.next();
            user.put(key, kwargs.getString(key));
        }

        InbeaconManager.getSharedInstance().attachUser(user);
        callbackContext.success();
    }

    private void detachUser(CallbackContext callbackContext) {
        InbeaconManager.getSharedInstance().detachUser();
        callbackContext.success();
    }

    private void refresh(CallbackContext callbackContext) {
        InbeaconManager.getSharedInstance().refresh();
        callbackContext.success();
    }

    private void verifyCapabilities(CallbackContext callbackContext) {
        VerifiedCapability verifiedCaps = InbeaconManager.getSharedInstance().verifyCapabilities();

        if (verifiedCaps != VerifiedCapability.CAP_OK) {
            switch (verifiedCaps) {
                case CAP_BLUETOOTH_DISABLED:
                    callbackContext.error("This device has bluetooth turned off");
                    break;
                case CAP_BLUETOOTH_LE_NOT_AVAILABLE:
                    callbackContext.error("This device does not support bluetooth LE needed for iBeacons");
                    break;
                case CAP_SDK_TOO_OLD:
                    callbackContext.error("This device SDK is too old");
                    break;
                default:
                    callbackContext.error("This device does not support inBeacon for an unknown reason");
                    break;
            }
        } else {
            callbackContext.success("This device supports iBeacons and the inBeacon SDK");
        }
    }

    private void askPermission(CallbackContext callbackContext) {
        InbeaconManager.getSharedInstance().askPermissions(this.cordova.getActivity());
        callbackContext.success();
    }

    //////////////// EVENT HANDLING /////////////////////////////////

    // TODO
}
