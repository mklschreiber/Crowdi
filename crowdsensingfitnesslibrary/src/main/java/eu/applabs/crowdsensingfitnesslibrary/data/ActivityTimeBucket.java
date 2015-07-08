package eu.applabs.crowdsensingfitnesslibrary.data;

import java.util.Calendar;
import java.util.Date;

public class ActivityTimeBucket {
    private long mActivityTime = 0;
    private Activity.Type mActivityType = Activity.Type.Unknown;
    private Date mActivityDate = null;

    public ActivityTimeBucket() {
        mActivityDate = Calendar.getInstance().getTime();
    }

    public void setActivityTime(long time) {
        mActivityTime = time;
    }

    public void setActivityType(Activity.Type type) {
        mActivityType = type;
    }

    public void setActivityDate(Date date) {
        mActivityDate = date;
    }

    public long getActivityTime() {
        return mActivityTime;
    }

    public Activity.Type getActivityType() {
        return mActivityType;
    }

    public Date getActivityDate() {
        return mActivityDate;
    }
}
