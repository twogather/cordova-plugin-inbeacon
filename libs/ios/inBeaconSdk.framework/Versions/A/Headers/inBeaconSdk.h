//
//  inBeaconSdk.h
//  inBeaconSdk
//
//  * @author Ronald van Woensel
//  * @author Zarko Kiroski
//  * @author Goran Dimitrovski
//
//  Copyright (c) 2014 inBeacon. All rights reserved.
//
#import <Foundation/Foundation.h>
@import UIKit;




@interface inBeaconSdk : NSObject

// singleton factory
+(inBeaconSdk *) getInstance;
+(inBeaconSdk *) inbeaconWithClientID: (NSString *)clientId andClientSecret: (NSString *) clientSecret;

-(inBeaconSdk *)initWithClientID: (NSString *)clientId andClientSecret: (NSString *) clientSecret;
-(void) setClientID: (NSString *) clientId andClientSecret: (NSString *) clientSecret;
-(void) refreshWithForce: (BOOL) force;
-(void) refresh;
-(void) setLogLevel: (int) level;   // 0=off
-(void) setApiEndpointBeta: (BOOL) doBeta;  // internal use only
-(void) authorisationAutoEnhanceMode: (BOOL) autoEnhance; // internal use only

-(BOOL) checkCapabilitiesAndRights: (NSError **) pError;
-(void) checkCapabilitiesAndRightsWithAlert;

-(void) attachUser: (NSDictionary *) userinfo;
-(void) detachUser;

- (NSArray *) getInRegions;
- (NSArray *) getBeaconState;
- (NSDictionary *) getDeviceInfo;

- (BOOL)didReceiveLocalNotification:(UILocalNotification *)notification;

- (void)modalclickWithClosebar: (BOOL) doclosebar;  // for internal testing only
@end

