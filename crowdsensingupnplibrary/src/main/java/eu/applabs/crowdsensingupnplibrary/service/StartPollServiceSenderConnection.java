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
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.UDAServiceId;

public class StartPollServiceSenderConnection implements ServiceConnection {

    private AndroidUpnpService mAndroidUpnpService = null;

    public void startPoll() {
        if(mAndroidUpnpService != null) {
            for (Device device : mAndroidUpnpService.getControlPoint().getRegistry().getRemoteDevices()) {
                Service pollNotificationService =
                        device.findService(new UDAServiceId("StartPollService"));

                if (pollNotificationService != null) {
                    StartPollActionInvocation action =
                            new StartPollActionInvocation(pollNotificationService);

                    if (action != null) {
                        mAndroidUpnpService.getControlPoint().execute(
                                new ActionCallback(action) {
                                    @Override
                                    public void success(ActionInvocation invocation) {
                                        ActionArgumentValue[] output = invocation.getOutput();
                                    }

                                    @Override
                                    public void failure(ActionInvocation invocation,
                                                        UpnpResponse operation,
                                                        String defaultMsg) {
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

        mAndroidUpnpService.getControlPoint().search();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mAndroidUpnpService = null;
    }

    private class StartPollActionInvocation extends ActionInvocation {
        StartPollActionInvocation(Service service) {
            super(service.getAction("StartPoll"));
        }
    }
}
