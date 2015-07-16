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
import eu.applabs.crowdsensingupnplibrary.data.HeartRateDataServiceDefinition;
import eu.applabs.crowdsensingupnplibrary.data.HeartRateServiceDefinition;

public class HeartRateDataServiceReceiverConnection implements ServiceConnection, PropertyChangeListener {

    public interface IHeartRateDataServiceReceiverConnectionListener {
        void onHeartRateReceived(String heartRate);
    }

    private UDN mUDN = new UDN(UUID.randomUUID());
    private AndroidUpnpService mAndroidUpnpService = null;
    private LocalService<HeartRateDataServiceDefinition> mHeartRateDataService = null;
    private List<IHeartRateDataServiceReceiverConnectionListener> mIHeartRateServiceReceiverConnectionListenerList = null;

    public HeartRateDataServiceReceiverConnection() {
        mIHeartRateServiceReceiverConnectionListenerList = new ArrayList<>();
    }

    public void registerListener(IHeartRateDataServiceReceiverConnectionListener listener) {
        mIHeartRateServiceReceiverConnectionListenerList.add(listener);
    }

    public void unregisterListener(IHeartRateDataServiceReceiverConnectionListener listener) {
        mIHeartRateServiceReceiverConnectionListenerList.remove(listener);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mAndroidUpnpService = (AndroidUpnpService) service;

        mHeartRateDataService = getHeartRateDataService();

        if(mHeartRateDataService == null) {
            LocalDevice heartRateDataServiceDevice = new DeviceFactory().createHeartRateDataServiceDevice(mUDN);

            if(heartRateDataServiceDevice != null) {
                mAndroidUpnpService.getRegistry().addDevice(heartRateDataServiceDevice);

                mHeartRateDataService = getHeartRateDataService();
            }
        }

        if(mHeartRateDataService != null) {
            mHeartRateDataService.getManager().getImplementation().registerListener(this);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mAndroidUpnpService = null;
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if(event != null) {
            if(event.getPropertyName().compareTo("mHeartRate") == 0) {
                String heartRate = mHeartRateDataService.getManager().getImplementation().getHeartRate();

                for(IHeartRateDataServiceReceiverConnectionListener listener : mIHeartRateServiceReceiverConnectionListenerList) {
                    listener.onHeartRateReceived(heartRate);
                }
            }
        }
    }

    private LocalService<HeartRateDataServiceDefinition> getHeartRateDataService() {
        if (mAndroidUpnpService == null) {
            return null;
        }

        LocalDevice heartRateDataServiceDevice = mAndroidUpnpService.getRegistry().getLocalDevice(mUDN, true);

        if (heartRateDataServiceDevice == null) {
            return null;
        }

        return (LocalService<HeartRateDataServiceDefinition>)
                heartRateDataServiceDevice.findService(new UDAServiceType("HeartRateDataService", 1));
    }
}
