package eu.applabs.crowdsensinglibrary;

import java.util.ArrayList;
import java.util.List;

import eu.applabs.crowdsensinglibrary.data.Command;
import eu.applabs.crowdsensinglibrary.data.Poll;
import eu.applabs.crowdsensinglibrary.source.FileCommandSource;
import eu.applabs.crowdsensinglibrary.source.FilePollSource;
import eu.applabs.crowdsensinglibrary.source.ICommandSource;
import eu.applabs.crowdsensinglibrary.source.IPollSource;
import eu.applabs.crowdsensinglibrary.source.StringCommandSource;
import eu.applabs.crowdsensinglibrary.source.StringPollSource;
import eu.applabs.crowdsensinglibrary.source.WebCommandSource;
import eu.applabs.crowdsensinglibrary.source.WebPollSource;

public class Library {

    private List<ILibraryResultListener> mILibraryResultListenerList = null;

    public Library() {
        mILibraryResultListenerList = new ArrayList<>();
    }

    public void deinit() {
        // Nothing to do...
    }

    public void loadCommands(String source, String user, String password) {
        ICommandSource iCommandSource;

        if(source.contains("http")) {
            // Seems to be a uri
            iCommandSource = new WebCommandSource();
        } else if(source.contains(".txt")) {
            // Seems to be a file
            iCommandSource = new FileCommandSource();
        } else {
            iCommandSource = new StringCommandSource();
        }

        new CommandSourceLoadThread(iCommandSource, source, user, password).start();
    }

    public void loadPoll(String source, String user, String password) {
        IPollSource iPollSource;

        if(source.contains("http")) {
            // Seems to be a uri
            iPollSource = new WebPollSource();
        } else if(source.contains(".txt")) {
            // Seems to be a file
            iPollSource = new FilePollSource();
        } else {
            iPollSource = new StringPollSource();
        }

        new PollSourceLoadThread(iPollSource, source, user, password).start();
    }

    public void uploadPoll(String destination, String user, String password, String poll) {
        IPollSource iPollSource;

        if(destination.contains("http")) {
            // Seems to be a uri
            iPollSource = new WebPollSource();
        } else if(destination.contains(".txt")) {
            // Seems to be a file
            iPollSource = new FilePollSource();
        } else {
            iPollSource = new StringPollSource();
        }

        new PollSourceUploadThread(iPollSource, destination, user, password, poll).start();
    }

    private void notifyListener(ILibraryResultListener.ExecutionStatus executionStatus,
                                Poll poll) {
        for(ILibraryResultListener listener : mILibraryResultListenerList) {
            listener.onLibraryResult(executionStatus, poll);
        }
    }

    private void notifyListener(ILibraryResultListener.ExecutionStatus executionStatus,
                                List<Command> list) {
        for(ILibraryResultListener listener : mILibraryResultListenerList) {
            listener.onLibraryResult(executionStatus, list);
        }
    }

    public void registerListener(ILibraryResultListener listener) {
        mILibraryResultListenerList.add(listener);
    }

    public void unregisterListener(ILibraryResultListener listener) {
        mILibraryResultListenerList.remove(listener);
    }

    // ---------------------------------------------------------------------------------------------

    private class CommandSourceLoadThread extends Thread {
        private ICommandSource mICommandSource = null;
        private String mSource = null;
        private String mUser = null;
        private String mPassword = null;

        public CommandSourceLoadThread(ICommandSource iCommandSource, String source, String user, String password) {
            mICommandSource = iCommandSource;
            mSource = source;
            mUser = user;
            mPassword = password;
        }

        @Override
        public void run() {
            super.run();

            if(mICommandSource != null && mSource != null) {
                List<Command> list = mICommandSource.loadCommands(mSource, mUser, mPassword);

                if(list != null) {
                    notifyListener(ILibraryResultListener.ExecutionStatus.Success,
                            list);
                } else {
                    notifyListener(ILibraryResultListener.ExecutionStatus.Error,
                            new ArrayList<Command>());
                }
            }
        }
    }

    // ---------------------------------------------------------------------------------------------

    private class PollSourceLoadThread extends Thread {

        private IPollSource mIPollSource = null;
        private String mSource = null;
        private String mUser = null;
        private String mPassword = null;

        public PollSourceLoadThread(IPollSource iPollSource, String source, String user, String password) {
            mIPollSource = iPollSource;
            mSource = source;
            mUser = user;
            mPassword = password;
        }

        @Override
        public void run() {
            super.run();

            if(mIPollSource != null && mSource != null) {
                Poll poll = mIPollSource.loadPoll(mSource, mUser, mPassword);

                if(poll != null) {
                    notifyListener(ILibraryResultListener.ExecutionStatus.Success,
                            poll);
                } else {
                    notifyListener(ILibraryResultListener.ExecutionStatus.Error,
                            new Poll("0.0.2"));
                }
            } else {
                notifyListener(ILibraryResultListener.ExecutionStatus.Error,
                        new Poll("0.0.2"));
            }
        }
    }

    private class PollSourceUploadThread extends Thread {

        private IPollSource mIPollSource = null;
        private String mDestination = null;
        private String mUser = null;
        private String mPassword = null;
        private String mPoll = null;

        public PollSourceUploadThread(IPollSource iPollSource, String destination, String user, String password, String poll) {
            mIPollSource = iPollSource;
            mDestination = destination;
            mUser = user;
            mPassword = password;
            mPoll = poll;
        }

        @Override
        public void run() {
            super.run();

            if(mIPollSource != null && mDestination != null && mPoll != null) {
                List<Command> commandList = mIPollSource.sendPoll(mDestination, mUser, mPassword, mPoll);

                if(commandList != null) {
                    notifyListener(ILibraryResultListener.ExecutionStatus.Success, commandList);
                } else {
                    notifyListener(ILibraryResultListener.ExecutionStatus.Error, commandList);
                }
            }
        }
    }
}
