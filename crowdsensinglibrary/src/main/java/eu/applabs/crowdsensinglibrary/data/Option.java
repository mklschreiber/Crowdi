package eu.applabs.crowdsensinglibrary.data;

public class Option {

    private static int sUniqueId = 0;

    private int mId = sUniqueId++;
    private String mValue = null;
    private String mLabel = null;
    private boolean mSelected = false;

    public Option() {
        mValue = "";
        mLabel = "";
        mSelected = false;
    }

    public void setValue(String value) {
        mValue = value;
    }

    public void setLabel(String label) {
        mLabel = label;
    }

    public void setSelected(boolean selected) {
        mSelected = selected;
    }

    public int getId() {
        return mId;
    }

    public String getValue() {
        return mValue;
    }

    public String getLabel() {
        return mLabel;
    }

    public boolean getSelected() {
        return mSelected;
    }
}
