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

### Features

#### Features available on both Android and iOS

 * Ranging
 * Monitoring
 
#### Features exclusive to iOS

 * Region Monitoring (or geo fencing), works in all app states. 
 * Advertising device as an iBeacon

### Installation

```
cordova plugin add https://github.com/twogather/cordova-plugin-ibeacon.git
```

### Usage

The plugin's API closely mimics the one exposed through the [CLLocationManager](https://developer.apple.com/library/ios/documentation/CoreLocation/Reference/CLLocationManager_Class/CLLocationManager/CLLocationManager.html) introduced in iOS 7.

Since version 2, the main ```IBeacon``` facade of the DOM is called ```LocationManager``` and it's API is based on promises instead of callbacks.
Another important change of version 2 is that it no longer pollutes the global namespace, instead all the model classes and utilities are accessible
through the ```cordova.plugins.locationManager``` reference chain.

Since version 3.2 the Klass dependency has been removed and therefore means creation of the delegate has changed.

#### iOS 8 Permissions

On iOS 8, you have to request permissions from the user of your app explicitly. You can do this through the plugin's API.
See the [LocationManager](https://github.com/petermetz/cordova-plugin-ibeacon/blob/master/www/LocationManager.js)'s 
related methods: ```requestWhenInUseAuthorization``` and ```requestAlwaysAuthorization``` for further details.

