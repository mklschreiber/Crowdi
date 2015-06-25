package eu.applabs.crowdsensingapp.gui;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.List;
import java.util.concurrent.TimeUnit;

import eu.applabs.crowdsensingapp.R;

public class MainWearActivity extends Activity {

    private TextView mTextView;
    private RelativeLayout mBackground;
    private WatchInflatedListener mWatchInflatedListener;
    private GoogleApiClient mGoogleApiClient;
    private List<Node> mNodeList;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private long mTimeStamp = 0;
    private int mColorCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWatchInflatedListener = new WatchInflatedListener();

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(mWatchInflatedListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mSensorManager != null && mWatchInflatedListener != null) {
            mSensorManager.unregisterListener(mWatchInflatedListener);
        }

        mWatchInflatedListener = null;
        mGoogleApiClient = null;
        mSensorManager = null;
        mSensor = null;
        mNodeList = null;
        mTextView = null;
    }

    private void setupConnection() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();

        new Thread(new Runnable() {
            @Override
            public void run() {
                mGoogleApiClient.blockingConnect(5000, TimeUnit.MICROSECONDS);

                NodeApi.GetConnectedNodesResult result =
                        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();

                mNodeList = result.getNodes();
                mGoogleApiClient.disconnect();
            }
        }).start();
    }

    private void sendHeartRate() {
        if(mNodeList != null && mGoogleApiClient != null && mTextView != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mGoogleApiClient.blockingConnect(5000, TimeUnit.MICROSECONDS);
                    for(Node node : mNodeList) {
                        Wearable.MessageApi.sendMessage(mGoogleApiClient,
                                node.getId(),
                                mTextView.getText().toString(),
                                null);
                    }
                    mGoogleApiClient.disconnect();
                }
            }).start();
        }
    }

    private class WatchInflatedListener implements WatchViewStub.OnLayoutInflatedListener,
            SensorEventListener {

        @Override
        public void onLayoutInflated(WatchViewStub watchViewStub) {
            mTextView = (TextView) watchViewStub.findViewById(R.id.text);
            mBackground = (RelativeLayout) watchViewStub.findViewById(R.id.background);

            mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);

            setupConnection();
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            try {
                final float[] values = event.values;

                if (values.length > 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTextView.setText(String.valueOf(values[0]));

                            long currentTime = System.currentTimeMillis();

                            if (currentTime - mTimeStamp > 3000) {
                                switch(mColorCounter) {
                                    case 0:
                                        mBackground.setBackgroundColor(getResources().getColor(R.color.blue));
                                        mColorCounter++;
                                        break;
                                    case 1:
                                        mBackground.setBackgroundColor(getResources().getColor(R.color.green));
                                        mColorCounter++;
                                        break;
                                    case 2:
                                        mBackground.setBackgroundColor(getResources().getColor(R.color.red));
                                        mColorCounter = 0;
                                        break;
                                    default:
                                        mColorCounter = 0;
                                }

                                mTimeStamp = currentTime;
                                sendHeartRate();
                            }
                        }
                    });
                }
            } catch (Exception e) {

            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) { /* Not needed */ }
    }
}
