package eu.applabs.crowdsensingapp.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import eu.applabs.crowdsensingapp.R;
import eu.applabs.crowdsensingapp.data.Constants;
import eu.applabs.crowdsensingapp.gui.MainWearActivity;

public class NotificationService extends WearableListenerService {

    private int mNotificationId = 001;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null) {
            String action = intent.getAction();

            if(action.equals(Constants.ACTION_WEAR_DISMISS)) {
                dismissNotification();
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for(DataEvent dataEvent : dataEvents) {
            if(dataEvent.getDataItem().getUri().getPath().equals(Constants.NOTIFICATION_WEAR_PATH)) {
                DataMapItem dataMapItem = DataMapItem.fromDataItem(dataEvent.getDataItem());
                String title = dataMapItem.getDataMap().getString(Constants.NOTIFICATION_WEAR_TITLE);
                String content = dataMapItem.getDataMap().getString(Constants.NOTIFICATION_WEAR_CONTENT);

                if(title != null && content != null &&
                        title.compareTo("") != 0 && content.compareTo("") != 0) {
                    sendNotification(title, content);
                } else {
                    dismissNotification();
                }
            }
        }
    }

    private void sendNotification(String title, String content) {
        Intent startIntent = new Intent(this, MainWearActivity.class);
        PendingIntent startPendingIntent = PendingIntent.getActivity(this, 0, startIntent, 0);

        Intent deleteIntent = new Intent(Constants.ACTION_WEAR_DISMISS);
        PendingIntent deletePendingIntent = PendingIntent
                .getService(this, 0, deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(title)
                .setContentText(content)
                .setDeleteIntent(deletePendingIntent)
                .setContentIntent(startPendingIntent);

        Notification notification = builder.build();

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(mNotificationId++, notification);

        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    private void dismissNotification() {
        new DismissNotificationCommand(this).execute();
    }

    private class DismissNotificationCommand implements GoogleApiClient.ConnectionCallbacks,
            ResultCallback<DataApi.DeleteDataItemsResult>, GoogleApiClient.OnConnectionFailedListener {

        private final GoogleApiClient mGoogleApiClient;

        private DismissNotificationCommand(Context context) {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addApi(Wearable.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }

        public void execute() {
            mGoogleApiClient.connect();
        }

        @Override
        public void onConnected(Bundle bundle) {
            Uri uri = new Uri.Builder().scheme(PutDataRequest.WEAR_URI_SCHEME)
                    .path(Constants.NOTIFICATION_WEAR_PATH).build();
            Wearable.DataApi.deleteDataItems(mGoogleApiClient, uri).setResultCallback(this);
        }

        @Override
        public void onConnectionSuspended(int i) {

        }

        @Override
        public void onResult(DataApi.DeleteDataItemsResult deleteDataItemsResult) {
            mGoogleApiClient.disconnect();
        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {

        }
    }
}
