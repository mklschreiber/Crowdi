package eu.applabs.crowdsensingfitnesslibrary.data;

import java.util.Calendar;
import java.util.Date;

public class StepBucket {
    private int mStepCount = 0;
    private Date mStepStartDate = null;
    private Date mStepEndDate = null;

    public StepBucket() {
        mStepStartDate = Calendar.getInstance().getTime();
        mStepEndDate = Calendar.getInstance().getTime();
    }

    public void setStepCount(int stepCount) {
        mStepCount = stepCount;
    }

    public void setStepStartDate(Date stepStartDate) {
        mStepStartDate = stepStartDate;
    }

    public void setStepEndDate(Date stepEndDate) {
        mStepEndDate = stepEndDate;
    }

    public int getStepCount() {
        return mStepCount;
    }

    public Date getStepStartDate() {
        return mStepStartDate;
    }

    public Date getStepEndDate() {
        return mStepEndDate;
    }
}
