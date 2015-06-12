package eu.applabs.crowdsensingtv.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.ArrayList;

public abstract class CSActivity extends Activity implements RecognitionListener {

    private static final String sClassName = CSActivity.class.getSimpleName();

    private SpeechRecognizer mSpeechRecognizer = null;
    private boolean mSpeechRecognizerIsActive = false;
    private InputMethodManager mKeyBoard = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizer.setRecognitionListener(this);
        mKeyBoard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    /*
     * Abstract methods
     */

    public abstract View getFocusedView();

    /*
     * Remote control actions
     */

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_SEARCH:
                if(!mSpeechRecognizerIsActive) {
                    // Reset input
                    try {
                        ((EditText) getFocusedView()).setText("");
                    } catch (Exception e) {
                        Log.e(sClassName, e.getMessage());
                    }

                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());

                    // Start the recognizer
                    mSpeechRecognizerIsActive = true;
                    mSpeechRecognizer.startListening(intent);

                    return true;
                }

                break;

            case KeyEvent.KEYCODE_DPAD_CENTER:
                try {
                    mKeyBoard.showSoftInput(getFocusedView(), 0);

                    return true;
                } catch (Exception e) {
                    Log.e(sClassName, e.getMessage());

                    return false;
                }
        }

        if(mSpeechRecognizerIsActive) {
            // Stop the recognizer
            mSpeechRecognizerIsActive = false;
            mSpeechRecognizer.stopListening();
            mSpeechRecognizer.cancel();

            return true;
        }

        return super.onKeyUp(keyCode, event);
    }

    /*
     * RecognitionListener methods
     */

    @Override
    public void onReadyForSpeech(Bundle params) {

    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onError(int error) {

    }

    @Override
    public void onResults(Bundle results) {
        processSpeechResult(results);
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        processSpeechResult(partialResults);
    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }

    /*
     * Own methods
     */

    public void processSpeechResult(Bundle result) {
        if(result != null) {
            String str = "";
            ArrayList data = result.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            for (int i = 0; i < data.size(); ++i) {
                str += data.get(i);
            }

            try {
                ((EditText) getFocusedView()).setText(str);
            } catch (Exception e) {
                Log.e(sClassName, e.getMessage());
            }
        }
    }
}
