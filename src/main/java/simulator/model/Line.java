package simulator.model;

/**
 * @since 5/27/2021
 * @author kcakmak
 */
public class Line {

    private boolean isValid;
    private boolean isDirty;
    // TODO: Tag in here is useless since we already use it as map key in CacheSet class. Added this just for better printing
    private long tag;
    private int frequency;
    private String hexAddress;
    private int recency;

    public Line(long tag, String hexAddress) {
        setTag(tag);
        setValid(true);
        setHexAddress(hexAddress);
        frequency = 1;
    }

    public void setRecency(int recency) {
        this.recency = recency;
    }

    public int getRecency() {
        return recency;
    }

    public String getHexAddress() {
        return hexAddress;
    }

    public void setHexAddress(String hexAddress) {
        this.hexAddress = hexAddress;
    }

    public int getFrequency() {
        return frequency;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void incrementFrequency() {
        ++frequency;
    }

    public void setDirty(boolean dirty) {
        isDirty = dirty;
    }

    public long getTag() {
        return tag;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public void setTag(long tag) {
        this.tag = tag;
    }

    public void writeInLine(long tag, String hexAddress, int recency) {
        setTag(tag);
        setValid(true);
        setHexAddress(hexAddress);
        setRecency(recency);
        setFrequency(1);
        setDirty(false);
    }

    @Override
    public String toString() {
        return "Line{" +
                "isValid=" + isValid +
                ", isDirty=" + isDirty +
                ", tag=" + tag +
                ", hexAddress(data)=" + hexAddress +
                ", frequency=" + frequency +
                ", recency=" + recency +
                '}';
    }
}
