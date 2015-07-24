package eu.applabs.crowdsensingapp.gui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import eu.applabs.crowdsensingapp.R;
import eu.applabs.crowdsensingapp.base.CSEditTextGroup;
import eu.applabs.crowdsensinglibrary.ILibraryResultListener;
import eu.applabs.crowdsensinglibrary.Library;
import eu.applabs.crowdsensinglibrary.data.Command;
import eu.applabs.crowdsensinglibrary.data.Field;
import eu.applabs.crowdsensinglibrary.data.Option;
import eu.applabs.crowdsensinglibrary.data.Poll;
import eu.applabs.crowdsensinglibrary.gui.CSDateElement;
import eu.applabs.crowdsensinglibrary.gui.CSTimeElement;
import eu.applabs.crowdsensinglibrary.tool.StopWatch;
import eu.applabs.crowdsensingupnplibrary.service.HeartRateDataServiceReceiverConnection;
import eu.applabs.crowdsensingupnplibrary.service.HeartRateServiceSenderConnection;

public class SinglePollActivity extends AppCompatActivity implements
        ILibraryResultListener,
        StopWatch.IStopWatchListener {

    private static final String sClassName = SinglePollActivity.class.getSimpleName();

    public static final String EXTRA_URL = "ExtraUrl";

    private SinglePollActivity mActivity = null;

    private LinearLayout mLinearLayout = null;

    private Library mLibrary = null;
    private Poll mPoll = null;
    private List<View> mViewList = null;

    private StopWatch mStopWatch = null;

    private HeartRateServiceSenderConnection mHeartRateServiceSenderConnection;
    private HeartRateDataServiceReceiverConnection mHeartRateDataServiceReceiverConnection;

    private String mUrl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singlepoll);

        mActivity = this;

        mViewList = new ArrayList<>();

        mStopWatch = new StopWatch(this);
        mStopWatch.registerListener(this);
        mStopWatch.start();

        mUrl = checkStartingIntent();

        mLibrary = Library.getInstance();
        mLibrary.registerListener(this);

        if(mUrl != null) {
            mLibrary.loadPoll(mUrl, sClassName);
        } else {
            Toast.makeText(this, R.string.SinglePollActivity_Toast_Error, Toast.LENGTH_SHORT).show();
        }

        mLinearLayout = (LinearLayout) findViewById(R.id.id_SinglePollActivity_LinearLayout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_singlepoll, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.id_SinglePollMenu_Action_Send:
                mStopWatch.stop();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLibraryResult(final ExecutionStatus status, final Poll poll, final String className) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(className.compareTo(sClassName) == 0) {
                    if(status == ExecutionStatus.Success) {
                        mPoll = poll;
                        generateUI();
                    }
                }
            }
        });
    }

    @Override
    public void onLibraryResult(final ExecutionStatus status, final List<Command> list, final String className) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(className.compareTo(sClassName) == 0) {
                    if(status == ExecutionStatus.Success) {
                        FinishedPollDialog dialog = new FinishedPollDialog(mActivity, mActivity, list);
                        dialog.show();
                    }
                }
            }
        });
    }

    public String checkStartingIntent() {
        Intent intent = getIntent();

        if(intent.getExtras().containsKey(EXTRA_URL)) {
            return intent.getExtras().getString(EXTRA_URL);
        }

        return null;
    }

    public void generateUI() {
        if(mLinearLayout != null && mPoll != null) {
            for(Field field : mPoll.getFieldList()) {
                View fv = createViewForField(field, false);
                mLinearLayout.addView(fv);
            }
        }
    }

    private View createViewForField(Field field, boolean firstField) {

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View questionView = inflater.inflate(R.layout.view_question, null, false);

        TextView title = (TextView) questionView.findViewById(R.id.id_QuestionView_Title);
        title.setText(field.getLabel());

        RelativeLayout content = (RelativeLayout) questionView.findViewById(R.id.id_QuestionView_Content);

        EditText editText;
        LinearLayout linearLayout;
        CSEditTextGroup csEditTextGroup;
        CSDateElement csDateElement;
        CSTimeElement csTimeElement;
        RadioGroup radioGroup;
        CheckBox checkBox;

        switch(field.getType()) {
            case text:
                csEditTextGroup = new CSEditTextGroup(this,
                        field,
                        mHeartRateServiceSenderConnection,
                        mHeartRateDataServiceReceiverConnection);
                mViewList.add(csEditTextGroup.getEditText()); // Add the child

                content.addView(csEditTextGroup);
                break;
            case textarea:
                csEditTextGroup = new CSEditTextGroup(this,
                        field,
                        mHeartRateServiceSenderConnection,
                        mHeartRateDataServiceReceiverConnection);
                mViewList.add(csEditTextGroup.getEditText()); // Add the child
                csEditTextGroup.getEditText().setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);

                content.addView(csEditTextGroup);
                break;
            case password:
                editText = new EditText(this);
                editText.setHint(field.getLabel());
                editText.setText(field.getValue());
                editText.setId(field.getId());
                editText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);

                content.addView(editText);
                break;
            case number:
                csEditTextGroup = new CSEditTextGroup(this,
                        field,
                        mHeartRateServiceSenderConnection,
                        mHeartRateDataServiceReceiverConnection);
                mViewList.add(csEditTextGroup.getEditText()); // Add the child
                csEditTextGroup.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);

                content.addView(csEditTextGroup);
                break;
            case email:
                editText = new EditText(this);
                editText.setHint(field.getLabel());
                editText.setId(field.getId());
                editText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

                content.addView(editText);
                break;
            case tel:
                editText = new EditText(this);
                editText.setHint(field.getLabel());
                editText.setText(field.getValue());
                editText.setId(field.getId());
                editText.setInputType(InputType.TYPE_CLASS_PHONE);

                content.addView(editText);
                break;
            case url:
                editText = new EditText(this);
                editText.setHint(field.getLabel());
                editText.setText(field.getValue());
                editText.setId(field.getId());
                editText.setInputType(InputType.TYPE_TEXT_VARIATION_URI);

                content.addView(editText);
                break;
            case date:
                csDateElement = new CSDateElement(this);
                csDateElement.setHint(field.getLabel());
                csDateElement.setText(field.getValue());
                csDateElement.setId(field.getId());
                csDateElement.displayCurrentDate();

                content.addView(csDateElement);
                break;
            case time:
                csTimeElement = new CSTimeElement(this);
                csTimeElement.setHint(field.getLabel());
                csTimeElement.setText(field.getValue());
                csTimeElement.setId(field.getId());
                csTimeElement.displayCurrentTime();

                content.addView(csTimeElement);
                break;
            case datetime:
                editText = new EditText(this); // TODO Parse it correct
                editText.setHint(field.getLabel());
                editText.setText(field.getValue());
                editText.setId(field.getId());

                content.addView(editText);
                break;
            case range:
                break;
            case select:
                radioGroup = new RadioGroup(this);
                radioGroup.setOrientation(RadioGroup.VERTICAL);
                for(Option o : field.getOptionList()) {
                    RadioButton rb = new RadioButton(this);
                    rb.setHint(o.getLabel());
                    rb.setText(o.getLabel());
                    rb.setId(o.getId());
                    radioGroup.addView(rb);
                }

                content.addView(radioGroup);
                break;
            case multiselect:
                linearLayout = new LinearLayout(this);
                linearLayout.setOrientation(LinearLayout.VERTICAL);

                for(Option o : field.getOptionList()) {
                    checkBox = new CheckBox(this);
                    checkBox.setHint(o.getLabel());
                    checkBox.setText(o.getLabel());
                    checkBox.setId(o.getId());
                    linearLayout.addView(checkBox);
                }

                content.addView(linearLayout);
                break;
            case checkbox:
                checkBox = new CheckBox(this);
                checkBox.setHint(field.getLabel());
                checkBox.setId(field.getId());

                content.addView(checkBox);
                break;
            case radio:
                radioGroup = new RadioGroup(this);
                radioGroup.setOrientation(RadioGroup.VERTICAL);
                for(Option o : field.getOptionList()) {
                    RadioButton rb = new RadioButton(this);
                    rb.setHint(o.getLabel());
                    rb.setText(o.getLabel());
                    rb.setId(o.getId());
                    radioGroup.addView(rb);
                }

                content.addView(radioGroup);
                break;
        }

        if(field.getCompositeField().compareTo("") != 0) {
            linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.VERTICAL);

            for(int i = 0; i < field.getFieldList().size(); ++i) {
                Field f = field.getFieldList().get(i);
                View v;

                if(i == 0) {
                    v = createViewForField(f, true);
                } else {
                    v = createViewForField(f, false);
                }

                if(v != null) {
                    mViewList.add(v);
                    linearLayout.addView(v);
                }
            }

            content.addView(linearLayout);
        }

        if(questionView != null) {
            if(firstField) {
                questionView.requestFocus();
            }

            mViewList.add(questionView);
        }

        return questionView;
    }

    // IStopWatchListener

    @Override
    public void onDismiss() {
        mLibrary.uploadPoll(mUrl, mPoll.toJSON().toString(), sClassName);
    }
}
