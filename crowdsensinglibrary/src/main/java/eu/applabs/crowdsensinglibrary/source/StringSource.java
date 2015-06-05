package eu.applabs.crowdsensinglibrary.source;

import java.util.List;

import eu.applabs.crowdsensinglibrary.data.Poll;
import eu.applabs.crowdsensinglibrary.parser.PollParser;

public class StringSource implements ISource {

    private static final String sClassName = StringSource.class.getSimpleName();

    @Override
    public Poll loadPoll(String source) {
        return new PollParser().parseString(source);
    }

    @Override
    public List<String> sendPoll(String destination, String poll) {
        return null;
    }
}
