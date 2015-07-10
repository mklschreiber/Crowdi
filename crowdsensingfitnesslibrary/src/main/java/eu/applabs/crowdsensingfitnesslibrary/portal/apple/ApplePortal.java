package eu.applabs.crowdsensingfitnesslibrary.portal.apple;

import android.app.Activity;
import android.content.Intent;

import java.util.concurrent.TimeUnit;

import eu.applabs.crowdsensingfitnesslibrary.portal.Portal;

public class ApplePortal extends Portal {
    @Override
    public PortalType getPortalType() {
        return PortalType.Apple;
    }

    @Override
    public void login(Activity activity) {

    }

    @Override
    public void logout() {

    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public boolean checkActivityResult(int requestCode, int resultCode, Intent data) {
        return false;
    }

    @Override
    public void getPerson() {

    }

    @Override
    public void getSteps(long startTime, long endTime, TimeUnit rangeUnit, int duration, TimeUnit durationUnit) {

    }

    @Override
    public void getActivities(long startTime, long endTime, TimeUnit rangeUnit, int duration, TimeUnit durationUnit) {

    }
}
