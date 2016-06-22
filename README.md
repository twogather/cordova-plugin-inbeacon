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

 * setLoglevel
 * checkCapabilitiesAndRightsWithAlert
 * getInRegions
 * getBeaconState

### Installation

```
cordova plugin add https://github.com/twogather/cordova-plugin-inbeacon.git
```

#### Installing with InBeacon clientId and secret in advance. 

Using this method you don't have to initialize the InBeacon SDK, it will be done automatically when your app starts.

```
cordova plugin add https://github.com/twogather/cordova-plugin-inbeacon.git --variable INBEACON_CLIENTID="your-clientid" --variable INBEACON_SECRET="your-secret"
```


### Usage

#### initialize

To enable the features of the InBeacon SDK you need to initialize first.

```
var userInfo = {
    clientId : 'clientId obtained from InBeacon',
    secret   : 'secret obtained from InBeacon'
};

cordova.plugins.inBeacon.initialize(userInfo, function () {
        console.log('Succesfully initialized inBeacon API');
    }, function () {
        console.error('inBeacon initialization failed');
    });
```

> Note: You can skip this step when this plugins has been installed by the second method!

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

