package eu.applabs.crowdsensingfitnesslibrary.portal;

import android.app.Activity;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import eu.applabs.crowdsensingfitnesslibrary.data.ActivityCountBucket;
import eu.applabs.crowdsensingfitnesslibrary.data.ActivityTimeBucket;
import eu.applabs.crowdsensingfitnesslibrary.data.Person;
import eu.applabs.crowdsensingfitnesslibrary.data.StepBucket;

public abstract class Portal {

    public enum PortalType {
        Undefined,
        Google
    }

    public enum RequestType {
        Undefined,
        Person,
        Step,
        ActivityCount,
        ActivityTime
    }

    private List<IPortalListener> mIPortalListenerList = null;

    public interface IPortalListener {
        void onPersonReceived(Person person);
        void onStepsReceived(List<StepBucket> list);
        void onActivityTimeReceived(List<ActivityTimeBucket> list);
        void onActivityCountReceived(List<ActivityCountBucket> list);
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

    public void notifyActivityTimeReceived(List<ActivityTimeBucket> list) {
        for(IPortalListener listener : mIPortalListenerList) {
            listener.onActivityTimeReceived(list);
        }
    }

    public void notifyActivityCountReceived(List<ActivityCountBucket> list) {
        for(IPortalListener listener : mIPortalListenerList) {
            listener.onActivityCountReceived(list);
        }
    }

    public abstract PortalType getPortalType();
    public abstract void login(Activity activity);
    public abstract void logout();
    public abstract boolean checkActivityResult(int requestCode, int resultCode, Intent data);

    public abstract void getPerson();
    public abstract void getSteps(long startTime,
                                  long endTime,
                                  TimeUnit rangeUnit,
                                  int duration,
                                  TimeUnit durationUnit);
    public abstract void getActivityCount(long startTime,
                                          long endTime,
                                          TimeUnit rangeUnit,
                                          int duration,
                                          TimeUnit durationUnit);
    public abstract void getActivityTime(long startTime,
                                         long endTime,
                                         TimeUnit rangeUnit,
                                         int duration,
                                         TimeUnit durationUnit);
}
