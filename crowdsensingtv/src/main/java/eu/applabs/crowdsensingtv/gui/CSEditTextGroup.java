package eu.applabs.crowdsensingtv.gui;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import org.fourthline.cling.model.action.ActionArgumentValue;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import eu.applabs.crowdsensingfitnesslibrary.FitnessLibrary;
import eu.applabs.crowdsensingfitnesslibrary.data.ActivityBucket;
import eu.applabs.crowdsensingfitnesslibrary.data.Person;
import eu.applabs.crowdsensingfitnesslibrary.data.StepBucket;
import eu.applabs.crowdsensingfitnesslibrary.portal.Portal;
import eu.applabs.crowdsensingfitnesslibrary.util.QuestionChecker;
import eu.applabs.crowdsensinglibrary.data.Field;
import eu.applabs.crowdsensinglibrary.gui.CSFitnessRequestResultDialog;
import eu.applabs.crowdsensingtv.R;
import eu.applabs.crowdsensingupnplibrary.service.HeartRateDataServiceReceiverConnection;
import eu.applabs.crowdsensingupnplibrary.service.WearNotificationServiceSenderConnection;

public class CSEditTextGroup extends RelativeLayout implements
        View.OnClickListener,
        FitnessLibrary.IFitnessLibraryListener,
        CSFitnessRequestResultDialog.ICSFitnessRequestResultDialogListener,
        WearNotificationServiceSenderConnection.IWearNotificationServiceSenderConnectionListener,
        CSHeartRateDialog.ICSHeartRateDialogListener {

    private CSEditTextGroup mCSEditTextGroup = null;

    private Activity mActivity = null;
    private Field mField = null;
    private WearNotificationServiceSenderConnection mWearNotificationServiceSenderConnection = null;
    private HeartRateDataServiceReceiverConnection mHeartRateDataServiceReceiverConnection = null;
    private FitnessLibrary mFitnessLibrary = null;
    private List<Portal.PortalType> mConnectedPortalList = null;

    private EditText mEditText = null;

    private int mRequestId = -1;

    public CSEditTextGroup(Activity activity, Field field,
                           WearNotificationServiceSenderConnection wearNotificationServiceSenderConnection,
                           HeartRateDataServiceReceiverConnection heartRateDataServiceReceiverConnection) {
        super(activity);

        mCSEditTextGroup = this;
        mActivity = activity;
        mField = field;

        mWearNotificationServiceSenderConnection = wearNotificationServiceSenderConnection;
        mHeartRateDataServiceReceiverConnection = heartRateDataServiceReceiverConnection;

        mFitnessLibrary = FitnessLibrary.getInstance();
        mConnectedPortalList = mFitnessLibrary.getConnectedPortals();

        setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View csEditTextGroupView;

        QuestionChecker.QuestionType questionType = QuestionChecker.check(mActivity, mField.getLabel());

        if(questionType == QuestionChecker.QuestionType.Undefined ||
                (questionType != QuestionChecker.QuestionType.Heart_Rate
                        && mConnectedPortalList.size() == 0)) {

            // If no specific question was found or no portal is connected show the regular EditText

            csEditTextGroupView = inflater.inflate(R.layout.view_csedittextgroup, this, false);
            mEditText = (EditText) csEditTextGroupView.findViewById(R.id.id_CSEditTextGroup_EditText);
        } else if (questionType == QuestionChecker.QuestionType.Heart_Rate) {

            // If the heart rate keyword was detected show the heart rate layout

            csEditTextGroupView = inflater.inflate(R.layout.view_csedittextgroup_heartrate, this, false);
            mEditText = (EditText) csEditTextGroupView.findViewById(R.id.id_CSEditTextGroup_EditText);

            Button buttonMeasureHeartRate = (Button) csEditTextGroupView.findViewById(R.id.id_CSEditTextGroup_Button_MeasureHeartRate);
            buttonMeasureHeartRate.setOnClickListener(this);
        } else {

            // Show the fitness layout

            mFitnessLibrary.registerListener(this);

            csEditTextGroupView = inflater.inflate(R.layout.view_csedittextgroup_fitness, this, false);
            mEditText = (EditText) csEditTextGroupView.findViewById(R.id.id_CSEditTextGroup_EditText);

            ImageButton imageButtonGoogle = (ImageButton) csEditTextGroupView.findViewById(R.id.id_CSEditTextGroup_ImageButton_GoogleFit);
            imageButtonGoogle.setOnClickListener(this);
            ImageButton imageButtonApple = (ImageButton) csEditTextGroupView.findViewById(R.id.id_CSEditTextGroup_ImageButton_AppleHealthKit);
            imageButtonApple.setOnClickListener(this);
            ImageButton imageButtonMicrosoft = (ImageButton) csEditTextGroupView.findViewById(R.id.id_CSEditTextGroup_ImageButton_MicrosoftHealthVault);
            imageButtonMicrosoft.setOnClickListener(this);
            ImageButton imageButtonFake = (ImageButton) csEditTextGroupView.findViewById(R.id.id_CSEditTextGroup_ImageButton_Fake);
            imageButtonFake.setOnClickListener(this);

            if (!mConnectedPortalList.contains(Portal.PortalType.Google)) {
                imageButtonGoogle.setVisibility(GONE);
            }

            if (!mConnectedPortalList.contains(Portal.PortalType.Apple)) {
                imageButtonApple.setVisibility(GONE);
            }

            if (!mConnectedPortalList.contains(Portal.PortalType.Microsoft)) {
                imageButtonMicrosoft.setVisibility(GONE);
            }

            if (!mConnectedPortalList.contains(Portal.PortalType.Fake)) {
                imageButtonFake.setVisibility(GONE);
            }
        }

        mEditText.setText(mField.getValue());

        addView(csEditTextGroupView);
    }

    public EditText getEditText() {
        return mEditText;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if(mFitnessLibrary != null) {
            mFitnessLibrary.unregisterListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.MONTH, -1);
        long startTime = cal.getTimeInMillis();
        mRequestId = (int) (Math.random() * 1000);

        switch (v.getId()) {
            case R.id.id_CSEditTextGroup_ImageButton_GoogleFit:
                switch(QuestionChecker.check(mActivity, mField.getLabel())) {
                    case Steps:
                        mFitnessLibrary.getSteps(Portal.PortalType.Google,
                                startTime,
                                endTime,
                                TimeUnit.MILLISECONDS,
                                1,
                                TimeUnit.DAYS,
                                mRequestId);
                        break;
                    case Activity_Count:
                        mFitnessLibrary.getActivities(Portal.PortalType.Google,
                                startTime,
                                endTime,
                                TimeUnit.MILLISECONDS,
                                1,
                                TimeUnit.DAYS,
                                mRequestId);
                        break;
                    case Activity_Duration:
                        mFitnessLibrary.getActivities(Portal.PortalType.Google,
                                startTime,
                                endTime,
                                TimeUnit.MILLISECONDS,
                                1,
                                TimeUnit.DAYS,
                                mRequestId);
                        break;
                }
                break;
            case R.id.id_CSEditTextGroup_ImageButton_AppleHealthKit:
                switch(QuestionChecker.check(mActivity, mField.getLabel())) {
                    case Steps:
                        mFitnessLibrary.getSteps(Portal.PortalType.Apple,
                                startTime,
                                endTime,
                                TimeUnit.MILLISECONDS,
                                1,
                                TimeUnit.DAYS,
                                mRequestId);
                        break;
                    case Activity_Count:
                        mFitnessLibrary.getActivities(Portal.PortalType.Apple,
                                startTime,
                                endTime,
                                TimeUnit.MILLISECONDS,
                                1,
                                TimeUnit.DAYS,
                                mRequestId);
                        break;
                    case Activity_Duration:
                        mFitnessLibrary.getActivities(Portal.PortalType.Apple,
                                startTime,
                                endTime,
                                TimeUnit.MILLISECONDS,
                                1,
                                TimeUnit.DAYS,
                                mRequestId);
                        break;
                }
                break;
            case R.id.id_CSEditTextGroup_ImageButton_MicrosoftHealthVault:
                switch(QuestionChecker.check(mActivity, mField.getLabel())) {
                    case Steps:
                        mFitnessLibrary.getSteps(Portal.PortalType.Microsoft,
                                startTime,
                                endTime,
                                TimeUnit.MILLISECONDS,
                                1,
                                TimeUnit.DAYS,
                                mRequestId);
                        break;
                    case Activity_Count:
                        mFitnessLibrary.getActivities(Portal.PortalType.Microsoft,
                                startTime,
                                endTime,
                                TimeUnit.MILLISECONDS,
                                1,
                                TimeUnit.DAYS,
                                mRequestId);
                        break;
                    case Activity_Duration:
                        mFitnessLibrary.getActivities(Portal.PortalType.Microsoft,
                                startTime,
                                endTime,
                                TimeUnit.MILLISECONDS,
                                1,
                                TimeUnit.DAYS,
                                mRequestId);
                        break;
                }
                break;
            case R.id.id_CSEditTextGroup_ImageButton_Fake:
                switch(QuestionChecker.check(mActivity, mField.getLabel())) {
                    case Steps:
                        mFitnessLibrary.getSteps(Portal.PortalType.Fake,
                                startTime,
                                endTime,
                                TimeUnit.MILLISECONDS,
                                1,
                                TimeUnit.DAYS,
                                mRequestId);
                        break;
                    case Activity_Count:
                        mFitnessLibrary.getActivities(Portal.PortalType.Fake,
                                startTime,
                                endTime,
                                TimeUnit.MILLISECONDS,
                                1,
                                TimeUnit.DAYS,
                                mRequestId);
                        break;
                    case Activity_Duration:
                        mFitnessLibrary.getActivities(Portal.PortalType.Fake,
                                startTime,
                                endTime,
                                TimeUnit.MILLISECONDS,
                                1,
                                TimeUnit.DAYS,
                                mRequestId);
                        break;
                }
                break;
            case R.id.id_CSEditTextGroup_Button_MeasureHeartRate:
                mWearNotificationServiceSenderConnection.getHeartRate();

                CSHeartRateDialog csHeartRateDialog = new CSHeartRateDialog(mActivity,
                        mHeartRateDataServiceReceiverConnection);
                csHeartRateDialog.registerListener(this);
                csHeartRateDialog.show();
                break;
        }
    }

    @Override
    public void onPersonReceived(ExecutionStatus status, int requestId, Person person) {
        // Not needed
    }

    @Override
    public void onStepsReceived(ExecutionStatus status, int requestId, final List<StepBucket> list) {
        if(status == ExecutionStatus.Success && requestId == mRequestId) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ArrayList<String> valueList = new ArrayList<>();
                    ArrayList<String> valueLabelList = new ArrayList<>();

                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

                    for (StepBucket bucket : list) {
                        valueList.add(String.valueOf(bucket.getStepCount()));
                        String startDate = sdf.format(bucket.getStepStartDate());
                        String endDate = sdf.format(bucket.getStepEndDate());

                        valueLabelList.add(mActivity.getString(R.string.CSFitnessRequestResultDialog_Label_Steps)
                                + "\n\n" + startDate + "\n - \n" + endDate);
                    }

                    CSFitnessRequestResultDialog dialog = new CSFitnessRequestResultDialog(mActivity,
                            mActivity.getString(R.string.CSFitnessRequestResultDialog_Label_Count),
                            mActivity.getString(R.string.CSFitnessRequestResultDialog_Label_TimeRange),
                            valueList,
                            valueLabelList);

                    dialog.setTitle(getResources().getString(R.string.CSFitnessRequestResultDialog_Title));
                    dialog.registerListener(mCSEditTextGroup);
                    dialog.show();
                }
            });
        }
    }

    @Override
    public void onActivitiesReceived(ExecutionStatus status, int requestId, final List<ActivityBucket> list) {
        if(status == ExecutionStatus.Success && requestId == mRequestId) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    QuestionChecker.QuestionType type = QuestionChecker.check(mActivity, mField.getLabel());

                    ArrayList<String> valueList = new ArrayList<>();
                    ArrayList<String> valueLabelList = new ArrayList<>();

                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                    String unit = null;

                    for (ActivityBucket bucket : list) {
                        if(type == QuestionChecker.QuestionType.Activity_Count) {
                            valueList.add(String.valueOf(bucket.getActivityCount()));
                        } else {
                            valueList.add(String.valueOf(bucket.getActivityDuration() / 1000 / 60));
                            unit = getResources().getString(R.string.CSFitnessRequestResultDialog_Unit_Minutes);
                        }

                        String activity = eu.applabs.crowdsensingfitnesslibrary.data.Activity.convertToString(bucket.getActivityType());
                        String startDate = sdf.format(bucket.getActivityStartDate());
                        String endDate = sdf.format(bucket.getActivityEndDate());

                        if(unit != null) {
                            valueLabelList.add(unit + "\n\n" + activity + "\n\n" + startDate + "\n - \n" + endDate);
                        } else {
                            valueLabelList.add(activity + "\n\n" + startDate + "\n - \n" + endDate);
                        }
                    }

                    CSFitnessRequestResultDialog dialog = null;

                    if(type == QuestionChecker.QuestionType.Activity_Count) {
                        dialog = new CSFitnessRequestResultDialog(mActivity,
                                mActivity.getString(R.string.CSFitnessRequestResultDialog_Label_Count),
                                mActivity.getString(R.string.CSFitnessRequestResultDialog_Label_TimeRange),
                                valueList,
                                valueLabelList);
                    } else {
                        dialog = new CSFitnessRequestResultDialog(mActivity,
                                mActivity.getString(R.string.CSFitnessRequestResultDialog_Label_Duration),
                                mActivity.getString(R.string.CSFitnessRequestResultDialog_Label_TimeRange),
                                valueList,
                                valueLabelList);
                    }

                    dialog.setTitle(getResources().getString(R.string.CSFitnessRequestResultDialog_Title));
                    dialog.registerListener(mCSEditTextGroup);
                    dialog.show();
                }
            });
        }
    }

    @Override
    public void onPortalConnectionStateChanged() {
        // Not needed
    }

    @Override
    public void onValueSelected(String value) {
        if(mEditText != null) {
            mEditText.setText(value);
        }
    }

    @Override
    public void onHeartRateSelected(String value) {
        if(mEditText != null) {
            mEditText.setText(value);
        }
    }

    // Heart rate upnp service

    @Override
    public void onDeviceAdded() {

    }

    @Override
    public void onResponseAvailable(String method, boolean success, ActionArgumentValue[] output) {
        if(success) {

        }
    }
}
