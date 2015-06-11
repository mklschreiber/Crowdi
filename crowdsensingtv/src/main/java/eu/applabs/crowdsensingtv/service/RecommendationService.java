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

import eu.applabs.crowdsensingtv.R;
import eu.applabs.crowdsensingtv.gui.SinglePollActivity;

public class RecommendationService extends IntentService {

    private static final String sClassName = RecommendationService.class.getSimpleName();

    public static final int sPeriodic_Recommendation = 42;
    public static final int sSpecial_Recommendation = 1337;

    public static final String sExtra_Recommendation_Content = sClassName + "ExtraRecommendationContent";
    public static final String sExtra_Recommendation_Url = sClassName + "ExtraRecommendationUrl";

    public RecommendationService() {
        super(RecommendationService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Bundle extras = intent.getExtras();

            Bitmap bitmap;
            Notification notification;
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if(extras != null && extras.containsKey(sExtra_Recommendation_Content) && extras.containsKey(sExtra_Recommendation_Url)) {
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.specialpoll);
                String content = extras.getString(sExtra_Recommendation_Content);
                String url = extras.getString(sExtra_Recommendation_Url);

                notification = new Notification.Builder(getApplicationContext())
                        .setAutoCancel(true)
                        .setContentTitle(getString(R.string.RecommendationService_Title_Recommendation))
                        .setContentText(content)
                        .setPriority(Notification.PRIORITY_MAX)
                        .setLargeIcon(bitmap)
                        .setSmallIcon(R.drawable.banner)
                        .setStyle(new Notification.BigPictureStyle().bigPicture(bitmap))
                        .setColor(getApplicationContext().getResources().getColor(R.color.fastlane_background))
                        .setCategory(Notification.CATEGORY_RECOMMENDATION)
                        .setContentIntent(buildPendingIntent(url))
                        .build();

                notificationManager.notify(sSpecial_Recommendation, notification);
            } else {
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.poll);

                notification = new Notification.Builder(getApplicationContext())
                        .setAutoCancel(true)
                        .setContentTitle(getString(R.string.RecommendationService_Title_Recommendation))
                        .setContentText(getString(R.string.RecommendationService_Content_Recommendation))
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setLargeIcon(bitmap)
                        .setSmallIcon(R.drawable.banner)
                        .setStyle(new Notification.BigPictureStyle().bigPicture(bitmap))
                        .setColor(getApplicationContext().getResources().getColor(R.color.fastlane_background))
                        .setCategory(Notification.CATEGORY_RECOMMENDATION)
                        .setContentIntent(buildPendingIntent(""))
                        .build();

                notificationManager.notify(sPeriodic_Recommendation, notification);
            }
        } catch(Exception e) {
            Log.e(sClassName, e.getMessage());
        }
    }

    private PendingIntent buildPendingIntent(String url) {
        Intent intent = new Intent(getApplicationContext(), SinglePollActivity.class);
        Bundle extras = new Bundle();
        extras.putString(sExtra_Recommendation_Url, url);
        intent.putExtras(extras);

        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        return pi;
    }
}
