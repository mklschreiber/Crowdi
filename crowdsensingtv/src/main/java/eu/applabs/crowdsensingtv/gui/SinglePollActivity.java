package eu.applabs.crowdsensingtv.gui;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v17.leanback.app.BackgroundManager;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.fourthline.cling.android.AndroidUpnpServiceImpl;

import java.util.List;

import eu.applabs.crowdsensinglibrary.ILibraryResultListener;
import eu.applabs.crowdsensinglibrary.Library;
import eu.applabs.crowdsensinglibrary.data.Command;
import eu.applabs.crowdsensinglibrary.data.Field;
import eu.applabs.crowdsensinglibrary.data.Poll;
import eu.applabs.crowdsensingtv.R;
import eu.applabs.crowdsensingtv.base.CSActivity;
import eu.applabs.crowdsensingupnplibrary.service.HeartRateDataServiceReceiverConnection;
import eu.applabs.crowdsensingupnplibrary.service.HeartRateServiceSenderConnection;

public class SinglePollActivity extends CSActivity implements ILibraryResultListener,
        View.OnClickListener {

    public static final String EXTRA_URL = "SinglePollActivityExtraUrl";

    private static final String sClassName = SinglePollActivity.class.getSimpleName();

    private Activity mActivity = null;

    private SinglePollFragment mSinglePollFragment = null;
    private Button mButtonPrev = null;
    private Button mButtonNext = null;
    private ProgressBar mProgressBar = null;

    private String mPollUrl = null;
    private Poll mPoll = null;
    private int mCurrentField = 0;

    private Library mLibrary = null;
    private HeartRateDataServiceReceiverConnection mHeartRateDataServiceReceiverConnection = null;
    private HeartRateServiceSenderConnection mHeartRateServiceSenderConnection = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singlepoll);

        mActivity = this;

        mHeartRateServiceSenderConnection = new HeartRateServiceSenderConnection();
        mHeartRateDataServiceReceiverConnection = new HeartRateDataServiceReceiverConnection();

        initializeButtons();
        mProgressBar = (ProgressBar) findViewById(R.id.id_SinglePollActivity_ProgressBar);

        mPollUrl = checkStartingIntent();

        mLibrary = Library.getInstance();
        mLibrary.registerListener(this);

        if(mPollUrl != null
                && mPollUrl.compareTo("") != 0
                && mLibrary != null
                && mLibrary.accountAvailable()) {

            mLibrary.loadPoll(mPollUrl, sClassName);
            changeButtonEnableStatus(true);
        } else {
            Toast.makeText(this, R.string.SinglePollActivity_Toast_Error, Toast.LENGTH_SHORT).show();
            changeButtonEnableStatus(false);
        }

        BackgroundManager backgroundManager = BackgroundManager.getInstance(this);
        backgroundManager.attach(this.getWindow());
        backgroundManager.setDrawable(getResources().getDrawable(R.drawable.background, null));
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mHeartRateServiceSenderConnection != null) {
            bindService(new Intent(mActivity, AndroidUpnpServiceImpl.class),
                    mHeartRateServiceSenderConnection,
                    Activity.BIND_AUTO_CREATE);
        }

        if(mHeartRateDataServiceReceiverConnection != null) {
            bindService(new Intent(mActivity, AndroidUpnpServiceImpl.class),
                    mHeartRateDataServiceReceiverConnection,
                    Activity.BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        mLibrary.unregisterListener(this);

        if(mHeartRateServiceSenderConnection != null) {
            unbindService(mHeartRateServiceSenderConnection);
        }

        if(mHeartRateDataServiceReceiverConnection != null) {
            unbindService(mHeartRateDataServiceReceiverConnection);
        }
    }

    @Override
    public View getFocusedView() {
        if(mSinglePollFragment != null) {
            return mSinglePollFragment.getFocusedView();
        }

        return null;
    }

    @Override
    public void onLibraryResult(final ExecutionStatus status, final Poll poll, final String className) {
        if(className.compareTo(sClassName) == 0) {
            mPoll = poll;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loadFragment(0, true);
                }
            });
        }
    }

    @Override
    public void onLibraryResult(final ExecutionStatus status, final List<Command> list, final String className) {
        if(className.compareTo(sClassName) == 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    FinishedPollDialog dialog = new FinishedPollDialog(mActivity, mActivity, list);
                    dialog.show();
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.id_SinglePollActivity_Button_Right:
                loadNextFragment();
                break;
            case R.id.id_SinglePollActivity_Button_Left:
                loadPrevFragment();
                break;
        }
    }

    private void initializeButtons() {
        mButtonPrev = (Button) findViewById(R.id.id_SinglePollActivity_Button_Left);
        mButtonPrev.setOnClickListener(this);
        mButtonNext = (Button) findViewById(R.id.id_SinglePollActivity_Button_Right);
        mButtonNext.setOnClickListener(this);
    }

    private void changeButtonEnableStatus(boolean status) {
        if(mButtonPrev != null && mButtonNext != null) {
            mButtonPrev.setEnabled(status);
            mButtonNext.setEnabled(status);
        }
    }

    private String checkStartingIntent() {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null && extras.containsKey(EXTRA_URL)) {
            return extras.getString(EXTRA_URL);
        }

        return "";
    }

    private void loadNextFragment() {
        if(mSinglePollFragment == null) {
            return;
        }

        mSinglePollFragment.updateFieldValues();

        if(mPoll != null && mPoll.getFieldList().size() > (mCurrentField + 1)) {
            if(mSinglePollFragment.allRequiredFieldsFilled()
                    && mSinglePollFragment.allInputsAreValid()) {
                mCurrentField++;
                loadFragment(mCurrentField, true);
            } else {
                Field f = mSinglePollFragment.getMissingField();

                if(f != null) {
                    Toast.makeText(this,
                            f.getLabel() + " input is missing",
                            Toast.LENGTH_SHORT).show();

                    return;
                }

                f = mSinglePollFragment.getInvalidField();

                if(f != null) {
                    Toast.makeText(this,
                            f.getLabel() + " input doesn't match pattern " + f.getPattern(),
                            Toast.LENGTH_SHORT).show();

                    return;
                }
            }
        } else {
            if(mLibrary != null && mLibrary.accountAvailable()) {
                mLibrary.uploadPoll(mPollUrl, mPoll.toJSON().toString(), sClassName);
            }
        }
    }

    private void loadPrevFragment() {
        if(mSinglePollFragment == null) {
            return;
        }

        mSinglePollFragment.updateFieldValues();

        if(mPoll != null && mCurrentField > 0) {
            mCurrentField--;
            loadFragment(mCurrentField, false);
        }
    }

    private void loadFragment(int number, boolean leftSlide) {
        if(mPoll != null && mPoll.getFieldList().size() > number) {
            mProgressBar.setProgress(((number + 1) * 100) / mPoll.getFieldList().size());

            mSinglePollFragment = new SinglePollFragment();

            FragmentTransaction ft = getFragmentManager().beginTransaction();

            if(leftSlide) {
                ft.setCustomAnimations(R.animator.enter_right, R.animator.exit_left);
            } else {
                ft.setCustomAnimations(R.animator.enter_left, R.animator.exit_right);
            }

            ft.replace(R.id.id_SinglePollActivity_FrameLayout, mSinglePollFragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.commit();

            mSinglePollFragment.setField(mPoll.getFieldList().get(number));
            mSinglePollFragment.setHeartRateServiceSenderConnection(mHeartRateServiceSenderConnection);
            mSinglePollFragment.setHeartRateDataServiceReceiverConnection(mHeartRateDataServiceReceiverConnection);
        }
    }
}
