package eu.applabs.crowdsensinglibrary;

import java.util.List;

import eu.applabs.crowdsensinglibrary.data.Command;
import eu.applabs.crowdsensinglibrary.data.Poll;

public interface ILibraryResultListener {
    public enum ExecutionStatus {
        Undefined,
        Success,
        Error
    }

    public void onLibraryResult(final ExecutionStatus status, final Poll poll);
    public void onLibraryResult(final ExecutionStatus status, final List<Command> list);
}
