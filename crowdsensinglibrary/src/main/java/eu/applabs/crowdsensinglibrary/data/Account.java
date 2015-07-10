package eu.applabs.crowdsensinglibrary.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;

import eu.applabs.crowdsensinglibrary.R;

public class Account {

    private static final String sUserName = "UserName";
    private static final String sPassword = "Password";

    private Context mContext = null;
    private SharedPreferences mSharedPreferences = null;
    private SharedPreferences.Editor mEditor = null;

    // Not saved in shared prefs
    private Drawable mLogo = null;

    public Account(Context context) {
        mContext = context;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mEditor = mSharedPreferences.edit();

        mLogo = context.getResources().getDrawable(R.drawable.unilogo);
    }

    public void setUserName(String userName) {
        mEditor.putString(sUserName, userName);
        mEditor.apply();
    }

    public void setPassword(String password) {
        mEditor.putString(sPassword, password);
        mEditor.apply();
    }

    public void setLogo(Drawable logo) {
        mLogo = logo;
    }

    public String getUserName() {
        return mSharedPreferences.getString(sUserName, "");
    }

    public String getPassword() {
        return mSharedPreferences.getString(sPassword, "");
    }

    public Drawable getLogo() {
        return mLogo;
    }
}
