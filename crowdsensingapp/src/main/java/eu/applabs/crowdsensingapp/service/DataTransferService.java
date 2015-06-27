package eu.applabs.crowdsensingapp.service;

import android.widget.Toast;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;

import eu.applabs.crowdsensingapp.data.Constants;

public class DataTransferService extends WearableListenerService {

    public static String sHeartRate = "0.0";

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for(DataEvent dataEvent : dataEvents) {
            if(dataEvent.getDataItem().getUri().getPath().equals(Constants.NOTIFICATION_APP_PATH)) {
                DataMapItem dataMapItem = DataMapItem.fromDataItem(dataEvent.getDataItem());
                String data = dataMapItem.getDataMap().getString(Constants.NOTIFICATION_APP_DATA);

                if(data != null && data.compareTo("") != 0) {
                    Toast.makeText(this, "Received data: " + data, Toast.LENGTH_SHORT).show();
                    sHeartRate = data;
                }
            }
        }
    }
}
