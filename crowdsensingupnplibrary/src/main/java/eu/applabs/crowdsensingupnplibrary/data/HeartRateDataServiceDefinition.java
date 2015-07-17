package eu.applabs.crowdsensingupnplibrary.data;

import org.fourthline.cling.binding.annotations.UpnpAction;
import org.fourthline.cling.binding.annotations.UpnpInputArgument;
import org.fourthline.cling.binding.annotations.UpnpService;
import org.fourthline.cling.binding.annotations.UpnpServiceId;
import org.fourthline.cling.binding.annotations.UpnpServiceType;
import org.fourthline.cling.binding.annotations.UpnpStateVariable;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

@UpnpService(
        serviceId = @UpnpServiceId("HeartRateDataService"),
        serviceType = @UpnpServiceType(value = "HeartRateDataService", version = 1)
)
public class HeartRateDataServiceDefinition {

    private PropertyChangeSupport mPropertyChangeSupport;

    @UpnpStateVariable(defaultValue = "", name = "mHeartRate")
    private String mHeartRate = "";

    public HeartRateDataServiceDefinition() {
        mPropertyChangeSupport = new PropertyChangeSupport(this);
    }

    public void registerListener(PropertyChangeListener listener) {
        mPropertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void unregisterListener(PropertyChangeListener listener) {
        mPropertyChangeSupport.removePropertyChangeListener(listener);
    }

    public String getHeartRate()
    {
        return mHeartRate;
    }

    @UpnpAction()
    public void setHeartRate(@UpnpInputArgument(name = "HeartRate", stateVariable = "mHeartRate") String heartRate)
    {
        String oldValue = mHeartRate + String.valueOf(System.currentTimeMillis());

        if(heartRate != null) {
            mHeartRate = heartRate;
        }

        mPropertyChangeSupport.firePropertyChange("mHeartRate", oldValue, mHeartRate);
    }
}

