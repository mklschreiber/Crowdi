package eu.applabs.crowdsensinglibrary.source;

import java.util.List;

import eu.applabs.crowdsensinglibrary.data.Command;
import eu.applabs.crowdsensinglibrary.parser.CommandParser;

public class StringCommandSource implements ICommandSource {

    @Override
    public List<Command> loadCommands(String source) {
        return new CommandParser().parseString(source);
    }

}
