package eu.applabs.crowdsensinglibrary.source;

import java.util.List;

import eu.applabs.crowdsensinglibrary.data.Command;
import eu.applabs.crowdsensinglibrary.data.Poll;
import eu.applabs.crowdsensinglibrary.parser.PollParser;

public class StringPollSource implements IPollSource {

    private static final String sClassName = StringPollSource.class.getSimpleName();

    @Override
    public Poll loadPoll(String source) {
        return new PollParser().parseString(source);
    }

    @Override
    public List<Command> sendPoll(String destination, String poll) {
        return null;
    }
}
