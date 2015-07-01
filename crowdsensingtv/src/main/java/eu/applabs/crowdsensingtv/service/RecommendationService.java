package eu.applabs.crowdsensingtv.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

import eu.applabs.crowdsensinglibrary.ILibraryResultListener;
import eu.applabs.crowdsensinglibrary.Library;
import eu.applabs.crowdsensinglibrary.data.Command;
import eu.applabs.crowdsensinglibrary.data.Poll;
import eu.applabs.crowdsensingtv.R;
import eu.applabs.crowdsensingtv.gui.MainActivity;
import eu.applabs.crowdsensingtv.gui.SinglePollActivity;

public class RecommendationService extends IntentService implements ILibraryResultListener {

    private static final String sClassName = RecommendationService.class.getSimpleName();
    private static final int BASE_ID = 42;

    public RecommendationService() {
        super(RecommendationService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Library library = new Library();
        library.registerListener(this);
        library.loadCommands(MainActivity.BASE_URL + "start", "Hans", "Test");
    }

    @Override
    public void onLibraryResult(ExecutionStatus status, Poll poll) {

    }

    @Override
    public void onLibraryResult(ExecutionStatus status, List<Command> list) {
        if(status == ExecutionStatus.Success) {
            try {
                for(int i = 0; i < list.size(); ++i) {
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.poll);
                    Notification notification = new Notification.Builder(getApplicationContext())
                            .setAutoCancel(true)
                            .setContentTitle(getString(R.string.RecommendationService_Title_Recommendation))
                            .setContentText(list.get(i).getInfo())
                            .setPriority(Notification.PRIORITY_HIGH)
                            .setLargeIcon(bitmap)
                            .setSmallIcon(R.drawable.banner)
                            .setStyle(new Notification.BigPictureStyle().bigPicture(bitmap))
                            .setColor(getApplicationContext().getResources().getColor(R.color.fastlane_background))
                            .setCategory(Notification.CATEGORY_RECOMMENDATION)
                            .setContentIntent(buildPendingIntent(MainActivity.BASE_URL + list.get(i).getCommand()))
                            .build();

                    notificationManager.notify(BASE_ID + i, notification);
                }
            } catch (Exception e) {
                Log.e(sClassName, e.getMessage());
            }
        }
    }

    private PendingIntent buildPendingIntent(String url) {
        Intent intent = new Intent(getApplicationContext(), SinglePollActivity.class);
        Bundle extras = new Bundle();
        extras.putString(SinglePollActivity.EXTRA_URL, url);
        intent.putExtras(extras);

        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        return pi;
    }
}

