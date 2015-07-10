package eu.applabs.crowdsensingtv.data;

import android.content.Context;
import android.graphics.drawable.Drawable;

import eu.applabs.crowdsensingtv.R;

public class LibraryAccount {

    private String mName = null;
    private Drawable mLogo = null;

    public LibraryAccount(Context context) {
        mName = "Uni-Portal";
        mLogo = context.getDrawable(R.drawable.unilogo);
    }

    public String getName() {
        return mName;
    }

    public Drawable getLogo() {
        return mLogo;
    }
}
