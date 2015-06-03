package eu.applabs.crowdsensinglibrary;

import java.util.ArrayList;
import java.util.List;

public class Field {

    public enum Type {
        undefined,
        text,
        textarea,
        password,
        number,
        email,
        tel,
        url,
        date,
        time,
        range,
        checkbox,
        radio
    }

    private String mName = null;
    private String mTitle = null;
    private Type mType = Type.undefined;
    private List<Field> mFields = null;
    private String mCompositeType = null;
    private String mPattern = null;
    private boolean mRequired = false;

    // Constructor

    public Field() {
        mName = "";
        mTitle = "";
        mType = Type.undefined;
        mFields = new ArrayList<>();
        mCompositeType = "";
        mPattern = "";
        mRequired = false;
    }

    // Setter

    public void setName(String name) {
        mName = name;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setType(Type type) {
        mType = type;
    }

    public void addField(Field field) {
        mFields.add(field);
    }

    public void removeField(Field field) {
        mFields.remove(field);
    }

    public void setCompositeType(String compositeType) {
        mType = Type.undefined;
        mCompositeType = compositeType;
    }

    public void setPattern(String pattern) {
        mPattern = pattern;
    }

    public void setRequired(boolean required) {
        mRequired = required;
    }

    // Getter

    public String getName() {
        return mName;
    }

    public String getTitle() {
        return mTitle;
    }

    public Type getType() {
        return mType;
    }

    public List<Field> getFields() {
        return mFields;
    }

    public String getCompositeType() {
        return mCompositeType;
    }

    public String getPattern() {
        return mPattern;
    }

    public boolean getRequired() {
        return mRequired;
    }
}
