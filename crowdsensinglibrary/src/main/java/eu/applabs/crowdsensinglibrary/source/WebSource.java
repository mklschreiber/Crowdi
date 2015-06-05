package eu.applabs.crowdsensinglibrary.source;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import eu.applabs.crowdsensinglibrary.data.Poll;
import eu.applabs.crowdsensinglibrary.parser.PollParser;

public class WebSource implements ISource{

    private static final String sClassName = WebSource.class.getSimpleName();

    @Override
    public Poll loadPoll(String source) {
        try {
            URL url = new URL(source);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");

            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(httpURLConnection.getInputStream()));

            String result = "";
            String line = null;

            while((line = bufferedReader.readLine()) != null) {
                result += line;
            }

            bufferedReader.close();

            return new PollParser().parseString(result);

        } catch(Exception e) {
            Log.e(sClassName, e.getMessage());
        }

        return null;
    }

    @Override
    public List<String> sendPoll(String destination, String poll) {
        List<String> urls = null;

        try {
            URL url = new URL(destination);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");

            // Upload

            BufferedWriter bufferedWriter = new BufferedWriter(
                    new OutputStreamWriter(httpURLConnection.getOutputStream()));

            bufferedWriter.write(poll);
            bufferedWriter.close();

            // Get the result

            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(httpURLConnection.getInputStream()));

            String result = "";
            String line = null;

            while((line = bufferedReader.readLine()) != null) {
                result += line;
            }

            bufferedReader.close();

            String[] urlsstring = result.split("\n");
            for(int i = 0; i < urlsstring.length; ++i) {
                urls.add(urlsstring[i]);
            }
        } catch (Exception e) {
            Log.e(sClassName, e.getMessage());
        }

        return urls;
    }
}
