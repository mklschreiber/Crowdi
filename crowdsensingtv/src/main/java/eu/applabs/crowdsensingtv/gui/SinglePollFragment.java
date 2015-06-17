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

import java.util.ArrayList;
import java.util.List;

import eu.applabs.crowdsensinglibrary.data.Field;
import eu.applabs.crowdsensingtv.R;
import eu.applabs.crowdsensingtv.base.CSDateElement;
import eu.applabs.crowdsensingtv.base.CSTimeElement;

public class SinglePollFragment extends Fragment {

    private static final String sClassName = SinglePollFragment.class.getSimpleName();

    private LinearLayout mLinearLayout = null;
    private List<View> mViewList = null;
    private Field mField = null;

    public SinglePollFragment() {
        mViewList = new ArrayList<>();
    }

    public void setField(Field field) {
        mField = field;
    }

    public boolean allRequiredFieldsFilled() {
        Field f = getMissingField();

        if(f == null) {
            return true;
        }

        return false;
    }

    public Field getMissingField() {
        if(mField.getRequired() &&
                mField.getValue().compareTo("") == 0 &&
                mField.getCompositeType().compareTo("") == 0) {
            return mField;
        } else {
            for(Field f : mField.getFieldList()) {
                if(f.getRequired() &&
                        f.getValue().compareTo("") == 0 &&
                        f.getCompositeType().compareTo("") == 0) {
                    return f;
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

        switch(field.getType()) {
            case text:
                et = new EditText(getActivity());
                et.setHint(field.getTitle());
                et.setText(field.getValue());
                et.setId(field.getId());
                view = et;
                break;
            case textarea:
                et = new EditText(getActivity());
                et.setHint(field.getTitle());
                et.setText(field.getValue());
                et.setId(field.getId());
                et.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                view = et;
                break;
            case password:
                et = new EditText(getActivity());
                et.setHint(field.getTitle());
                et.setText(field.getValue());
                et.setId(field.getId());
                et.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                view = et;
                break;
            case number:
                et = new EditText(getActivity());
                et.setHint(field.getTitle());
                et.setText(field.getValue());
                et.setId(field.getId());
                et.setInputType(InputType.TYPE_CLASS_NUMBER);
                view = et;
                break;
            case email:
                et = new EditText(getActivity());
                et.setHint(field.getTitle());
                et.setId(field.getId());
                et.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                view = et;
                break;
            case tel:
                et = new EditText(getActivity());
                et.setHint(field.getTitle());
                et.setText(field.getValue());
                et.setId(field.getId());
                et.setInputType(InputType.TYPE_CLASS_PHONE);
                view = et;
                break;
            case url:
                et = new EditText(getActivity());
                et.setHint(field.getTitle());
                et.setText(field.getValue());
                et.setId(field.getId());
                et.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
                view = et;
                break;
            case date:
                CSDateElement de = new CSDateElement(getActivity());
                de.setHint(field.getTitle());
                de.setText(field.getValue());
                de.setId(field.getId());
                de.displayCurrentDate();
                view = de;
                break;
            case time:
                CSTimeElement te = new CSTimeElement(getActivity());
                te.setHint(field.getTitle());
                te.setText(field.getValue());
                te.setId(field.getId());
                te.displayCurrentTime();
                view = te;
                break;
            case range:
                break;
            case checkbox:
                CheckBox cb = new CheckBox(getActivity());
                cb.setHint(field.getTitle());
                cb.setId(field.getId());
                view = cb;
                break;
            case radio:
                RadioButton rb = new RadioButton(getActivity());
                rb.setHint(field.getTitle());
                rb.setId(field.getId());
                view = rb;
                break;
        }

        if(field.getCompositeType().compareTo("") != 0) {
            LinearLayout ll = new LinearLayout(getActivity());
            ll.setOrientation(LinearLayout.VERTICAL);

            for(int i = 0; i < field.getFieldList().size(); ++i) {
                Field f = field.getFieldList().get(i);
                View v;

                if(i == 0) {
                    v = createViewForField(f, true);
                } else {
                    v = createViewForField(f, false);
                }

                mViewList.add(v);

                if(v != null) {
                    ll.addView(v);
                }
            }

            view = ll;
        }

        if(firstField) {
            view.requestFocus();
            view.setNextFocusUpId(R.id.id_SinglePollActivity_Button_Right);
        }

        mViewList.add(view);
        return view;
    }
}
