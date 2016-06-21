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

static NSString *const IO_KEY = @"io";
static NSString *const IO_IN = @"i";
static NSString *const IO_OUT = @"o";

@implementation CDVInBeacon

@synthesize listenerCallbackId;

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
	} :command :false];
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
			[self.inBeacon refresh];
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

- (void) checkCapabilitiesAndRights:(CDVInvokedUrlCommand *)command {
	[self _handleCallSafely:^CDVPluginResult *(CDVInvokedUrlCommand * command) {
		NSError* error;
		if(![[inBeaconSdk getInstance] checkCapabilitiesAndRights:&error]) {
			return [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:error.userInfo];
		} else {
			return [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
		}
	} :command];
}

- (void) checkCapabilitiesAndRightsWithAlert:(CDVInvokedUrlCommand *)command {
	[self _handleCallSafely:^CDVPluginResult *(CDVInvokedUrlCommand * command) {
		[[inBeaconSdk getInstance] checkCapabilitiesAndRightsWithAlert];
		
		return [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
	} :command :false];
}

- (void) getInRegions:(CDVInvokedUrlCommand *)command {
	[self _handleCallSafely:^CDVPluginResult *(CDVInvokedUrlCommand * command) {
		NSArray *currentRegions = [[inBeaconSdk getInstance]  getInRegions];
		
		return [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsArray:currentRegions];
	} :command];
}

- (void) getBeaconState:(CDVInvokedUrlCommand *)command {
	[self _handleCallSafely:^CDVPluginResult *(CDVInvokedUrlCommand * command) {
		NSArray *beaconState = [[inBeaconSdk getInstance] getBeaconState];
		
		return [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsArray:beaconState];
	} :command];
}

- (void) onNotification:(NSNotification *)notification {
	NSDictionary *primaryEvent = nil;
	NSDictionary *secondaryEvent = nil;

	
	if([notification.name isEqual: @"inb:region"]) {
	
		NSString* eventType = [notification.userInfo[IO_KEY]  isEqual: IO_IN] ? @"enterregion" : @"exitregion";
		primaryEvent = [NSDictionary dictionaryWithObjectsAndKeys:
						eventType, @"name",
						notification.userInfo, @"data",
						nil];
		
	} else if([notification.name  isEqual: @"inb:location"]){
		
		NSString* eventType = [notification.userInfo[IO_KEY]  isEqual: IO_IN] ? @"enterlocation" : @"exitlocation";
		primaryEvent = [NSDictionary dictionaryWithObjectsAndKeys:
						eventType, @"name",
						notification.userInfo, @"data",
						nil];
	
	} else if([notification.name  isEqual: @"inb:proximity"]){
	
		NSString* eventType = [notification.userInfo[IO_KEY]  isEqual: IO_IN] ? @"enterproximity" : @"exitproximity";
		primaryEvent = [NSDictionary dictionaryWithObjectsAndKeys:
						eventType, @"name",
						notification.userInfo, @"data",
						nil];
		secondaryEvent = [NSDictionary dictionaryWithObjectsAndKeys:
							@"proximity", @"name",
							notification.userInfo, @"data",
						nil];
	
	} else if([notification.name  isEqual: @"inb:locationsUpdate"]){
		
		primaryEvent = [NSDictionary dictionaryWithObjectsAndKeys:
						@"regionsupdate", @"name",
						notification.userInfo, @"data",
						nil];
		
	} else if([notification.name  isEqual: @"inb:AppEvent"]){
		
 		primaryEvent = [NSDictionary dictionaryWithObjectsAndKeys:
											@"appevent", @"name",
											notification.userInfo, @"data",
											nil];

	} else if([notification.name  isEqual: @"inb:AppAction"]){
		
		primaryEvent = [NSDictionary dictionaryWithObjectsAndKeys:
						@"appaction", @"name",
						notification.userInfo, @"data",
						nil];
		
	}

	if( primaryEvent != nil){
		[self notifyListener:primaryEvent];
	}
	if( secondaryEvent != nil){
		[self notifyListener:secondaryEvent];
	}
}

- (void) startListener:(CDVInvokedUrlCommand *)command {
	self.listenerCallbackId = command.callbackId;
	
	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onNotification:) name:@"inb:region" object:nil];
	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onNotification:) name:@"inb:location" object:nil];
	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onNotification:) name:@"inb:proximity" object:nil];
	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onNotification:) name:@"inb:locationsUpdate" object:nil];
	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onNotification:) name:@"inb:AppEvent" object:nil];
	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onNotification:) name:@"inb:AppAction" object:nil];
}

- (void) stopListener:(CDVInvokedUrlCommand *)command {
	if(self.listenerCallbackId){
		[self _handleCallSafely:^CDVPluginResult *(CDVInvokedUrlCommand * command) {
			
			CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];

			[result setKeepCallbackAsBool:NO];
			
			return result;
			
		} :command :true :self.listenerCallbackId];
	}
	
	self.listenerCallbackId = nil;
	
	[[NSNotificationCenter defaultCenter] removeObserver:self name:@"inb:region" object:nil];
	[[NSNotificationCenter defaultCenter] removeObserver:self name:@"inb:location" object:nil];
	[[NSNotificationCenter defaultCenter] removeObserver:self name:@"inb:proximity" object:nil];
	[[NSNotificationCenter defaultCenter] removeObserver:self name:@"inb:locationsUpdate" object:nil];
	[[NSNotificationCenter defaultCenter] removeObserver:self name:@"inb:AppEvent" object:nil];
	[[NSNotificationCenter defaultCenter] removeObserver:self name:@"inb:AppAction" object:nil];
}

- (void) notifyListener:(NSDictionary *)event{
	if(self.listenerCallbackId){
		[self _handleCallSafely:^CDVPluginResult *(CDVInvokedUrlCommand* command){
			CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:event];
			
			[result setKeepCallbackAsBool:YES];
		
			return result;
		} :nil :true :self.listenerCallbackId];
	}
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
    [self _handleCallSafely:unsafeHandler :command :runInBackground :command.callbackId];
    
}

- (void) _handleExceptionOfCommand: (CDVInvokedUrlCommand*) command : (NSException*) exception {
    NSLog(@"Uncaught exception: %@", exception.description);
    NSLog(@"Stack trace: %@", [exception callStackSymbols]);

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

- (void) dealloc {
	[self stopListener:nil];
}

@end
