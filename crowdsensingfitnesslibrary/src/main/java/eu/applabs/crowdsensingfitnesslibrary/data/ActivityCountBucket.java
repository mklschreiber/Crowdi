package eu.applabs.crowdsensingfitnesslibrary.data;

import java.util.Calendar;
import java.util.Date;

public class ActivityCountBucket {
    private int mActivityCount = 0;
    private Activity.Type mActivityType = Activity.Type.Unknown;
    private Date mActivityDate = null;

    public ActivityCountBucket() {
        mActivityDate = Calendar.getInstance().getTime();
    }

    public void setActivityCount(int activityCount) {
        mActivityCount = activityCount;
    }

    public void setActivityType(Activity.Type type) {
        mActivityType = type;
    }

    public void setActivityDate(Date activityDate) {
        mActivityDate = activityDate;
    }

    public int getActivityCount() {
        return mActivityCount;
    }

    public Activity.Type getActivityType() {
        return mActivityType;
    }

    public Date getActivityDate() {
        return mActivityDate;
    }
}
