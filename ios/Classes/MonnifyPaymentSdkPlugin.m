#import "MonnifyPaymentSdkPlugin.h"
#if __has_include(<monnify_payment_sdk/monnify_payment_sdk-Swift.h>)
#import <monnify_payment_sdk/monnify_payment_sdk-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "monnify_payment_sdk-Swift.h"
#endif

@implementation MonnifyPaymentSdkPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftMonnifyPaymentSdkPlugin registerWithRegistrar:registrar];
}
@end
