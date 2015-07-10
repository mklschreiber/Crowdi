package eu.applabs.crowdsensingtv.data;

import android.content.Context;
import android.graphics.drawable.Drawable;

import eu.applabs.crowdsensingfitnesslibrary.portal.Portal;
import eu.applabs.crowdsensingtv.R;

public class FitnessAccount {

    private Context mContext = null;

    private Portal.PortalType mType = null;
    private Drawable mLogo = null;
    private String mName = null;

    public FitnessAccount(Context context) {
        mContext = context;

        mType = Portal.PortalType.Undefined;
        mName = "";
        mLogo = context.getDrawable(R.drawable.poll);
    }

    public void setType(Portal.PortalType type) {
        mType = type;

        switch(type) {
            case Google:
                mName = "Google Fit";
                mLogo = mContext.getDrawable(R.drawable.googlefit);
                break;
            case Apple:
                mName = "Apple Health Kit";
                mLogo = mContext.getDrawable(R.drawable.applehealthkit);
                break;
            case Microsoft:
                mName = "Microsoft Health Vault";
                mLogo = mContext.getDrawable(R.drawable.microsofthealthvault);
                break;
            case Undefined:
                break;
        }
    }

    public Portal.PortalType getType() {
        return mType;
    }

    public String getName() {
        return mName;
    }

    public Drawable getLogo() {
        return mLogo;
    }
}