package eu.applabs.crowdsensinglibrary.parser;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import eu.applabs.crowdsensinglibrary.data.Field;
import eu.applabs.crowdsensinglibrary.data.Poll;

public class PollParser {
    private static final String sClassName = PollParser.class.getSimpleName();

    public Poll parseString(String string) {
        List<Field> fieldList = parseString(string, null);
        Poll poll = new Poll("0.0.1");
        poll.setFieldList(fieldList);

        return poll;
    }

    private List<Field> parseString(String string, JSONArray compositeTypes) {
        List<Field> poll = null;

        try {
            JSONObject object = new JSONObject(string);
            JSONArray fields = object.getJSONArray("fields");

            if(object.has("compositeTypes")) {
                compositeTypes = object.getJSONArray("compositeTypes");
            }

            if(fields != null) {
                poll = new ArrayList<>();

                for (int i = 0; i < fields.length(); ++i) {
                    Field field = new Field();
                    JSONObject f = fields.getJSONObject(i);

                    if(f.has("name")) {
                        field.setName(f.getString("name"));
                    }

                    if(f.has("title")) {
                        field.setTitle(f.getString("title"));
                    }

                    if(f.has("compositeType")) {
                        String compositeTypeId = f.getString("compositeType");

                        for(int ii = 0; ii < compositeTypes.length(); ++ii) {
                            JSONObject ct = compositeTypes.getJSONObject(ii);

                            if(compositeTypeId.compareTo(ct.getString("compositeType")) == 0) {
                                // Found the compositeType

                                // Recursive call
                                List<Field> compositeTypeFields = parseString(ct.toString(), compositeTypes);

                                field.setCompositeType(compositeTypeId);
                                for(Field compositeTypeField : compositeTypeFields) {
                                    field.addField(compositeTypeField);
                                }
                            }
                        }
                    }

                    if(f.has("type")) {
                        field.setType(f.getString("type"));
                    }

                    if(f.has("pattern")) {
                        field.setPattern(f.getString("pattern"));
                    }

                    if(f.has("required")) {
                        field.setRequired(f.getBoolean("required"));
                    }

                    poll.add(field);
                }
            }
        } catch (Exception e) {
            Log.e(sClassName, e.getMessage());
        }

        return poll;
    }
}
