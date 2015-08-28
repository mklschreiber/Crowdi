package eu.applabs.crowdsensingfitnesslibrary.portal.fake;

import android.app.Activity;
import android.content.Intent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import eu.applabs.crowdsensingfitnesslibrary.FitnessLibrary;
import eu.applabs.crowdsensingfitnesslibrary.data.ActivityBucket;
import eu.applabs.crowdsensingfitnesslibrary.data.Person;
import eu.applabs.crowdsensingfitnesslibrary.data.StepBucket;
import eu.applabs.crowdsensingfitnesslibrary.portal.Portal;
import eu.applabs.crowdsensingfitnesslibrary.settings.SettingsManager;

public class FakePortal extends Portal {

    private Activity mActivity = null;
    private SettingsManager mSettingsManager = null;
    private boolean mConnected = false;

    private Person mPerson = null;

    @Override
    public PortalType getPortalType() {
        return PortalType.Fake;
    }

    @Override
    public void login(Activity activity) {
        mActivity = activity;
        mSettingsManager = new SettingsManager(mActivity);
        mConnected = true;

        mPerson = new Person();
        mPerson.setName("Hans");
        mPerson.setSize("1,83");
        mPerson.setWeight("84");

        if(mSettingsManager != null) {
            List<PortalType> list = mSettingsManager.getConnectedServices();
            if(!list.contains(PortalType.Fake)) {
                list.add(PortalType.Fake);
                mSettingsManager.setConnectedServices(list);
            }
        }

        notifyPortalConnectionStateChanged();
    }

    @Override
    public void logout() {
        mConnected = false;

        if(mSettingsManager != null) {
            List<PortalType> list = mSettingsManager.getConnectedServices();
            if(list.contains(PortalType.Fake)) {
                list.remove(PortalType.Fake);
                mSettingsManager.setConnectedServices(list);
            }
        }

        notifyPortalConnectionStateChanged();
    }

    @Override
    public boolean isConnected() {
        return mConnected;
    }

    @Override
    public boolean checkActivityResult(int requestCode, int resultCode, Intent data) {
        return false;
    }

    @Override
    public void getPerson(int requestId) {
        notifyPersonReceived(FitnessLibrary.IFitnessLibraryListener.ExecutionStatus.Success, requestId, mPerson);
    }

    @Override
    public void getSteps(long startTime, long endTime, TimeUnit rangeUnit, int duration, TimeUnit durationUnit, int requestId) {
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);

        ArrayList<StepBucket> list = new ArrayList<>();

        for(int i = 10; i > 0; i--) {
            cal.add(Calendar.DAY_OF_MONTH, i);

            StepBucket sb = new StepBucket();
            sb.setStepCount((int) (Math.random() * 1000));
            sb.setStepStartDate(cal.getTime());
            sb.setStepEndDate(cal.getTime());

            list.add(sb);
            cal.setTime(now);
        }

        notifyStepsReceived(FitnessLibrary.IFitnessLibraryListener.ExecutionStatus.Success, requestId, list);
    }

    @Override
    public void getActivities(long startTime, long endTime, TimeUnit rangeUnit, int duration, TimeUnit durationUnit, int requestId) {
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);

        ArrayList<ActivityBucket> list = new ArrayList<>();

        for(int i = 10; i > 0; i--) {
            cal.add(Calendar.DAY_OF_MONTH, i);

            ActivityBucket ab = new ActivityBucket();
            ab.setActivityCount((int) (Math.random() * 10));
            ab.setActivityType(eu.applabs.crowdsensingfitnesslibrary.data.Activity.Type.Aerobic);
            ab.setActivityDuration((int) (Math.random() * 1000));
            ab.setActivityStartDate(cal.getTime());
            ab.setActivityEndDate(cal.getTime());

            list.add(ab);
            cal.setTime(now);
        }

        notifyActivitiesReceived(FitnessLibrary.IFitnessLibraryListener.ExecutionStatus.Success, requestId, list);
    }
}
