#import "TimPlugin.h"
#if __has_include(<tim/tim-Swift.h>)
#import <tim/tim-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "tim-Swift.h"
#endif

@implementation TimPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftTimPlugin registerWithRegistrar:registrar];
}
@end
