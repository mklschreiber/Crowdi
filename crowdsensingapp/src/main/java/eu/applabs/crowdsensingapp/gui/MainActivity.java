package eu.applabs.crowdsensingapp.gui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.List;

import eu.applabs.crowdsensingapp.R;
import eu.applabs.crowdsensingfitnesslibrary.FitnessLibrary;
import eu.applabs.crowdsensingfitnesslibrary.data.ActivityBucket;
import eu.applabs.crowdsensingfitnesslibrary.data.Person;
import eu.applabs.crowdsensingfitnesslibrary.data.StepBucket;
import eu.applabs.crowdsensingfitnesslibrary.portal.Portal;
import eu.applabs.crowdsensinglibrary.ILibraryResultListener;
import eu.applabs.crowdsensinglibrary.Library;
import eu.applabs.crowdsensinglibrary.data.Command;
import eu.applabs.crowdsensinglibrary.data.Poll;

public class MainActivity extends AppCompatActivity implements
        ILibraryResultListener,
        View.OnClickListener, FitnessLibrary.IFitnessLibraryListener {

    private static final String sClassName = MainActivity.class.getSimpleName();

    public static final String BASE_URL = "http://as.applabs.eu:8080/FancyModule/";
    public static final String START_URL = "http://as.applabs.eu:8080/FancyModule/start";

    private LinearLayout mLinearLayout = null;
    private List<Command> mCommandList = null;

    private Library mLibrary = null;
    private FitnessLibrary mFitnessLibrary = null;

    private MainActivity mActivity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mActivity = this;

        // Fake boot_complete to ensure the starting of the service
        BootupActivity ba = new BootupActivity();
        ba.onReceive(this, new Intent().setAction(Intent.ACTION_BOOT_COMPLETED));

        mLibrary = Library.getInstance();
        mLibrary.init(this);
        mLibrary.login("Hans", "Test");
        mLibrary.registerListener(this);

        mFitnessLibrary = FitnessLibrary.getInstance();
        mFitnessLibrary.init(this);
        mFitnessLibrary.registerListener(this);
        mFitnessLibrary.connect(Portal.PortalType.Google);

        mLibrary.loadCommands(START_URL, sClassName);

        mLinearLayout = (LinearLayout) findViewById(R.id.id_MainActivity_LinearLayout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.id_MainMenu_Action_Test:
                Intent intent = new Intent(this, TestActivity.class);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLibraryResult(final ILibraryResultListener.ExecutionStatus status, final Poll poll, final String className) {

    }

    @Override
    public void onLibraryResult(final ILibraryResultListener.ExecutionStatus status, final List<Command> list, final String className) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(className.compareTo(sClassName) == 0) {
                    // Response to our request

                    if(status == ILibraryResultListener.ExecutionStatus.Success) {
                        mCommandList = list;

                        for(int i = 0; i < mCommandList.size(); ++i) {
                            Command command = mCommandList.get(i);

                            Button b = new Button(getApplicationContext());
                            b.setText(command.getInfo());
                            b.setId(command.getId());
                            b.setOnClickListener(mActivity);
                            b.setLayoutParams(new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT));

                            if(i == 0) {
                                b.requestFocus();
                            }

                            mLinearLayout.addView(b);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        for(Command command : mCommandList) {
            if(command.getId() == v.getId()) {
                Intent intent = new Intent(this, SinglePollActivity.class);
                intent.putExtra(SinglePollActivity.EXTRA_URL, BASE_URL + command.getCommand());

                startActivity(intent);
            }
        }
    }

    @Override
    public void onPersonReceived(FitnessLibrary.IFitnessLibraryListener.ExecutionStatus status, int requestId, Person person) {

    }

    @Override
    public void onStepsReceived(FitnessLibrary.IFitnessLibraryListener.ExecutionStatus status, int requestId, List<StepBucket> list) {

    }

    @Override
    public void onActivitiesReceived(FitnessLibrary.IFitnessLibraryListener.ExecutionStatus status, int requestId, List<ActivityBucket> list) {

    }

    @Override
    public void onPortalConnectionStateChanged() {

    }
}
