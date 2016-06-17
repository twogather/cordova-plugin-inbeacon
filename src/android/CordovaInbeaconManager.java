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

// TODO connect & translate InbeaconManager!!
import com.inbeacon.sdk.InbeaconManager;
import com.inbeacon.sdk.VerifiedCapability;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;

public class CordovaInbeaconManager extends CordovaPlugin {

    public static final String TAG = "com.inbeacon.cordova";
    public static final String clientId = "123";
    public static final String clientSecret = "3456789";

    /**
     * Constructor.
     */
    public CordovaInbeaconManager() {
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
        if (action.equals("initialize")) {
            initialize(args, callbackContext);
        } else if (action.equals("attachUser")) {
            attachUser(args, callbackContext);
        } else if (action.equals("detachUser")) {
            detachUser(callbackContext);
        } else if (action.equals("refresh")) {
            refresh(callbackContext);
        } else if (action.equals("verifyCapabilities")) {
            verifyCapabilities(callbackContext);
        } else if (action.equals("askPermissions")) {
            askPermission(callbackContext);
        } else {
            return false;
        }
        return true;
    }

    private void initialize(JSONArray args, CallbackContext callbackContext) {
        String clientId = args.getString(0);
        String clientSecret = args.getString(1);
        InbeaconManager.initialize(callbackContext, clientId, clientSecret);
//        callbackContext.success();
    }

    private void attachUser(JSONArray args, CallbackContext callbackContext) { }

    private void detachUser(CallbackContext callbackContext) { }

    private void refresh(CallbackContext callbackContext) { }

    private void verifyCapabilities(CallbackContext callbackContext) { }

    private void askPermission(CallbackContext callbackContext) { }

}
