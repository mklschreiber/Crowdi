package eu.applabs.crowdsensingapp.gui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import eu.applabs.crowdsensingapp.R;
import eu.applabs.crowdsensingapp.service.UpnpService;
import eu.applabs.crowdsensingapp.settings.SettingsManager;
import eu.applabs.crowdsensingfitnesslibrary.FitnessLibrary;
import eu.applabs.crowdsensingfitnesslibrary.data.ActivityBucket;
import eu.applabs.crowdsensingfitnesslibrary.data.Person;
import eu.applabs.crowdsensingfitnesslibrary.data.StepBucket;
import eu.applabs.crowdsensingfitnesslibrary.portal.Portal;
import eu.applabs.crowdsensinglibrary.gui.CSFitnessRequestResultDialog;
import eu.applabs.crowdsensingwearlibrary.service.DataTransferService;

public class TestActivity extends AppCompatActivity implements
        View.OnClickListener,
        FitnessLibrary.IFitnessLibraryListener,
        CSFitnessRequestResultDialog.ICSFitnessRequestResultDialogListener, CompoundButton.OnCheckedChangeListener {

    private TestActivity mActivity = null;
    private UpnpService mUpnpService = null;
    private UpnpServiceConnection mUpnpServiceConnection = null;

    private int mRequestId = 0;

    private SettingsManager mSettingsManager = null;
    private FitnessLibrary mFitnessLibrary = null;
    private Button mButtonStartPoll = null;
    private Button mButtonGetSteps = null;
    private Button mButtonGetActivities = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        mActivity = this;

        mSettingsManager = new SettingsManager(mActivity);

        mFitnessLibrary = FitnessLibrary.getInstance();
        mFitnessLibrary.registerListener(this);

        mButtonStartPoll = (Button) findViewById(R.id.id_MainActivity_Button_StartPoll);
        mButtonStartPoll.setOnClickListener(this);

        mButtonGetSteps = (Button) findViewById(R.id.id_MainActivity_Button_GetSteps);
        mButtonGetSteps.setOnClickListener(this);

        mButtonGetActivities = (Button) findViewById(R.id.id_MainActivity_Button_GetActivities);
        mButtonGetActivities.setOnClickListener(this);

        Button b = (Button) findViewById(R.id.id_MainActivity_Button_ShowNotification);
        b.setOnClickListener(this);

        Switch s = (Switch) findViewById(R.id.id_MainActivity_Switch_UPnPService);
        s.setOnCheckedChangeListener(this);

        if(mFitnessLibrary.getConnectedPortals().size() == 0) {
            mButtonGetActivities.setEnabled(false);
            mButtonGetSteps.setEnabled(false);
        }

        if(mSettingsManager.getUpnpServiceEnabled()) {
            s.setChecked(true);

            mUpnpServiceConnection = new UpnpServiceConnection();
            bindService(new Intent(this, UpnpService.class),
                    mUpnpServiceConnection,
                    Context.BIND_AUTO_CREATE);
        } else {
            mButtonStartPoll.setEnabled(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mFitnessLibrary.checkActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(mUpnpServiceConnection != null) {
            unbindService(mUpnpServiceConnection);
        }
    }

    @Override
    public void onClick(View v) {
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.WEEK_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();

        mRequestId = (int) (Math.random() * 1000);

        switch(v.getId()) {
            case R.id.id_MainActivity_Button_ShowNotification:
                if(mUpnpService != null) {
                    mUpnpService.showNotification("Pizza", "Pizza wählen", "Auf TV starten",
                            DataTransferService.ACTION_START_ON_TV, "http://as.applabs.eu:8080/FancyModule/pizza");
                }
                break;
            case R.id.id_MainActivity_Button_StartPoll:
                if(mUpnpService != null) {
                    mUpnpService.startPoll("http://as.applabs.eu:8080/FancyModule/pizza");
                }
                break;
            case R.id.id_MainActivity_Button_GetSteps:
                mFitnessLibrary.getSteps(Portal.PortalType.Google,
                        startTime,
                        endTime,
                        TimeUnit.MILLISECONDS,
                        1,
                        TimeUnit.DAYS,
                        mRequestId
                        );
                break;
            case R.id.id_MainActivity_Button_GetActivities:
                mFitnessLibrary.getActivities(Portal.PortalType.Google,
                        startTime,
                        endTime,
                        TimeUnit.MILLISECONDS,
                        1,
                        TimeUnit.DAYS,
                        mRequestId
                );
                break;
        }
    }

    @Override
    public void onPersonReceived(ExecutionStatus status, int requestId, Person person) {

    }

    @Override
    public void onStepsReceived(ExecutionStatus status, int requestId, final List<StepBucket> list) {
        if(status == ExecutionStatus.Success && requestId == mRequestId) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ArrayList<String> valueList = new ArrayList<>();
                    ArrayList<String> valueLabelList = new ArrayList<>();

                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

                    for (StepBucket bucket : list) {
                        valueList.add(String.valueOf(bucket.getStepCount()));
                        String startDate = sdf.format(bucket.getStepStartDate());
                        String endDate = sdf.format(bucket.getStepEndDate());

                        valueLabelList.add("Steps\n\n" + startDate + "\n - \n" + endDate);
                    }

                    CSFitnessRequestResultDialog dialog = new CSFitnessRequestResultDialog(mActivity, "Anzahl", "Zeitraum", valueList, valueLabelList);
                    dialog.registerListener(mActivity);
                    dialog.show();
                }
            });
        }
    }

    @Override
    public void onActivitiesReceived(ExecutionStatus status, int requestId, final List<ActivityBucket> list) {
        if(status == ExecutionStatus.Success && requestId == mRequestId) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ArrayList<String> valueList = new ArrayList<>();
                    ArrayList<String> valueLabelList = new ArrayList<>();

                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

                    for (ActivityBucket bucket : list) {
                        valueList.add(String.valueOf(bucket.getActivityCount()));
                        String activity = eu.applabs.crowdsensingfitnesslibrary.data.Activity.convertToString(bucket.getActivityType());
                        String startDate = sdf.format(bucket.getActivityStartDate());
                        String endDate = sdf.format(bucket.getActivityEndDate());

                        valueLabelList.add(activity + "\n\n" + startDate + "\n - \n" + endDate);
                    }

                    CSFitnessRequestResultDialog dialog = new CSFitnessRequestResultDialog(mActivity, "Anzahl", "Zeitraum", valueList, valueLabelList);
                    dialog.registerListener(mActivity);
                    dialog.show();
                }
            });
        }
    }

    @Override
    public void onPortalConnectionStateChanged() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mButtonGetActivities != null && mButtonGetSteps != null) {
                    if(mFitnessLibrary.getConnectedPortals().size() == 0) {
                        mButtonGetActivities.setEnabled(false);
                        mButtonGetSteps.setEnabled(false);
                    } else {
                        mButtonGetActivities.setEnabled(true);
                        mButtonGetSteps.setEnabled(true);
                    }
                }
            }
        });
    }

    @Override
    public void onValueSelected(String value) {
        Toast.makeText(this, value, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Intent intent = new Intent(this, UpnpService.class);

        if(buttonView.getId() == R.id.id_MainActivity_Switch_UPnPService) {
            if(isChecked) {
                mSettingsManager.setUpnpServiceEnabled(true);

                startService(intent);
                mButtonStartPoll.setEnabled(true);
            } else {
                mSettingsManager.setUpnpServiceEnabled(false);

                stopService(intent);
                mButtonStartPoll.setEnabled(false);
            }
        }
    }

    private class UpnpServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            UpnpService.LocalBinder binder = (UpnpService.LocalBinder) service;
            mUpnpService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mUpnpService = null;
        }
    }
}
