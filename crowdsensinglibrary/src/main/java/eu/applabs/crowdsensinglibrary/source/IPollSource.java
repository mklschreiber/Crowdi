package eu.applabs.crowdsensinglibrary.source;

import java.util.List;

import eu.applabs.crowdsensinglibrary.data.Command;
import eu.applabs.crowdsensinglibrary.data.Poll;

public interface IPollSource {
    Poll loadPoll(String source, String user, String password);
    List<Command> sendPoll(String destination, String user, String password, String poll);
}
