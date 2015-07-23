package eu.applabs.crowdsensinglibrary.tool;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import java.util.ArrayList;
import java.util.List;

public class StopWatch {

    public interface IStopWatchListener {
        void onDismiss();
    }

    private Context mContext = null;
    private List<IStopWatchListener> mIStopWatchListenerList = null;

    private long mStartTime = 0;
    private long mEndTime = 0;

    public StopWatch(Context context) {
        mContext = context;
        mIStopWatchListenerList = new ArrayList<>();
    }

    public void registerListener(IStopWatchListener listener) {
        mIStopWatchListenerList.add(listener);
    }

    public void unregisterListener(IStopWatchListener listener) {
        mIStopWatchListenerList.remove(listener);
    }

    public void start() {
        mStartTime = System.currentTimeMillis();
    }

    public void stop() {
        mEndTime = System.currentTimeMillis();

        String time = String.valueOf((mEndTime - mStartTime) / 1000);

        new AlertDialog.Builder(mContext)
                .setTitle("Time")
                .setMessage("You needed " + time + " seconds for the poll")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for(IStopWatchListener listener : mIStopWatchListenerList) {
                            listener.onDismiss();
                        }

                        dialog.dismiss();
                    }
                }).create().show();
    }
}
