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
import eu.applabs.crowdsensingupnplibrary.data.StartPollServiceDefinition;

public class StartPollServiceReceiverConnection implements ServiceConnection, PropertyChangeListener {

    public interface IStartPollServiceReceiverConnectionListener {
        void onStartPoll();
    }

    private UDN mUDN = new UDN(UUID.randomUUID());
    private AndroidUpnpService mAndroidUpnpService = null;
    private LocalService<StartPollServiceDefinition> mStartPollService = null;
    private List<IStartPollServiceReceiverConnectionListener> mIHeartRateServiceReceiverConnectionListenerList = null;

    public StartPollServiceReceiverConnection() {
        mIHeartRateServiceReceiverConnectionListenerList = new ArrayList<>();
    }

    public void registerListener(IStartPollServiceReceiverConnectionListener listener) {
        mIHeartRateServiceReceiverConnectionListenerList.add(listener);
    }

    public void unregisterListener(IStartPollServiceReceiverConnectionListener listener) {
        mIHeartRateServiceReceiverConnectionListenerList.remove(listener);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mAndroidUpnpService = (AndroidUpnpService) service;

        mStartPollService = getStartPollService();

        if(mStartPollService == null) {
            LocalDevice heartRateServiceDevice = new DeviceFactory().createStartPollServiceDevice(mUDN);

            if(heartRateServiceDevice != null) {
                mAndroidUpnpService.getRegistry().addDevice(heartRateServiceDevice);

                mStartPollService = getStartPollService();
            }
        }

        if(mStartPollService != null) {
            mStartPollService.getManager().getImplementation().registerListener(this);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mAndroidUpnpService = null;
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if(event != null) {
            if(event.getPropertyName().compareTo("mStartPoll") == 0) {
                for(IStartPollServiceReceiverConnectionListener listener : mIHeartRateServiceReceiverConnectionListenerList) {
                    listener.onStartPoll();
                }
            }
        }
    }

    private LocalService<StartPollServiceDefinition> getStartPollService() {
        if (mAndroidUpnpService == null) {
            return null;
        }

        LocalDevice heartRateServiceDevice = mAndroidUpnpService.getRegistry().getLocalDevice(mUDN, true);

        if (heartRateServiceDevice == null) {
            return null;
        }

        return (LocalService<StartPollServiceDefinition>)
                heartRateServiceDevice.findService(new UDAServiceType("StartPollService", 1));
    }
}
