package eu.applabs.crowdsensingupnplibrary.service;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.android.AndroidUpnpServiceImpl;
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

public class HeartRateDataServiceSenderConnection extends DefaultRegistryListener implements ServiceConnection {

    private static final String sClassName = HeartRateServiceSenderConnection.class.getSimpleName();

    public interface IHeartRateDataServiceSenderConnectionListener
    {
        void onDeviceAdded();
        void onResponseAvailable(String method, boolean success, ActionArgumentValue[] output);
    }

    private AndroidUpnpService mAndroidUpnpService = null;
    private List<IHeartRateDataServiceSenderConnectionListener> mICSUpnpServiceConnectionListenerList = null;
    private PeriodicSearchThread mPeriodicSearchThread = null;

    public HeartRateDataServiceSenderConnection() {
        mICSUpnpServiceConnectionListenerList = new ArrayList<>();
    }

    public void registerListener(IHeartRateDataServiceSenderConnectionListener listener) {
        mICSUpnpServiceConnectionListenerList.add(listener);
    }

    public void unregisterListener(IHeartRateDataServiceSenderConnectionListener listener) {
        mICSUpnpServiceConnectionListenerList.remove(listener);
    }

    public boolean devicesAvailable() {
        if(mAndroidUpnpService != null) {
            if(mAndroidUpnpService.getControlPoint().getRegistry().getRemoteDevices().size() > 0) {
                return true;
            }
        }

        return false;
    }

    public void setHeartRate(String heartRate) {
        if(mAndroidUpnpService != null) {
            for(Device device : mAndroidUpnpService.getControlPoint().getRegistry().getRemoteDevices()) {
                Service heartRateDataService = device.findService(new UDAServiceId("HeartRateDataService"));

                if(heartRateDataService != null) {
                    SetHeartRateActionInvocation action =
                            new SetHeartRateActionInvocation(heartRateDataService, heartRate);

                    mAndroidUpnpService.getControlPoint().execute(
                            new ActionCallback(action) {
                                @Override
                                public void success(ActionInvocation invocation) {
                                    ActionArgumentValue[] output = invocation.getOutput();

                                    for (IHeartRateDataServiceSenderConnectionListener listener : mICSUpnpServiceConnectionListenerList) {
                                        listener.onResponseAvailable("SetHeartRate", true, output);
                                    }
                                }

                                @Override
                                public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                                    for (IHeartRateDataServiceSenderConnectionListener listener : mICSUpnpServiceConnectionListenerList) {
                                        listener.onResponseAvailable("SetHeartRate", false, null);
                                    }
                                }
                            }
                    );
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

        for(IHeartRateDataServiceSenderConnectionListener listener : mICSUpnpServiceConnectionListenerList) {
            listener.onDeviceAdded();
        }
    }

    private class SetHeartRateActionInvocation extends ActionInvocation {
        SetHeartRateActionInvocation(Service service, String heartRate) {
            super(service.getAction("SetHeartRate"));

            try {
                setInput("HeartRate", heartRate);
            } catch (Exception e) {
                Log.e(sClassName, e.getMessage());
            }
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
