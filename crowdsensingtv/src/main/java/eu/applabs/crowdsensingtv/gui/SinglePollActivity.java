package eu.applabs.crowdsensingtv.gui;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import eu.applabs.crowdsensinglibrary.ILibraryResultListener;
import eu.applabs.crowdsensinglibrary.Library;
import eu.applabs.crowdsensinglibrary.data.Poll;
import eu.applabs.crowdsensingtv.R;
import eu.applabs.crowdsensingtv.base.CrowdSensingActivity;

public class SinglePollActivity extends CrowdSensingActivity implements ILibraryResultListener,
        View.OnClickListener {

    private static final String sClassName = SinglePollActivity.class.getSimpleName();

    private SinglePollFragment mSinglePollFragment = null;
    private ProgressBar mProgressBar = null;

    private Poll mPoll = null;
    private int mCurrentField = 0;

    private Library mLibrary = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singlepoll);

        mProgressBar = (ProgressBar) findViewById(R.id.id_SinglePollActivity_ProgressBar);

        mLibrary = new Library();
        mLibrary.registerListener(this);
        mLibrary.loadPoll("https://www.applabs.eu/json.txt"); // Web resource

        Button b = (Button) findViewById(R.id.id_SinglePollActivity_Button_Left);
        b.setOnClickListener(this);
        b = (Button) findViewById(R.id.id_SinglePollActivity_Button_Right);
        b.setOnClickListener(this);
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

    private void loadNextFragment() {
        if(mPoll != null && mPoll.getFieldList().size() > (mCurrentField + 1)) {
            mCurrentField++;
            loadFragment(mCurrentField, true);
        } else {
            // TODO: Send the data

            Intent intent = new Intent(this, FinishedPollActivity.class);
            startActivity(intent);

            // Clear from stack
            finish();
        }
    }

    private void loadPrevFragment() {
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
