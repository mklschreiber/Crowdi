package eu.applabs.crowdsensinglibrary.data;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Poll {

    private static final String sClassName = Poll.class.getSimpleName();

    private String mVersion = null;
    private List<Field> mFieldList = null;

    public Poll(String version) {
        mVersion = version;
        mFieldList = new ArrayList<>();
    }

    public void addField(Field field) {
        mFieldList.add(field);
    }

    public void removeField(Field field) {
        mFieldList.remove(field);
    }

    public void setFieldList(List<Field> list) {
        mFieldList = list;
    }

    public List<Field> getFieldList() {
        return mFieldList;
    }

    public JSONObject toJSON() {
        JSONObject poll = new JSONObject();
        JSONArray fields = new JSONArray();
        JSONArray compositeTypes = new JSONArray();
        List<String> addedCompositeTypes = new ArrayList<>();

        try {
            poll.put("version", mVersion);

            for(Field f : mFieldList) {
                fields.put(f.toJSON());

                if(f.getCompositeType().compareTo("") != 0 && !addedCompositeTypes.contains(f.getCompositeType())) {
                    addedCompositeTypes.add(f.getCompositeType()); // Mark as already added
                    compositeTypes.put(f.toJSONCompositeType());
                }
            }

            poll.put("fields", fields);
            poll.put("compositeTypes", compositeTypes);

        } catch(Exception e) {
            Log.e(sClassName, e.getMessage());
        }

        return poll;
    }
}
