package eu.applabs.crowdsensingtv.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.fourthline.cling.android.AndroidUpnpServiceImpl;
import org.fourthline.cling.model.action.ActionArgumentValue;

import java.util.List;

import eu.applabs.crowdsensinglibrary.Library;
import eu.applabs.crowdsensinglibrary.data.Command;
import eu.applabs.crowdsensinglibrary.data.Poll;
import eu.applabs.crowdsensingtv.R;
import eu.applabs.crowdsensingtv.gui.SinglePollActivity;
import eu.applabs.crowdsensingtv.gui.MainActivity;
import eu.applabs.crowdsensingtv.settings.SettingsManager;
import eu.applabs.crowdsensingupnplibrary.service.WearNotificationServiceSenderConnection;

public class RecommendationService extends IntentService implements
        Library.ILibraryResultListener,
        WearNotificationServiceSenderConnection.IWearNotificationServiceSenderConnectionListener {

    private static final String sClassName = RecommendationService.class.getSimpleName();
    private static final int BASE_ID = 42;

    private SettingsManager mSettingsManager = null;
    private Library mLibrary = null;
    private WearNotificationServiceSenderConnection mWearNotificationServiceSenderConnection = null;

    public RecommendationService() {
        super(RecommendationService.class.getSimpleName());

        mLibrary = Library.getInstance();
        mWearNotificationServiceSenderConnection = new WearNotificationServiceSenderConnection();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mSettingsManager = new SettingsManager(this.getApplicationContext());

        if(mWearNotificationServiceSenderConnection != null) {
            mWearNotificationServiceSenderConnection.registerListener(this);
            bindService(new Intent(this, AndroidUpnpServiceImpl.class), mWearNotificationServiceSenderConnection, BIND_AUTO_CREATE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(mWearNotificationServiceSenderConnection != null) {
            mWearNotificationServiceSenderConnection.unregisterListener(this);
            unbindService(mWearNotificationServiceSenderConnection);
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(mLibrary != null && mLibrary.accountAvailable()) {
            long lastTimeStamp = mSettingsManager.getNotificationTimeStamp();
            long currentTimeStamp = System.currentTimeMillis();

            // Difference should be greater than 30 minutes
            if((currentTimeStamp - lastTimeStamp) > (30 * 60 * 1000)) {
                mSettingsManager.setNotificationTimeStamp(currentTimeStamp);
                mLibrary.registerListener(this);
                mLibrary.loadCommands(MainActivity.START_URL, sClassName);
            }
        }
    }

    @Override
    public void onLibraryResult(ExecutionStatus status, Poll poll, String className) {
        if(className.compareTo(sClassName) == 0) {

        }
    }

    @Override
    public void onLibraryResult(ExecutionStatus status, List<Command> list, String className) {
        if(className.compareTo(sClassName) == 0) {
            if (status == ExecutionStatus.Success) {
                try {
                    for (int i = 0; i < list.size(); ++i) {
                        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.poll);
                        Notification notification = new Notification.Builder(getApplicationContext())
                                .setAutoCancel(true)
                                .setContentTitle(getString(R.string.RecommendationService_Title_Recommendation))
                                .setContentText(list.get(i).getInfo())
                                .setPriority(Notification.PRIORITY_HIGH)
                                .setLargeIcon(bitmap)
                                .setSmallIcon(R.drawable.icon_flat)
                                .setStyle(new Notification.BigPictureStyle().bigPicture(bitmap))
                                .setColor(getApplicationContext().getResources().getColor(R.color.primary))
                                .setCategory(Notification.CATEGORY_RECOMMENDATION)
                                .setContentIntent(buildPendingIntent(MainActivity.BASE_URL + list.get(i).getCommand()))
                                .build();

                        notificationManager.notify(BASE_ID + i, notification);

                        mWearNotificationServiceSenderConnection.startNotification(
                                getString(R.string.RecommendationService_Title_Recommendation),
                                list.get(i).getInfo(),
                                MainActivity.BASE_URL + list.get(i).getCommand());

                        // Break the loop to display only the first command
                        //break;
                    }
                } catch (Exception e) {
                    Log.e(sClassName, e.getMessage());
                }
            }
        }
    }

    private PendingIntent buildPendingIntent(String url) {
        Intent intent = new Intent().setClass(getApplicationContext(), SinglePollActivity.class);
        intent.setAction("SinglePollActivity" + String.valueOf(Math.random()));
        intent.putExtra(SinglePollActivity.EXTRA_URL, url);

        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        return pi;
    }

    @Override
    public void onDeviceAdded() {

    }

    @Override
    public void onResponseAvailable(String method, boolean success, ActionArgumentValue[] output) {

    }
}

