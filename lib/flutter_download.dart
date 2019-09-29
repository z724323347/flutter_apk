import 'dart:async';
import 'dart:ffi';
import 'package:flutter/services.dart';
import 'package:permission_handler/permission_handler.dart';

class FlutterDownload {
  static const MethodChannel _channel =
      const MethodChannel('native.flutter.io/flutter_download');

  static const EventChannel _eventChannel =
      const EventChannel('event.native.flutter.io/flutter_download');

  // app 静默下载
  static Future<String> onlyApkDownload(Map map) async {
    print('onlyApkDownload');
    assert(map != null);
    String result = await _channel.invokeMethod('only_download', map);
    return result;
  }

  static Future<void> apkDownload(Map map) async {
    print('apkDownload');
    assert(map != null);
    return _channel.invokeMethod('apk_download', map);
  }

  // flutter_download
  static Future<StreamSubscription> flutterDownload(Map map) async {
    print('flutterDownload');
    assert(map != null);
    return _eventChannel.receiveBroadcastStream(map).listen((Object event) {
      // print('eventMap 1------------- $event');
    }, onError: (Object error) {});
  }

  // push_check
  static Future<bool> checkPushStatus() async {
    print('checkPushStatus');
    bool state = await _channel.invokeMethod('check_push');
    return state;
  }

  // 进入推送系统设置
  static Future<void> goSysSetting() async {
    print('goSysSetting');
    await _channel.invokeMethod('goSysSetting');
  }

  //插件方式
  static Future<void> pluginGotoSys() async {
    await PermissionHandler().openAppSettings();
  }

  /// 手机屏幕常亮  设置为常亮:true  ，取消常亮: false
  static Future<void> screenLongLight(bool isLight) async {
    assert(isLight != null);
    Map map = {'isLight': isLight};
    await _channel.invokeMethod('long_light', map);
  }
}
