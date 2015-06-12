package eu.applabs.crowdsensingtv.base;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import eu.applabs.crowdsensingtv.R;

public class CSTimePickerDialog extends Dialog implements View.OnClickListener {

    public interface OnTimeSetListener {
        void onTimeSet(int hour, int minute);
    }

    private OnTimeSetListener mOnTimeSetListener = null;
    private Button mButtonHour = null;
    private Button mButtonMinute = null;

    public CSTimePickerDialog(Context context, OnTimeSetListener listener) {
        super(context);
        setContentView(R.layout.dialog_cstimepicker);

        mOnTimeSetListener = listener;

        mButtonHour = (Button) findViewById(R.id.id_CSTimePickerDialog_Button_Hour);
        mButtonMinute = (Button) findViewById(R.id.id_CSTimePickerDialog_Button_Minute);

        findViewById(R.id.id_CSTimePickerDialog_Button_Ok).setOnClickListener(this);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Button button;
        int value;

        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_DPAD_UP:
                button = getFocusedButton();
                value = Integer.valueOf(button.getText().toString());
                value++;
                button.setText(String.valueOf(value));
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                button = getFocusedButton();
                value = Integer.valueOf(button.getText().toString());
                value--;
                button.setText(String.valueOf(value));
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.id_CSTimePickerDialog_Button_Ok:
                int hour = Integer.valueOf(mButtonHour.getText().toString());
                int minute = Integer.valueOf(mButtonMinute.getText().toString());

                if(mOnTimeSetListener != null) {
                    mOnTimeSetListener.onTimeSet(hour, minute);
                }
                break;
        }
    }

    private Button getFocusedButton() {
        if(mButtonHour.isFocused()) {
            return mButtonHour;
        } else {
            return mButtonMinute;
        }
    }
}
