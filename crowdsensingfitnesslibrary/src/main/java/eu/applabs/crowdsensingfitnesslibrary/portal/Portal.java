package eu.applabs.crowdsensingfitnesslibrary.portal;

import android.app.Activity;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import eu.applabs.crowdsensingfitnesslibrary.FitnessLibrary;
import eu.applabs.crowdsensingfitnesslibrary.data.ActivityBucket;
import eu.applabs.crowdsensingfitnesslibrary.data.Person;
import eu.applabs.crowdsensingfitnesslibrary.data.StepBucket;

public abstract class Portal {

    public enum PortalType {
        Undefined,
        Google,
        Apple,
        Microsoft,
        Fake
    }

    public enum RequestType {
        Undefined,
        Person,
        Step,
        Activity
    }

    private List<IPortalListener> mIPortalListenerList = null;

    public interface IPortalListener {
        void onPersonReceived(FitnessLibrary.IFitnessLibraryListener.ExecutionStatus status, int requestId, Person person);
        void onStepsReceived(FitnessLibrary.IFitnessLibraryListener.ExecutionStatus status, int requestId, List<StepBucket> list);
        void onActivitiesReceived(FitnessLibrary.IFitnessLibraryListener.ExecutionStatus status, int requestId, List<ActivityBucket> list);
        void onPortalConnectionStateChanged();
    }

    public Portal() {
        mIPortalListenerList = new ArrayList<>();
    }

    public void registerListener(IPortalListener listener) {
        if(!mIPortalListenerList.contains(listener)) {
            mIPortalListenerList.add(listener);
        }
    }

    public void unregisterListener(IPortalListener listener) {
        mIPortalListenerList.remove(listener);
    }

    public void notifyPersonReceived(FitnessLibrary.IFitnessLibraryListener.ExecutionStatus status, int requestId, Person person) {
        for(IPortalListener listener : mIPortalListenerList) {
            listener.onPersonReceived(status, requestId, person);
        }
    }

    public void notifyStepsReceived(FitnessLibrary.IFitnessLibraryListener.ExecutionStatus status, int requestId, List<StepBucket> list) {
        for(IPortalListener listener : mIPortalListenerList) {
            listener.onStepsReceived(status, requestId, list);
        }
    }

    public void notifyActivitiesReceived(FitnessLibrary.IFitnessLibraryListener.ExecutionStatus status, int requestId, List<ActivityBucket> list) {
        for(IPortalListener listener : mIPortalListenerList) {
            listener.onActivitiesReceived(status, requestId, list);
        }
    }

    public void notifyPortalConnectionStateChanged() {
        for(IPortalListener listener : mIPortalListenerList) {
            listener.onPortalConnectionStateChanged();
        }
    }

    public abstract PortalType getPortalType();
    public abstract void login(Activity activity);
    public abstract void logout();
    public abstract boolean isConnected();
    public abstract boolean checkActivityResult(int requestCode, int resultCode, Intent data);

    public abstract void getPerson(int requestId);
    public abstract void getSteps(long startTime,
                                  long endTime,
                                  TimeUnit rangeUnit,
                                  int duration,
                                  TimeUnit durationUnit,
                                  int requestId);
    public abstract void getActivities(long startTime,
                                       long endTime,
                                       TimeUnit rangeUnit,
                                       int duration,
                                       TimeUnit durationUnit,
                                       int requestId);
}
