package eu.applabs.crowdsensingtv.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import org.fourthline.cling.android.AndroidUpnpServiceImpl;

import java.util.ArrayList;
import java.util.List;

import eu.applabs.crowdsensingtv.R;
import eu.applabs.crowdsensingupnplibrary.service.HeartRateDataServiceReceiverConnection;

public class CSHeartRateDialog extends Dialog implements View.OnClickListener,
        HeartRateDataServiceReceiverConnection.IHeartRateDataServiceReceiverConnectionListener{

    public interface ICSHeartRateDialogListener {
        void onHeartRateSelected(String value);
    }

    CSHeartRateDialog mCSHeartRateDialog = null;
    Activity mActivity = null;
    LinearLayout mLinearLayout = null;
    List<ICSHeartRateDialogListener> mICSHeartRateDialogListenerList = null;
    HeartRateDataServiceReceiverConnection mHeartRateDataServiceReceiverConnection = null;

    public CSHeartRateDialog(Activity activity, HeartRateDataServiceReceiverConnection heartRateDataServiceReceiverConnection) {
        super(activity);
        setContentView(R.layout.dialog_csheartrate);

        mHeartRateDataServiceReceiverConnection = heartRateDataServiceReceiverConnection;
        mCSHeartRateDialog = this;
        setTitle(R.string.CSHeartRateDialog_Title);

        mActivity = activity;
        mLinearLayout = (LinearLayout) findViewById(R.id.id_CSHeartRateDialog_LinearLayout);
        mICSHeartRateDialogListenerList = new ArrayList<>();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mHeartRateDataServiceReceiverConnection != null) {
            mHeartRateDataServiceReceiverConnection.registerListener(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(mHeartRateDataServiceReceiverConnection != null) {
            mHeartRateDataServiceReceiverConnection.unregisterListener(this);
        }
    }

    public void registerListener(ICSHeartRateDialogListener listener) {
        mICSHeartRateDialogListenerList.add(listener);
    }

    public void unregisterListener(ICSHeartRateDialogListener listener) {
        mICSHeartRateDialogListenerList.remove(listener);
    }

    @Override
    public void onClick(View v) {
        if(v instanceof Button) {
            Button b = (Button) v;

            for(ICSHeartRateDialogListener listener : mICSHeartRateDialogListenerList) {
                listener.onHeartRateSelected(b.getText().toString());
            }

            dismiss();
        }
    }

    @Override
    public void onHeartRateReceived(final String heartRate) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Button b = new Button(mActivity);
                b.setText(heartRate);
                b.setOnClickListener(mCSHeartRateDialog);

                mLinearLayout.addView(b);
            }
        });
    }
}
