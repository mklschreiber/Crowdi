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
        serviceId = @UpnpServiceId("WearNotificationService"),
        serviceType = @UpnpServiceType(value = "WearNotificationService", version = 1)
)
public class WearNotificationServiceDefinition {

    private PropertyChangeSupport mPropertyChangeSupport;

    @UpnpStateVariable(defaultValue = "0", name = "mStartMeasuring")
    private boolean mStartMeasuring = false;

    @UpnpStateVariable(defaultValue = "0", name = "mStartNotification")
    private boolean mStartNotification = false;

    @UpnpStateVariable(defaultValue = "", name = "mNotificationTitle")
    private String mNotificationTitle = "";

    @UpnpStateVariable(defaultValue = "", name = "mNotificationContent")
    private String mNotificationContent = "";

    @UpnpStateVariable(defaultValue = "", name = "mNotificationUrl")
    private String mNotificationUrl = "";

    public WearNotificationServiceDefinition() {
        mPropertyChangeSupport = new PropertyChangeSupport(this);
    }

    public void registerListener(PropertyChangeListener listener) {
        mPropertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void unregisterListener(PropertyChangeListener listener) {
        mPropertyChangeSupport.removePropertyChangeListener(listener);
    }

    public String getNotificationTitle() {
        return mNotificationTitle;
    }

    public String getNotificationContent() {
        return mNotificationContent;
    }

    public String getNotificationUrl() {
        return mNotificationUrl;
    }

    @UpnpAction()
    public void getHeartRate()
    {
        // Set the variable to trigger PropertyChanged
        boolean oldValue = mStartMeasuring;
        mStartMeasuring = true;

        mPropertyChangeSupport.firePropertyChange("mStartMeasuring", oldValue, mStartMeasuring);
    }

    @UpnpAction
    public void startNotification(@UpnpInputArgument(name = "NotificationTitle", stateVariable = "mNotificationTitle") String notificationTitle,
                                  @UpnpInputArgument(name = "NotificationContent", stateVariable = "mNotificationContent") String notificationContent,
                                  @UpnpInputArgument(name = "NotificationUrl", stateVariable = "mNotificationUrl") String notificationUrl)
    {
        if(notificationTitle != null) {
            mNotificationTitle = notificationTitle;
        }

        if(notificationContent != null) {
            mNotificationContent = notificationContent;
        }

        if(notificationUrl != null) {
            mNotificationUrl = notificationUrl;
        }

        mStartNotification = true;

        mPropertyChangeSupport.firePropertyChange("mStartNotification", !mStartNotification, mStartNotification);
    }
}
