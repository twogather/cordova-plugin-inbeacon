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

#import <Cordova/CDV.h>
#import <Foundation/Foundation.h>
#import <inBeaconSdk/inBeaconSdk.h>

typedef CDVPluginResult* (^CDVPluginCommandHandler)(CDVInvokedUrlCommand*);


@interface CDVInBeacon : CDVPlugin {
	NSString* listenerCallbackId;
}

@property (retain) inBeaconSdk *inBeacon;
@property (strong) NSString* listenerCallbackId;

- (void)initialize:(CDVInvokedUrlCommand*)command;
- (void)refresh:(CDVInvokedUrlCommand*)command;
- (void)setLogLevel:(CDVInvokedUrlCommand*)command;

- (void)attachUser:(CDVInvokedUrlCommand*)command;
- (void)detachUser:(CDVInvokedUrlCommand*)command;

- (void)checkCapabilitiesAndRights:(CDVInvokedUrlCommand*)command;
- (void)checkCapabilitiesAndRightsWithAlert:(CDVInvokedUrlCommand*)command;

- (void)getInRegions:(CDVInvokedUrlCommand*)command;
- (void)getBeaconState:(CDVInvokedUrlCommand*)command;

- (void)startListener:(CDVInvokedUrlCommand*)command;
- (void)stopListener:(CDVInvokedUrlCommand*)command;
- (void)onNotification:(NSNotification*)notification;
- (void)notifyListener:(NSDictionary*)event;

-(void)dealloc;

@end

