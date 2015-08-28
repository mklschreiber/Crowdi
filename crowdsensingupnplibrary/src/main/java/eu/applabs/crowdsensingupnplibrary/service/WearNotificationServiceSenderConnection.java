package eu.applabs.crowdsensingupnplibrary.service;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.model.action.ActionArgumentValue;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.UDAServiceId;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;

import java.util.ArrayList;
import java.util.List;

public class WearNotificationServiceSenderConnection extends DefaultRegistryListener implements ServiceConnection {

    private static final String sClassName = WearNotificationServiceSenderConnection.class.getSimpleName();

    public interface IWearNotificationServiceSenderConnectionListener
    {
        void onDeviceAdded();
        void onResponseAvailable(String method, boolean success, ActionArgumentValue[] output);
    }

    private AndroidUpnpService mAndroidUpnpService = null;
    private List<IWearNotificationServiceSenderConnectionListener> mICSUpnpServiceConnectionListenerList = null;
    private PeriodicSearchThread mPeriodicSearchThread = null;

    public WearNotificationServiceSenderConnection() {
        mICSUpnpServiceConnectionListenerList = new ArrayList<>();
    }

    public void registerListener(IWearNotificationServiceSenderConnectionListener listener) {
        mICSUpnpServiceConnectionListenerList.add(listener);
    }

    public void unregisterListener(IWearNotificationServiceSenderConnectionListener listener) {
        mICSUpnpServiceConnectionListenerList.remove(listener);
    }

    public boolean devicesAvailable() {
        if(mAndroidUpnpService != null) {
            for(Device device : mAndroidUpnpService.getControlPoint().getRegistry().getRemoteDevices()) {
                Service heartRateService = device.findService(new UDAServiceId("WearNotificationService"));

                if(heartRateService != null) {
                    return true;
                }
            }
        }

        return false;
    }

    public void startNotification(String title, String content, String url) {
        if(mAndroidUpnpService != null) {
            for (Device device : mAndroidUpnpService.getControlPoint().getRegistry().getRemoteDevices()) {
                Service pollNotificationService =
                        device.findService(new UDAServiceId("WearNotificationService"));

                if (pollNotificationService != null) {
                    StartNotificationActionInvocation action =
                            new StartNotificationActionInvocation(pollNotificationService, title, content, url);

                    if (action != null) {
                        mAndroidUpnpService.getControlPoint().execute(
                                new ActionCallback(action) {
                                    @Override
                                    public void success(ActionInvocation invocation) {
                                        ActionArgumentValue[] output = invocation.getOutput();

                                        for (IWearNotificationServiceSenderConnectionListener listener : mICSUpnpServiceConnectionListenerList) {
                                            listener.onResponseAvailable("StartNotification", true, output);
                                        }
                                    }

                                    @Override
                                    public void failure(ActionInvocation invocation,
                                                        UpnpResponse operation,
                                                        String defaultMsg) {

                                        for (IWearNotificationServiceSenderConnectionListener listener : mICSUpnpServiceConnectionListenerList) {
                                            listener.onResponseAvailable("StartNotification", false, null);
                                        }
                                    }
                                }
                        );
                    }
                }
            }
        }
    }

    public void getHeartRate() {
        if(mAndroidUpnpService != null) {
            for (Device device : mAndroidUpnpService.getControlPoint().getRegistry().getRemoteDevices()) {
                Service heartRateService =
                        device.findService(new UDAServiceId("WearNotificationService"));

                if (heartRateService != null) {
                    GetHeartRateActionInvocation action =
                            new GetHeartRateActionInvocation(heartRateService);

                    if (action != null) {
                        mAndroidUpnpService.getControlPoint().execute(
                                new ActionCallback(action) {
                                    @Override
                                    public void success(ActionInvocation invocation) {
                                        ActionArgumentValue[] output = invocation.getOutput();

                                        for (IWearNotificationServiceSenderConnectionListener listener : mICSUpnpServiceConnectionListenerList) {
                                            listener.onResponseAvailable("GetHeartRate", true, output);
                                        }
                                    }

                                    @Override
                                    public void failure(ActionInvocation invocation,
                                                        UpnpResponse operation,
                                                        String defaultMsg) {

                                        for (IWearNotificationServiceSenderConnectionListener listener : mICSUpnpServiceConnectionListenerList) {
                                            listener.onResponseAvailable("GetHeartRate", false, null);
                                        }
                                    }
                                }
                        );
                    }
                }
            }
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mAndroidUpnpService = (AndroidUpnpService) service;

        mAndroidUpnpService.getRegistry().addListener(this);
        mAndroidUpnpService.getControlPoint().search();

        mPeriodicSearchThread = new PeriodicSearchThread(mAndroidUpnpService);
        mPeriodicSearchThread.start();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        if(mAndroidUpnpService != null) {
            mAndroidUpnpService.getRegistry().removeListener(this);
        }

        if(mPeriodicSearchThread != null) {
            mPeriodicSearchThread.stopThread();
        }

        mPeriodicSearchThread = null;
        mAndroidUpnpService = null;
    }

    @Override
    public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
        super.remoteDeviceAdded(registry, device);

        for(IWearNotificationServiceSenderConnectionListener listener : mICSUpnpServiceConnectionListenerList) {
            listener.onDeviceAdded();
        }
    }

    private class StartNotificationActionInvocation extends ActionInvocation {
        StartNotificationActionInvocation(Service service, String title, String content, String url) {
            super(service.getAction("StartNotification"));

            try {
                setInput("NotificationTitle", title);
                setInput("NotificationContent", content);
                setInput("NotificationUrl", url);
            } catch(Exception e) {
                Log.e(sClassName, e.getMessage());
            }
        }
    }

    private class GetHeartRateActionInvocation extends ActionInvocation {
        GetHeartRateActionInvocation(Service service) {
            super(service.getAction("GetHeartRate"));
        }
    }

    private class PeriodicSearchThread extends Thread {
        private AndroidUpnpService mAndroidUpnpService = null;
        private boolean mRunning = false;

        public PeriodicSearchThread(AndroidUpnpService service) {
            mAndroidUpnpService = service;
        }

        public void stopThread() {
            try {
                mRunning = false;

                this.interrupt();
            } catch (Exception e) {
                Log.e(sClassName, e.getMessage());
            }
        }

        @Override
        public void run() {
            super.run();
            try {
                mRunning = true;

                while (mRunning) {
                    mAndroidUpnpService.getControlPoint().search();
                    Thread.sleep(3000);
                }
            } catch (Exception e) {
                Log.e(sClassName, e.getMessage());
            }
        }
    }
}
