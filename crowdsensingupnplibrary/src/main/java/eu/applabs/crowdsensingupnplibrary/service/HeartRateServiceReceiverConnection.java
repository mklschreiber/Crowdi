package eu.applabs.crowdsensingupnplibrary.service;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.types.UDAServiceType;
import org.fourthline.cling.model.types.UDN;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import eu.applabs.crowdsensingupnplibrary.data.DeviceFactory;
import eu.applabs.crowdsensingupnplibrary.data.HeartRateServiceDefinition;

public class HeartRateServiceReceiverConnection implements ServiceConnection, PropertyChangeListener {

    public interface IHeartRateServiceReceiverConnectionListener {
        void onStartNotification(String title, String content, String url);
        void onStartMeasuring();
    }

    private UDN mUDN = new UDN(UUID.randomUUID());
    private AndroidUpnpService mAndroidUpnpService = null;
    private LocalService<HeartRateServiceDefinition> mHeartRateService = null;
    private List<IHeartRateServiceReceiverConnectionListener> mIHeartRateServiceReceiverConnectionListenerList = null;

    public HeartRateServiceReceiverConnection() {
        mIHeartRateServiceReceiverConnectionListenerList = new ArrayList<>();
    }

    public void registerListener(IHeartRateServiceReceiverConnectionListener listener) {
        mIHeartRateServiceReceiverConnectionListenerList.add(listener);
    }

    public void unregisterListener(IHeartRateServiceReceiverConnectionListener listener) {
        mIHeartRateServiceReceiverConnectionListenerList.remove(listener);
    }

    public void sendHeartRate(String heartRate) {
        if(mHeartRateService != null) {
            mHeartRateService.getManager().getImplementation().setHeartRate(heartRate);
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mAndroidUpnpService = (AndroidUpnpService) service;

        mHeartRateService = getHeartRateService();

        if(mHeartRateService == null) {
            LocalDevice heartRateServiceDevice = new DeviceFactory().createHeartRateServiceDevice(mUDN);

            if(heartRateServiceDevice != null) {
                mAndroidUpnpService.getRegistry().addDevice(heartRateServiceDevice);

                mHeartRateService = getHeartRateService();
            }
        }

        if(mHeartRateService != null) {
            mHeartRateService.getManager().getImplementation().registerListener(this);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mAndroidUpnpService = null;
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if(event != null) {
            if(event.getPropertyName().compareTo("mStartNotification") == 0) {
                String notificationTitle = mHeartRateService.getManager().getImplementation().getNotificationTitle();
                String notificationContent = mHeartRateService.getManager().getImplementation().getNotificationContent();
                String notificationUrl = mHeartRateService.getManager().getImplementation().getNotificationUrl();

                for(IHeartRateServiceReceiverConnectionListener listener : mIHeartRateServiceReceiverConnectionListenerList) {
                    listener.onStartNotification(notificationTitle, notificationContent, notificationUrl);
                }
            } else if (event.getPropertyName().compareTo("mStartMeasuring") == 0) {
                for(IHeartRateServiceReceiverConnectionListener listener : mIHeartRateServiceReceiverConnectionListenerList) {
                    listener.onStartMeasuring();
                }
            }
        }
    }

    private LocalService<HeartRateServiceDefinition> getHeartRateService() {
        if (mAndroidUpnpService == null) {
            return null;
        }

        LocalDevice heartRateServiceDevice = mAndroidUpnpService.getRegistry().getLocalDevice(mUDN, true);

        if (heartRateServiceDevice == null) {
            return null;
        }

        return (LocalService<HeartRateServiceDefinition>)
                heartRateServiceDevice.findService(new UDAServiceType("HeartRateService", 1));
    }
}
