import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:tim/tim.dart';

void main() {
  const MethodChannel channel = MethodChannel('tim');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await Tim.platformVersion, '42');
  });
}
