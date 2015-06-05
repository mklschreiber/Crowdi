package eu.applabs.crowdsensinglibrary;

import eu.applabs.crowdsensinglibrary.data.Poll;

public interface ILibraryResultListener {
    public enum ExecutionStatus {
        Undefined,
        Success,
        Error
    }

    public void onLibraryResult(final ExecutionStatus status, final Poll poll);
}
