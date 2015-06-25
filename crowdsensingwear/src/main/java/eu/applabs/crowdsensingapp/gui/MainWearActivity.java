package eu.applabs.crowdsensingapp.gui;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.List;

import eu.applabs.crowdsensingapp.R;
import eu.applabs.crowdsensingapp.data.Constants;

public class MainWearActivity extends WearableActivity {

    private TextView mTextView;
    private RelativeLayout mBackground;
    private WatchInflatedListener mWatchInflatedListener;
    private GoogleApiClient mGoogleApiClient;
    private List<Node> mNodeList;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private long mTimeStamp = 0;
    private int mColorCounter = 0;
    private String mData;
    private boolean mAmbientMode = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAmbientEnabled();

        mData = "0.0";
        mWatchInflatedListener = new WatchInflatedListener();

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(mWatchInflatedListener);
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        mAmbientMode = true;

        mBackground.setBackgroundColor(getResources().getColor(R.color.black));
    }

    @Override
    public void onExitAmbient() {
        super.onExitAmbient();
        mAmbientMode = false;

        updateData(mData);
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();

        mTextView.setText(mData);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        disconnect();

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

    private void connect() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();

        if(mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    private void disconnect() {
        if(mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    private void updateData(String data) {
        mData = data;

        if(!mAmbientMode) {
            mTextView.setText(mData);

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

            sendData(mData);
        }
    }

    private void sendData(String data) {
        if(mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            PutDataMapRequest dataMapRequest = PutDataMapRequest.create(Constants.NOTIFICATION_APP_PATH);
            dataMapRequest.getDataMap().putDouble(Constants.NOTIFICATION_APP_TIMESTAMP, System.currentTimeMillis());
            dataMapRequest.getDataMap().putString(Constants.NOTIFICATION_APP_DATA, data);
            PutDataRequest putDataRequest = dataMapRequest.asPutDataRequest();
            Wearable.DataApi.putDataItem(mGoogleApiClient, putDataRequest);
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

            connect();
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            try {
                final float[] values = event.values;

                if (values.length > 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            long currentTime = System.currentTimeMillis();

                            if (currentTime - mTimeStamp > 3000) {
                                updateData(String.valueOf(values[0]));
                                mTimeStamp = currentTime;
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
