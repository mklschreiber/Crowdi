package eu.applabs.crowdsensingfitnesslibrary.data;

import java.util.Calendar;
import java.util.Date;

public class ActivityBucket {
    private int mActivityCount = 0;
    private int mActivityDuration = 0;
    private Activity.Type mActivityType = Activity.Type.Unknown;
    private Date mActivityStartDate = null;
    private Date mActivityEndDate = null;

    public ActivityBucket() {
        mActivityStartDate = Calendar.getInstance().getTime();
        mActivityEndDate = Calendar.getInstance().getTime();
    }

    public void setActivityCount(int activityCount) {
        mActivityCount = activityCount;
    }

    public void setActivityDuration(int activityDuration) {
        mActivityDuration = activityDuration;
    }

    public void setActivityType(Activity.Type type) {
        mActivityType = type;
    }

    public void setActivityStartDate(Date activityStartDate) {
        mActivityStartDate = activityStartDate;
    }

    public void setActivityEndDate(Date activityEndDate) {
        mActivityEndDate = activityEndDate;
    }

    public int getActivityCount() {
        return mActivityCount;
    }

    public int getActivityDuration() {
        return mActivityDuration;
    }

    public Activity.Type getActivityType() {
        return mActivityType;
    }

    public Date getActivityStartDate() {
        return mActivityStartDate;
    }

    public Date getActivityEndDate() {
        return mActivityEndDate;
    }
}
