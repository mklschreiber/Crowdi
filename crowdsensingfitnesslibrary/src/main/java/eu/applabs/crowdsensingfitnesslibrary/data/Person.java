package eu.applabs.crowdsensingfitnesslibrary.data;

public class Person {
    private String mName = null;
    private String mSize = null;
    private String mWeight = null;

    public Person() {
        mName = "";
        mSize = "";
        mWeight = "";
    }

    public void setName(String name) {
        mName = name;
    }

    public void setSize(String size) {
        mSize = size;
    }

    public void setWeight(String weight) {
        mWeight = weight;
    }

    public String getName() {
        return mName;
    }

    public String getSize() {
        return mSize;
    }

    public String getWeight() {
        return mWeight;
    }
}
