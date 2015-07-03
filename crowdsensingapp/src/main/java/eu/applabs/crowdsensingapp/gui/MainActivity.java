package eu.applabs.crowdsensingapp.gui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.fourthline.cling.android.AndroidUpnpServiceImpl;

import eu.applabs.crowdsensingapp.R;
import eu.applabs.crowdsensingfitnesslibrary.FitnessLibrary;
import eu.applabs.crowdsensingwearlibrary.gui.WearConnectionActivity;
import eu.applabs.crowdsensingwearlibrary.service.DataTransferService;
import eu.applabs.crowdsensingupnplibrary.service.HeartRateServiceReceiverConnection;
import eu.applabs.crowdsensingupnplibrary.service.StartPollServiceSenderConnection;

public class MainActivity extends WearConnectionActivity implements View.OnClickListener,
        HeartRateServiceReceiverConnection.IHeartRateServiceReceiverConnectionListener {

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

        mFitnessLibrary = new FitnessLibrary(this);
        mFitnessLibrary.connect();

        mHeartRateServiceReceiverConnection = new HeartRateServiceReceiverConnection();
        mStartPollServiceSenderConnection = new StartPollServiceSenderConnection();

        Button b = (Button) findViewById(R.id.button);
        b.setOnClickListener(this);

        b = (Button) findViewById(R.id.button2);
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
            mFitnessLibrary.disconnect();
            mFitnessLibrary = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.button:
                showNotification("Pizza", "Pizza wählen", "Auf TV starten",
                        DataTransferService.ACTION_START_ON_TV, "http://as.applabs.eu:8080/FancyModule/pizza");
                break;
            case R.id.button2:
                mStartPollServiceSenderConnection.startPoll("http://as.applabs.eu:8080/FancyModule/pizza");
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
}
