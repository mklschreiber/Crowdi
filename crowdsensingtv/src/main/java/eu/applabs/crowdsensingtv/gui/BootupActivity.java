package eu.applabs.crowdsensingtv.gui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import eu.applabs.crowdsensingtv.service.RecommendationService;

public class BootupActivity extends BroadcastReceiver {

    private static final String sClassName = BootupActivity.class.getSimpleName();
    private static final long sInitialDelay = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(sClassName, "BootupActivity called");
        Toast.makeText(context, "BootupActivity called", Toast.LENGTH_SHORT).show();

        if (intent.getAction().endsWith(Intent.ACTION_BOOT_COMPLETED)) {
            scheduleRecommendationUpdate(context);
        }
    }

    private void scheduleRecommendationUpdate(Context context) {
        Log.d(sClassName, "Scheduling recommendations update");

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        /*Intent recommendationIntent = new Intent(context, RecommendationService.class);
        PendingIntent alarmIntent = PendingIntent.getService(context, 0, recommendationIntent, 0);

        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                sInitialDelay,
                AlarmManager.INTERVAL_HALF_HOUR,
                alarmIntent);*/

        // New

        Intent recommendationIntent2 = new Intent(context, RecommendationService.class);
        PendingIntent alarmIntent2 = PendingIntent.getService(context, 0, recommendationIntent2, 0);

        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                sInitialDelay,
                AlarmManager.INTERVAL_HALF_HOUR,
                alarmIntent2);
    }
}
