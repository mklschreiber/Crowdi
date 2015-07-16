package eu.applabs.crowdsensingtv.base;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

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
import eu.applabs.crowdsensinglibrary.data.Field;
import eu.applabs.crowdsensinglibrary.gui.CSFitnessRequestResultDialog;

public class CSEditTextGroup extends LinearLayout implements View.OnClickListener,
        FitnessLibrary.IFitnessLibraryListener,
        CSFitnessRequestResultDialog.ICSFitnessRequestResultDialogListener{

    public enum QuestionType {
        Undefined,
        Steps,
        Activity
    }

    private static final int sGoogleId = 0;
    private static final int sAppleId = 1;
    private static final int sMicrosoftId = 2;

    private CSEditTextGroup mCSEditTextGroup = null;

    private Activity mActivity = null;
    private Field mField = null;
    private FitnessLibrary mFitnessLibrary = null;
    private List<Portal.PortalType> mConnectedPortalList = null;

    private EditText mEditText = null;

    private int mRequestId = -1;

    public CSEditTextGroup(Activity activity, Field field) {
        super(activity);

        mCSEditTextGroup = this;

        setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        setWeightSum(8);

        mActivity = activity;
        mField = field;

        mFitnessLibrary = FitnessLibrary.getInstance();
        mConnectedPortalList = mFitnessLibrary.getConnectedPortals();

        mEditText = new EditText(mActivity);
        mEditText.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                5f));
        mEditText.setHint(mField.getLabel());
        mEditText.setText(mField.getValue());
        mEditText.setId(mField.getId());
        mEditText.setFocusable(true);
        addView(mEditText);

        if(checkQuestionType(field.getLabel()) != QuestionType.Undefined) {
            if (mConnectedPortalList.contains(Portal.PortalType.Google)) {
                //ImageButton imageButtonGoogle = new ImageButton(mActivity);
                //imageButtonGoogle.seth

                Button buttonGoogle = new Button(mActivity);
                buttonGoogle.setLayoutParams(new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f));
                buttonGoogle.setText("Google");
                buttonGoogle.setId(sGoogleId);
                buttonGoogle.setOnClickListener(this);
                addView(buttonGoogle);
            }

            if (mConnectedPortalList.contains(Portal.PortalType.Apple)) {
                Button buttonApple = new Button(mActivity);
                buttonApple.setLayoutParams(new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f));
                buttonApple.setText("Apple");
                buttonApple.setId(sAppleId);
                buttonApple.setOnClickListener(this);
                addView(buttonApple);
            }

            if (mConnectedPortalList.contains(Portal.PortalType.Microsoft)) {
                Button buttonMicrosoft = new Button(mActivity);
                buttonMicrosoft.setLayoutParams(new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f));
                buttonMicrosoft.setText("Microsoft");
                buttonMicrosoft.setId(sMicrosoftId);
                buttonMicrosoft.setOnClickListener(this);
                addView(buttonMicrosoft);
            }
        } else {
            mEditText.setLayoutParams(new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    8f));
        }
    }

    public EditText getEditText() {
        return mEditText;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if(mFitnessLibrary != null) {
            mFitnessLibrary.registerListener(this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if(mFitnessLibrary != null) {
            mFitnessLibrary.unregisterListener(this);
        }
    }

    private QuestionType checkQuestionType(String question) {
        String temp = question.toLowerCase();

        //  TODO Change to steps
        if(temp.contains("s")) {
            return QuestionType.Steps;
        } else if(temp.contains("activity")) {
            return QuestionType.Activity;
        }

        return QuestionType.Undefined;
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
            case sGoogleId:
                switch(checkQuestionType(mField.getLabel())) {
                    case Steps:
                        mFitnessLibrary.getSteps(Portal.PortalType.Google,
                                startTime,
                                endTime,
                                TimeUnit.MILLISECONDS,
                                1,
                                TimeUnit.DAYS,
                                mRequestId);
                        break;
                    case Activity:
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
            case sAppleId:
                switch(checkQuestionType(mField.getLabel())) {
                    case Steps:
                        mFitnessLibrary.getSteps(Portal.PortalType.Apple,
                                startTime,
                                endTime,
                                TimeUnit.MILLISECONDS,
                                1,
                                TimeUnit.DAYS,
                                mRequestId);
                        break;
                    case Activity:
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
            case sMicrosoftId:
                switch(checkQuestionType(mField.getLabel())) {
                    case Steps:
                        mFitnessLibrary.getSteps(Portal.PortalType.Microsoft,
                                startTime,
                                endTime,
                                TimeUnit.MILLISECONDS,
                                1,
                                TimeUnit.DAYS,
                                mRequestId);
                        break;
                    case Activity:
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

                        valueLabelList.add("Steps\n\n" + startDate + "\n - \n" + endDate);
                    }

                    CSFitnessRequestResultDialog dialog = new CSFitnessRequestResultDialog(mActivity, "Anzahl", "Zeitraum", valueList, valueLabelList);
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
}
