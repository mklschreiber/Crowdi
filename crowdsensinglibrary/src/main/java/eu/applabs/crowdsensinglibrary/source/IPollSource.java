package eu.applabs.crowdsensinglibrary.source;

import java.util.List;

import eu.applabs.crowdsensinglibrary.data.Command;
import eu.applabs.crowdsensinglibrary.data.Poll;

public interface IPollSource {
    Poll loadPoll(String source);
    List<Command> sendPoll(String destination, String poll);
}
