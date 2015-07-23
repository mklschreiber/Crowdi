package eu.applabs.crowdsensinglibrary.gui;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import java.util.Calendar;

import eu.applabs.crowdsensinglibrary.R;

public class CSDatePickerDialog extends Dialog implements View.OnClickListener {

    public interface OnDateSetListener {
        void onDateSet(int year, int month, int day);
    }

    private CSDatePickerDialog.OnDateSetListener mOnDateSetListener = null;
    private Button mButtonDay = null;
    private Button mButtonMonth = null;
    private Button mButtonYear = null;
    private Calendar mCalendar = null;

    private int mYear = 0;
    private int mMonth = 0;
    private int mDay = 0;

    public CSDatePickerDialog(Context context, CSDatePickerDialog.OnDateSetListener listener) {
        super(context);
        setContentView(R.layout.dialog_csdatepicker);
        setTitle(R.string.CSDatePickerDialog_Title);

        mOnDateSetListener = listener;

        mButtonDay = (Button) findViewById(R.id.id_CSDatePickerDialog_Button_Day);
        mButtonMonth = (Button) findViewById(R.id.id_CSDatePickerDialog_Button_Month);
        mButtonYear = (Button) findViewById(R.id.id_CSDatePickerDialog_Button_Year);

        mCalendar = Calendar.getInstance();
        mYear = mCalendar.get(Calendar.YEAR);
        mMonth = mCalendar.get(Calendar.MONTH) + 1;
        mDay = mCalendar.get(Calendar.DAY_OF_MONTH);

        mButtonYear.setText(String.valueOf(mYear));
        mButtonMonth.setText(String.valueOf(mMonth));
        mButtonDay.setText(String.valueOf(mDay));

        findViewById(R.id.id_CSDatePickerDialog_Button_Ok).setOnClickListener(this);
        findViewById(R.id.id_CSDatePickerDialog_Button_YearIncrement).setOnClickListener(this);
        findViewById(R.id.id_CSDatePickerDialog_Button_YearDecrement).setOnClickListener(this);
        findViewById(R.id.id_CSDatePickerDialog_Button_MonthIncrement).setOnClickListener(this);
        findViewById(R.id.id_CSDatePickerDialog_Button_MonthDecrement).setOnClickListener(this);
        findViewById(R.id.id_CSDatePickerDialog_Button_DayIncrement).setOnClickListener(this);
        findViewById(R.id.id_CSDatePickerDialog_Button_DayDecrement).setOnClickListener(this);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_DPAD_UP:
                if(getFocusedButton() == mButtonYear) {
                    incrementYear();
                } else if (getFocusedButton() == mButtonMonth) {
                    incrementMonth();
                } else {
                    incrementDay();
                }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if(getFocusedButton() == mButtonYear) {
                    decrementYear();
                } else if (getFocusedButton() == mButtonMonth) {
                    decrementMonth();
                } else {
                    decrementDay();
                }
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.id_CSDatePickerDialog_Button_Ok) {
            if(mOnDateSetListener != null) {
                mOnDateSetListener.onDateSet(mYear, mMonth, mDay);
            }

            dismiss();
        } else if(v.getId() == R.id.id_CSDatePickerDialog_Button_YearIncrement) {
            incrementYear();
        } else if(v.getId() == R.id.id_CSDatePickerDialog_Button_YearDecrement) {
            decrementYear();
        } else if(v.getId() == R.id.id_CSDatePickerDialog_Button_MonthIncrement) {
            incrementMonth();
        } else if(v.getId() == R.id.id_CSDatePickerDialog_Button_MonthDecrement) {
            decrementMonth();
        } else if(v.getId() == R.id.id_CSDatePickerDialog_Button_DayIncrement) {
            incrementDay();
        } else if(v.getId() == R.id.id_CSDatePickerDialog_Button_DayDecrement) {
            decrementDay();
        }
    }

    private Button getFocusedButton() {
        if(mButtonDay.isFocused()) {
            return mButtonDay;
        } else if(mButtonMonth.isFocused()) {
            return mButtonMonth;
        } else {
            return mButtonYear;
        }
    }

    private void incrementYear() {
        mYear = Integer.valueOf(mButtonYear.getText().toString());
        mYear++;

        mButtonYear.setText(String.valueOf(mYear));
        checkDay();
    }

    private void decrementYear() {
        mYear = Integer.valueOf(mButtonYear.getText().toString());

        if(mYear > 0) {
            mYear--;
        }

        mButtonYear.setText(String.valueOf(mYear));
        checkDay();
    }

    private void incrementMonth() {
        mMonth = Integer.valueOf(mButtonMonth.getText().toString());

        if(mMonth < 12) {
            mMonth++;
        } else {
            mMonth = 1;
            incrementYear();
        }

        mButtonMonth.setText(String.valueOf(mMonth));
        checkDay();
    }

    private void decrementMonth() {
        mMonth = Integer.valueOf(mButtonMonth.getText().toString());

        if(mMonth > 1) {
            mMonth--;
        } else {
            decrementYear();

            mMonth = 12;
        }

        mButtonMonth.setText(String.valueOf(mMonth));
        checkDay();
    }

    private void incrementDay() {
        mDay = Integer.valueOf(mButtonDay.getText().toString());

        if(mDay < getLastDayOfMonth()) {
            mDay++;
        } else {
            incrementMonth();

            mDay = 1;
        }

        mButtonDay.setText(String.valueOf(mDay));
    }

    private void decrementDay() {
        mDay = Integer.valueOf(mButtonDay.getText().toString());

        if(mDay > 1) {
            mDay--;
        } else {
            decrementMonth();

            mDay = getLastDayOfMonth();
        }

        mButtonDay.setText(String.valueOf(mDay));
    }

    private int getLastDayOfMonth() {
        updateCalendar();

        return mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    private void updateCalendar() {
        mCalendar.set(Calendar.DAY_OF_MONTH, mDay);
        mCalendar.set(Calendar.MONTH, mMonth - 1);
        mCalendar.set(Calendar.YEAR, mYear);
    }

    private void checkDay() {
        int day = mDay;
        mDay = 1; // Set to a valid value

        if(day > getLastDayOfMonth()) {
            mDay = getLastDayOfMonth();
        } else {
            mDay = day;
        }

        mButtonDay.setText(String.valueOf(mDay));
    }
}
