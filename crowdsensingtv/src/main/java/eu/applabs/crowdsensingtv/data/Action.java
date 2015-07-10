package eu.applabs.crowdsensingtv.data;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import eu.applabs.crowdsensingtv.R;

public class Action {
    private String mTitle = null;
    private Drawable mIcon = null;
    private Intent mIntent = null;

    public Action(Context context) {
        mTitle = "";
        mIcon = context.getDrawable(R.drawable.poll);
        mIntent = new Intent();
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setIcon(Drawable icon) {
        mIcon = icon;
    }

    public void setIntent(Intent intent) {
        mIntent = intent;
    }

    public String getTitle() {
        return mTitle;
    }

    public Drawable getIcon() {
        return mIcon;
    }

    public Intent getIntent() {
        return mIntent;
    }
}