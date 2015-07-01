package eu.applabs.crowdsensinglibrary.source;


import java.util.List;

import eu.applabs.crowdsensinglibrary.data.Command;

public interface ICommandSource {
    List<Command> loadCommands(String source, String user, String password);
}
