package eu.applabs.crowdsensingfitnesslibrary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResult;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class FitnessLibrary {

    private static final String sClassName = FitnessLibrary.class.getSimpleName();

    private static final int sRequestOAuth = 1;
    private static final String sAuthPending = "auth_state_pending";

    private Activity mActivity = null;
    private boolean mAuthInProgress = false;
    private GoogleApiClient mGoogleApiClient = null;
    private GConnectionCallback mGConnectionCallback = null;
    private GConnectionFailedListener mGConnectionFailedListener = null;

    public FitnessLibrary(Activity activity) {
        mActivity = activity;

        mGConnectionCallback = new GConnectionCallback();
        mGConnectionFailedListener = new GConnectionFailedListener();
    }

    public void connect() {
        mGoogleApiClient = new GoogleApiClient.Builder(mActivity)
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_LOCATION_READ))
                .addScope(new Scope(Scopes.FITNESS_NUTRITION_READ))
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ))
                .addScope(new Scope(Scopes.FITNESS_BODY_READ))
                .addConnectionCallbacks(mGConnectionCallback)
                .addOnConnectionFailedListener(mGConnectionFailedListener)
                .build();

        if(mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    public void checkActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == sRequestOAuth) {
            mAuthInProgress = false;

            if(resultCode == Activity.RESULT_OK) {
                if (mGoogleApiClient != null
                        && !mGoogleApiClient.isConnecting()
                        && !mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
            }
        }
    }

    public void disconnect() {
        if(mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
            mGoogleApiClient = null;
        }
    }

    public void readSteps() {
        StepThread st = new StepThread(mGoogleApiClient);
        st.start();
    }

    private class StepThread extends Thread {

        private GoogleApiClient mGoogleApiClient = null;

        public StepThread(GoogleApiClient googleApiClient) {
            mGoogleApiClient = googleApiClient;
        }

        @Override
        public void run() {
            super.run();

            if(mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                Calendar cal = Calendar.getInstance();
                Date now = new Date();
                cal.setTime(now);
                long endTime = cal.getTimeInMillis();
                cal.add(Calendar.WEEK_OF_YEAR, -1);
                long startTime = cal.getTimeInMillis();

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Log.i(sClassName, "Range Start: " + dateFormat.format(startTime));
                Log.i(sClassName, "Range End: " + dateFormat.format(endTime));

                DataReadRequest readRequest = new DataReadRequest.Builder()
                        // The data request can specify multiple data types to return, effectively
                        // combining multiple data queries into one call.
                        // In this example, it's very unlikely that the request is for several hundred
                        // datapoints each consisting of a few steps and a timestamp.  The more likely
                        // scenario is wanting to see how many steps were walked per day, for 7 days.
                        .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                                // Analogous to a "Group By" in SQL, defines how data should be aggregated.
                                // bucketByTime allows for a time span, whereas bucketBySession would allow
                                // bucketing by "sessions", which would need to be defined in code.
                        .bucketByTime(1, TimeUnit.DAYS)
                        .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                        .build();

                DataReadResult dataReadResult =
                        Fitness.HistoryApi.readData(mGoogleApiClient, readRequest).await(1, TimeUnit.MINUTES);

                List<DataSet> list = dataReadResult.getDataSets();
                logDataSet(list);

                List<Bucket> bucketlist = dataReadResult.getBuckets();

                for(Bucket bucket : bucketlist) {
                    List<DataSet> l = bucket.getDataSets();
                    logDataSet(l);
                }


            }
        }
    }

    public void logDataSet(List<DataSet> list) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        if(list != null) {
            for(DataSet dataSet : list) {
                Log.i(sClassName, "Data returned for Data type: " + dataSet.getDataType().getName());

                for (DataPoint dp : dataSet.getDataPoints()) {
                    Log.i(sClassName, "Data point:");
                    Log.i(sClassName, "\tType: " + dp.getDataType().getName());
                    Log.i(sClassName, "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
                    Log.i(sClassName, "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
                    for(Field field : dp.getDataType().getFields()) {
                        Log.i(sClassName, "\tField: " + field.getName() + " Value: " + dp.getValue(field));
                    }
                }
            }
        }
    }

    private class GConnectionCallback implements GoogleApiClient.ConnectionCallbacks {
        @Override
        public void onConnected(Bundle bundle) {
            int i = 0;
            i++;

            readSteps();
        }

        @Override
        public void onConnectionSuspended(int i) {
            int x = 0;
            x++;
        }
    }

    private class GConnectionFailedListener implements GoogleApiClient.OnConnectionFailedListener {
        @Override
        public void onConnectionFailed(ConnectionResult result) {
            Log.i(sClassName, "Connection failed. Cause: " + result.toString());
            if (!result.hasResolution()) {
                // Show the localized error dialog
                GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(),
                        mActivity, 0).show();
                return;
            }
            // The failure has a resolution. Resolve it.
            // Called typically when the app is not yet authorized, and an
            // authorization dialog is displayed to the user.
            if (!mAuthInProgress) {
                try {
                    Log.i(sClassName, "Attempting to resolve failed connection");
                    mAuthInProgress = true;
                    result.startResolutionForResult(mActivity, sRequestOAuth);
                } catch (IntentSender.SendIntentException e) {
                    Log.e(sClassName,
                            "Exception while starting resolution activity", e);
                }
            }
        }
    }
}
