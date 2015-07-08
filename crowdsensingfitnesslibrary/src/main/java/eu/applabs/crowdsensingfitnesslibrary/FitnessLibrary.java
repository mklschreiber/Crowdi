package eu.applabs.crowdsensingfitnesslibrary;

import android.app.Activity;
import android.content.Intent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import eu.applabs.crowdsensingfitnesslibrary.data.ActivityCountBucket;
import eu.applabs.crowdsensingfitnesslibrary.data.ActivityTimeBucket;
import eu.applabs.crowdsensingfitnesslibrary.data.Person;
import eu.applabs.crowdsensingfitnesslibrary.data.StepBucket;
import eu.applabs.crowdsensingfitnesslibrary.portal.Portal;
import eu.applabs.crowdsensingfitnesslibrary.portal.google.GooglePortal;

public class FitnessLibrary implements Portal.IPortalListener{

    public interface IFitnessLibraryListener {
        void onPersonReceived(Person person);
        void onStepsReceived(List<StepBucket> list);
        void onActivityTimeReceived(List<ActivityTimeBucket> list);
        void onActivityCountReceived(List<ActivityCountBucket> list);
    }

    private static final String sClassName = FitnessLibrary.class.getSimpleName();

    private Activity mActivity = null;
    private List<Portal> mPortalList = null;
    private List<IFitnessLibraryListener> mIFitnessLibraryListenerList = null;

    public FitnessLibrary(Activity activity) {
        mActivity = activity;
        mPortalList = new ArrayList<>();
        mIFitnessLibraryListenerList = new ArrayList<>();

        mPortalList.add(new GooglePortal());
    }

    public void connect(Portal.PortalType type) {
        Portal portal = findPortal(type);

        if(portal != null) {
            portal.login(mActivity);
        }
    }

    public void disconnect(Portal.PortalType type) {
        Portal portal = findPortal(type);

        if(portal != null) {
            portal.logout();
        }
    }

    public void checkActivityResult(int requestCode, int resultCode, Intent data) {
        for(Portal portal : mPortalList) {
            if(portal.checkActivityResult(requestCode, resultCode, data)) {
                return;
            }
        }
    }

    public void registerListener(IFitnessLibraryListener listener) {
        mIFitnessLibraryListenerList.add(listener);
    }

    public void unregisterListener(IFitnessLibraryListener listener) {
        mIFitnessLibraryListenerList.remove(listener);
    }

    public void getPerson(Portal.PortalType type) {
        Portal portal = findPortal(type);

        if(portal != null) {
            portal.getPerson();
        }
    }

    public void getSteps(Portal.PortalType type,
                         long startTime,
                         long endTime,
                         TimeUnit rangeUnit,
                         int duration,
                         TimeUnit durationUnit) {
        Portal portal = findPortal(type);

        if(portal != null) {
            portal.getSteps(startTime, endTime, rangeUnit, duration, durationUnit);
        }
    }

    public void getActivityCount(Portal.PortalType type,
                                 long startTime,
                                 long endTime,
                                 TimeUnit rangeUnit,
                                 int duration,
                                 TimeUnit durationUnit) {
        Portal portal = findPortal(type);

        if(portal != null) {
            portal.getActivityCount(startTime, endTime, rangeUnit, duration, durationUnit);
        }
    }

    public void getActivityTime(Portal.PortalType type,
                                long startTime,
                                long endTime,
                                TimeUnit rangeUnit,
                                int duration,
                                TimeUnit durationUnit) {
        Portal portal = findPortal(type);

        if(portal != null) {
            portal.getActivityTime(startTime, endTime, rangeUnit, duration, durationUnit);
        }
    }

    private Portal findPortal(Portal.PortalType type) {
        for(Portal portal : mPortalList) {
            if(portal.getPortalType() == type) {
                return portal;
            }
        }

        return null;
    }

    @Override
    public void onPersonReceived(Person person) {
        for(IFitnessLibraryListener listener : mIFitnessLibraryListenerList) {
            listener.onPersonReceived(person);
        }
    }

    @Override
    public void onStepsReceived(List<StepBucket> list) {
        for(IFitnessLibraryListener listener : mIFitnessLibraryListenerList) {
            listener.onStepsReceived(list);
        }
    }

    @Override
    public void onActivityTimeReceived(List<ActivityTimeBucket> list) {
        for(IFitnessLibraryListener listener : mIFitnessLibraryListenerList) {
            listener.onActivityTimeReceived(list);
        }
    }

    @Override
    public void onActivityCountReceived(List<ActivityCountBucket> list) {
        for(IFitnessLibraryListener listener : mIFitnessLibraryListenerList) {
            listener.onActivityCountReceived(list);
        }
    }
}
