package eu.applabs.crowdsensinglibrary.source;

import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import eu.applabs.crowdsensinglibrary.data.Command;
import eu.applabs.crowdsensinglibrary.parser.CommandParser;

public class WebCommandSource implements ICommandSource {

    private static final String sClassName = WebCommandSource.class.getSimpleName();

    @Override
    public List<Command> loadCommands(String source, String user, String password) {
        try {
            URL url = new URL(source);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");

            if(user.compareTo("") != 0 && password.compareTo("") != 0) {
                String encoded = Base64.encodeToString((user + ":" + password).getBytes("UTF-8"), Base64.NO_WRAP);
                httpURLConnection.setRequestProperty("Authorization", "Basic " +  encoded);
            }

            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(httpURLConnection.getInputStream()));

            String result = "";
            String line = null;

            while((line = bufferedReader.readLine()) != null) {
                result += line;
            }

            bufferedReader.close();

            return new CommandParser().parseString(result);

        } catch(Exception e) {
            Log.e(sClassName, e.getMessage());
        }

        return null;
    }

}
