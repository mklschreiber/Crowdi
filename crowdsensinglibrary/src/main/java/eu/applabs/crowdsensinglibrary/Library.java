package eu.applabs.crowdsensinglibrary;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import eu.applabs.crowdsensinglibrary.data.Account;
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

    private static Library mInstance = null;
    private boolean mInitialized = false;

    private Context mContext = null;
    private Account mAccount = null;
    private List<ILibraryResultListener> mILibraryResultListenerList = null;

    private Library() {  }

    public static Library getInstance() {
        if(Library.mInstance == null) {
            Library.mInstance = new Library();
            Library.mInstance.mILibraryResultListenerList = new ArrayList<>();
        }

        return mInstance;
    }

    public void init(Context context) {
        if(!mInitialized) {
            mContext = context;
            mAccount = new Account(mContext);

            mInitialized = true;
        }
    }

    public void deinit() {
        // Nothing to do...
    }

    public boolean accountAvailable() {
        if(mAccount != null
                && mAccount.getUserName().compareTo("") != 0
                && mAccount.getPassword().compareTo("") != 0) {
            return true;
        }

        return false;
    }

    public void login(String username, String password) {
        if(mAccount != null) {
            mAccount.setUserName(username);
            mAccount.setPassword(password);
        }
    }

    public void logout() {
        if(mAccount != null) {
            mAccount.setUserName("");
            mAccount.setPassword("");
        }
    }

    public void loadCommands(String source, String className) {
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

        new CommandSourceLoadThread(iCommandSource, source, className).start();
    }

    public void loadPoll(String source, String className) {
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

        new PollSourceLoadThread(iPollSource, source, className).start();
    }

    public void uploadPoll(String destination, String poll, String className) {
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

        new PollSourceUploadThread(iPollSource, destination, poll, className).start();
    }

    private void notifyListener(ILibraryResultListener.ExecutionStatus executionStatus,
                                Poll poll,
                                String className) {
        for(ILibraryResultListener listener : mILibraryResultListenerList) {
            listener.onLibraryResult(executionStatus, poll, className);
        }
    }

    private void notifyListener(ILibraryResultListener.ExecutionStatus executionStatus,
                                List<Command> list,
                                String className) {
        for(ILibraryResultListener listener : mILibraryResultListenerList) {
            listener.onLibraryResult(executionStatus, list, className);
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
        private String mClassName = null;

        public CommandSourceLoadThread(ICommandSource iCommandSource, String source, String className) {
            mICommandSource = iCommandSource;
            mSource = source;
            mClassName = className;
        }

        @Override
        public void run() {
            super.run();

            if(mICommandSource != null && mSource != null) {
                List<Command> list = mICommandSource.loadCommands(mSource,
                        mAccount.getUserName(), mAccount.getPassword());

                if(list != null) {
                    notifyListener(ILibraryResultListener.ExecutionStatus.Success,
                            list, mClassName);
                } else {
                    notifyListener(ILibraryResultListener.ExecutionStatus.Error,
                            new ArrayList<Command>(), mClassName);
                }
            }
        }
    }

    // ---------------------------------------------------------------------------------------------

    private class PollSourceLoadThread extends Thread {

        private IPollSource mIPollSource = null;
        private String mSource = null;
        private String mClassName = null;

        public PollSourceLoadThread(IPollSource iPollSource, String source, String className) {
            mIPollSource = iPollSource;
            mSource = source;
            mClassName = className;
        }

        @Override
        public void run() {
            super.run();

            if(mIPollSource != null && mSource != null) {
                Poll poll = mIPollSource.loadPoll(mSource,
                        mAccount.getUserName(), mAccount.getPassword());

                if(poll != null) {
                    notifyListener(ILibraryResultListener.ExecutionStatus.Success,
                            poll, mClassName);
                } else {
                    notifyListener(ILibraryResultListener.ExecutionStatus.Error,
                            new Poll("0.0.2"), mClassName);
                }
            } else {
                notifyListener(ILibraryResultListener.ExecutionStatus.Error,
                        new Poll("0.0.2"), mClassName);
            }
        }
    }

    private class PollSourceUploadThread extends Thread {

        private IPollSource mIPollSource = null;
        private String mDestination = null;
        private String mPoll = null;
        private String mClassName = null;

        public PollSourceUploadThread(IPollSource iPollSource, String destination, String poll, String className) {
            mIPollSource = iPollSource;
            mDestination = destination;
            mPoll = poll;
            mClassName = className;
        }

        @Override
        public void run() {
            super.run();

            if(mIPollSource != null && mDestination != null && mPoll != null) {
                List<Command> commandList = mIPollSource.sendPoll(mDestination,
                        mAccount.getUserName(), mAccount.getPassword(), mPoll);

                if(commandList != null) {
                    notifyListener(ILibraryResultListener.ExecutionStatus.Success, commandList, mClassName);
                } else {
                    notifyListener(ILibraryResultListener.ExecutionStatus.Error, commandList, mClassName);
                }
            }
        }
    }
}
