package eu.applabs.crowdsensingtv.gui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.speech.SpeechRecognizer;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import java.util.ArrayList;
import java.util.List;

import eu.applabs.crowdsensinglibrary.ILibraryResultListener;
import eu.applabs.crowdsensinglibrary.Library;
import eu.applabs.crowdsensinglibrary.data.Field;
import eu.applabs.crowdsensinglibrary.data.Poll;
import eu.applabs.crowdsensingtv.R;
import eu.applabs.crowdsensingtv.base.CrowdSensingActivity;

public class CompletePollActivity extends CrowdSensingActivity implements ILibraryResultListener,
        View.OnClickListener {

    private static final String sClassName = CompletePollActivity.class.getSimpleName();

    private Poll mPoll = null;

    private SpeechRecognizer mSpeechRecognizer = null;

    private Library mLibrary = null;
    private LinearLayout mLinearLayout = null;
    private ProgressDialog mProgressDialog = null;
    private List<View> mViewList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizer.setRecognitionListener(this);

        mViewList = new ArrayList<>();
        mLinearLayout = (LinearLayout) findViewById(R.id.id_MainActivity_LL_Poll);

        Button sendButton = (Button) findViewById(R.id.id_MainActivity_Button_Send);
        sendButton.setOnClickListener(this);

        mLibrary = new Library();
        mLibrary.registerListener(this);

        mProgressDialog = ProgressDialog.show(this, "Loading", "Loading. Please wait...", true);

        //library.loadPoll(getPoll()); // Local String
        mLibrary.loadPoll("https://www.applabs.eu/json.txt"); // Web resource
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mLibrary.unregisterListener(this);
        mLibrary.deinit();
    }

    @Override
    public void onLibraryResult(final ExecutionStatus status, final Poll poll) {
        mPoll = poll;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (status == ExecutionStatus.Success) {
                    // Create the UI

                    for (Field field : poll.getFieldList()) {
                        View view = createViewForField(field);
                        if (view != null) {
                            mLinearLayout.addView(view);
                        }
                    }
                }

                mProgressDialog.dismiss();
            }
        });
    }

    private View createViewForField(Field field) {
        View view = null;

        EditText et = null;

        switch(field.getType()) {
            case text:
                et = new EditText(this);
                et.setHint(field.getTitle());
                view = et;
                break;
            case textarea:
                et = new EditText(this);
                et.setHint(field.getTitle());
                et.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                view = et;
                break;
            case password:
                et = new EditText(this);
                et.setHint(field.getTitle());
                et.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                view = et;
                break;
            case number:
                et = new EditText(this);
                et.setHint(field.getTitle());
                et.setInputType(InputType.TYPE_CLASS_NUMBER);
                view = et;
                break;
            case email:
                et = new EditText(this);
                et.setHint(field.getTitle());
                et.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                view = et;
                break;
            case tel:
                et = new EditText(this);
                et.setHint(field.getTitle());
                et.setInputType(InputType.TYPE_CLASS_PHONE);
                view = et;
                break;
            case url:
                et = new EditText(this);
                et.setHint(field.getTitle());
                et.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
                view = et;
                break;
            case date:
                et = new EditText(this);
                et.setHint(field.getTitle());
                et.setInputType(InputType.TYPE_CLASS_DATETIME);
                view = et;
                break;
            case time:
                et = new EditText(this);
                et.setHint(field.getTitle());
                et.setInputType(InputType.TYPE_CLASS_DATETIME);
                view = et;
                break;
            case range:
                break;
            case checkbox:
                CheckBox cb = new CheckBox(this);
                cb.setHint(field.getTitle());
                view = cb;
                break;
            case radio:
                RadioButton rb = new RadioButton(this);
                rb.setHint(field.getTitle());
                view = rb;
                break;
        }

        if(field.getCompositeType().compareTo("") != 0) {
            LinearLayout ll = new LinearLayout(this);
            ll.setOrientation(LinearLayout.VERTICAL);

            for(Field f : field.getFieldList()) {
                View v = createViewForField(f);
                mViewList.add(v);

                if(v != null) {
                    ll.addView(v);
                }
            }

            view = ll;
        }

        mViewList.add(view);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.id_MainActivity_Button_Send:
                if(mPoll != null) {
                    mLibrary.uploadPoll("https://www.applabs.eu/pollupload", mPoll.toJSON().toString());
                }
                break;
        }
    }

    private String getPoll() {
        String poll = "{\n" +
                "  \"version\": \"0.0.1\",\n" +
                "  \"fields\":\n" +
                "  [\n" +
                "    {\n" +
                "      \"name\":\"fancyInput\",\n" +
                "      \"title\":\"Fancy input\",\n" +
                "      \"type\":\"text\",\n" +
                "      \"pattern\":\"([a-z]+\\\\.?)+[a-z]+\",\n" +
                "      \"required\":true\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\":\"fancyInputAgain\",\n" +
                "      \"title\":\"One more fancy input\",\n" +
                "      \"type\":\"text\",\n" +
                "      \"required\":false\n" +
                "    },\n" +
                "    {\n" +
                "      \"title\":\"text field\",\n" +
                "      \"name\":\"fancyText\",\n" +
                "      \"type\":\"textarea\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\":\"fancyAddress\",\n" +
                "      \"compositeType\":\"address\",\n" +
                "      \"title\":\"First address\"\n" +
                "    },\n" +
                "    {\n" +
                "    \"name\":\"fancyAddressAgain\",\n" +
                "    \"compositeType\":\"address\"\n" +
                "  },\n" +
                "    {\n" +
                "      \"name\":\"fancyBanking\",\n" +
                "      \"compositeType\":\"banking\"\n" +
                "    }\n" +
                "  ],\n" +
                "\n" +
                "\n" +
                "  \"compositeTypes\":[\n" +
                "    {\n" +
                "      \"compositeType\": \"address\",\n" +
                "      \"fields\": [\n" +
                "        {\n" +
                "          \"name\": \"givenName\",\n" +
                "          \"title\": \"Given Name\",\n" +
                "          \"type\": \"text\",\n" +
                "          \"length\": 100,\n" +
                "          \"required\": true\n" +
                "        },\n" +
                "        {\n" +
                "          \"name\": \"surname\",\n" +
                "          \"title\": \"Surname\",\n" +
                "          \"type\": \"text\",\n" +
                "          \"length\": 100,\n" +
                "          \"required\": true\n" +
                "        },\n" +
                "        {\n" +
                "          \"name\": \"street\",\n" +
                "          \"title\": \"Street\",\n" +
                "          \"type\": \"text\",\n" +
                "          \"length\": 100,\n" +
                "          \"required\": true\n" +
                "        },\n" +
                "        {\n" +
                "          \"name\": \"streetNumber\",\n" +
                "          \"title\": \"Street Number\",\n" +
                "          \"type\": \"text\",\n" +
                "          \"pattern\": \"[0-9]+(\\\\\\\\[0-9]*[a-z]?)?\",\n" +
                "          \"required\": true\n" +
                "        },\n" +
                "        {\n" +
                "          \"name\": \"tel\",\n" +
                "          \"title\": \"TelNr\",\n" +
                "          \"type\": \"tel\"\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "\n" +
                "    {\n" +
                "      \"compositeType\": \"banking\",\n" +
                "      \"fields\": [\n" +
                "        {\n" +
                "          \"name\": \"ownerName\",\n" +
                "          \"title\": \"Owner name\",\n" +
                "          \"type\": \"text\",\n" +
                "          \"length\": 100,\n" +
                "          \"required\": true\n" +
                "        },\n" +
                "        {\n" +
                "          \"name\": \"iban\",\n" +
                "          \"title\": \"IBAN\",\n" +
                "          \"type\": \"text\",\n" +
                "          \"length\": 100,\n" +
                "          \"required\": true,\n" +
                "          \"pattern\": \"[a-zA-Z]{2}[0-9]{2}[a-zA-Z0-9]{4}[0-9]{7}([a-zA-Z0-9]?){0,16}\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"name\": \"bic\",\n" +
                "          \"title\": \"BIC/SWIFT\",\n" +
                "          \"type\": \"text\",\n" +
                "          \"length\": 100,\n" +
                "          \"required\": true,\n" +
                "          \"pattern\": \"([a-zA-Z]{4}[a-zA-Z]{2}[a-zA-Z0-9]{2}([a-zA-Z0-9]{3})?)\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"name\": \"bank\",\n" +
                "          \"title\": \"Bank Name\",\n" +
                "          \"type\": \"text\",\n" +
                "          \"pattern\": \"[0-9]+(\\\\\\\\[0-9]*[a-z]?)?\",\n" +
                "          \"required\": true\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        return poll;
    }

    @Override
    public View getFocusedView() {
        for(View v : mViewList) {
            if(v.isFocused()) {
                return v;
            }
        }

        return null;
    }
}

