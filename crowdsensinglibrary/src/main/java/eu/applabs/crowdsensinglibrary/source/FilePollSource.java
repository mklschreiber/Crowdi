package eu.applabs.crowdsensinglibrary.source;


import java.util.List;

import eu.applabs.crowdsensinglibrary.data.Command;
import eu.applabs.crowdsensinglibrary.data.Poll;

public class FilePollSource implements IPollSource {

    @Override
    public Poll loadPoll(String source, String user, String password) {
        return null;
    }

    @Override
    public List<Command> sendPoll(String destination, String user, String password, String poll) {
        return null;
    }
}
