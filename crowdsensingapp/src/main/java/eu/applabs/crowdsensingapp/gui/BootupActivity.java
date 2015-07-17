package eu.applabs.crowdsensingapp.gui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import eu.applabs.crowdsensingapp.service.UpnpService;

public class BootupActivity extends BroadcastReceiver {

    private static final String sClassName = BootupActivity.class.getSimpleName();
    private static final long sInitialDelay = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(sClassName, "BootupActivity called");

        if (intent.getAction().endsWith(Intent.ACTION_BOOT_COMPLETED)) {
            Intent upnpIntent = new Intent(context, UpnpService.class);
            context.startService(upnpIntent);
        }
    }
}
