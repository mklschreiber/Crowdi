package eu.applabs.crowdsensingapp.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.InputStream;

import eu.applabs.crowdsensingapp.R;
import eu.applabs.crowdsensingwearlibrary.data.Constants;
import eu.applabs.crowdsensingapp.gui.MainWearActivity;

public class NotificationService extends WearableListenerService {

    private static final String sClassName = NotificationService.class.getSimpleName();

    public static final String EXTRA_ACTION = sClassName + "ExtraAction";

    private int mNotificationId = 001;
    private GoogleApiClient mGoogleApiClient;

   @Override
    public void onCreate() {
        super.onCreate();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();

        if(mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null) {
            String action = intent.getAction();

            if(action.equals(Constants.ACTION_WEAR_DISMISS)) {
                dismissNotification();
            } else if(action.equals(Constants.ACTION_WEAR)) {
                String a = intent.getExtras().getString(EXTRA_ACTION);
                sendAction(a);
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
                String action = dataMapItem.getDataMap().getString(Constants.NOTIFICATION_WEAR_ACTION);
                String action_label = dataMapItem.getDataMap().getString(Constants.NOTIFICATION_WEAR_ACTION_LABEL);

                if(action == null) {
                    action = "";
                    action_label = "";
                }

                if(title != null
                        && content != null
                        && title.compareTo("") != 0
                        && content.compareTo("") != 0) {
                    showNotification(title, content, action_label, action);
                } else {
                    dismissNotification();
                }
            }
        }
    }

    private void sendAction(String action) {
        if(mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            PutDataMapRequest dataMapRequest = PutDataMapRequest.create(Constants.NOTIFICATION_APP_PATH);
            dataMapRequest.getDataMap().putString(Constants.NOTIFICATION_APP_ACTION, action);
            dataMapRequest.getDataMap().putLong(Constants.NOTIFICATION_APP_TIMESTAMP, System.currentTimeMillis());
            PutDataRequest putDataRequest = dataMapRequest.asPutDataRequest();
            Wearable.DataApi.putDataItem(mGoogleApiClient, putDataRequest);
        }
    }

    private void showNotification(String title, String content, String action_label, String action) {
        Intent activityIntent = new Intent(this, MainWearActivity.class);
        PendingIntent activityPendingIntent = PendingIntent.getActivity(this, 0, activityIntent, 0);

        Intent deleteIntent = new Intent(Constants.ACTION_WEAR_DISMISS);
        PendingIntent deletePendingIntent = PendingIntent
                .getService(this, 0, deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(title)
                .setContentText(content)
                .setDeleteIntent(deletePendingIntent)
                .setContentIntent(activityPendingIntent);

        if(action != null && action.compareTo("") != 0
                && action_label != null && action_label.compareTo("") != 0) {

            Intent serviceIntent = new Intent(Constants.ACTION_WEAR);
            serviceIntent.putExtra(EXTRA_ACTION, action);
            PendingIntent servicePendingIntent = PendingIntent.getService(this, 0, serviceIntent, 0);

            builder.addAction(R.drawable.open_tv, action_label, servicePendingIntent);
        }

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
