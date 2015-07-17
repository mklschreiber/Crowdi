package eu.applabs.crowdsensingtv.base;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import java.util.Calendar;

import eu.applabs.crowdsensingtv.R;

public class CSTimePickerDialog extends Dialog implements View.OnClickListener {

    public interface OnTimeSetListener {
        void onTimeSet(int hour, int minute);
    }

    private OnTimeSetListener mOnTimeSetListener = null;
    private Button mButtonHour = null;
    private Button mButtonMinute = null;
    private Calendar mCalendar = null;

    private int mHour = 0;
    private int mMinute = 0;

    public CSTimePickerDialog(Context context, OnTimeSetListener listener) {
        super(context);
        setContentView(R.layout.dialog_cstimepicker);
        setTitle(R.string.CSTimePickerDialog_Title);

        mOnTimeSetListener = listener;

        mButtonHour = (Button) findViewById(R.id.id_CSTimePickerDialog_Button_Hour);
        mButtonMinute = (Button) findViewById(R.id.id_CSTimePickerDialog_Button_Minute);

        mCalendar = Calendar.getInstance();
        mHour = mCalendar.get(Calendar.HOUR_OF_DAY);
        mMinute = mCalendar.get(Calendar.MINUTE);

        mButtonHour.setText(String.valueOf(mHour));
        mButtonMinute.setText(String.valueOf(mMinute));

        findViewById(R.id.id_CSTimePickerDialog_Button_Ok).setOnClickListener(this);
        findViewById(R.id.id_CSTimePickerDialog_Button_HourIncrement).setOnClickListener(this);
        findViewById(R.id.id_CSTimePickerDialog_Button_HourDecrement).setOnClickListener(this);
        findViewById(R.id.id_CSTimePickerDialog_Button_MinuteIncrement).setOnClickListener(this);
        findViewById(R.id.id_CSTimePickerDialog_Button_MinuteDecrement).setOnClickListener(this);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_DPAD_UP:
                if (getFocusedButton() == mButtonHour) {
                    incrementHours();
                } else {
                    incrementMinutes();
                }

                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if(getFocusedButton() == mButtonHour) {
                    decrementHours();
                } else {
                    decrementMinutes();
                }

                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.id_CSTimePickerDialog_Button_Ok:
                if(mOnTimeSetListener != null) {
                    mOnTimeSetListener.onTimeSet(mHour, mMinute);
                }

                dismiss();
                break;
            case R.id.id_CSTimePickerDialog_Button_HourIncrement:
                incrementHours();
                break;
            case R.id.id_CSTimePickerDialog_Button_HourDecrement:
                decrementHours();
                break;
            case R.id.id_CSTimePickerDialog_Button_MinuteIncrement:
                incrementMinutes();
                break;
            case R.id.id_CSTimePickerDialog_Button_MinuteDecrement:
                decrementMinutes();
        }
    }

    private Button getFocusedButton() {
        if(mButtonHour.isFocused()) {
            return mButtonHour;
        } else {
            return mButtonMinute;
        }
    }

    private void incrementHours() {
        mHour = Integer.valueOf(mButtonHour.getText().toString());

        if(mHour < 23) {
            mHour++;
        } else {
            mHour = 0;
        }

        mButtonHour.setText(String.valueOf(mHour));
    }

    private void decrementHours() {
        mHour = Integer.valueOf(mButtonHour.getText().toString());

        if(mHour > 0) {
            mHour--;
        } else {
            mHour = 23;
        }

        mButtonHour.setText(String.valueOf(mHour));
    }

    private void incrementMinutes() {
        mMinute = Integer.valueOf(mButtonMinute.getText().toString());

        if(mMinute < 59) {
            mMinute++;
        } else {
            mMinute = 0;
            incrementHours();
        }

        mButtonMinute.setText(String.valueOf(mMinute));
    }

    private void decrementMinutes() {
        mMinute = Integer.valueOf(mButtonMinute.getText().toString());

        if(mMinute > 0) {
            mMinute--;
        } else {
            mMinute = 59;
            decrementHours();
        }

        mButtonMinute.setText(String.valueOf(mMinute));
    }
}
