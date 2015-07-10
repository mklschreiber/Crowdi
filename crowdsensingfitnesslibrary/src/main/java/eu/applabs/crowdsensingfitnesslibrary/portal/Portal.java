package eu.applabs.crowdsensingfitnesslibrary.portal;

import android.app.Activity;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import eu.applabs.crowdsensingfitnesslibrary.data.ActivityBucket;
import eu.applabs.crowdsensingfitnesslibrary.data.Person;
import eu.applabs.crowdsensingfitnesslibrary.data.StepBucket;

public abstract class Portal {

    public enum PortalType {
        Undefined,
        Google,
        Apple,
        Microsoft
    }

    public enum RequestType {
        Undefined,
        Person,
        Step,
        Activity
    }

    private List<IPortalListener> mIPortalListenerList = null;

    public interface IPortalListener {
        void onPersonReceived(Person person);
        void onStepsReceived(List<StepBucket> list);
        void onActivitiesReceived(List<ActivityBucket> list);
        void onPortalConnectionStateChanged();
    }

    public Portal() {
        mIPortalListenerList = new ArrayList<>();
    }

    public void registerListener(IPortalListener listener) {
        mIPortalListenerList.add(listener);
    }

    public void unregisterListener(IPortalListener listener) {
        mIPortalListenerList.remove(listener);
    }

    public void notifyPersonReceived(Person person) {
        for(IPortalListener listener : mIPortalListenerList) {
            listener.onPersonReceived(person);
        }
    }

    public void notifyStepsReceived(List<StepBucket> list) {
        for(IPortalListener listener : mIPortalListenerList) {
            listener.onStepsReceived(list);
        }
    }

    public void notifyActivitiesReceived(List<ActivityBucket> list) {
        for(IPortalListener listener : mIPortalListenerList) {
            listener.onActivitiesReceived(list);
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

    public abstract void getPerson();
    public abstract void getSteps(long startTime,
                                  long endTime,
                                  TimeUnit rangeUnit,
                                  int duration,
                                  TimeUnit durationUnit);
    public abstract void getActivities(long startTime,
                                       long endTime,
                                       TimeUnit rangeUnit,
                                       int duration,
                                       TimeUnit durationUnit);
}
