package eu.applabs.crowdsensingtv.gui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.List;

import eu.applabs.crowdsensinglibrary.data.Command;
import eu.applabs.crowdsensingtv.R;

public class FinishedPollDialog extends Dialog implements View.OnClickListener {

    private Activity mActivity = null;
    private Context mContext = null;
    private List<Command> mCommandList = null;
    private LinearLayout mLinearLayout = null;

    public FinishedPollDialog(Context context, Activity activity, List<Command> list) {
        super(context);

        mActivity = activity;
        mContext = context;
        mCommandList = list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_finishedpoll);
        mLinearLayout = (LinearLayout) findViewById(R.id.id_FinishedPollDialog_LinearLayout);

        generateUI();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(mActivity != null) {
            mActivity.finish();
        }
    }

    @Override
    public void onClick(View v) {
        if(mCommandList != null) {
            for(Command command : mCommandList) {
                if(command.getId() == v.getId()) {
                    Intent intent = new Intent(mActivity, SinglePollActivity.class);
                    Bundle extras = new Bundle();
                    extras.putString(SinglePollActivity.EXTRA_URL, MainActivity.BASE_URL + command.getCommand());
                    intent.putExtras(extras);

                    mContext.startActivity(intent);

                    mActivity.finish();
                }
            }
        }
    }

    private void generateUI() {
        for(int i = 0; i < mCommandList.size(); ++i) {
            Command command = mCommandList.get(i);

            Button b = new Button(mContext);
            b.setText(command.getInfo());
            b.setId(command.getId());
            b.setOnClickListener(this);

            if(i == 0) {
                b.requestFocus();
            }

            mLinearLayout.addView(b);
        }
    }
}
