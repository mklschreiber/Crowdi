package eu.applabs.crowdsensingfitnesslibrary.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import eu.applabs.crowdsensingfitnesslibrary.portal.Portal;

public class SettingsManager {
    private Context mContext = null;
    private SharedPreferences mSharedPreferences = null;
    private SharedPreferences.Editor mEditor = null;

    private static final String sConnectedServices = "ConnectedServices";

    public SettingsManager(Context context) {
        mContext = context;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mEditor = mSharedPreferences.edit();
    }

    public void setConnectedServices(List<Portal.PortalType> list) {
        Set<String> connectedServicesSet = new HashSet<>();
        for(Portal.PortalType type : list) {
            connectedServicesSet.add(String.valueOf(type.ordinal()));
        }

        mEditor.putStringSet(sConnectedServices, connectedServicesSet);
        mEditor.apply();
    }

    public List<Portal.PortalType> getConnectedServices() {
        Set<String> connectedServicesSet = mSharedPreferences.getStringSet(sConnectedServices, new HashSet<String>());

        List<Portal.PortalType> list = new ArrayList<>();
        Iterator<String> iterator = connectedServicesSet.iterator();

        while(iterator.hasNext()) {
            list.add(Portal.PortalType.values()[Integer.valueOf(iterator.next())]);
        }

        return list;
    }
}
