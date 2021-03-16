class Device {
  final String name;
  final String uuid;
  final String ip;

  Device({required this.name, required this.uuid, required this.ip});
  static Device formMap(dynamic map) {
    return Device(
      name: map['name'],
      uuid: map['uuid'],
      ip: map['ip'],
    );
  }
}