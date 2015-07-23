package eu.applabs.crowdsensinglibrary.gui;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import java.util.Calendar;

public class CSTimeElement extends Button implements View.OnClickListener, CSTimePickerDialog.OnTimeSetListener {

    private Context mContext = null;
    private Calendar mCalendar = null;

    private int mHour = 0;
    private int mMinute = 0;

    public CSTimeElement(Context context) {
        super(context);

        mContext = context;
        mCalendar = Calendar.getInstance();

        mHour = mCalendar.get(Calendar.HOUR_OF_DAY);
        mMinute = mCalendar.get(Calendar.MINUTE);

        setOnClickListener(this);
    }

    public void displayCurrentTime() {
        setText(String.valueOf(mHour) + ":" + String.valueOf(mMinute));
    }

    @Override
    public void onTimeSet(int hour, int minute) {
        mHour = hour;
        mMinute = minute;

        setText(String.valueOf(mHour) + ":" + String.valueOf(mMinute));
    }

    @Override
    public void onClick(View v) {
        CSTimePickerDialog dialog = new CSTimePickerDialog(mContext, this);
        dialog.show();
    }
}
