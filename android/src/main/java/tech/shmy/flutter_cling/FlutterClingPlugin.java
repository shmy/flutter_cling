package tech.shmy.flutter_cling;

import android.app.Activity;

import androidx.annotation.NonNull;

import java.util.Objects;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * FlutterClingPlugin
 */
public class FlutterClingPlugin implements FlutterPlugin, MethodCallHandler, EventChannel.StreamHandler, ActivityAware {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private Activity activity;
    private MethodChannel methodChannel;
    private EventChannel eventChannel;
    private final String channelName = "tech.shmy.plugins/flutter_cling/";

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        methodChannel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), channelName + "method");
        eventChannel = new EventChannel(flutterPluginBinding.getBinaryMessenger(), channelName + "event");
        methodChannel.setMethodCallHandler(this);
        eventChannel.setStreamHandler(this);
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        switch (call.method) {
            case "search":
                Cling.search(activity);
                break;
            case "stop":
                Cling.stop(activity);
                break;
            case "playUrl":
                String url = call.argument("url").toString();
                String uuid = call.argument("uuid").toString();
                Cling.playUrl(uuid, url);
                break;
            case "getList":
                result.success(Cling.getBrowseRegistryListener().getDeviceHashList());
                break;
            default:
                result.notImplemented();
                break;
        }
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        methodChannel.setMethodCallHandler(null);
    }

    @Override
    public void onListen(Object arguments, EventChannel.EventSink eventSink) {
        BrowseRegistryListener.setEventSink(eventSink);
    }

    @Override
    public void onCancel(Object arguments) {
        BrowseRegistryListener.setEventSink(null);
    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        activity = binding.getActivity();
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        activity = null;
    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
        activity = binding.getActivity();
    }

    @Override
    public void onDetachedFromActivity() {
        activity = null;
    }

}
