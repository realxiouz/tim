import 'dart:async';
import 'dart:convert';

import 'package:flutter/services.dart';

class Tim {
  static MethodChannel _channel = MethodChannel('tim')..setMethodCallHandler(_handler);
  // static MethodChannel _native = MethodChannel('native')..setMethodCallHandler(_handler);
  static StreamController responseController =
    new StreamController.broadcast();

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<bool> initTim() async {
    final bool res = await _channel.invokeMethod('init');
    return res;
  }

  static Future<String> login() async {
    return await _channel.invokeMethod('login');
  }

  static Future<dynamic> getConversationList() async {
    var data = await _channel.invokeMethod('getConversationList');
    for (var item in data) {
      print(item);
    }
    // var arr = jsonDecode(data);
    // for (int i = 0; i < arr.length; i++) {
    //   print(arr[i]['mConversation']);
    // }
    return data;
  }

  static Future<dynamic> _handler(MethodCall methodCall) {
    if ("onNewMessages" == methodCall.method) {
      responseController
          .add(methodCall.arguments);
    } 
    return Future.value(true);
  }
}
