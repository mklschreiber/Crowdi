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
        serviceId = @UpnpServiceId("StartPollService"),
        serviceType = @UpnpServiceType(value = "StartPollService", version = 1)
)
public class StartPollServiceDefinition {

    private PropertyChangeSupport mPropertyChangeSupport;

    @UpnpStateVariable(defaultValue = "", name = "mPollUrl")
    private String mPollUrl = "";

    @UpnpStateVariable(defaultValue = "0", name = "mStartPoll")
    private boolean mStartPoll = false;

    public StartPollServiceDefinition() {
        mPropertyChangeSupport = new PropertyChangeSupport(this);
    }

    public String getPollUrl() {
        return mPollUrl;
    }

    public void registerListener(PropertyChangeListener listener) {
        mPropertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void unregisterListener(PropertyChangeListener listener) {
        mPropertyChangeSupport.removePropertyChangeListener(listener);
    }

    @UpnpAction
    public void startPoll(@UpnpInputArgument(name = "PollUrl", stateVariable = "mPollUrl") String pollUrl)
    {
        if(pollUrl != null) {
            mPollUrl = pollUrl;
        }

        mStartPoll = true;

        mPropertyChangeSupport.firePropertyChange("mStartPoll", !mStartPoll, mStartPoll);
    }
}