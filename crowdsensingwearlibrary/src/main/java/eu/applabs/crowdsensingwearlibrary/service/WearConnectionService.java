package eu.applabs.crowdsensingwearlibrary.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public abstract class WearConnectionService extends Service {

    private DataTransferServiceBroadcastReceiver mDataTransferServiceBroadcastReceiver = null;

    abstract public void onDataReceived(String data);
    abstract public void onStartOnTvReceived(String url);

    @Override
    public void onCreate() {
        super.onCreate();

        mDataTransferServiceBroadcastReceiver = new DataTransferServiceBroadcastReceiver();

        IntentFilter filter = new IntentFilter();
        filter.addAction(DataTransferService.ACTION_DATA_RECEIVED);
        filter.addAction(DataTransferService.ACTION_START_ON_TV_RECEIVED);

        registerReceiver(mDataTransferServiceBroadcastReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(mDataTransferServiceBroadcastReceiver != null) {
            unregisterReceiver(mDataTransferServiceBroadcastReceiver);
            mDataTransferServiceBroadcastReceiver = null;
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
