package eu.applabs.crowdsensingfitnesslibrary;

import android.app.Activity;
import android.content.Intent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import eu.applabs.crowdsensingfitnesslibrary.data.ActivityBucket;
import eu.applabs.crowdsensingfitnesslibrary.data.Person;
import eu.applabs.crowdsensingfitnesslibrary.data.StepBucket;
import eu.applabs.crowdsensingfitnesslibrary.portal.Portal;
import eu.applabs.crowdsensingfitnesslibrary.portal.apple.ApplePortal;
import eu.applabs.crowdsensingfitnesslibrary.portal.google.GooglePortal;
import eu.applabs.crowdsensingfitnesslibrary.portal.microsoft.MicrosoftPortal;
import eu.applabs.crowdsensingfitnesslibrary.settings.SettingsManager;

public class FitnessLibrary implements Portal.IPortalListener{

    public interface IFitnessLibraryListener {
        void onPersonReceived(Person person);
        void onStepsReceived(List<StepBucket> list);
        void onActivitiesReceived(List<ActivityBucket> list);
        void onPortalConnectionStateChanged();
    }

    private static final String sClassName = FitnessLibrary.class.getSimpleName();

    private static FitnessLibrary mInstance = null;
    private boolean mInitialized = false;

    private Activity mActivity = null;
    private SettingsManager mSettingsManager = null;
    private List<Portal> mPortalList = null;
    private List<IFitnessLibraryListener> mIFitnessLibraryListenerList = null;

    private FitnessLibrary() { }

    public static FitnessLibrary getInstance() {
        if(FitnessLibrary.mInstance == null) {
            FitnessLibrary.mInstance = new FitnessLibrary();
            FitnessLibrary.mInstance.mPortalList = new ArrayList<>();
            FitnessLibrary.mInstance.mIFitnessLibraryListenerList = new ArrayList<>();
        }

        return FitnessLibrary.mInstance;
    }

    public void init(Activity activity) {
        if(!mInitialized) {
            mActivity = activity;
            mSettingsManager = new SettingsManager(mActivity);

            mPortalList.add(new GooglePortal());
            mPortalList.add(new ApplePortal());
            mPortalList.add(new MicrosoftPortal());

            List<Portal.PortalType> list = mSettingsManager.getConnectedServices();

            for(Portal.PortalType type : list) {
                connect(type);
            }
        }
    }

    public void connect(Portal.PortalType type) {
        connect(type, mActivity);
    }

    public boolean isConnected(Portal.PortalType type) {
        Portal portal = findPortal(type);

        if(portal != null) {
            return portal.isConnected();
        }

        return false;
    }

    public void connect(Portal.PortalType type, Activity activity) {
        Portal portal = findPortal(type);

        if(portal != null) {
            portal.registerListener(this);
            portal.login(activity);
        }
    }

    public void disconnect(Portal.PortalType type) {
        Portal portal = findPortal(type);

        if(portal != null) {
            portal.unregisterListener(this);
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

    public void getActivities(Portal.PortalType type,
                                 long startTime,
                                 long endTime,
                                 TimeUnit rangeUnit,
                                 int duration,
                                 TimeUnit durationUnit) {
        Portal portal = findPortal(type);

        if(portal != null) {
            portal.getActivities(startTime, endTime, rangeUnit, duration, durationUnit);
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
    public void onActivitiesReceived(List<ActivityBucket> list) {
        for(IFitnessLibraryListener listener : mIFitnessLibraryListenerList) {
            listener.onActivitiesReceived(list);
        }
    }

    @Override
    public void onPortalConnectionStateChanged() {
        for(IFitnessLibraryListener listener : mIFitnessLibraryListenerList) {
            listener.onPortalConnectionStateChanged();
        }
    }
}
