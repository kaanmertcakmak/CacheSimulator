package simulator.cacheoperations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import simulator.model.CacheParameters;
import simulator.model.Command;
import simulator.model.Line;
import simulator.model.ReplacementPolicy;
import simulator.model.Statistics;
import simulator.model.WriteAlgorithm;

/**
 * @since 5/27/2021
 * @author kcakmak
 */
public class CacheSet {

    private final Map<Long, Line> lineMap;
    private final int numWays;

    public CacheSet(int numWays) {
        this.numWays = numWays;
        lineMap = new HashMap<>(numWays);
    }

    public Line insertNewLineToTheCache(long tag, int recency, Command command, WriteAlgorithm writeAlgorithm) {
        Line line = new Line(tag);
        line.setRecency(recency);
        Statistics.INSTANCE.incrementMisses();
        if(command.getName().equals(Command.STORE.getName()) && writeAlgorithm.isEqualTo(WriteAlgorithm.WRITE_THROUGH)) {
            Statistics.INSTANCE.incrementWriteCountToTheMainMemory();
        }
        return line;
    }

    public Line replaceLineWithRemovedOne(long tag, ReplacementPolicy replacementPolicy,
                                          Command command, WriteAlgorithm writeAlgorithm, int recency) {
        Line line = removeLineBlock(lineMap, replacementPolicy);
        Statistics.INSTANCE.incrementMisses();
        if((command.getName().equals(Command.STORE.getName())
                && writeAlgorithm.isEqualTo(WriteAlgorithm.WRITE_THROUGH))
        || (writeAlgorithm.isEqualTo(WriteAlgorithm.WRITE_BACK)
                && line.isDirty())) {
            Statistics.INSTANCE.incrementWriteCountToTheMainMemory();
        }

        line.writeInLine(tag, recency);
        return line;
    }

    /**
     * Cache operation method
     * @param tag Tag that will be stored or gathered to the cache
     * @param replacementPolicy Parameter to define we will replace values with LRU or LFU
     * @param command Memory command. Can be either LOAD or STORE
     * @param writeAlgorithm Write algorithm can be either write_back or write_through
     */
    public void insertInSet(long tag, ReplacementPolicy replacementPolicy, Command command, WriteAlgorithm writeAlgorithm) {
        int recency = CacheParameters.INSTANCE.getRecency() + 1;
        Line line = lineMap.get(tag);
        if(lineMap.size() != numWays && Objects.isNull(line)) {
            // When we have lines less than ways count, and the line that we look for is not in set...
            line = insertNewLineToTheCache(tag, recency, command, writeAlgorithm);
            lineMap.put(tag, line);
        } else if (Objects.isNull(line)){
            // line block with key as "tag" equals null, that means we don't have any line with that tag, so we replace it
            line = replaceLineWithRemovedOne(tag, replacementPolicy, command, writeAlgorithm, recency);
            lineMap.put(tag, line);
        } else {
            // Enters this else part when we found what we look for in cache, so it is a hit
            line.setRecency(recency);
            line.incrementFrequency();
            Statistics.INSTANCE.incrementHits();

            if(command.getName().equals(Command.STORE.getName())) {
                if(writeAlgorithm.isEqualTo(WriteAlgorithm.WRITE_THROUGH)) {
                    Statistics.INSTANCE.incrementWriteCountToTheMainMemory();
                    line.setDirty(false);
                } else {
                    line.setDirty(true);
                }
            }

        }
    }

    /**
     * Removes line block due to replacement policy for replacing it later
     * We are getting the line with a tag that has least frequency (or recency) and remove it
     * @param lineMap Line that will be removed
     * @param replacementPolicy Parameter to define we will remove values with LRU or LFU
     *                          if LRU, we will get the line with minimum recency value and remove it from map.
     *                          if LFU, we will get the line with minimum frequency value and remove it from map.
     * @return  Removed line
     */
    public Line removeLineBlock(Map<Long, Line> lineMap, ReplacementPolicy replacementPolicy) {
        List<Map.Entry<Long, Line>> lineEntries = new ArrayList<>(lineMap.entrySet());
        long keyOfTheLineToBeReplaced = 0;
        int min = Integer.MAX_VALUE;

        for(Map.Entry<Long, Line> line : lineEntries) {
            int compareParam;
            if(replacementPolicy.getName().equals(ReplacementPolicy.LFU.getName())) {
                compareParam = line.getValue().getFrequency();
            } else {
                compareParam = line.getValue().getRecency();
            }
            min = Math.min(min, compareParam);
        }

        // get line block with least use count or least recency depending of replacement policy
        for(Map.Entry<Long, Line> line : lineEntries) {
            if(replacementPolicy.getName().equals(ReplacementPolicy.LFU.getName())) {
                if(min == line.getValue().getFrequency()) {
                    keyOfTheLineToBeReplaced = line.getKey();
                }
            } else {
                if(min == line.getValue().getRecency()) {
                    keyOfTheLineToBeReplaced = line.getKey();
                }
            }
        }

        return lineMap.remove(keyOfTheLineToBeReplaced);
    }


    @Override
    public String toString() {
        return "CacheSet{" +
                "lineMap=" + lineMap +
                '}';
    }
}
