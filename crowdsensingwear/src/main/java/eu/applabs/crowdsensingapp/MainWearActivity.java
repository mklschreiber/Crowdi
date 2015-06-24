package eu.applabs.crowdsensingapp;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.widget.TextView;

public class MainWearActivity extends Activity {

    private TextView mTextView;
    private WatchInflatedListener mWatchInfaltedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWatchInfaltedListener = new WatchInflatedListener();

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(mWatchInfaltedListener);
    }

    private class WatchInflatedListener implements WatchViewStub.OnLayoutInflatedListener, SensorEventListener {

        @Override
        public void onLayoutInflated(WatchViewStub watchViewStub) {
            mTextView = (TextView) watchViewStub.findViewById(R.id.text);

            SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            final float[] values = event.values;

            if(values.length > 0) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTextView.setText(String.valueOf(values[0]));
                    }
                });
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }
}
