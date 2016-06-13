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

#import "CDVInBeacon.h"
#import <inBeaconSdk/inBeaconSdk.h>
#import <AdSupport/ASIdentifierManager.h>

@implementation CDVInBeacon {

}

# pragma mark CDVPlugin

- (void)pluginInitialize
{
    [inBeaconSdk getInstance];
}

# pragma mark - InBeaconSdk calls
- (void) initialize:(CDVInvokedUrlCommand*)command{ 
    [self _handleCallSafely:^CDVPluginResult *(CDVInvokedUrlCommand * command) {
		
		NSDictionary* kwargs = command.arguments[0];
		NSString* clientId = [kwargs objectForKey:@"clientId"];
		NSString* secret = [kwargs objectForKey:@"secret"];
		if (clientId != nil && secret != nil) {
			self.inBeacon = [inBeaconSdk inbeaconWithClientID: clientId andClientSecret: secret];
			
			return [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
		} else {
			return [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
		}
    } :command];
}

- (void) refresh:(CDVInvokedUrlCommand *)command {
	[self _handleCallSafely:^CDVPluginResult *(CDVInvokedUrlCommand * command) {
		
		[self.inBeacon refresh];
		
		return [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
	} :command];
}

- (void) setLogLevel:(CDVInvokedUrlCommand *)command {
	[self _handleCallSafely:^CDVPluginResult *(CDVInvokedUrlCommand * command) {
		
		int level = (int) [command.arguments objectAtIndex:0];
		[self.inBeacon setLogLevel:level];
		
		return [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
	} :command];

}

- (void) attachUser:(CDVInvokedUrlCommand *)command {
	[self _handleCallSafely:^CDVPluginResult *(CDVInvokedUrlCommand * command) {
	
		NSDictionary* userInfo = command.arguments[0];
		if([userInfo count] > 0){
			[self.inBeacon attachUser:userInfo];
		}
		
		return [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
	} :command];
}

- (void) detachUser:(CDVInvokedUrlCommand *)command {
	[self _handleCallSafely:^CDVPluginResult *(CDVInvokedUrlCommand * command) {
		
		[self.inBeacon detachUser];
		
		return [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
	} :command];
	
}



#pragma mark Utilities

- (NSError*) errorWithCode: (int)code andDescription:(NSString*) description {

    NSMutableDictionary* details;
    if (description != nil) {
        details = [NSMutableDictionary dictionary];
        [details setValue:description forKey:NSLocalizedDescriptionKey];
    }
    
    return [[NSError alloc] initWithDomain:@"CDVInBeacon" code:code userInfo:details];
}

- (void) _handleCallSafely: (CDVPluginCommandHandler) unsafeHandler : (CDVInvokedUrlCommand*) command  {
    [self _handleCallSafely:unsafeHandler :command :true];
}

- (void) _handleCallSafely: (CDVPluginCommandHandler) unsafeHandler : (CDVInvokedUrlCommand*) command : (BOOL) runInBackground :(NSString*) callbackId {
    if (runInBackground) {
        [self.commandDelegate runInBackground:^{
            @try {
                [self.commandDelegate sendPluginResult:unsafeHandler(command) callbackId:callbackId];
            }
            @catch (NSException * exception) {
                [self _handleExceptionOfCommand:command :exception];
            }
        }];
    } else {
        @try {
            [self.commandDelegate sendPluginResult:unsafeHandler(command) callbackId:callbackId];
        }
        @catch (NSException * exception) {
            [self _handleExceptionOfCommand:command :exception];
        }
    }
}

- (void) _handleCallSafely: (CDVPluginCommandHandler) unsafeHandler : (CDVInvokedUrlCommand*) command : (BOOL) runInBackground {
    [self _handleCallSafely:unsafeHandler :command :true :command.callbackId];
    
}

- (void) _handleExceptionOfCommand: (CDVInvokedUrlCommand*) command : (NSException*) exception {
    NSLog(@"Uncaught exception: %@", exception.description);
    NSLog(@"Stack trace: %@", [exception callStackSymbols]);

    // When calling without a request (LocationManagerDelegate callbacks) from the client side the command can be null.
    if (command == nil) {
        return;
    }
    CDVPluginResult* pluginResult = nil;
    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:exception.description];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (BOOL) isBelowIos7 {
    return [[[UIDevice currentDevice] systemVersion] floatValue] < 7.0;
}

@end
