package eu.applabs.crowdsensingfitnesslibrary.portal.google;

import android.app.Activity;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import eu.applabs.crowdsensingfitnesslibrary.data.ActivityCountBucket;
import eu.applabs.crowdsensingfitnesslibrary.data.ActivityTimeBucket;
import eu.applabs.crowdsensingfitnesslibrary.data.Person;
import eu.applabs.crowdsensingfitnesslibrary.data.StepBucket;
import eu.applabs.crowdsensingfitnesslibrary.portal.Portal;

public class GooglePortal extends Portal implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ReadFitnessThread.IReadFitnessThreadListener {

    private static final String sClassName = GooglePortal.class.getSimpleName();

    private static final int sRequestOAuth = 1;

    private int mRequestId = 0;
    private Activity mActivity = null;
    private boolean mAuthInProgress = false;
    private GoogleApiClient mGoogleApiClient = null;

    private Map<Integer, RequestType> mRequestMap = null;

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

    public List<ActivityCountBucket> convertToActivityCountBucket(List<Bucket> list) {
        List<ActivityCountBucket> returnList = new ArrayList<>();

        try {
            if (list != null) {
                for (Bucket bucket : list) {
                    List<DataSet> dataSets = bucket.getDataSets();

                    if (dataSets != null) {
                        for (DataSet dataSet : dataSets) {
                            for (DataPoint dp : dataSet.getDataPoints()) {
                                ActivityCountBucket activityCountBucket = new ActivityCountBucket();

                                Field field = getField(dp.getDataType().getFields(), "num_sequents");

                                if (field != null) {
                                    activityCountBucket.setActivityCount(dp.getValue(field).asInt());
                                }

                                field = getField(dp.getDataType().getFields(), "activity");

                                if (field != null) {
                                    activityCountBucket.setActivityType(
                                            eu.applabs.crowdsensingfitnesslibrary.data.Activity.Type.values()[dp.getValue(field).asInt()]);
                                }

                                Calendar c = Calendar.getInstance();
                                c.setTimeInMillis(dp.getStartTime(TimeUnit.MILLISECONDS));
                                activityCountBucket.setActivityDate(c.getTime());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Something went wrong
        }

        return returnList;
    }

    public Field getField(List<Field> list, String name) {
        for(Field field : list) {
            if(field.getName().compareTo(name) == 0) {
                return field;
            }
        }

        return null;
    }

    @Override
    public PortalType getPortalType() {
        return PortalType.Google;
    }

    @Override
    public void login(Activity activity) {
        mActivity = activity;
        mRequestMap = new HashMap<>();

        mGoogleApiClient = new GoogleApiClient.Builder(mActivity)
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_LOCATION_READ))
                .addScope(new Scope(Scopes.FITNESS_NUTRITION_READ))
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ))
                .addScope(new Scope(Scopes.FITNESS_BODY_READ))
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        if(mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void logout() {
        if(mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
            mGoogleApiClient = null;
        }
    }

    @Override
    public boolean checkActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == sRequestOAuth) {
            mAuthInProgress = false;

            if(resultCode == Activity.RESULT_OK) {
                if (mGoogleApiClient != null
                        && !mGoogleApiClient.isConnecting()
                        && !mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
            }

            return true;
        }

        return false;
    }

    @Override
    public void getPerson() {
        if(mGoogleApiClient != null && mGoogleApiClient.isConnected()) {

        }
    }

    @Override
    public void getSteps(long startTime,
                         long endTime,
                         TimeUnit rangeUnit,
                         int duration,
                         TimeUnit durationUnit) {

        if(mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            DataReadRequest request = new DataReadRequest.Builder()
                    .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                    .bucketByTime(duration, durationUnit)
                    .setTimeRange(startTime, endTime, rangeUnit)
                    .build();

            int requestId = mRequestId++;
            mRequestMap.put(requestId, RequestType.Step);
            new ReadFitnessThread(mGoogleApiClient, requestId, request, this).start();
        }
    }

    @Override
    public void getActivityCount(long startTime,
                                 long endTime,
                                 TimeUnit rangeUnit,
                                 int duration,
                                 TimeUnit durationUnit) {

        if(mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            DataReadRequest request = new DataReadRequest.Builder()
                    .aggregate(DataType.TYPE_ACTIVITY_SEGMENT, DataType.AGGREGATE_ACTIVITY_SUMMARY)
                    .bucketByTime(duration, durationUnit)
                    .setTimeRange(startTime, endTime, rangeUnit)
                    .build();

            int requestId = mRequestId++;
            mRequestMap.put(requestId, RequestType.ActivityCount);
            new ReadFitnessThread(mGoogleApiClient, requestId, request, this).start();
        }
    }

    @Override
    public void getActivityTime(long startTime,
                                long endTime,
                                TimeUnit rangeUnit,
                                int duration,
                                TimeUnit durationUnit) {

        if(mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            DataReadRequest request = new DataReadRequest.Builder()
                    .aggregate(DataType.TYPE_ACTIVITY_SEGMENT, DataType.AGGREGATE_ACTIVITY_SUMMARY)
                    .bucketByTime(duration, durationUnit)
                    .setTimeRange(startTime, endTime, rangeUnit)
                    .build();

            int requestId = mRequestId++;
            mRequestMap.put(requestId, RequestType.ActivityCount);
            new ReadFitnessThread(mGoogleApiClient, requestId, request, this).start();
        }
    }

    // GoogleApiClient.ConnectionCallbacks

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    // GoogleApiClient.OnConnectionFailedListener

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), mActivity, 0).show();
            return;
        }

        if (!mAuthInProgress) {
            try {
                mAuthInProgress = true;
                result.startResolutionForResult(mActivity, sRequestOAuth);
            } catch (IntentSender.SendIntentException e) {
                Log.e(sClassName, "Exception while starting resolution activity", e);
            }
        }
    }

    // ReadFitnessThread.IReadFitnessThreadListener

    @Override
    public void onSuccess(int requestId, List<Bucket> list) {
        if(mRequestMap != null && mRequestMap.containsKey(requestId)) {
            Portal.RequestType type = mRequestMap.get(requestId);

            for(Bucket bucket : list) {
                logDataSet(bucket.getDataSets());
            }

            switch(type) {
                case Undefined:
                    break;
                case ActivityTime:

                    notifyActivityTimeReceived(new ArrayList<ActivityTimeBucket>());
                    break;
                case ActivityCount:
                    List<ActivityCountBucket> resultList = convertToActivityCountBucket(list);
                    notifyActivityCountReceived(resultList);
                    break;
                case Person:
                    notifyPersonReceived(new Person());
                    break;
                case Step:
                    notifyStepsReceived(new ArrayList<StepBucket>());
                    break;
            }

            mRequestMap.remove(requestId);
        }
    }

    @Override
    public void onError(int requestId, Exception e) {
        if(mRequestMap != null && mRequestMap.containsKey(requestId)) {
            Portal.RequestType type = mRequestMap.get(requestId);

            switch(type) {
                case Undefined:
                    break;
                case ActivityTime:
                    notifyActivityTimeReceived(new ArrayList<ActivityTimeBucket>());
                    break;
                case ActivityCount:
                    notifyActivityCountReceived(new ArrayList<ActivityCountBucket>());
                    break;
                case Person:
                    notifyPersonReceived(new Person());
                    break;
                case Step:
                    notifyStepsReceived(new ArrayList<StepBucket>());
                    break;
            }

            mRequestMap.remove(requestId);
        }
    }
}
