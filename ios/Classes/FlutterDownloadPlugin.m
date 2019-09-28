#import "FlutterDownloadPlugin.h"
#import <flutter_download/flutter_download-Swift.h>

@implementation FlutterDownloadPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftFlutterDownloadPlugin registerWithRegistrar:registrar];
}
@end
