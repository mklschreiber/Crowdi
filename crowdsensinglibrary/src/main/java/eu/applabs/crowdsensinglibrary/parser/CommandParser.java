package eu.applabs.crowdsensinglibrary.parser;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import eu.applabs.crowdsensinglibrary.data.Command;

public class CommandParser {
    private static final String sClassName = CommandParser.class.getSimpleName();

    public List<Command> parseString(String string) {
        List<Command> list = new ArrayList<>();

        try {
            JSONObject object = new JSONObject(string);
            JSONArray array = object.getJSONArray("next");

            for(int i = 0; i < array.length(); ++i) {
                JSONObject jsonCommand = array.getJSONObject(i);

                Command command = new Command();
                command.setCommand(jsonCommand.getString("command"));
                command.setMethod(jsonCommand.getString("method"));
                command.setInfo(jsonCommand.getString("info"));

                list.add(command);
            }

        } catch (Exception e) {
            Log.e(sClassName, e.getMessage());
        }

        return list;
    }
}
