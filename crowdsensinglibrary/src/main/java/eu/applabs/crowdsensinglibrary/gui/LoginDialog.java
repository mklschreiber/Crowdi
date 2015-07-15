package eu.applabs.crowdsensinglibrary.gui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import eu.applabs.crowdsensinglibrary.ILibraryResultListener;
import eu.applabs.crowdsensinglibrary.Library;
import eu.applabs.crowdsensinglibrary.R;
import eu.applabs.crowdsensinglibrary.data.Command;
import eu.applabs.crowdsensinglibrary.data.Poll;

public class LoginDialog extends Dialog implements View.OnClickListener {

    public interface ILoginDialogListener {
        void onLoginCanceled();
        void onLoginSaved();
    }

    private Context mContext = null;
    private List<ILoginDialogListener> mILoginDialogListenerList = null;
    private EditText mEditTextUserName = null;
    private EditText mEditTextPassword = null;
    private Library mLibrary = null;

    public LoginDialog(Context context) {
        super(context);
        mContext = context;
        mLibrary = Library.getInstance();

        mILoginDialogListenerList = new ArrayList<>();
    }

    public void registerListener(ILoginDialogListener listener) {
        mILoginDialogListenerList.add(listener);
    }

    public void unregisterListener(ILoginDialogListener listener) {
        mILoginDialogListenerList.remove(listener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_login);

        setTitle("Login");

        Button b = (Button) findViewById(R.id.id_LoginDialog_Button_Ok);
        b.setOnClickListener(this);
        b = (Button) findViewById(R.id.id_LoginDialog_Button_Cancel);
        b.setOnClickListener(this);

        mEditTextUserName = (EditText) findViewById(R.id.id_LoginDialog_EditText_UserName);
        mEditTextUserName.requestFocus();

        mEditTextPassword = (EditText) findViewById(R.id.id_LoginDialog_EditText_Password);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.id_LoginDialog_Button_Ok) {
            if(mEditTextUserName != null && mEditTextPassword != null) {
                String userName = mEditTextUserName.getText().toString();
                String password = mEditTextPassword.getText().toString();

                if(userName.compareTo("") == 0 || password.compareTo("") == 0) {
                    Toast.makeText(mContext,
                            mContext.getResources().getString(R.string.LoginDialog_Toast_Invalid),
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                mLibrary.login(userName, password);

                for (ILoginDialogListener listener : mILoginDialogListenerList) {
                    listener.onLoginSaved();
                }

                dismiss();
            }
        } else if(v.getId() == R.id.id_LoginDialog_Button_Cancel) {
            for(ILoginDialogListener listener : mILoginDialogListenerList) {
                listener.onLoginCanceled();
            }

            dismiss();
        }
    }
}
