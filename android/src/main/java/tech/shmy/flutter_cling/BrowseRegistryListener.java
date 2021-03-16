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
import java.util.HashSet;

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
        deviceList.clear();
        sendEvent();
    }

    public ArrayList<HashMap<String, String>> getDeviceHashList() {
        HashMap<String, Device> maps = new HashMap<>();
        ArrayList<HashMap<String, String>> items = new ArrayList<>();
        for (Device device : deviceList) {
            String uuid = device.getIdentity().getUdn().getIdentifierString();
            if (maps.containsKey(uuid)) {
                continue;
            }
            maps.put(uuid, device);
            HashMap<String, String> item = new HashMap<>();
            String name = device.getDetails().getFriendlyName();
            URL ip = device.getDetails().getBaseURL();
            item.put("name", name);
            item.put("uuid", uuid);
            item.put("ip", ip == null ? "Unknown" : ip.toString());
            items.add(item);
        }
        maps.clear();
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
        System.out.println("remoteDeviceAdded: " + device.getDetails().getFriendlyName());
        onAdded(device);
        super.remoteDeviceAdded(registry, device);
    }

    @Override
    public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
        System.out.println("remoteDeviceRemoved: " + device.getDetails().getFriendlyName());
        onRemove(device);
        super.remoteDeviceRemoved(registry, device);
    }

    @Override
    public void deviceAdded(Registry registry, Device device) {
        System.out.println("deviceAdded: " + device.getDetails().getFriendlyName());
        onAdded(device);
        super.deviceAdded(registry, device);
    }

    @Override
    public void deviceRemoved(Registry registry, Device device) {
        System.out.println("deviceRemoved: " + device.getDetails().getFriendlyName());
        onRemove(device);
        super.deviceRemoved(registry, device);
    }
}
