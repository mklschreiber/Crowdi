package eu.applabs.crowdsensingtv.gui;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import eu.applabs.crowdsensingfitnesslibrary.FitnessLibrary;
import eu.applabs.crowdsensingfitnesslibrary.portal.Portal;
import eu.applabs.crowdsensinglibrary.data.Field;
import eu.applabs.crowdsensinglibrary.data.Option;
import eu.applabs.crowdsensinglibrary.gui.CSDateElement;
import eu.applabs.crowdsensinglibrary.gui.CSTimeElement;
import eu.applabs.crowdsensingtv.R;
import eu.applabs.crowdsensingtv.base.CSEditTextGroup;
import eu.applabs.crowdsensingupnplibrary.service.HeartRateDataServiceReceiverConnection;
import eu.applabs.crowdsensingupnplibrary.service.HeartRateServiceSenderConnection;

public class SinglePollFragment extends Fragment {

    private static final String sClassName = SinglePollFragment.class.getSimpleName();

    private LinearLayout mLinearLayout = null;
    private List<View> mViewList = null;
    private Field mField = null;
    private HeartRateServiceSenderConnection mHeartRateServiceSenderConnection = null;
    private HeartRateDataServiceReceiverConnection mHeartRateDataServiceReceiverConnection = null;

    private FitnessLibrary mFitnessLibrary = null;
    List<Portal.PortalType> mConnectedPortalsList;

    public SinglePollFragment() {
        mViewList = new ArrayList<>();
    }

    public void setField(Field field) {
        mField = field;
    }

    public void setHeartRateServiceSenderConnection(HeartRateServiceSenderConnection heartRateServiceSenderConnection) {
        mHeartRateServiceSenderConnection = heartRateServiceSenderConnection;
    }

    public void setHeartRateDataServiceReceiverConnection(HeartRateDataServiceReceiverConnection heartRateDataServiceReceiverConnection) {
        mHeartRateDataServiceReceiverConnection = heartRateDataServiceReceiverConnection;
    }

    public boolean allRequiredFieldsFilled() {
        return true;

        /*Field f = getMissingField();

        if(f == null) {
            return true;
        }

        return false;*/
    }

    public boolean allInputsAreValid() {
        return true;

        /*Field f = getInvalidField();

        if(f == null) {
            return true;
        }

        return false;*/
    }

    public Field getMissingField() {
        if(mField.getRequired() &&
                mField.getValue().compareTo("") == 0 &&
                mField.getCompositeField().compareTo("") == 0) {
            return mField;
        } else {
            for(Field f : mField.getFieldList()) {
                if(f.getRequired() &&
                        f.getValue().compareTo("") == 0 &&
                        f.getCompositeField().compareTo("") == 0) {
                    return f;
                }
            }
        }
        return null;
    }

    public Field getInvalidField() {
        if(mField.getPattern().compareTo("") != 0) {
            if(!Pattern.matches(mField.getPattern(), mField.getValue())) {
                return mField;
            } else {
                for(Field f : mField.getFieldList()) {
                    if(!Pattern.matches(f.getPattern(), f.getValue())) {
                        return f;
                    }
                }
            }
        }

        return null;
    }

    public View getFocusedView() {
        for(View v : mViewList) {
            if(v.isFocused()) {
                return v;
            }
        }

        return null;
    }

    public void updateFieldValues() {
        if(mField != null && mViewList != null && mViewList.size() > 0) {
            for(View v : mViewList) {
                if (v instanceof CSEditTextGroup) {
                    CSEditTextGroup csEditTextGroup = (CSEditTextGroup) v;
                    Field field = mField.getField(csEditTextGroup.getEditText().getId());
                    field.setValue(csEditTextGroup.getEditText().getText().toString());
                } else if (v instanceof EditText) {
                    EditText et = (EditText) v;
                    Field field = mField.getField(et.getId());
                    field.setValue(et.getText().toString());
                } else if (v instanceof CSTimeElement) {
                    CSTimeElement te = (CSTimeElement) v;
                    Field field = mField.getField(te.getId());
                    field.setValue(te.getText().toString());
                } else if (v instanceof CSDateElement) {
                    CSDateElement de = (CSDateElement) v;
                    Field field = mField.getField(de.getId());
                    field.setValue(de.getText().toString());
                } else if (v instanceof CheckBox) {
                    CheckBox cb = (CheckBox) v;
                    Field field = mField.getField(cb.getId());
                    field.setSelected(cb.isChecked());
                } else if (v instanceof RadioGroup) {
                    RadioGroup rg = (RadioGroup) v;
                    Field field = mField.getField(rg.getId());
                    for(int i = 0; i < rg.getChildCount(); ++i) {
                        View view = rg.getChildAt(i);

                        if(view instanceof RadioButton) {
                            RadioButton rb = (RadioButton) view;

                            if(rb.isChecked()) {
                                field.getOption(rb.getId()).setSelected(true);
                            }
                        }
                    }
                } else if (v instanceof MultiSelectView) {
                    MultiSelectView msv = (MultiSelectView) v;
                    Field field = mField.getField(msv.getId());
                    for(Option option : field.getOptionList()) {
                        CheckBox cb = (CheckBox) msv.findViewById(option.getId());
                        option.setSelected(cb.isChecked());
                    }
                }
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_singlepoll, container, false);
        mLinearLayout = (LinearLayout) v.findViewById(R.id.id_SinglePollFragment_LinearLayout);
        mFitnessLibrary = FitnessLibrary.getInstance();
        mConnectedPortalsList = mFitnessLibrary.getConnectedPortals();

        if(mField != null) {
            View fv = createViewForField(mField, true);
            mLinearLayout.addView(fv);
            mLinearLayout.setNextFocusUpId(R.id.id_SinglePollActivity_Button_Right);
        }

        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        updateFieldValues();
    }

    private View createViewForField(Field field, boolean firstField) {

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        MultiSelectView multiSelectView;

        switch(field.getType()) {
            case text:
                csEditTextGroup = new CSEditTextGroup(getActivity(),
                        field,
                        mHeartRateServiceSenderConnection,
                        mHeartRateDataServiceReceiverConnection);
                mViewList.add(csEditTextGroup.getEditText()); // Add the child

                content.addView(csEditTextGroup);
                break;
            case textarea:
                csEditTextGroup = new CSEditTextGroup(getActivity(),
                        field,
                        mHeartRateServiceSenderConnection,
                        mHeartRateDataServiceReceiverConnection);
                mViewList.add(csEditTextGroup.getEditText()); // Add the child
                csEditTextGroup.getEditText().setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);

                content.addView(csEditTextGroup);
                break;
            case password:
                editText = new EditText(getActivity());
                editText.setHint(field.getLabel());
                editText.setText(field.getValue());
                editText.setId(field.getId());
                editText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);

                mViewList.add(editText);
                content.addView(editText);
                break;
            case number:
                csEditTextGroup = new CSEditTextGroup(getActivity(),
                        field,
                        mHeartRateServiceSenderConnection,
                        mHeartRateDataServiceReceiverConnection);
                mViewList.add(csEditTextGroup.getEditText()); // Add the child
                csEditTextGroup.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);

