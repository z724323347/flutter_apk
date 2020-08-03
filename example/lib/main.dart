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
  String _loadingString = 'Unknown';
  String _pushState = 'Unknown';
  var result = '';
  @override
  void initState() {
    super.initState();
  }

  Future<void> flutterLoading() async {
    String platformVersion;
    Map resultMap = {};
    try {
      Map map = {
        'name': 'test_event.apk',
        'url': 'https://cnd.wn3331.com/appdownload/zyjh/signed.apk',
      };
      StreamSubscription strem = await FlutterDownload.flutterDownload(map);
      strem.onData((Object event) {
        print('strem---------------- $event');
        setState(() {
          resultMap = event;
          _platformVersion = '文件大小 :${resultMap['max'].toString()}' +
              '\n 当前下载进度${resultMap['progress'].toString()}';
          resultMap['max'] == resultMap['progress'] ? strem.cancel() : null;
        });
      });
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }
  }

  Future<void> onlyDownload() async {
    Map map = {
      'name': 'test_event.apk',
      'url': 'https://cnd.wn3331.com/appdownload/zyjh/signed.apk',
    };
    String reslut = await FlutterDownload.onlyApkDownload(map);
    setState(() {
      _loadingString = reslut;
    });
  }

  Future<void> pushStatus() async {
    bool reslut = await FlutterDownload.checkPushStatus();
    setState(() {
      _pushState = 'starus : ${reslut}';
    });
  }

  Future<void> gotoSys() async {
    await FlutterDownload.goSysSetting();
  }

  Future<void> otherGoto() async {
    // await FlutterDownload.pluginGotoSys();
  }

  Future<void> wakeLock(bool isLight) async {
    await FlutterDownload.screenLongLight(isLight);
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
          appBar: AppBar(
            title: const Text('Plugin example app'),
          ),
          body: SingleChildScrollView(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.start,
              crossAxisAlignment: CrossAxisAlignment.center,
              children: <Widget>[
                Container(
                  color: Colors.white,
                  width: 400,
                  height: 150,
                  child: Column(
                    children: <Widget>[
                      OutlineButton(
                        onPressed: () {
                          flutterLoading();
                        },
                        child: Text('Flutter 获取下载进度'),
                      ),
                      Text(' \n$_platformVersion\n')
                    ],
                  ),
                ),
                Container(
                  color: Colors.white,
                  width: 400,
                  height: 100,
                  child: Column(
                    children: <Widget>[
                      OutlineButton(
                        onPressed: () {
                          onlyDownload();
                        },
                        child: Text('Flutter only下载'),
                      ),
                      Text(' \n$_loadingString\n')
                    ],
                  ),
                ),
                Container(
                  color: Colors.white,
                  width: 400,
                  height: 100,
                  child: Column(
                    children: <Widget>[
                      OutlineButton(
                        onPressed: () {
                          pushStatus();
                        },
                        child: Text('Flutter 获取推送状态'),
                      ),
                      Text(' \n$_pushState\n')
                    ],
                  ),
                ),
                Container(
                  color: Colors.white,
                  width: 400,
                  child: Column(
                    children: <Widget>[
                      OutlineButton(
                        onPressed: () {
                          gotoSys();
                        },
                        child: Text('Flutter 进入系统设置'),
                      ),
                    ],
                  ),
                ),
                Container(
                  color: Colors.white,
                  width: 400,
                  child: Column(
                    children: <Widget>[
                      OutlineButton(
                        onPressed: () {
                          otherGoto();
                        },
                        child: Text('由其他插件 进入系统设置'),
                      ),
                    ],
                  ),
                ),
                Container(
                  color: Colors.white,
                  width: 400,
                  child: Column(
                    children: <Widget>[
                      OutlineButton(
                        onPressed: () {
                          wakeLock(true);
                        },
                        child: Text('打开屏幕常亮'),
                      ),
                    ],
                  ),
                ),
                Container(
                  color: Colors.white,
                  width: 400,
                  child: Column(
                    children: <Widget>[
                      OutlineButton(
                        onPressed: () {
                          wakeLock(false);
                        },
                        child: Text('关闭屏幕常亮'),
                      ),
                    ],
                  ),
                ),
              ],
            ),
          )),
    );
  }
}
