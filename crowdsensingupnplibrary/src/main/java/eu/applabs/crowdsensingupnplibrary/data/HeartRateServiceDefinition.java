package eu.applabs.crowdsensingupnplibrary.data;

import org.fourthline.cling.binding.annotations.UpnpAction;
import org.fourthline.cling.binding.annotations.UpnpInputArgument;
import org.fourthline.cling.binding.annotations.UpnpOutputArgument;
import org.fourthline.cling.binding.annotations.UpnpService;
import org.fourthline.cling.binding.annotations.UpnpServiceId;
import org.fourthline.cling.binding.annotations.UpnpServiceType;
import org.fourthline.cling.binding.annotations.UpnpStateVariable;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

@UpnpService(
        serviceId = @UpnpServiceId("HeartRateService"),
        serviceType = @UpnpServiceType(value = "HeartRateService", version = 1)
)
public class HeartRateServiceDefinition {

    private PropertyChangeSupport mPropertyChangeSupport;

    @UpnpStateVariable(defaultValue = "0.0", name = "mHeartRate")
    private String mHeartRate = "0.0";

    @UpnpStateVariable(defaultValue = "0", name = "mStartMeasuring")
    private boolean mStartMeasuring = false;

    @UpnpStateVariable(defaultValue = "0", name = "mStartNotification")
    private boolean mStartNotification = false;

    public HeartRateServiceDefinition() {
        mPropertyChangeSupport = new PropertyChangeSupport(this);
    }

    public void registerListener(PropertyChangeListener listener) {
        mPropertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void unregisterListener(PropertyChangeListener listener) {
        mPropertyChangeSupport.removePropertyChangeListener(listener);
    }

    public void setHeartRate(String heartRate) {
        mHeartRate = heartRate;

        // Set the flag to indicate measuring is completed
        mStartMeasuring = false;
    }

    @UpnpAction(out = @UpnpOutputArgument(name = "HeartRate", stateVariable = "mHeartRate"))
    public String getHeartRate()
    {
        // Set the variable to trigger PropertyChanged
        mStartMeasuring = true;

        mPropertyChangeSupport.firePropertyChange("mStartMeasuring", false, true);

        while(mStartMeasuring) { /* Block this thread till the measuring is completed */ }

        return mHeartRate;
    }

    @UpnpAction
    public void startNotification()
    {
        boolean oldValue = mStartNotification;
        mStartNotification = true;

        mPropertyChangeSupport.firePropertyChange("mStartNotification", oldValue, mStartNotification);
    }
}
