import 'dart:convert';

import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:tim/tim.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';

  @override
  void initState() {
    super.initState();
    initPlatformState();
    Tim.responseController.stream.listen((data){
      print(data.runtimeType);
      var d = jsonDecode(data);
      // print(d);
      // print(d["ConverstaionType"]);
      // print(d["elements"]);
      print(data);
    });
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      platformVersion = await Tim.platformVersion;
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
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
            children: <Widget>[
              Text('Running on: $_platformVersion\n'),
              Container(
                child: FlatButton(child: Text('tim init'), onPressed: () async{
                  var data = await Tim.initTim();
                  print(data);
                },),
              ),
              Container(
                child: FlatButton(child: Text('tim login'), onPressed: () async{
                  var data = await Tim.login();
                  print(data);
                },),
              ),
              Container(
                child: FlatButton(child: Text('list'), onPressed: () async{
                  var data = await Tim.getConversationList();
                  print(data);
                },),
              )
            ],
          ),
        ),
      ),
    );
  }
}
