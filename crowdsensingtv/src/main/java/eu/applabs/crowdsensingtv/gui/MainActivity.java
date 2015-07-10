/*package eu.applabs.crowdsensingtv.gui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.fourthline.cling.android.AndroidUpnpServiceImpl;
import org.fourthline.cling.model.action.ActionArgumentValue;

import java.util.List;

import eu.applabs.crowdsensinglibrary.ILibraryResultListener;
import eu.applabs.crowdsensinglibrary.Library;
import eu.applabs.crowdsensinglibrary.data.Command;
import eu.applabs.crowdsensinglibrary.data.Poll;
import eu.applabs.crowdsensinglibrary.gui.CSFitnessRequestResultDialog;
import eu.applabs.crowdsensinglibrary.gui.LoginDialog;
import eu.applabs.crowdsensinglibrary.settings.LibrarySettingsManager;
import eu.applabs.crowdsensingtv.R;

import eu.applabs.crowdsensingtv.service.UpnpService;
import eu.applabs.crowdsensingupnplibrary.service.HeartRateServiceSenderConnection;

public class MainActivity extends Activity implements ILibraryResultListener,
        View.OnClickListener,
        HeartRateServiceSenderConnection.IHeartRateServiceSenderConnectionListener,
        CSFitnessRequestResultDialog.ICSFitnessRequestResultDialogListener,
        LoginDialog.ILoginDialogListener{

    public static final String BASE_URL = "http://as.applabs.eu:8080/FancyModule/";

    private MainActivity mActivity = null;

    private LinearLayout mLinearLayout = null;
    private List<Command> mCommandList = null;

    private HeartRateServiceSenderConnection mHeartRateServiceSenderConnection = null;

    private LibrarySettingsManager mLibraryLibrarySettingsManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mActivity = this;
        mLinearLayout = (LinearLayout) findViewById(R.id.id_MainActivity_LL_Commands);

        mLibraryLibrarySettingsManager = new LibrarySettingsManager(this);
        mHeartRateServiceSenderConnection = new HeartRateServiceSenderConnection();

        if(!mLibraryLibrarySettingsManager.loginAvailable()) {
            LoginDialog dialog = new LoginDialog(this);
            dialog.registerListener(this);
            dialog.show();
        } else {
            onLoginSaved();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(this, UpnpService.class);
        startService(intent);

        mHeartRateServiceSenderConnection.registerListener(this);
        bindService(new Intent(this, AndroidUpnpServiceImpl.class), mHeartRateServiceSenderConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();

        mHeartRateServiceSenderConnection.unregisterListener(this);
        unbindService(mHeartRateServiceSenderConnection);
    }

    @Override
    public void onDeviceAdded() {
        mHeartRateServiceSenderConnection.startNotification("Pizza", "Pizza bestellen", "http://as.applabs.eu:8080/FancyModule/pizza");
        mHeartRateServiceSenderConnection.getHeartRate();
    }

    @Override
    public void onResponseAvailable(String method, boolean success, ActionArgumentValue[] output) {
        Toast.makeText(getApplicationContext(), method + ", Status: " + String.valueOf(success), Toast.LENGTH_SHORT).show();
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

    @Override
    public void onValueSelected(String value) {
        Toast.makeText(this, value, Toast.LENGTH_SHORT).show();
    }

    // ILoginDialogListener

    @Override
    public void onLoginCanceled() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        });
    }

    @Override
    public void onLoginSaved() {
        if(mLibraryLibrarySettingsManager != null && mLibraryLibrarySettingsManager.loginAvailable()) {
            Library library = new Library();
            library.registerListener(this);
            library.loadCommands(BASE_URL + "start",
                    mLibraryLibrarySettingsManager.getUserName(),
                    mLibraryLibrarySettingsManager.getPassword());

            startPeriodicNotification();
        }
    }
}
*/