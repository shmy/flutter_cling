package tech.shmy.flutter_cling;
import android.os.Handler;
import android.os.Looper;

import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.ServiceType;
import org.fourthline.cling.model.types.UDAServiceType;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import io.flutter.plugin.common.EventChannel;

public class BrowseRegistryListener extends DefaultRegistryListener {
    private static EventChannel.EventSink eventSink;
    private static final ServiceType AV_TRANSPORT_SERVICE = new UDAServiceType("AVTransport");
    private final ArrayList<Device> deviceList = new ArrayList<>();
    private final Handler uiThreadHandler = new Handler(Looper.getMainLooper());

    public static void setEventSink(EventChannel.EventSink eventSink) {
        BrowseRegistryListener.eventSink = eventSink;
    }

    public Service getDeviceByUUID(String uuid) {
        for (Device device : deviceList) {
            String currentUuid = device.getIdentity().getUdn().getIdentifierString();
            if (uuid.equals(currentUuid)) {
                return device.findService(AV_TRANSPORT_SERVICE);
            }
        }
        return null;
    }
    public void clearDevices() {
        this.deviceList.clear();
    }
    public ArrayList<HashMap<String, String>> getDeviceHashList () {
        ArrayList<HashMap<String, String>> items = new ArrayList<>();
        for (Device device : deviceList) {
            HashMap<String, String> item = new HashMap<>();
            URL ip = device.getDetails().getBaseURL();
            item.put("name", device.getDetails().getFriendlyName());
            item.put("uuid", device.getIdentity().getUdn().getIdentifierString());
            item.put("ip", ip == null ? "Unknown" : ip.toString());
            items.add(item);
        }
        return items;
    }
    private void sendEvent() {
        if (BrowseRegistryListener.eventSink != null) {
            uiThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    BrowseRegistryListener.eventSink.success(getDeviceHashList());
                }
            });
        }
    }
    private void onAdded(Device device) {
        deviceList.add(device);
        sendEvent();
    }
    private void onRemove(Device device) {
        deviceList.remove(device);
        sendEvent();
    }
    @Override
    public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
        super.remoteDeviceAdded(registry, device);
        onAdded(device);
    }

    @Override
    public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
        super.remoteDeviceRemoved(registry, device);
        onRemove(device);
    }

    @Override
    public void deviceAdded(Registry registry, Device device) {
        super.deviceAdded(registry, device);
        onAdded(device);
    }

    @Override
    public void deviceRemoved(Registry registry, Device device) {
        super.deviceRemoved(registry, device);
        onRemove(device);
    }
}
