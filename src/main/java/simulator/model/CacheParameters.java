package simulator.model;

/**
 * @since 5/27/2021
 * @author kcakmak
 */
public enum CacheParameters {

    INSTANCE;

    private int setIndex;
    private int offset;
    private int recency;

    public int getSetIndex() {
        return setIndex;
    }

    public void setSetIndex(int setIndex) {
        this.setIndex = setIndex;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getRecency() {
        return recency;
    }

    public void setRecency(int recency) {
        this.recency = recency;
    }
}
