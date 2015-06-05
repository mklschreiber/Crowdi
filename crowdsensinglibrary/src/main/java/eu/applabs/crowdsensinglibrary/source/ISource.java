package eu.applabs.crowdsensinglibrary.source;

import java.util.List;

import eu.applabs.crowdsensinglibrary.data.Poll;

public interface ISource {
    Poll loadPoll(String source);
    List<String> sendPoll(String destination, String poll);
}
