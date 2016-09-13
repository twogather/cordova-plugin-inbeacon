<!---
 license: Licensed to the Apache Software Foundation (ASF) under one
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
-->


## ![inBeacon Cordova Plugin](http://snqpo25gig94mv8mcrpilul6.wpengine.netdna-cdn.com/wp-content/uploads/2016/02/inbeacon-dark-retina.png) Cordova inBeacon plugin

This plugin enables the InBeacon API for Cordova based apps on iOS and Android.

### Features

#### Features available on both Android and iOS

##### API calls

 * initialize
 * refresh
 * attachUser
 * detachUser
 * checkCapabilitiesAndRights

##### API events

 * inbeacon.enterregion
 * inbeacon.exitregion
 * inbeacon.enterlocation
 * inbeacon.exitlocation
 * inbeacon.regionsupdate
 * inbeacon.enterproximity
 * inbeacon.exitproximity
 * inbeacon.proximity
 * inbeacon.appevent
 * inbeacon.appaction

#### Features exclusive to iOS

##### API calls

 * setLogLevel
 * checkCapabilitiesAndRightsWithAlert
 * getInRegions
 * getBeaconState

#### Feature exclusive to Android

##### API call

 * askPermissions


#### Installing with InBeacon clientId and secret

Make sure you have your inbeacon client-id and secret ready, you can supply these when installing the plugin

```
cordova plugin add https://github.com/inbeacon/cordova-plugin-inbeacon.git --variable INBEACON_CLIENTID="your-clientid" --variable INBEACON_SECRET="your-secret"
```

You can also enter the variables in the fetch.json file in the plugins directory later:
```
        "variables": {
            "INBEACON_CLIENTID": "your client-id",
            "INBEACON_SECRET": "your client-secret"
        }
```

### Usage

#### initialize

The sdk is automatically initialized when the plugin is installed with the correct client-id and client-secret.

#### refresh

```
cordova.plugins.inBeacon.refresh(function(){
        console.log('refresh done!');
    }, function () {
        console.error('refresh failed');
    });
```

#### attachUser

Attach local userinformation to inbeacon. For instance, the user might enter account information in the app. It is also possible not to attach a user, in that case the device is anonymous.

```
var userInfo = {
    name  : 'Dwight Schultz',
    email : 'dwight@ateam.com'
};
cordova.plugins.inBeacon.attachUser(userInfo, function () {
        console.log('Succesfully attached user');
    }, function () {
        console.error('Failed to attach user');
    });
```

#### events

To handle an InBeacon event just add a new event listener.

```
document.addEventListener('inbeacon.enterregion', handleEnterRegion, false);

function handleEnterRegion(event){
    console.log('Event name:' + event.name);
    console.log('Event data:' + JSON.stringify(event.data));
}
```

#### Minimal application

For the most simple implementation, in index.js just do a refresh in onDeviceReady of your app object, like this:
```
onDeviceReady: function() {
	app.receivedEvent('deviceready');
	cordova.plugins.inBeacon.refresh(function(){
		console.log('refresh done!');
	}, function () {
		console.error('refresh failed');
	});
},
```
