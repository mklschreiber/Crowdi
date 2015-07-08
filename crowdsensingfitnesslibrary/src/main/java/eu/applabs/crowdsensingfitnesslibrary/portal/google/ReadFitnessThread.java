package eu.applabs.crowdsensingfitnesslibrary.portal.google;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResult;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ReadFitnessThread extends Thread {

    public interface IReadFitnessThreadListener {
        void onSuccess(int requestId, List<Bucket> list);
        void onError(int requestId, Exception e);
    }

    private GoogleApiClient mGoogleApiClient = null;
    private int mRequestId = 0;
    private DataReadRequest mDataReadRequest = null;
    private IReadFitnessThreadListener mIReadFitnessThreadListener = null;

    public ReadFitnessThread(GoogleApiClient googleApiClient,
                             int requestId,
                             DataReadRequest dataReadRequest,
                             IReadFitnessThreadListener iReadFitnessThreadListener) {
        mGoogleApiClient = googleApiClient;
        mRequestId = requestId;
        mDataReadRequest = dataReadRequest;
        mIReadFitnessThreadListener = iReadFitnessThreadListener;
    }

    @Override
    public void run() {
        super.run();

        try {
            DataReadResult dataReadResult =
                    Fitness.HistoryApi.readData(mGoogleApiClient, mDataReadRequest)
                            .await(1, TimeUnit.MINUTES);

            List<Bucket> bucketList = dataReadResult.getBuckets();

            mIReadFitnessThreadListener.onSuccess(mRequestId, bucketList);
        } catch (Exception e) {
            mIReadFitnessThreadListener.onError(mRequestId, e);
        }
    }
}
