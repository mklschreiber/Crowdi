package eu.applabs.crowdsensinglibrary.source;


import java.util.List;

import eu.applabs.crowdsensinglibrary.data.Poll;

public class FileSource implements ISource {

    @Override
    public Poll loadPoll(String source) {
        return null;
    }

    @Override
    public List<String> sendPoll(String destination, String poll) {
        return null;
    }
}
