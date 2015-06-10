package eu.applabs.crowdsensingtv.gui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import eu.applabs.crowdsensingtv.R;

public class FinishedPollActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finishedpoll);

        Button b = (Button) findViewById(R.id.id_FinishedPollActivity_Button_StartNewPoll);
        b.setOnClickListener(this);
        b = (Button) findViewById(R.id.id_FinishedPollActivity_Button_ExitApplication);
        b.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.id_FinishedPollActivity_Button_StartNewPoll:
                Intent intent = new Intent(this, SinglePollActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.id_FinishedPollActivity_Button_ExitApplication:
                finish();
                break;
        }
    }
}
