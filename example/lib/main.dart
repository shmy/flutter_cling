import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:flutter_cling/flutter_cling.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  List<Device> a = [];

  @override
  void initState() {
    super.initState();
    FlutterCling.startWithListener((List<Device> data) {
      setState(() {
        a = data;
      });
    });
  }

  @override
  void dispose() {
    super.dispose();
    FlutterCling.dispose();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Column(
            children: [
              MaterialButton(
                child: Text("search"),
                onPressed: () async {
                  await FlutterCling.search();
                },
              ),
              MaterialButton(
                  child: Text("getList"),
                  onPressed: () async {
                    var b = await FlutterCling.devices;
                    setState(() {
                      a = b;
                    });
                  }),
            ]..addAll(a.map<Widget>((item) {
                return ListTile(
                  title: Text(item.name),
                  onTap: () async {
                    await FlutterCling.playUrl(item.uuid,
                        "https://bili.meijuzuida.com/20190731/21094_0a89b649/index.m3u8");
                  },
                );
              })),
          ),
        ),
      ),
    );
  }
}
