package eu.applabs.crowdsensingapp.gui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import org.fourthline.cling.android.AndroidUpnpServiceImpl;

import eu.applabs.crowdsensingapp.R;
import eu.applabs.crowdsensingwearlibrary.data.Constants;
import eu.applabs.crowdsensingapp.service.DataTransferService;
import eu.applabs.crowdsensingupnplibrary.service.HeartRateServiceReceiverConnection;
import eu.applabs.crowdsensingupnplibrary.service.StartPollServiceSenderConnection;

public class MainActivity extends Activity implements View.OnClickListener, HeartRateServiceReceiverConnection.IHeartRateServiceReceiverConnectionListener {

    private GoogleApiClient mGoogleApiClient;

    private HeartRateServiceReceiverConnection mHeartRateServiceReceiverConnection;
    private StartPollServiceSenderConnection mStartPollServiceSenderConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHeartRateServiceReceiverConnection = new HeartRateServiceReceiverConnection();
        mStartPollServiceSenderConnection = new StartPollServiceSenderConnection();

        init();
        Button b = (Button) findViewById(R.id.button);
        b.setOnClickListener(this);

        b = (Button) findViewById(R.id.button2);
        b.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();

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
        mGoogleApiClient.disconnect();

        mHeartRateServiceReceiverConnection.unregisterListener(this);
        unbindService(mHeartRateServiceReceiverConnection);

        unbindService(mStartPollServiceSenderConnection);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.button:
                showNotification("Pizza", "Pizza wählen", "Auf TV starten", DataTransferService.EXTRA_START_ON_TV, "http://as.applabs.eu:8080/FancyModule/pizza");
                break;
            case R.id.button2:
                mStartPollServiceSenderConnection.startPoll("http://as.applabs.eu:8080/FancyModule/pizza");
                break;
        }
    }

    private void init() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
    }

    private void showNotification(String title, String content) {
        showNotification(title, content, null, null, null);
    }

    private void showNotification(String title, String content, String action_label, String action, String url) {
        if(mGoogleApiClient.isConnected()) {
            PutDataMapRequest dataMapRequest = PutDataMapRequest.create(Constants.NOTIFICATION_WEAR_PATH);
            dataMapRequest.getDataMap().putString(Constants.NOTIFICATION_WEAR_TITLE, title);
            dataMapRequest.getDataMap().putString(Constants.NOTIFICATION_WEAR_CONTENT, content);
            dataMapRequest.getDataMap().putLong(Constants.NOTIFICATION_WEAR_TIMESTAMP, System.currentTimeMillis());

            if(action_label != null) {
                dataMapRequest.getDataMap().putString(Constants.NOTIFICATION_WEAR_ACTION_LABEL, action_label);
            }

            if(action != null) {
                dataMapRequest.getDataMap().putString(Constants.NOTIFICATION_WEAR_ACTION, action);
            }

            if(url != null) {
                dataMapRequest.getDataMap().putString(Constants.NOTIFICATION_WEAR_ACTION_URL, url);
            }

            PutDataRequest putDataRequest = dataMapRequest.asPutDataRequest();
            Wearable.DataApi.putDataItem(mGoogleApiClient, putDataRequest);
        }
    }

    @Override
    public void onStartNotification(String title, String content, String url) {
        showNotification(title, content, "Auf TV starten", DataTransferService.EXTRA_START_ON_TV, url);
    }

    @Override
    public void onStartMeasuring() {
        showNotification("Messung starten", "...");
    }
}
