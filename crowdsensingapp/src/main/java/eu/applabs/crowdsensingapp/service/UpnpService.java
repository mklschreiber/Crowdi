package eu.applabs.crowdsensingapp.service;

import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import org.fourthline.cling.android.AndroidUpnpServiceImpl;

import eu.applabs.crowdsensingapp.settings.SettingsManager;
import eu.applabs.crowdsensingupnplibrary.service.HeartRateDataServiceSenderConnection;
import eu.applabs.crowdsensingupnplibrary.service.HeartRateServiceReceiverConnection;
import eu.applabs.crowdsensingupnplibrary.service.StartPollServiceSenderConnection;
import eu.applabs.crowdsensingwearlibrary.service.AppConnectionService;
import eu.applabs.crowdsensingwearlibrary.service.DataTransferService;

public class UpnpService extends AppConnectionService implements
        HeartRateServiceReceiverConnection.IHeartRateServiceReceiverConnectionListener {

    private final IBinder mBinder = new LocalBinder();

    private HeartRateServiceReceiverConnection mHeartRateServiceReceiverConnection;
    private StartPollServiceSenderConnection mStartPollServiceSenderConnection;
    private HeartRateDataServiceSenderConnection mHeartRateDataServiceSenderConnection;

    private SettingsManager mSettingsManager = null;

    public void startPoll(String url) {
        if(mStartPollServiceSenderConnection != null) {
            mStartPollServiceSenderConnection.startPoll(url);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mSettingsManager = new SettingsManager(getApplicationContext());

        if(mSettingsManager.getUpnpServiceEnabled()) {

            mHeartRateDataServiceSenderConnection = new HeartRateDataServiceSenderConnection();
            mHeartRateServiceReceiverConnection = new HeartRateServiceReceiverConnection();
            mStartPollServiceSenderConnection = new StartPollServiceSenderConnection();

            mHeartRateServiceReceiverConnection.registerListener(this);
            bindService(new Intent(this, AndroidUpnpServiceImpl.class),
                    mHeartRateServiceReceiverConnection,
                    Context.BIND_AUTO_CREATE);

            bindService(new Intent(this, AndroidUpnpServiceImpl.class),
                    mStartPollServiceSenderConnection,
                    Context.BIND_AUTO_CREATE);

            bindService(new Intent(this, AndroidUpnpServiceImpl.class),
                    mHeartRateDataServiceSenderConnection,
                    Context.BIND_AUTO_CREATE);

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(mHeartRateServiceReceiverConnection != null) {
            mHeartRateServiceReceiverConnection.unregisterListener(this);
            unbindService(mHeartRateServiceReceiverConnection);
        }

        if(mStartPollServiceSenderConnection != null) {
            unbindService(mStartPollServiceSenderConnection);
        }

        if(mHeartRateDataServiceSenderConnection != null) {
            unbindService(mHeartRateDataServiceSenderConnection);
        }
    }

    // AppConnectionService

    @Override
    public void onDataReceived(String data) {
        Toast.makeText(this, "Received data: " + data, Toast.LENGTH_SHORT).show();

        if(mHeartRateDataServiceSenderConnection != null) {
            mHeartRateDataServiceSenderConnection.setHeartRate(data);
        }
    }

    @Override
    public void onStartOnTvReceived(String url) {
        if(mStartPollServiceSenderConnection != null) {
            mStartPollServiceSenderConnection.startPoll(url);
        }
    }

    // IHeartRateServiceReceiverConnection

    @Override
    public void onStartNotification(String title, String content, String url) {
        showNotification(title, content, "Auf TV starten", DataTransferService.ACTION_START_ON_TV, url);
    }

    @Override
    public void onStartMeasuring() {
        showNotification("Messung starten", "...");
    }

    public class LocalBinder extends Binder {
        public UpnpService getService() {
            return UpnpService.this;
        }
    }
}
