package eu.applabs.crowdsensinglibrary.data;

public class Command {

    public enum Method {
        Undefined,
        GET,
        POST
    }

    private static final String sClassName = Command.class.getSimpleName();
    private static int sUniqueId = 0;

    private int mId = ++sUniqueId;
    private String mCommand = null;
    private Method mMethod = Method.Undefined;
    private String mInfo = null;

    public Command() {
        mCommand = "";
        mInfo = "";
    }

    public void setCommand(String command) {
        mCommand = command;
    }

    public void setMethod(Method method) {
        mMethod = method;
    }

    public void setMethod(String string) {
        if(string != null) {
            if(string.toLowerCase().compareTo("get") == 0) {
                mMethod = Method.GET;
            } else if(string.toLowerCase().compareTo("post") == 0) {
                mMethod = Method.POST;
            } else {
                mMethod = Method.Undefined;
            }
        }
    }

    public void setInfo(String info) {
        mInfo = info;
    }

    public int getId() {
        return mId;
    }

    public String getCommand() {
        return mCommand;
    }

    public Method getMethod() {
        return mMethod;
    }

    public String getInfo() {
        return mInfo;
    }
}
