package eu.applabs.crowdsensinglibrary.parser;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import eu.applabs.crowdsensinglibrary.data.Field;
import eu.applabs.crowdsensinglibrary.data.Option;
import eu.applabs.crowdsensinglibrary.data.Poll;

public class PollParser {
    private static final String sClassName = PollParser.class.getSimpleName();

    public Poll parseString(String string) {
        List<Field> fieldList = parseString(string, null);
        Poll poll = new Poll("0.0.2");
        poll.setFieldList(fieldList);

        return poll;
    }

    private List<Field> parseString(String string, JSONArray compositeFields) {
        List<Field> poll = null;

        try {
            JSONObject object = new JSONObject(string);
            JSONArray fields = object.getJSONArray("fields");

            if(object.has("compositeFields")) {
                compositeFields = object.getJSONArray("compositeFields");
            }

            if(fields != null) {
                poll = new ArrayList<>();

                for (int i = 0; i < fields.length(); ++i) {
                    Field field = new Field();
                    JSONObject f = fields.getJSONObject(i);

                    if(f.has("name")) {
                        field.setName(f.getString("name"));
                    }

                    if(f.has("label")) {
                        field.setLabel(f.getString("label"));
                    }

                    if(f.has("compositeField")) {
                        String compositeField = f.getString("compositeField");

                        for(int ii = 0; ii < compositeFields.length(); ++ii) {
                            JSONObject cf = compositeFields.getJSONObject(ii);

                            if(compositeField.compareTo(cf.getString("name")) == 0) {
                                // Found the compositeField

                                // Recursive call
                                List<Field> compositeTypeFields = parseString(cf.toString(), compositeFields);

                                field.setCompositeField(compositeField);
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

                    if(f.has("options")) {
                        JSONArray array = f.getJSONArray("options");

                        for(int ii = 0; ii < array.length(); ++ii) {
                            JSONObject o = array.getJSONObject(ii);

                            Option option = new Option();
                            option.setLabel(o.getString("label"));
                            option.setValue(o.getString("value"));

                            field.addOption(option);
                        }
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
