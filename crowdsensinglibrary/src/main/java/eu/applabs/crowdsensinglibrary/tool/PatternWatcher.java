package eu.applabs.crowdsensinglibrary.tool;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Pattern;

import eu.applabs.crowdsensinglibrary.data.Field;

public class PatternWatcher implements TextWatcher {

    private Context mContext = null;
    private EditText mEditText = null;
    private Field mField = null;
    private boolean mWatcherRegistered = false;
    private boolean mInputValid = false;

    public PatternWatcher(Context context) {
        mContext = context;
    }

    public void startWatching(EditText editText, Field field) {
        mEditText = editText;
        mField = field;

        if(mField != null && mField.getPattern().compareTo("") != 0) {
            mWatcherRegistered = true;
            mEditText.addTextChangedListener(this);
        } else {
            mInputValid = true; // No pattern specified input needs to be valid!
        }
    }

    public void stopWatching() {
        if(mWatcherRegistered) {
            mWatcherRegistered = false;
            mEditText.removeTextChangedListener(this);
        }
    }

    public boolean inputIsValid() {
        return mInputValid;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String str = mEditText.getText().toString();
        mInputValid = Pattern.matches(mField.getPattern(), str);

        if(str != null && str.compareTo("") != 0) {
            if (!mInputValid) {
                Toast.makeText(mContext, "Input is not valid against pattern: " + mField.getPattern(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
