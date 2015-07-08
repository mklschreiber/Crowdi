package eu.applabs.crowdsensingfitnesslibrary.data;

import java.util.Calendar;
import java.util.Date;

public class StepBucket {
    private int mStepCount = 0;
    private Date mStepDate = null;

    public StepBucket() {
        mStepDate = Calendar.getInstance().getTime();
    }

    public void setStepCount(int stepCount) {
        mStepCount = stepCount;
    }

    public void setStepDate(Date stepDate) {
        mStepDate = stepDate;
    }

    public int getStepCount() {
        return mStepCount;
    }

    public Date getStepDate() {
        return mStepDate;
    }
}
