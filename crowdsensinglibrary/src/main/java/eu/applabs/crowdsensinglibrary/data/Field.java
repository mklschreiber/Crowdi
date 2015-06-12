package eu.applabs.crowdsensinglibrary.data;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Field {

    private static final String sClassName = Field.class.getSimpleName();
    private static int sUniqueId = 0;

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

    private int mId = ++sUniqueId;
    private String mName = null;
    private String mTitle = null;
    private Type mType = Type.undefined;
    private List<Field> mFields = null;
    private String mCompositeType = null;
    private String mPattern = null;
    private boolean mRequired = false;
    private String mValue = null;

    // Constructor

    public Field() {
        mName = "";
        mTitle = "";
        mType = Type.undefined;
        mFields = new ArrayList<>();
        mCompositeType = "";
        mPattern = "";
        mRequired = false;
        mValue = "";
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

    public void setType(String type) {
        if(type.compareTo("text") == 0) {
            mType = Type.text;
        } else if(type.compareTo("textarea") == 0) {
            mType = Type.textarea;
        } else if(type.compareTo("password") == 0) {
            mType = Type.password;
        } else if(type.compareTo("number") == 0) {
            mType = Type.number;
        } else if(type.compareTo("email") == 0) {
            mType = Type.email;
        } else if(type.compareTo("tel") == 0) {
            mType = Type.tel;
        } else if(type.compareTo("url") == 0) {
            mType = Type.url;
        } else if(type.compareTo("date") == 0) {
            mType = Type.date;
        } else if(type.compareTo("time") == 0) {
            mType = Type.time;
        } else if(type.compareTo("range") == 0) {
            mType = Type.range;
        } else if(type.compareTo("checkbox") == 0) {
            mType = Type.checkbox;
        } else if(type.compareTo("radio") == 0) {
            mType = Type.radio;
        } else {
            mType = Type.undefined;
        }
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

    public void setValue(String value) {
        mValue = value;
    }

    // Getter

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getTitle() {
        return mTitle;
    }

    public Type getType() {
        return mType;
    }

    public List<Field> getFieldList() {
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

    public String getValue() {
        return mValue;
    }

    public Field getField(int id) {
        if(mId == id) {
            return this;
        } else {
            for(Field f : mFields) {
                Field field = f.getField(id);

                if(field != null) {
                    return field;
                }
            }
        }

        return null;
    }

    // Misc

    private String typeToString(Type type) {
        switch(type) {
            case text:
                return "text";
            case textarea:
                return "textarea";
            case password:
                return "password";
            case number:
                return "number";
            case email:
                return "email";
            case tel:
                return "tel";
            case url:
                return "url";
            case date:
                return "date";
            case time:
                return "time";
            case range:
                return "range";
            case checkbox:
                return "checkbox";
            case radio:
                return "radio";
            default:
                return "";
        }
    }

    public JSONObject toJSON() {
        JSONObject field = new JSONObject();

        try {
            if (mName.compareTo("") != 0) {
                field.put("name", mName);
            }

            if (mTitle.compareTo("") != 0) {
                field.put("title", mTitle);
            }

            if (mType != Type.undefined) {
                field.put("type", typeToString(mType));
            } else if (mCompositeType.compareTo("") != 0) {
                field.put("compositeType", mCompositeType);
            }

            if (mPattern.compareTo("") != 0) {
                field.put("pattern", mPattern);
            }

            if (mRequired) {
                field.put("required", mRequired);
            }

            if (mValue.compareTo("") != 0) {
                field.put("value", mValue);
            }

        } catch(Exception e) {
            Log.e(sClassName, e.getMessage());
        }

        return field;
    }

    public JSONObject toJSONCompositeType() {
        JSONObject compositeType = new JSONObject();

        try {
            if(mCompositeType.compareTo("") != 0) {
                compositeType.put("compositeType", mCompositeType);
            }

            JSONArray fields = new JSONArray();

            for(Field f : mFields) {
                fields.put(f.toJSON());
            }

            compositeType.put("fields", fields);
        } catch(Exception e) {
            Log.e(sClassName, e.getMessage());
        }

        return compositeType;
    }
}
