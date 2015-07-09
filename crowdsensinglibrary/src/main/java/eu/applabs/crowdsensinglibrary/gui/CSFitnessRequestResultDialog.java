package eu.applabs.crowdsensinglibrary.gui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import eu.applabs.crowdsensinglibrary.R;

public class CSFitnessRequestResultDialog extends Dialog implements View.OnClickListener {

    public interface ICSFitnessRequestResultDialogListener {
        void onValueSelected(String value);
    }

    private Context mContext = null;
    private CSFitnessRequestResultDialog mCSFitnessRequestResultDialog = null;
    private String mLabelVerticalAxis = null;
    private String mLabelHorizontalAxis = null;
    private LinearLayout mLinearLayout = null;
    private boolean mButtonsGenerated = false;

    private List<ICSFitnessRequestResultDialogListener> mICSFitnessRequestResultDialogListenerList = null;

    public CSFitnessRequestResultDialog(Context context,
                                        String labelVerticalAxis,
                                        String labelHorizontalAxis) {
        super(context);

        mContext = context;
        mCSFitnessRequestResultDialog = this;
        mLabelVerticalAxis = labelVerticalAxis;
        mLabelHorizontalAxis = labelHorizontalAxis;
        mICSFitnessRequestResultDialogListenerList = new ArrayList<>();
    }

    public void registerListener(ICSFitnessRequestResultDialogListener listener) {
        mICSFitnessRequestResultDialogListenerList.add(listener);
    }

    public void unregisterListener(ICSFitnessRequestResultDialogListener listener) {
        mICSFitnessRequestResultDialogListenerList.remove(listener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_csfitnessrequestresult);

        VerticalTextView vtv = (VerticalTextView) findViewById(R.id.id_CSFitnessRequestResultDialog_VerticalAxis);
        vtv.setText(mLabelVerticalAxis);

        TextView tv = (TextView) findViewById(R.id.id_CSFitnessRequestResultDialog_HorizontalAxis);
        tv.setText(mLabelHorizontalAxis);

        mLinearLayout = (LinearLayout) findViewById(R.id.id_CSFitnessRequestResultDialog_LinearLayout);
        mLinearLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if(!mButtonsGenerated) {
                            mButtonsGenerated = true;

                            for (int i = 1; i < 10; ++i) {
                                Button button = new Button(mContext);
                                button.setText("Button" + String.valueOf(i));
                                button.setHeight(mLinearLayout.getHeight() / i);
                                button.setOnClickListener(mCSFitnessRequestResultDialog);

                                mLinearLayout.addView(button);
                            }
                        }
                    }
                }
        );
    }

    @Override
    public void onClick(View v) {
        Button button = (Button) v;

        if(button != null) {
            for (ICSFitnessRequestResultDialogListener listener : mICSFitnessRequestResultDialogListenerList) {
                listener.onValueSelected(button.getText().toString());
            }
        }

        dismiss();
    }
}
