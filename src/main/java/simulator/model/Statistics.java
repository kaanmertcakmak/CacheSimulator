package simulator.model;

/**
 * @since 5/27/2021
 * @author kcakmak
 * Statistics class to collect statistics of simulator
 */
public enum Statistics {

    INSTANCE;

    private int numAccesses;
    private int numHits;
    private int numMisses;
    private double cacheHitRate;
    private int writesCountToTheMainMemory;


    public void setNumAccesses(int numAccesses) {
        this.numAccesses = numAccesses;
    }

    public int getNumAccesses() {
        return numAccesses;
    }

    public int getNumHits() {
        return numHits;
    }

    public int getWritesCountToTheMainMemory() {
        return writesCountToTheMainMemory;
    }

    public int getNumMisses() {
        return numMisses;
    }

    void setNumHits(int numHits) {
        this.numHits = numHits;
    }

    public void incrementHits() {
        ++numHits;
    }

    void setWritesCountToTheMainMemory(int writesCountToTheMainMemory) {
        this.writesCountToTheMainMemory = writesCountToTheMainMemory;
    }

    void setNumMisses(int numMisses) {
        this.numMisses = numMisses;
    }

    public void incrementMisses() {
        ++numMisses;
    }

    public void incrementWriteCountToTheMainMemory() {
        ++writesCountToTheMainMemory;
    }

    public double getCacheHitRate() {
        cacheHitRate = ((double) this.numHits / this.numAccesses) * 100.0;
        return cacheHitRate;
    }

    public void reset() {
        setNumAccesses(0);
        setNumMisses(0);
        setNumHits(0);
        setWritesCountToTheMainMemory(0);
    }

    @Override
    public String toString() {
        return "Statistics{\n" +
                "Num Accesses=" + numAccesses +
                "\n, Num Hits=" + numHits +
                "\n, Num Misses=" + numMisses +
                "\n, Cache hit rate= %" + getCacheHitRate() +
                "\n, Num Writes to the main memory= " + writesCountToTheMainMemory +
                '}';
    }
}
