package eu.applabs.crowdsensingupnplibrary.service;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

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

public class HeartRateServiceSenderConnection extends DefaultRegistryListener implements ServiceConnection {

    public interface IHeartRateServiceSenderConnectionListener
    {
        void onDeviceAdded();
        void onResponseAvailable(String method, boolean success, ActionArgumentValue[] output);
    }

    private AndroidUpnpService mAndroidUpnpService = null;
    private List<IHeartRateServiceSenderConnectionListener> mICSUpnpServiceConnectionListenerList = null;

    public HeartRateServiceSenderConnection() {
        mICSUpnpServiceConnectionListenerList = new ArrayList<>();
    }

    public void registerListener(IHeartRateServiceSenderConnectionListener listener) {
        mICSUpnpServiceConnectionListenerList.add(listener);
    }

    public void unregisterListener(IHeartRateServiceSenderConnectionListener listener) {
        mICSUpnpServiceConnectionListenerList.remove(listener);
    }

    public void startNotification() {
        if(mAndroidUpnpService != null) {
            for (Device device : mAndroidUpnpService.getControlPoint().getRegistry().getRemoteDevices()) {
                Service pollNotificationService =
                        device.findService(new UDAServiceId("HeartRateService"));

                if (pollNotificationService != null) {
                    StartNotificationActionInvocation action =
                            new StartNotificationActionInvocation(pollNotificationService);

                    if (action != null) {
                        mAndroidUpnpService.getControlPoint().execute(
                                new ActionCallback(action) {
                                    @Override
                                    public void success(ActionInvocation invocation) {
                                        ActionArgumentValue[] output = invocation.getOutput();

                                        for (IHeartRateServiceSenderConnectionListener listener : mICSUpnpServiceConnectionListenerList) {
                                            listener.onResponseAvailable("StartNotification", true, output);
                                        }
                                    }

                                    @Override
                                    public void failure(ActionInvocation invocation,
                                                        UpnpResponse operation,
                                                        String defaultMsg) {

                                        for (IHeartRateServiceSenderConnectionListener listener : mICSUpnpServiceConnectionListenerList) {
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
                        device.findService(new UDAServiceId("HeartRateService"));

                if (heartRateService != null) {
                    GetHeartRateActionInvocation action =
                            new GetHeartRateActionInvocation(heartRateService);

                    if (action != null) {
                        mAndroidUpnpService.getControlPoint().execute(
                                new ActionCallback(action) {
                                    @Override
                                    public void success(ActionInvocation invocation) {
                                        ActionArgumentValue[] output = invocation.getOutput();

                                        for (IHeartRateServiceSenderConnectionListener listener : mICSUpnpServiceConnectionListenerList) {
                                            listener.onResponseAvailable("GetHeartRate", true, output);
                                        }
                                    }

                                    @Override
                                    public void failure(ActionInvocation invocation,
                                                        UpnpResponse operation,
                                                        String defaultMsg) {

                                        for (IHeartRateServiceSenderConnectionListener listener : mICSUpnpServiceConnectionListenerList) {
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
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        if(mAndroidUpnpService != null) {
            mAndroidUpnpService.getRegistry().removeListener(this);
        }

        mAndroidUpnpService = null;
    }

    @Override
    public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
        super.remoteDeviceAdded(registry, device);

        for(IHeartRateServiceSenderConnectionListener listener : mICSUpnpServiceConnectionListenerList) {
            listener.onDeviceAdded();
        }
    }

    private class StartNotificationActionInvocation extends ActionInvocation {
        StartNotificationActionInvocation(Service service) {
            super(service.getAction("StartNotification"));
        }
    }

    private class GetHeartRateActionInvocation extends ActionInvocation {
        GetHeartRateActionInvocation(Service service) {
            super(service.getAction("GetHeartRate"));
        }
    }
}
