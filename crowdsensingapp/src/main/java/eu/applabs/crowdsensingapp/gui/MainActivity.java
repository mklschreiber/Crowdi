package eu.applabs.crowdsensingapp.gui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.fourthline.cling.android.AndroidUpnpServiceImpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import eu.applabs.crowdsensingapp.R;
import eu.applabs.crowdsensingfitnesslibrary.FitnessLibrary;
import eu.applabs.crowdsensingfitnesslibrary.data.ActivityBucket;
import eu.applabs.crowdsensingfitnesslibrary.data.Person;
import eu.applabs.crowdsensingfitnesslibrary.data.StepBucket;
import eu.applabs.crowdsensingfitnesslibrary.portal.Portal;
import eu.applabs.crowdsensinglibrary.gui.CSFitnessRequestResultDialog;
import eu.applabs.crowdsensingwearlibrary.gui.WearConnectionActivity;
import eu.applabs.crowdsensingwearlibrary.service.DataTransferService;
import eu.applabs.crowdsensingupnplibrary.service.HeartRateServiceReceiverConnection;
import eu.applabs.crowdsensingupnplibrary.service.StartPollServiceSenderConnection;

public class MainActivity extends WearConnectionActivity implements View.OnClickListener,
        HeartRateServiceReceiverConnection.IHeartRateServiceReceiverConnectionListener,
        FitnessLibrary.IFitnessLibraryListener, CSFitnessRequestResultDialog.ICSFitnessRequestResultDialogListener {

    private MainActivity mActivity = null;
    private HeartRateServiceReceiverConnection mHeartRateServiceReceiverConnection;
    private StartPollServiceSenderConnection mStartPollServiceSenderConnection;

    private FitnessLibrary mFitnessLibrary = null;

    @Override
    public void onDataReceived(String data) {
        Toast.makeText(this, "Received data: " + data, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStartOnTvReceived(String url) {
        if(mStartPollServiceSenderConnection != null) {
            mStartPollServiceSenderConnection.startPoll(url);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActivity = this;

        mFitnessLibrary = new FitnessLibrary(this);
        mFitnessLibrary.registerListener(this);
        mFitnessLibrary.connect(Portal.PortalType.Google);

        mHeartRateServiceReceiverConnection = new HeartRateServiceReceiverConnection();
        mStartPollServiceSenderConnection = new StartPollServiceSenderConnection();

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

        mHeartRateServiceReceiverConnection.registerListener(this);
        bindService(new Intent(this, AndroidUpnpServiceImpl.class),
                mHeartRateServiceReceiverConnection,
                Context.BIND_AUTO_CREATE);

        bindService(new Intent(this, AndroidUpnpServiceImpl.class),
                mStartPollServiceSenderConnection,
                Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();

        mHeartRateServiceReceiverConnection.unregisterListener(this);
        unbindService(mHeartRateServiceReceiverConnection);

        unbindService(mStartPollServiceSenderConnection);

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

        switch(v.getId()) {
            case R.id.button:
                showNotification("Pizza", "Pizza wählen", "Auf TV starten",
                        DataTransferService.ACTION_START_ON_TV, "http://as.applabs.eu:8080/FancyModule/pizza");
                break;
            case R.id.button2:
                mStartPollServiceSenderConnection.startPoll("http://as.applabs.eu:8080/FancyModule/pizza");
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
                        TimeUnit.DAYS
                        );
                break;
            case R.id.button5:
                mFitnessLibrary.getActivities(Portal.PortalType.Google,
                        startTime,
                        endTime,
                        TimeUnit.MILLISECONDS,
                        1,
                        TimeUnit.DAYS
                );
                break;
        }
    }

    @Override
    public void onStartNotification(String title, String content, String url) {
        showNotification(title, content, "Auf TV starten", DataTransferService.ACTION_START_ON_TV, url);
    }

    @Override
    public void onStartMeasuring() {
        showNotification("Messung starten", "...");
    }

    @Override
    public void onPersonReceived(Person person) {

    }

    @Override
    public void onStepsReceived(final List<StepBucket> list) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> valueList = new ArrayList<>();
                ArrayList<String> valueLabelList = new ArrayList<>();

                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

                for(StepBucket bucket : list) {
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

    @Override
    public void onActivitiesReceived(final List<ActivityBucket> list) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> valueList = new ArrayList<>();
                ArrayList<String> valueLabelList = new ArrayList<>();

                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

                for(ActivityBucket bucket : list) {
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

    @Override
    public void onValueSelected(String value) {
        Toast.makeText(this, value, Toast.LENGTH_SHORT).show();
    }
}
