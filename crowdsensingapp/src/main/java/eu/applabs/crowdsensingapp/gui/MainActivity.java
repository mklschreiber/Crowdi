package eu.applabs.crowdsensingapp.gui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import eu.applabs.crowdsensingapp.R;
import eu.applabs.crowdsensingapp.service.UpnpService;
import eu.applabs.crowdsensingfitnesslibrary.FitnessLibrary;
import eu.applabs.crowdsensingfitnesslibrary.data.ActivityBucket;
import eu.applabs.crowdsensingfitnesslibrary.data.Person;
import eu.applabs.crowdsensingfitnesslibrary.data.StepBucket;
import eu.applabs.crowdsensingfitnesslibrary.portal.Portal;
import eu.applabs.crowdsensinglibrary.gui.CSFitnessRequestResultDialog;
import eu.applabs.crowdsensingwearlibrary.service.DataTransferService;

public class MainActivity extends Activity implements
        View.OnClickListener,
        FitnessLibrary.IFitnessLibraryListener,
        CSFitnessRequestResultDialog.ICSFitnessRequestResultDialogListener {

    private MainActivity mActivity = null;
    private UpnpService mUpnpService = null;
    private UpnpServiceConnection mUpnpServiceConnection = null;

    private int mRequestId = 0;

    private FitnessLibrary mFitnessLibrary = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Fake boot_complete to ensure the starting of the service
        BootupActivity ba = new BootupActivity();
        ba.onReceive(this, new Intent().setAction(Intent.ACTION_BOOT_COMPLETED));

        mUpnpServiceConnection = new UpnpServiceConnection();
        bindService(new Intent(this, UpnpService.class),
                mUpnpServiceConnection,
                Context.BIND_AUTO_CREATE);

        mActivity = this;

        mFitnessLibrary = FitnessLibrary.getInstance();
        mFitnessLibrary.init(this);
        mFitnessLibrary.registerListener(this);
        mFitnessLibrary.connect(Portal.PortalType.Google);

        Button b = (Button) findViewById(R.id.button);
        b.setOnClickListener(this);

        b = (Button) findViewById(R.id.button2);
        b.setOnClickListener(this);

        b = (Button) findViewById(R.id.button3);
        b.setOnClickListener(this);

        b = (Button) findViewById(R.id.button4);
        b.setOnClickListener(this);

        b = (Button) findViewById(R.id.button5);
        b.setOnClickListener(this);
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

        unbindService(mUpnpServiceConnection);

        if(mFitnessLibrary != null) {
            mFitnessLibrary.disconnect(Portal.PortalType.Google);
            mFitnessLibrary = null;
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
            case R.id.button:
                if(mUpnpService != null) {
                    mUpnpService.showNotification("Pizza", "Pizza wählen", "Auf TV starten",
                            DataTransferService.ACTION_START_ON_TV, "http://as.applabs.eu:8080/FancyModule/pizza");
                }
                break;
            case R.id.button2:
                if(mUpnpService != null) {
                    mUpnpService.startPoll("http://as.applabs.eu:8080/FancyModule/pizza");
                }
                break;
            case R.id.button3:
                mFitnessLibrary.getPerson(Portal.PortalType.Google);
                break;
            case R.id.button4:
                mFitnessLibrary.getSteps(Portal.PortalType.Google,
                        startTime,
                        endTime,
                        TimeUnit.MILLISECONDS,
                        1,
                        TimeUnit.DAYS,
                        mRequestId
                        );
                break;
            case R.id.button5:
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
        // Nothing to do...
    }

    @Override
    public void onValueSelected(String value) {
        Toast.makeText(this, value, Toast.LENGTH_SHORT).show();
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
