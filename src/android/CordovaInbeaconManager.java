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
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class CordovaInbeaconManager extends CordovaPlugin {

    public static final String TAG = "cordova-plugin-inbeacon";
    private BroadcastReceiver broadcastReceiver;
    private CallbackContext eventCallbackContext;

    /**
     * Constructor
     */
    public CordovaInbeaconManager() {
        broadcastReceiver = null;
    }

    /**
     *  Initialize inBeaconManager if 'clientId' and 'secret' plugin parameters are set.
     */
    public void pluginInitialize() {
        Activity activity = cordova.getActivity();
        ApplicationInfo appliInfo = null;
        try {
            appliInfo = activity.getPackageManager().getApplicationInfo(activity.getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        String clientId = appliInfo.metaData.getString("com.inbeacon.android.CLIENTID");
        String clientSecret = appliInfo.metaData.getString("com.inbeacon.android.SECRET");

        if (clientId != null && clientSecret != null) {
            initInbeaconManager(cordova.getActivity().getApplicationContext(), clientId, clientSecret);
            initEventListener();
        }
    }

    /**
     * The final call you receive before your activity is destroyed.
     */
    public void onDestroy() {
        if (broadcastReceiver != null) {
            cordova.getActivity().unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }

        // release events in JS side
        if (eventCallbackContext != null) {
            PluginResult result = new PluginResult(PluginResult.Status.OK);
            result.setKeepCallback(false);
            eventCallbackContext.sendPluginResult(result);
            eventCallbackContext = null;
        }

        super.onDestroy();
    }

    public void onReset() {}


    //////////////// PLUGIN ENTRY POINT //////////////////////////////////////

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
            initialize(args.optJSONObject(0), callbackContext);
        } else if ("attachUser".equals(action)) {
            attachUser(args.optJSONObject(0), callbackContext);
        } else if ("detachUser".equals(action)) {
            detachUser(callbackContext);
        } else if ("refresh".equals(action)) {
            refresh(callbackContext);
        } else if ("verifyCapabilities".equals(action)) {
            verifyCapabilities(callbackContext);
        } else if ("askPermissions".equals(action)) {
            askPermission(callbackContext);
        } else if ("startListener".equals(action)) {
            registerEventCallback(callbackContext);
        } else if ("stopListener".equals(action)) {
            unregisterEventCallback(callbackContext);
        } else {
            return false;
        }
        return true;
    }

    //////////////// INBEACON SDK METHODS ////////////////////////////////////

    private void initialize(JSONObject kwargs, CallbackContext callbackContext) throws JSONException {
        if (InbeaconManager.getSharedInstance() != null) {
            callbackContext.error("InBeaconManager is already initialized");
            return;
        }
        String clientId = kwargs.getString("clientId");
        String clientSecret = kwargs.getString("clientSecret");
        Context context = cordova.getActivity().getApplicationContext();

        initInbeaconManager(context, clientId, clientSecret);
        initEventListener();

        callbackContext.success();
    }

    private void attachUser(JSONObject kwargs, CallbackContext callbackContext) throws JSONException {
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
        InbeaconManager.getSharedInstance().askPermissions(cordova.getActivity());
        callbackContext.success();
    }

    //////////////// CORDOVA FUNCTIONS ///////////////////////////////////////

    // broadcastReceiver uses this.eventCallbackContext to pass on events to JavaScript
    private void registerEventCallback(CallbackContext callbackContext) {
        if (this.eventCallbackContext != null) {
            callbackContext.error("Inbeacon event listener is already running");
            return;
        }

        this.eventCallbackContext = callbackContext;
        sendNoResult();
    }

    private void unregisterEventCallback(CallbackContext callbackContext) {
        this.eventCallbackContext = null;
        callbackContext.success();
    }

    private void initInbeaconManager(Context context, String clientId, String clientSecret) {
        InbeaconManager.initialize(context, clientId, clientSecret);
        InbeaconManager.getSharedInstance().askPermissions(cordova.getActivity());
    }

    private void initEventListener() {

        if (broadcastReceiver != null) {
            // "Already listening, not adding again"
            return;
        }

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                sendUpdate(getEventObject(intent));
            }
        };

        // get all notifications from the inBeacon SDK to our broadcastReceiver
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.inbeacon.sdk.event.enterregion");     // user entered a region
        filter.addAction("com.inbeacon.sdk.event.exitregion");      // user left a region
        filter.addAction("com.inbeacon.sdk.event.enterlocation");   // user entered a location
        filter.addAction("com.inbeacon.sdk.event.exitlocation");    // user left a location
        filter.addAction("com.inbeacon.sdk.event.regionsupdate");   // region definitions were updated
        filter.addAction("com.inbeacon.sdk.event.enterproximity");  // user entered a beacon proximity
        filter.addAction("com.inbeacon.sdk.event.exitproximity");   // user left a beacon proximity
        filter.addAction("com.inbeacon.sdk.event.proximity");       // low level proximity update, once every second when beacons are around
        filter.addAction("com.inbeacon.sdk.event.appevent");        // defined in the backend for special app-specific pages to show
        filter.addAction("com.inbeacon.sdk.event.appaction");       // defined in the backend to handle your own local notifications

        LocalBroadcastManager
                .getInstance(cordova.getActivity().getApplicationContext())
                .registerReceiver(broadcastReceiver, filter);

    }

    /**
     * Transform Android event data into JSON Object used by JavaScript
     * @param intent inBeacon SDK event data
     * @return a JSONObject with keys 'event', 'name' and 'data'
     */
    private JSONObject getEventObject(Intent intent) {
        String action  = intent.getAction();
        String event   = action.substring(action.lastIndexOf(".")+1); // last part of action
        String message = intent.getStringExtra("message");
        Bundle extras  = intent.getExtras();

        JSONObject eventObject = new JSONObject();
        JSONObject data = new JSONObject();

        try {
            if (extras != null) {
                Set<String> keys = extras.keySet();
                for (String key : keys) {
                    data.put(key, extras.get(key));                     // Android API < 19
//                    data.put(key, JSONObject.wrap(extras.get(key)));    // Android API >= 19
                }
            }
            data.put("message", message);

            eventObject.put("event", event);
            eventObject.put("name", event);
            eventObject.put("data", data);

        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return eventObject;
    }

    /**
     * Send a new plugin result back to JavaScript, without closing callback
     *
     * @param data InBeacon event result containing message and extras
     */
    private void sendUpdate(JSONObject data) {
        if (this.eventCallbackContext != null) {
            PluginResult result = new PluginResult(PluginResult.Status.OK, data);
            result.setKeepCallback(true);
            this.eventCallbackContext.sendPluginResult(result);
        }
    }

    /**
     * Send a new plugin result with no result. Use to keep callback channel open for events
     */
    private void sendNoResult() {
        if (this.eventCallbackContext != null) {
            PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
            pluginResult.setKeepCallback(true);
            this.eventCallbackContext.sendPluginResult(pluginResult);
        }
    }

}
