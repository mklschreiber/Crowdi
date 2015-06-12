package eu.applabs.crowdsensingtv.base;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import eu.applabs.crowdsensingtv.R;

public class CSDatePickerDialog extends Dialog implements View.OnClickListener {

    public interface OnDateSetListener {
        void onDateSet(int year, int month, int day);
    }

    private CSDatePickerDialog.OnDateSetListener mOnDateSetListener = null;
    private Button mButtonDay = null;
    private Button mButtonMonth = null;
    private Button mButtonYear = null;

    public CSDatePickerDialog(Context context, CSDatePickerDialog.OnDateSetListener listener) {
        super(context);
        setContentView(R.layout.dialog_csdatepicker);

        mOnDateSetListener = listener;

        mButtonDay = (Button) findViewById(R.id.id_CSDatePickerDialog_Button_Day);
        mButtonMonth = (Button) findViewById(R.id.id_CSDatePickerDialog_Button_Month);
        mButtonYear = (Button) findViewById(R.id.id_CSDatePickerDialog_Button_Year);

        findViewById(R.id.id_CSDatePickerDialog_Button_Ok).setOnClickListener(this);
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
            case R.id.id_CSDatePickerDialog_Button_Ok:
                int year = Integer.valueOf(mButtonYear.getText().toString());
                int month = Integer.valueOf(mButtonMonth.getText().toString());
                int day = Integer.valueOf(mButtonDay.getText().toString());

                if(mOnDateSetListener != null) {
                    mOnDateSetListener.onDateSet(year, month, day);
                }
                break;
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
}
