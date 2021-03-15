import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:flutter_cling/flutter_cling.dart';

void main() {
  runApp(MyApp());
}

class LifecycleEventHandler extends WidgetsBindingObserver {
  LifecycleEventHandler();

  @override
  Future<void> didChangeAppLifecycleState(AppLifecycleState state) async {
    print('-------' + state.toString() + '-------');
    switch (state) {
      case AppLifecycleState.inactive:
      case AppLifecycleState.paused:
        await FlutterCling.stop();
        break;
      case AppLifecycleState.resumed:
        await FlutterCling.search();
        break;
      default:
        break;
    }
  }
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  List<dynamic> a = [];

  @override
  void initState() {
    WidgetsBinding.instance.addObserver(LifecycleEventHandler());
    super.initState();
    FlutterCling.initialize((List<dynamic> data) {
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
                    print(b);
                    setState(() {
                      a = b;
                    });
                  }),
            ]..addAll(a.map<Widget>((item) {
                return ListTile(
                  title: Text(item["name"]),
                  onTap: () async {
                    await FlutterCling.playUrl(item["uuid"],
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
