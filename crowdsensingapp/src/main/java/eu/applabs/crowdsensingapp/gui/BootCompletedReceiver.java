package eu.applabs.crowdsensingapp.gui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import eu.applabs.crowdsensingapp.service.UpnpService;

public class BootCompletedReceiver extends BroadcastReceiver {

    private static final String sClassName = BootCompletedReceiver.class.getSimpleName();
    private static final long sInitialDelay = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(sClassName, "BootCompletedReceiver called");

        if (intent.getAction().endsWith(Intent.ACTION_BOOT_COMPLETED)) {
            Intent upnpIntent = new Intent(context, UpnpService.class);
            context.startService(upnpIntent);
        }
    }
}
