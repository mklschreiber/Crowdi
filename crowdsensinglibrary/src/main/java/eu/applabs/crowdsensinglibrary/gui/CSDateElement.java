package eu.applabs.crowdsensinglibrary.gui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Calendar;

import eu.applabs.crowdsensinglibrary.R;

public class CSDateElement extends Button implements View.OnClickListener, CSDatePickerDialog.OnDateSetListener {

    private Context mContext = null;
    private Calendar mCalendar = null;

    private int mYear = 0;
    private int mMonth = 0;
    private int mDay = 0;

    public CSDateElement(Context context) {
        super(context);

        mContext = context;
        mCalendar = Calendar.getInstance();

        mYear = mCalendar.get(Calendar.YEAR);
        mMonth = mCalendar.get(Calendar.MONTH) + 1;
        mDay = mCalendar.get(Calendar.DAY_OF_MONTH);

        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        setOnClickListener(this);
    }

    public void displayCurrentDate() {
        setText(String.valueOf(mDay) + "." + String.valueOf(mMonth) + "." + String.valueOf(mYear));
    }

    @Override
    public void onDateSet(int year, int month, int day) {
        mYear = year;
        mMonth = month;
        mDay = day;

        setText(String.valueOf(mDay) + "." + String.valueOf(mMonth) + "." + String.valueOf(mYear));
    }

    @Override
    public void onClick(View v) {
        CSDatePickerDialog dialog = new CSDatePickerDialog(mContext, this);
        dialog.show();
    }
}

