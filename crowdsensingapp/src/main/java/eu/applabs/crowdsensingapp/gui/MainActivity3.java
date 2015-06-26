package eu.applabs.crowdsensingapp.gui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.android.AndroidUpnpServiceImpl;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.types.UDAServiceType;
import org.fourthline.cling.model.types.UDN;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.UUID;

import eu.applabs.crowdsensingapp.R;
import eu.applabs.crowdsensingapp.data.Constants;
import eu.applabs.crowdsensingapp.service.DataTransferService;
import eu.applabs.crowdsensingupnplibrary.data.DeviceFactory;
import eu.applabs.crowdsensingupnplibrary.data.HeartRateService;

public class MainActivity3 extends Activity implements View.OnClickListener {

    private GoogleApiClient mGoogleApiClient;

    private AndroidUpnpService mAndroidUpnpService;
    private UDN mUDN = new UDN(UUID.randomUUID());
    private UpnpServiceConnection mUpnpServiceConnection;
    public static LocalService<HeartRateService> mHeartRateService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        mUpnpServiceConnection = new UpnpServiceConnection();

        init();
        Button b = (Button) findViewById(R.id.button);
        b.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();

        bindService(new Intent(this, AndroidUpnpServiceImpl.class),
                mUpnpServiceConnection,
                Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();

        unbindService(mUpnpServiceConnection);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.button:
                sendNotification("Messung starten", "Herzfrequenzmessung starten");
                break;
        }
    }

    private void init() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
    }

    private void sendNotification(String title, String content) {
        if(mGoogleApiClient.isConnected()) {
            PutDataMapRequest dataMapRequest = PutDataMapRequest.create(Constants.NOTIFICATION_WEAR_PATH);
            dataMapRequest.getDataMap().putDouble(Constants.NOTIFICATION_WEAR_TIMESTAMP, System.currentTimeMillis());
            dataMapRequest.getDataMap().putString(Constants.NOTIFICATION_WEAR_TITLE, title);
            dataMapRequest.getDataMap().putString(Constants.NOTIFICATION_WEAR_CONTENT, content);
            PutDataRequest putDataRequest = dataMapRequest.asPutDataRequest();
            Wearable.DataApi.putDataItem(mGoogleApiClient, putDataRequest);
        }
    }

    private class UpnpServiceConnection implements ServiceConnection, PropertyChangeListener {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mAndroidUpnpService = (AndroidUpnpService) service;

            mHeartRateService = getHeartRateService();

            if(mHeartRateService == null) {
                LocalDevice heartRateServiceDevice = new DeviceFactory().createDevice(mUDN);

                if(heartRateServiceDevice != null) {
                    mAndroidUpnpService.getRegistry().addDevice(heartRateServiceDevice);

                    mHeartRateService = getHeartRateService();
                }
            }

            if(mHeartRateService != null) {
                mHeartRateService.getManager().getImplementation().registerListener(this);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mAndroidUpnpService = null;
        }

        @Override
        public void propertyChange(PropertyChangeEvent event) {
            sendNotification("Messung starten", "Herzfrequenzmessung starten");

            /*while(DataTransferService.sHeartRate.compareTo("0.0") == 0) {
                // Wait
            }*/

            mHeartRateService.getManager().getImplementation().setHeartRate("63.0");
        }
    }

    protected LocalService<HeartRateService> getHeartRateService() {
        if (mAndroidUpnpService == null)
            return null;

        LocalDevice binaryLightDevice;
        if ((binaryLightDevice = mAndroidUpnpService.getRegistry().getLocalDevice(mUDN, true)) == null)
            return null;

        return (LocalService<HeartRateService>)
                binaryLightDevice.findService(new UDAServiceType("HeartRateService", 1));
    }
}
