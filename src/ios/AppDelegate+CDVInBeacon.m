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

#import <inBeaconSDK/inBeaconSDK.h>
#import "AppDelegate+CDVInBeacon.h"
#import <objc/runtime.h>

@implementation AppDelegate (CDVInBeacon)


+ (void)load {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        
        Class class = [self class];

        SEL originalSelector = @selector(application:didFinishLaunchingWithOptions:);
        SEL swizzledSelector = @selector(xxx_application:didFinishLaunchingWithOptions:);
        
        Method originalMethod = class_getInstanceMethod(class, originalSelector);
        Method swizzledMethod = class_getInstanceMethod(class, swizzledSelector);
        
        BOOL didAddMethod = class_addMethod(class, originalSelector, method_getImplementation(swizzledMethod), method_getTypeEncoding(swizzledMethod));
        
        if (didAddMethod) {
            class_replaceMethod(class, swizzledSelector, method_getImplementation(originalMethod), method_getTypeEncoding(originalMethod));
        } else {
            method_exchangeImplementations(originalMethod, swizzledMethod);
        }
 
        SEL selectorForNotification = @selector(application:didReceiveLocalNotification:);
        SEL swizzledSelectorForNotification = @selector(xxx_application:didReceiveLocalNotification:);
        
        Method methodForNotification = class_getInstanceMethod(class, selectorForNotification);
        Method swizzledMethodForNotification = class_getInstanceMethod(class, swizzledSelectorForNotification);
        
        BOOL didAddMethodForNotification = class_addMethod(class, selectorForNotification, method_getImplementation(swizzledMethodForNotification), method_getTypeEncoding(swizzledMethodForNotification));
        
        if (didAddMethodForNotification) {
            class_replaceMethod(class, swizzledSelectorForNotification, method_getImplementation(methodForNotification), method_getTypeEncoding(methodForNotification));
        } else {
            method_exchangeImplementations(methodForNotification, swizzledMethodForNotification);
        }
        
    });
}

- (BOOL) xxx_application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {

    [[inBeaconSdk getInstance] setLogLevel:1];  // 0=none 1=error 2=log 3=info 4=debug
    
    NSString *clientId = [[[NSBundle mainBundle] infoDictionary] objectForKey:@"inBeacon API clientId"];
    NSString *secret = [[[NSBundle mainBundle] infoDictionary] objectForKey:@"inBeacon API secret"];
    
    if (clientId != nil && secret != nil && ![clientId  isEqual: @"inbclientid"] && ![secret  isEqual: @"inbsecret"]) {
        inBeaconSdk *inBeacon = [inBeaconSdk inbeaconWithClientID: clientId andClientSecret: secret];
        [inBeacon refresh];
    }

    return [self xxx_application:application didFinishLaunchingWithOptions:launchOptions];
}

- (void) xxx_application:(UIApplication *)application didReceiveLocalNotification:(UILocalNotification *)notification {
    [[inBeaconSdk getInstance] didReceiveLocalNotification:notification];   // make sure local notifications pass through the inbeacon SDK
}
    
@end
