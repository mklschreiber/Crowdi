package eu.applabs.crowdsensingapp.service;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;

import org.fourthline.cling.android.AndroidUpnpServiceImpl;

import eu.applabs.crowdsensingwearlibrary.data.Constants;
import eu.applabs.crowdsensingupnplibrary.service.StartPollServiceSenderConnection;

public class DataTransferService extends WearableListenerService {

    private static final String sClassName = DataTransferService.class.getSimpleName();

    public static String sHeartRate = "0.0";

    public static final String EXTRA_START_ON_TV = sClassName + "StartOnTv";

    private StartPollServiceSenderConnection mStartPollServiceSenderConnection;

    @Override
    public void onCreate() {
        super.onCreate();

        mStartPollServiceSenderConnection = new StartPollServiceSenderConnection();
        bindService(new Intent(this, AndroidUpnpServiceImpl.class),
                mStartPollServiceSenderConnection,
                Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unbindService(mStartPollServiceSenderConnection);
        mStartPollServiceSenderConnection = null;
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for(DataEvent dataEvent : dataEvents) {
            if(dataEvent.getDataItem().getUri().getPath().equals(Constants.NOTIFICATION_APP_PATH)) {
                DataMapItem dataMapItem = DataMapItem.fromDataItem(dataEvent.getDataItem());
                String data = dataMapItem.getDataMap().getString(Constants.NOTIFICATION_APP_DATA);

                String action = dataMapItem.getDataMap().getString(Constants.NOTIFICATION_APP_ACTION);

                if(data != null && data.compareTo("") != 0) {
                    Toast.makeText(this, "Received data: " + data, Toast.LENGTH_SHORT).show();
                    sHeartRate = data;
                }

                if(action != null && action.compareTo(EXTRA_START_ON_TV) == 0) {
                    mStartPollServiceSenderConnection.startPoll();
                }
            }
        }
    }
}
