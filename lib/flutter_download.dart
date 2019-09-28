import 'dart:async';
import 'package:flutter/services.dart';

class FlutterDownload {
  static const MethodChannel _channel =
      const MethodChannel('native.flutter.io/flutter_download');

  static const EventChannel _eventChannel =
      const EventChannel('event.native.flutter.io/flutter_download');

  //
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

  static Future<Map> flutterDownload(Map map) async {
    print('flutterDownload');
    assert(map != null);
    Map eventMap = {};
    StreamController streamController = StreamController.broadcast();

    _eventChannel.receiveBroadcastStream(map).listen((Object event) {
      eventMap = event;
      print('eventMap 1------------- $eventMap');
      // return Future.value(eventMap);
      streamController.close();
    //  return eventMap;
    }, onError: (Object error) {});

    await streamController.stream.drain();

    // }]);
    print('eventMap 2------------- $eventMap');
    return eventMap;
  }
}
