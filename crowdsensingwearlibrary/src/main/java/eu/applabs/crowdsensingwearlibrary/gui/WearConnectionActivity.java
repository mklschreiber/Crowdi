package eu.applabs.crowdsensingwearlibrary.gui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import eu.applabs.crowdsensingwearlibrary.data.Constants;
import eu.applabs.crowdsensingwearlibrary.service.DataTransferService;

public abstract class WearConnectionActivity extends Activity {

    private DataTransferServiceBroadcastReceiver mDataTransferServiceBroadcastReceiver = null;
    private GoogleApiClient mGoogleApiClient = null;

    abstract public void onDataReceived(String data);
    abstract public void onStartOnTvReceived(String url);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDataTransferServiceBroadcastReceiver = new DataTransferServiceBroadcastReceiver();

        IntentFilter filter = new IntentFilter();
        filter.addAction(DataTransferService.ACTION_DATA_RECEIVED);
        filter.addAction(DataTransferService.ACTION_START_ON_TV_RECEIVED);

        registerReceiver(mDataTransferServiceBroadcastReceiver, filter);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();

        if(mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mDataTransferServiceBroadcastReceiver != null) {
            unregisterReceiver(mDataTransferServiceBroadcastReceiver);
            mDataTransferServiceBroadcastReceiver = null;
        }

        if(mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
            mGoogleApiClient = null;
        }
    }

    public void showNotification(String title, String content) {
        showNotification(title, content, null, null, null);
    }

    public void showNotification(String title, String content, String action_label, String action, String url) {
        if(mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
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

    private class DataTransferServiceBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent != null) {
                if(intent.getAction().compareTo(DataTransferService.ACTION_DATA_RECEIVED) == 0
                        && intent.getExtras().containsKey(DataTransferService.EXTRA_DATA)) {
                    String data = intent.getExtras().getString(DataTransferService.EXTRA_DATA);
                    onDataReceived(data);
                } else if(intent.getAction().compareTo(DataTransferService.ACTION_START_ON_TV_RECEIVED) == 0
                        && intent.getExtras().containsKey(DataTransferService.EXTRA_URL)) {
                    String url = intent.getExtras().getString(DataTransferService.EXTRA_URL);
                    onStartOnTvReceived(url);
                }
            }
        }
    }
}
