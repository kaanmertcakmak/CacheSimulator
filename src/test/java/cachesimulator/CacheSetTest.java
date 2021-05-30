package cachesimulator;

import org.junit.Assert;
import org.junit.Test;

import simulator.cacheoperations.CacheSet;
import simulator.model.Command;
import simulator.model.Line;
import simulator.model.Statistics;
import simulator.model.WriteAlgorithm;

/**
 * @since 5/30/2021
 */
public class CacheSetTest {

    @Test
    public void testInsertNewLineToTheCacheWhenCommandIsLoad() {
        CacheSet cacheSet = new CacheSet(1);

        long expectedTag = 201252386;

        Statistics statistics = Statistics.INSTANCE;
        int missesBefore = statistics.getNumMisses();
        Line insertedLine = cacheSet.insertNewLineToTheCache(expectedTag, 2, Command.LOAD, WriteAlgorithm.WRITE_THROUGH);

        int missesAfter = statistics.getNumMisses();
        Assert.assertEquals(expectedTag, insertedLine.getTag());
        Assert.assertEquals(2, insertedLine.getRecency());
        Assert.assertEquals(1, missesAfter - missesBefore);
    }

    @Test
    public void testInsertNewLineToTheCacheWhenCommandIsStoreAndPolicyIsWriteThrough() {
        CacheSet cacheSet = new CacheSet(2);

        long expectedTag = 201252386;

        Statistics statistics = Statistics.INSTANCE;
        int numWriteMainMemoryBefore = statistics.getWritesCountToTheMainMemory();
        cacheSet.insertNewLineToTheCache(expectedTag, 2, Command.STORE, WriteAlgorithm.WRITE_THROUGH);

        int numWriteMainMemoryAfter = statistics.getWritesCountToTheMainMemory();

        Assert.assertEquals(1, numWriteMainMemoryAfter - numWriteMainMemoryBefore);
    }
}
