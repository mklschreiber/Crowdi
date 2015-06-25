package eu.applabs.crowdsensingapp.service;

import android.widget.Toast;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class ListenerService extends WearableListenerService {

    String mNodeId;

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);

        mNodeId = messageEvent.getSourceNodeId();
        Toast.makeText(this, messageEvent.getPath(), Toast.LENGTH_SHORT).show();
    }
}
