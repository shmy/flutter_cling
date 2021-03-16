package tech.shmy.flutter_cling;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.android.AndroidUpnpServiceImpl;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.support.avtransport.callback.Play;
import org.fourthline.cling.support.avtransport.callback.SetAVTransportURI;

public class Cling {
    private static Boolean isSearchStarted = false;
    private static AndroidUpnpService androidUpnpService;
    private static ServiceConnection serviceConnection;
    private static final BrowseRegistryListener browseRegistryListener = new BrowseRegistryListener();
    public static BrowseRegistryListener getBrowseRegistryListener() {
        return browseRegistryListener;
    }
    public static void search(Activity activity) {
        if (isSearchStarted) {
            return;
        }
        System.out.println("-----Search start-----");
        serviceConnection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                androidUpnpService = (AndroidUpnpService) service;
                System.out.println("A upnpService Connected: " + androidUpnpService.toString());
                androidUpnpService.getRegistry().addListener(browseRegistryListener);
                androidUpnpService.getControlPoint().search();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                System.out.println("A upnpService Disconnected: " + name);
            }
        };
        Context context = activity.getApplicationContext();
        isSearchStarted = context.getApplicationContext().bindService(new Intent(activity, DLNABrowserService.class),
                serviceConnection, Context.BIND_AUTO_CREATE);
    }
    public static void stop(Activity activity) {
        if (serviceConnection != null) {
            System.out.println("---Stop search---");
            Context context = activity.getApplicationContext();
            context.getApplicationContext().unbindService(serviceConnection);
            serviceConnection = null;
            isSearchStarted = false;
            browseRegistryListener.clearDevices();
        }
    }
    public static void playUrl(String uuid, String url) {
        final Service avtService = browseRegistryListener.getDeviceByUUID(uuid);
        if (avtService == null) {
            return;
        }
        System.out.println("Do play url: " + url + ", uuid: " + uuid);

        androidUpnpService.getControlPoint().execute(new SetAVTransportURI(avtService, url) {
            @Override
            public void success(ActionInvocation invocation) {
                System.out.println("Set url success: " + invocation.toString());
                Cling.doPlay(avtService);
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                System.out.println("Set url error: " + defaultMsg);
            }
        });

    }

    private static void doPlay(Service avtService) {
        androidUpnpService.getControlPoint().execute(new Play(avtService) {
            @Override
            public void success(ActionInvocation invocation) {
                System.out.println("Play success: " + invocation.toString());
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                System.out.println("Play error: " + defaultMsg);
            }
        });
    }
}
