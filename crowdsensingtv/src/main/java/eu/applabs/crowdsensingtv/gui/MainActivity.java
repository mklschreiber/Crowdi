package eu.applabs.crowdsensingtv.gui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

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

import java.util.List;

import eu.applabs.crowdsensinglibrary.ILibraryResultListener;
import eu.applabs.crowdsensinglibrary.Library;
import eu.applabs.crowdsensinglibrary.data.Command;
import eu.applabs.crowdsensinglibrary.data.Poll;
import eu.applabs.crowdsensingtv.R;
import eu.applabs.crowdsensingtv.service.CSUpnpService;

public class MainActivity extends Activity implements ILibraryResultListener, View.OnClickListener {

    public static final String BASE_URL = "http://as.applabs.eu:8080/FancyModule/";

    private MainActivity mActivity = null;

    private LinearLayout mLinearLayout = null;
    private List<Command> mCommandList = null;

    private AndroidUpnpService mAndroidUpnpService = null;
    private UpnpServiceConnection mUpnpServiceConnection = null;
    private BrowseRegistryListener mBrowseRegistryListener = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mActivity = this;
        mLinearLayout = (LinearLayout) findViewById(R.id.id_MainActivity_LL_Commands);

        Library library = new Library();
        library.registerListener(this);
        library.loadCommands(BASE_URL + "start");

        startPeriodicNotification();

        mBrowseRegistryListener = new BrowseRegistryListener();
        mUpnpServiceConnection = new UpnpServiceConnection();
    }

    @Override
    protected void onStart() {
        super.onStart();

        bindService(new Intent(this, CSUpnpService.class), mUpnpServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();

        unbindService(mUpnpServiceConnection);
    }

    @Override
    public void onLibraryResult(final ExecutionStatus status, final Poll poll) {

    }

    @Override
    public void onLibraryResult(final ExecutionStatus status, final List<Command> list) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(status == ExecutionStatus.Success) {
                    mCommandList = list;

                    for(int i = 0; i < mCommandList.size(); ++i) {
                        Command command = mCommandList.get(i);

                        Button b = new Button(getApplicationContext());
                        b.setText(command.getInfo());
                        b.setId(command.getId());
                        b.setOnClickListener(mActivity);

                        if(i == 0) {
                            b.requestFocus();
                        }

                        mLinearLayout.addView(b);
                    }
                } else {
                    Toast.makeText(mActivity, "Es konnte keine Verbindung zum Server" +
                            " aufgebaut werden...", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(mCommandList != null) {
            for(Command command : mCommandList) {
                if(command.getId() == v.getId()) {
                    Intent intent = new Intent(this, SinglePollActivity.class);
                    Bundle extras = new Bundle();
                    extras.putString(SinglePollActivity.EXTRA_URL, BASE_URL + command.getCommand());
                    intent.putExtras(extras);

                    startActivity(intent);
                }
            }
        }
    }

    private void startPeriodicNotification() {
        BootupActivity ba = new BootupActivity();
        ba.onReceive(this, new Intent().setAction(Intent.ACTION_BOOT_COMPLETED));
    }

    private class UpnpServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mAndroidUpnpService = (AndroidUpnpService) service;

            mAndroidUpnpService.getRegistry().addListener(mBrowseRegistryListener);
            for(Device device : mAndroidUpnpService.getRegistry().getDevices()) {
                int i = 0;
                i++;
            }

            mAndroidUpnpService.getControlPoint().search();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mAndroidUpnpService = null;
        }
    }

    private class BrowseRegistryListener extends DefaultRegistryListener {
        @Override
        public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
            super.remoteDeviceAdded(registry, device);

            Service heartRateService;
            if((heartRateService = device.findService(new UDAServiceId("HeartRateService"))) != null) {
                // Service found
                executeAction(heartRateService);
            }
        }

        private void executeAction(Service heartRateService) {

            ActionInvocation startHeartRateMeasuring =
                    new StartHeartRateMeasuringActionInvocation(heartRateService);

            mAndroidUpnpService.getControlPoint().execute(
                    new ActionCallback(startHeartRateMeasuring) {
                        @Override
                        public void success(ActionInvocation invocation) {
                            ActionArgumentValue[] output = invocation.getOutput();

                            for(ActionArgumentValue value : output) {
                                Log.d("test", String.valueOf(value));
                            }
                        }

                        @Override
                        public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                            int i = 0;
                            i++;
                        }
                    }
            );
        }

        class StartHeartRateMeasuringActionInvocation extends ActionInvocation {
            StartHeartRateMeasuringActionInvocation(Service service) {
                super(service.getAction("GetHeartRate"));
            }
        }
    }
}