                content.addView(csEditTextGroup);
                break;
            case email:
                editText = new EditText(getActivity());
                editText.setHint(field.getLabel());
                editText.setId(field.getId());
                editText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

                mViewList.add(editText);
                content.addView(editText);
                break;
            case tel:
                editText = new EditText(getActivity());
                editText.setHint(field.getLabel());
                editText.setText(field.getValue());
                editText.setId(field.getId());
                editText.setInputType(InputType.TYPE_CLASS_PHONE);

                mViewList.add(editText);
                content.addView(editText);
                break;
            case url:
                editText = new EditText(getActivity());
                editText.setHint(field.getLabel());
                editText.setText(field.getValue());
                editText.setId(field.getId());
                editText.setInputType(InputType.TYPE_TEXT_VARIATION_URI);

                mViewList.add(editText);
                content.addView(editText);
                break;
            case date:
                csDateElement = new CSDateElement(getActivity());
                csDateElement.setHint(field.getLabel());
                csDateElement.setText(field.getValue());
                csDateElement.setId(field.getId());
                csDateElement.displayCurrentDate();

                mViewList.add(csDateElement);
                content.addView(csDateElement);
                break;
            case time:
                csTimeElement = new CSTimeElement(getActivity());
                csTimeElement.setHint(field.getLabel());
                csTimeElement.setText(field.getValue());
                csTimeElement.setId(field.getId());
                csTimeElement.displayCurrentTime();

                mViewList.add(csTimeElement);
                content.addView(csTimeElement);
                break;
            case datetime:
                editText = new EditText(getActivity()); // TODO Parse it correct
                editText.setHint(field.getLabel());
                editText.setText(field.getValue());
                editText.setId(field.getId());

                mViewList.add(editText);
                content.addView(editText);
                break;
            case range:
                break;
            case select:
                radioGroup = new RadioGroup(getActivity());
                radioGroup.setId(field.getId());
                radioGroup.setOrientation(RadioGroup.VERTICAL);
                for(Option o : field.getOptionList()) {
                    RadioButton rb = new RadioButton(getActivity());
                    rb.setHint(o.getLabel());
                    rb.setText(o.getLabel());
                    rb.setId(o.getId());
                    rb.setChecked(o.getSelected());
                    rb.setSelected(o.getSelected());

                    radioGroup.addView(rb);
                }

                mViewList.add(radioGroup);
                content.addView(radioGroup);
                break;
            case multiselect:
                multiSelectView = new MultiSelectView(getActivity());
                multiSelectView.setOrientation(LinearLayout.VERTICAL);
                multiSelectView.setId(field.getId());

                for(Option o : field.getOptionList()) {
                    checkBox = new CheckBox(getActivity());
                    checkBox.setHint(o.getLabel());
                    checkBox.setText(o.getLabel());
                    checkBox.setChecked(o.getSelected());
                    checkBox.setSelected(o.getSelected());
                    checkBox.setId(o.getId());

                    multiSelectView.addView(checkBox);
                }

                mViewList.add(multiSelectView);
                content.addView(multiSelectView);
                break;
            case checkbox:
                checkBox = new CheckBox(getActivity());
                checkBox.setHint(field.getLabel());
                checkBox.setId(field.getId());
                checkBox.setChecked(field.getSelected());
                checkBox.setSelected(field.getSelected());

                mViewList.add(checkBox);
                content.addView(checkBox);
                break;
            case radio:
                radioGroup = new RadioGroup(getActivity());
                radioGroup.setOrientation(RadioGroup.VERTICAL);
                radioGroup.setId(field.getId());

                for(Option o : field.getOptionList()) {
                    RadioButton rb = new RadioButton(getActivity());
                    rb.setHint(o.getLabel());
                    rb.setText(o.getLabel());
                    rb.setId(o.getId());
                    rb.setChecked(o.getSelected());
                    rb.setSelected(o.getSelected());

                    radioGroup.addView(rb);
                }

                mViewList.add(radioGroup);
                content.addView(radioGroup);
                break;
        }

        if(field.getCompositeField().compareTo("") != 0) {
            linearLayout = new LinearLayout(getActivity());
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
}
