package eu.applabs.crowdsensingapp.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SettingsManager {

    private static final String sUpnpServiceEnabled = "UpnpServiceEnabled";

    private Context mContext = null;
    private SharedPreferences mSharedPreferences = null;
    private SharedPreferences.Editor mEditor = null;

    public SettingsManager(Context context) {
        mContext = context;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mEditor = mSharedPreferences.edit();
    }

    public void setUpnpServiceEnabled(boolean enabled) {
        mEditor.putBoolean(sUpnpServiceEnabled, enabled);
        mEditor.apply();
    }

    public boolean getUpnpServiceEnabled() {
        return mSharedPreferences.getBoolean(sUpnpServiceEnabled, true);
    }
}
