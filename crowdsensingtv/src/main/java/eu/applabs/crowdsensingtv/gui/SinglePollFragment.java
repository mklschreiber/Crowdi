package eu.applabs.crowdsensingtv.gui;

import android.app.Fragment;
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
                try {
                    EditText et = (EditText) v;
                    Field field = mField.getField(et.getId());
                    field.setValue(et.getText().toString());
                } catch (Exception e) {
                    Log.e(sClassName, e.getMessage());
                }

                try {
                    CSTimeElement te = (CSTimeElement) v;
                    Field field = mField.getField(te.getId());
                    field.setValue(te.getText().toString());
                } catch (Exception e) {
                    Log.e(sClassName, e.getMessage());
                }

                try {
                    CSDateElement de = (CSDateElement) v;
                    Field field = mField.getField(de.getId());
                    field.setValue(de.getText().toString());
                } catch (Exception e) {
                    Log.e(sClassName, e.getMessage());
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
        View view = null;
        EditText et = null;
        LinearLayout ll = null;
        CSEditTextGroup csEditTextGroup = null;

        switch(field.getType()) {
            case text:
                csEditTextGroup = new CSEditTextGroup(getActivity(),
                        field,
                        mHeartRateServiceSenderConnection,
                        mHeartRateDataServiceReceiverConnection);
                csEditTextGroup.setOrientation(LinearLayout.HORIZONTAL);
                mViewList.add(csEditTextGroup.getEditText()); // Add the child

                view = csEditTextGroup;
                break;
            case textarea:
                csEditTextGroup = new CSEditTextGroup(getActivity(),
                        field,
                        mHeartRateServiceSenderConnection,
                        mHeartRateDataServiceReceiverConnection);
                csEditTextGroup.setOrientation(LinearLayout.HORIZONTAL);
                mViewList.add(csEditTextGroup.getEditText()); // Add the child
                csEditTextGroup.getEditText().setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);

                view = csEditTextGroup;
                break;
            case password:
                et = new EditText(getActivity());
                et.setHint(field.getLabel());
                et.setText(field.getValue());
                et.setId(field.getId());
                et.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                view = et;
                break;
            case number:
                csEditTextGroup = new CSEditTextGroup(getActivity(),
                        field,
                        mHeartRateServiceSenderConnection,
                        mHeartRateDataServiceReceiverConnection);
                csEditTextGroup.setOrientation(LinearLayout.HORIZONTAL);
                mViewList.add(csEditTextGroup.getEditText()); // Add the child
                csEditTextGroup.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);

                view = csEditTextGroup;
                break;
            case email:
                et = new EditText(getActivity());
                et.setHint(field.getLabel());
                et.setId(field.getId());
                et.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                view = et;
                break;
            case tel:
                et = new EditText(getActivity());
                et.setHint(field.getLabel());
                et.setText(field.getValue());
                et.setId(field.getId());
                et.setInputType(InputType.TYPE_CLASS_PHONE);
                view = et;
                break;
            case url:
                et = new EditText(getActivity());
                et.setHint(field.getLabel());
                et.setText(field.getValue());
                et.setId(field.getId());
                et.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
                view = et;
                break;
            case date:
                CSDateElement de = new CSDateElement(getActivity());
                de.setHint(field.getLabel());
                de.setText(field.getValue());
                de.setId(field.getId());
                de.displayCurrentDate();
                view = de;
                break;
            case time:
                CSTimeElement te = new CSTimeElement(getActivity());
                te.setHint(field.getLabel());
                te.setText(field.getValue());
                te.setId(field.getId());
                te.displayCurrentTime();
                view = te;
                break;
            case datetime:
                et = new EditText(getActivity()); // TODO Parse it correct
                et.setHint(field.getLabel());
                et.setText(field.getValue());
                et.setId(field.getId());
                view = et;
                break;
            case range:
                break;
            case select:
                RadioGroup rg = new RadioGroup(getActivity());
                rg.setOrientation(RadioGroup.VERTICAL);
                for(Option o : field.getOptionList()) {
                    RadioButton rb = new RadioButton(getActivity());
                    rb.setHint(o.getLabel());
                    rb.setText(o.getLabel());
                    rb.setId(o.getId());
                    rg.addView(rb);
                }
                view = rg;
                break;
            case multiselect:
                ll = new LinearLayout(getActivity());
                ll.setOrientation(LinearLayout.VERTICAL);

                for(Option o : field.getOptionList()) {
                    CheckBox cb = new CheckBox(getActivity());
                    cb.setHint(o.getLabel());
                    cb.setText(o.getLabel());
                    cb.setId(o.getId());
                    ll.addView(cb);
                }
                view = ll;
                break;
            case checkbox:
                CheckBox cb = new CheckBox(getActivity());
                cb.setHint(field.getLabel());
                cb.setId(field.getId());
                view = cb;
                break;
            case radio:
                RadioGroup rgg = new RadioGroup(getActivity());
                rgg.setOrientation(RadioGroup.VERTICAL);
                for(Option o : field.getOptionList()) {
                    RadioButton rb = new RadioButton(getActivity());
                    rb.setHint(o.getLabel());
                    rb.setText(o.getLabel());
                    rb.setId(o.getId());
                    rgg.addView(rb);
                }
                view = rgg;
                break;
        }

        if(field.getCompositeField().compareTo("") != 0) {
            ll = new LinearLayout(getActivity());
            ll.setOrientation(LinearLayout.VERTICAL);

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
                    ll.addView(v);
                }
            }

            view = ll;
        }

        if(view != null) {
            if (firstField) {
                view.requestFocus();
                view.setNextFocusUpId(R.id.id_SinglePollActivity_Button_Right);
            }

            mViewList.add(view);
        }

        return view;
    }
}
