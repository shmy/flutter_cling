import 'dart:async';

import 'package:flutter/cupertino.dart';
import 'package:flutter/services.dart';
import 'package:flutter_cling/device.dart';
export 'package:flutter_cling/device.dart';

class FlutterCling {
  static const _CHANNEL_NAME = "tech.shmy.plugins/flutter_cling/";
  static const MethodChannel _methodChannel =
      const MethodChannel(_CHANNEL_NAME + "method");
  static const EventChannel _eventChannel =
      const EventChannel(_CHANNEL_NAME + "event");
  static StreamSubscription? _eventSubscription;

  static startWithListener(ValueChanged<List<Device>> listener) {
    _eventSubscription =
        _eventChannel.receiveBroadcastStream().listen((dynamic data) {
      listener(data.map<Device>((e) => Device.formMap(e)).toList());
    });
    _start();
  }

  static dispose() {
    _shutdown();
    _eventSubscription?.cancel();
  }

  static Future<List<Device>> get devices async {
    final List<dynamic> devices = await _methodChannel.invokeMethod('getDevices');
    return devices.map((e) => Device.formMap(e)).toList();
  }

  static _start() async {
    await _methodChannel.invokeMethod('start');
  }

  static search() async {
    await _methodChannel.invokeMethod('search');
  }

  static _shutdown() async {
    await _methodChannel.invokeMethod('shutdown');
  }

  static playUrl(String uuid, String url) async {
    await _methodChannel.invokeMethod('playUrl', {
      "uuid": uuid,
      "url": url,
    });
  }
}
