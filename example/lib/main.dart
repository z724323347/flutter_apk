import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:flutter_download/flutter_download.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  var result = '';
  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    Map resultMap ={};
    try {
      // platformVersion = await FlutterDownload.platformVersion;
      Map map = {
        'name': 'test_event.apk',
        'url': 'https://cnd.wn3331.com/appdownload/zyjh/signed.apk',
        // 'msg': 'response.data.toString()'
      };
      // result = await FlutterDownload.onlyApkDownload(map);
      StreamSubscription strem = await FlutterDownload.flutterDownload(map);
      strem.onData((Object event) {
        print('strem---------------- $event');
        setState(() {
          resultMap = event;
          _platformVersion = '文件大小 :${resultMap['max'].toString()}' + '\n 当前下载进度${resultMap['progress'].toString()}';
        });
      });
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    // if (!mounted) return;

  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Text('Running on: \n$_platformVersion\n'),
        ),
      ),
    );
  }
}
