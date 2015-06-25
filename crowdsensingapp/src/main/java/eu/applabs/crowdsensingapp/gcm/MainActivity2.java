package eu.applabs.crowdsensingapp.gcm;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

import eu.applabs.crowdsensingapp.MainActivity;
import eu.applabs.crowdsensingapp.R;

public class MainActivity2 extends Activity implements View.OnClickListener {

    private GoogleCloudMessaging mGoogleCloudMessaging;
    private String mRegId;
    private Context mContext;
    private ShareExternalServer mShareExternalServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        mContext = this;
        mShareExternalServer = new ShareExternalServer();

        Button b = (Button) findViewById(R.id.register);
        b.setOnClickListener(this);

        b = (Button) findViewById(R.id.share);
        b.setOnClickListener(this);

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("EXTRA_EVENT_ID", 1);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Notification.Builder notificationBuilder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Test Title")
                .setContentText("Test Text")
                .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(1, notificationBuilder.build());
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.register:
                registerGCM();
                break;
            case R.id.share:
                shareRegIdInBackground();
                break;
        }
    }

    private void registerGCM() {
        mGoogleCloudMessaging = GoogleCloudMessaging.getInstance(this);
        // Check if we already registered
        registerInBackground();
    }

    private void shareRegIdInBackground() {
        new AsyncTask<Void, String, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String result = mShareExternalServer.shareRegIdWithAppServer(mContext, mRegId);
                return result;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Toast.makeText(getApplicationContext(), s,
                        Toast.LENGTH_LONG).show();
            }
        }.execute();
    }

    private void registerInBackground() {
        new AsyncTask<Void, String, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    if (mGoogleCloudMessaging == null) {
                        mGoogleCloudMessaging = GoogleCloudMessaging.getInstance(mContext);
                    }
                    mRegId = mGoogleCloudMessaging.register(Config.GOOGLE_PROJECT_ID);

                } catch (IOException ex) { }

                return mRegId;
            }

            @Override
            protected void onPostExecute(String string) {
                super.onPostExecute(string);

                Toast.makeText(getApplicationContext(), "Registered with GCM Server."
                        + string, Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }
}
