package eu.applabs.crowdsensingtv.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SettingsManager {

    private static final String sNotificationTimeStamp = "NotificationTimeStamp";

    private Context mContext = null;
    private SharedPreferences mSharedPreferences = null;
    private SharedPreferences.Editor mEditor = null;

    public SettingsManager(Context context) {
        mContext = context;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mEditor = mSharedPreferences.edit();
    }

    public void setNotificationTimeStamp(long timeInMillis) {
        mEditor.putLong(sNotificationTimeStamp, timeInMillis);
        mEditor.apply();
    }

    public long getNotificationTimeStamp() {
        return mSharedPreferences.getLong(sNotificationTimeStamp, 0);
    }
}
