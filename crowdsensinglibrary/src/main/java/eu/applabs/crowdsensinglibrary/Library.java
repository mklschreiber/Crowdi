package eu.applabs.crowdsensinglibrary;

import java.util.ArrayList;
import java.util.List;

import eu.applabs.crowdsensinglibrary.data.Poll;
import eu.applabs.crowdsensinglibrary.source.FileSource;
import eu.applabs.crowdsensinglibrary.source.ISource;
import eu.applabs.crowdsensinglibrary.source.StringSource;
import eu.applabs.crowdsensinglibrary.source.WebSource;

public class Library {

    private List<ILibraryResultListener> mILibraryResultListenerList = null;

    public Library() {
        mILibraryResultListenerList = new ArrayList<>();
    }

    public void deinit() {
        // Nothing to do...
    }

    public void loadPoll(String source) {
        ISource isource = null;

        if(source.contains("http")) {
            // Seems to be a uri
            isource = new WebSource();
        } else if(source.contains(".txt")) {
            // Seems to be a file
            isource = new FileSource();
        } else {
            isource = new StringSource();
        }

        SourceLoadThread slt = new SourceLoadThread(isource, source);
        slt.start();
    }

    public void uploadPoll(String destination, String poll) {
        ISource isource = null;

        if(destination.contains("http")) {
            // Seems to be a uri
            isource = new WebSource();
        } else if(destination.contains(".txt")) {
            // Seems to be a file
            isource = new FileSource();
        } else {
            isource = new StringSource();
        }

        SourceUploadThread sut = new SourceUploadThread(isource, destination, poll);
        sut.start();
    }

    private void notifyListener(ILibraryResultListener.ExecutionStatus executionStatus,
                                Poll poll) {
        for(ILibraryResultListener listener : mILibraryResultListenerList) {
            listener.onLibraryResult(executionStatus, poll);
        }
    }

    public void registerListener(ILibraryResultListener listener) {
        mILibraryResultListenerList.add(listener);
    }

    public void unregisterListener(ILibraryResultListener listener) {
        mILibraryResultListenerList.remove(listener);
    }

    // ---------------------------------------------------------------------------------------------

    private class SourceLoadThread extends Thread {

        private ISource mISource = null;
        private String mSource = null;

        public SourceLoadThread(ISource isource, String source) {
            mISource = isource;
            mSource = source;
        }

        @Override
        public void run() {
            super.run();

            if(mISource != null && mSource != null) {
                Poll poll = mISource.loadPoll(mSource);

                if(poll != null) {
                    notifyListener(ILibraryResultListener.ExecutionStatus.Success,
                            poll);
                } else {
                    notifyListener(ILibraryResultListener.ExecutionStatus.Error,
                            new Poll("0.0.1"));
                }
            } else {
                notifyListener(ILibraryResultListener.ExecutionStatus.Error,
                        new Poll("0.0.1"));
            }
        }
    }

    private class SourceUploadThread extends Thread {

        private ISource mISource = null;
        private String mDestination = null;
        private String mPoll = null;

        public SourceUploadThread(ISource isource, String destination, String poll) {
            mISource = isource;
            mDestination = destination;
            mPoll = poll;
        }

        @Override
        public void run() {
            super.run();

            if(mISource != null && mDestination != null && mPoll != null) {
                List<String> uris = mISource.sendPoll(mDestination, mPoll);

                // TODO Notify the listener
            }
        }
    }
}
