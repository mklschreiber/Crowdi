package eu.applabs.crowdsensingwearlibrary.service;

import android.content.Intent;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;

import eu.applabs.crowdsensingwearlibrary.data.Constants;

public class DataTransferService extends WearableListenerService {

    private static final String sClassName = DataTransferService.class.getSimpleName();

    public static final String ACTION_START_ON_TV = sClassName + "ActionStartOnTv";

    public static final String ACTION_DATA_RECEIVED = sClassName + "ActionDataReceived";
    public static final String ACTION_START_ON_TV_RECEIVED = sClassName + "ActionStartOnTvReceived";

    public static final String EXTRA_DATA = sClassName + "ExtraData";
    public static final String EXTRA_URL = sClassName + "ExtraUrl";

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for(DataEvent dataEvent : dataEvents) {
            if(dataEvent.getDataItem().getUri().getPath().equals(Constants.NOTIFICATION_APP_PATH)) {

                DataMapItem dataMapItem = DataMapItem.fromDataItem(dataEvent.getDataItem());
                String data = dataMapItem.getDataMap().getString(Constants.NOTIFICATION_APP_DATA);

                String action = dataMapItem.getDataMap().getString(Constants.NOTIFICATION_APP_ACTION);
                String url = dataMapItem.getDataMap().getString(Constants.NOTIFICATION_APP_URL);

                if(data != null && data.compareTo("") != 0) {
                    Intent intent = new Intent(ACTION_DATA_RECEIVED);
                    intent.putExtra(EXTRA_DATA, data);

                    sendBroadcast(intent);
                }

                if(action != null && url != null && action.compareTo(ACTION_START_ON_TV) == 0) {
                    Intent intent = new Intent(ACTION_START_ON_TV_RECEIVED);
                    intent.putExtra(EXTRA_URL, url);

                    sendBroadcast(intent);
                }
            }
        }
    }
}
