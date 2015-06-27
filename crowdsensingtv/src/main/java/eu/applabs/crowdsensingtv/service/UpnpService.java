package eu.applabs.crowdsensingtv.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import org.fourthline.cling.android.AndroidUpnpServiceImpl;

import eu.applabs.crowdsensingtv.gui.MainActivity;
import eu.applabs.crowdsensingupnplibrary.service.StartPollServiceReceiverConnection;

public class UpnpService extends Service implements StartPollServiceReceiverConnection.IStartPollServiceReceiverConnectionListener {

    private StartPollServiceReceiverConnection mStartPollServiceReceiverConnection = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mStartPollServiceReceiverConnection = new StartPollServiceReceiverConnection();
        mStartPollServiceReceiverConnection.registerListener(this);

        bindService(new Intent(this, AndroidUpnpServiceImpl.class),
                mStartPollServiceReceiverConnection, BIND_AUTO_CREATE);

        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        mStartPollServiceReceiverConnection.unregisterListener(this);
        unbindService(mStartPollServiceReceiverConnection);
    }

    @Override
    public void onStartPoll() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
