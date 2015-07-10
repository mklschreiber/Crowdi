package eu.applabs.crowdsensingtv.gui;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.List;

import eu.applabs.crowdsensinglibrary.ILibraryResultListener;
import eu.applabs.crowdsensinglibrary.Library;
import eu.applabs.crowdsensinglibrary.data.Command;
import eu.applabs.crowdsensinglibrary.data.Field;
import eu.applabs.crowdsensinglibrary.data.Poll;
import eu.applabs.crowdsensingtv.R;
import eu.applabs.crowdsensingtv.base.CSActivity;

public class SinglePollActivity extends CSActivity implements ILibraryResultListener,
        View.OnClickListener {

    public static final String EXTRA_URL = "SinglePollActivityExtraUrl";

    private static final String sClassName = SinglePollActivity.class.getSimpleName();

    private Activity mActivity = null;

    private SinglePollFragment mSinglePollFragment = null;
    private ProgressBar mProgressBar = null;

    private String mPollUrl = null;
    private Poll mPoll = null;
    private int mCurrentField = 0;

    private Library mLibrary = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singlepoll);

        mActivity = this;

        initializeButtons();
        mProgressBar = (ProgressBar) findViewById(R.id.id_SinglePollActivity_ProgressBar);

        mPollUrl = checkStartingIntent();
        mLibrary = Library.getInstance();
        mLibrary.registerListener(this);

        if(mPollUrl != null
                && mPollUrl.compareTo("") != 0
                && mLibrary != null
                && mLibrary.accountAvailable()) {

            mLibrary.loadPoll(mPollUrl);

        } else {
            Toast.makeText(this, "Error during starting process...", Toast.LENGTH_SHORT).show();
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
    public void onLibraryResult(final ExecutionStatus status, final Poll poll) {
        mPoll = poll;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadFragment(0, true);
            }
        });
    }

    @Override
    public void onLibraryResult(final ExecutionStatus status, final List<Command> list) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FinishedPollDialog dialog = new FinishedPollDialog(mActivity, mActivity, list);
                dialog.show();
            }
        });
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
        Button b = (Button) findViewById(R.id.id_SinglePollActivity_Button_Left);
        b.setOnClickListener(this);
        b = (Button) findViewById(R.id.id_SinglePollActivity_Button_Right);
        b.setOnClickListener(this);
    }

    private String checkStartingIntent() {
        Intent startingIntent = getIntent();
        Bundle extras = startingIntent.getExtras();

        if(extras != null && extras.containsKey(EXTRA_URL)) {
            return extras.getString(EXTRA_URL);
        }

        return "";
    }

    private void loadNextFragment() {
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
                mLibrary.uploadPoll(mPollUrl, mPoll.toJSON().toString());
            }
        }
    }

    private void loadPrevFragment() {
        mSinglePollFragment.updateFieldValues();

        if(mPoll != null && mCurrentField > 0) {
            mCurrentField--;
            loadFragment(mCurrentField, false);
        }
    }

    private void loadFragment(int number, boolean leftSlide) {
        if(mPoll != null && mPoll.getFieldList().size() > number) {
            mProgressBar.setProgress((number * 100) / mPoll.getFieldList().size());

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
        }
    }
}
