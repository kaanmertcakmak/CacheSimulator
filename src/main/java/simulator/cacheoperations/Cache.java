package simulator.cacheoperations;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import simulator.model.CacheParameters;
import simulator.model.Command;
import simulator.model.ReplacementPolicy;
import simulator.model.WriteAlgorithm;

import static java.lang.System.out;

/**
 * @since 5/25/2021
 * @author kcakmak
 * Cache class that includes main cache operations
 */
public class Cache {

    // Kume sayisi
    private final int numSets;
    // Line boyutu
    private final int lineSize;
    // Degistirme algoritması
    private final ReplacementPolicy replacementPolicy;
    // Yazma algoritması
    private final WriteAlgorithm writeAlgorithm;

    private final Map<Integer, CacheSet> cacheSets;

    public Cache(int numSets, int lineSize, ReplacementPolicy replacementPolicy, WriteAlgorithm writeAlgorithm) {
        this.numSets = numSets;
        this.lineSize = lineSize;
        this.replacementPolicy = replacementPolicy;
        this.writeAlgorithm = writeAlgorithm;
        cacheSets = new HashMap<>(numSets);
    }

    /**
     * Base methods that operates all cache operations
     * @param hexAddress Hex address as an input
     * @param numWays Parameters to decide how many line blocks will be generated
     * @param command Memory command. Can be either LOAD or STORE
     */
    public void insertInCache(String hexAddress, int numWays, Command command) {
        long tag = generateTag(hexAddress);
        int setIndex = CacheParameters.INSTANCE.getSetIndex();
        CacheSet cacheSet = cacheSets.computeIfAbsent(setIndex, c -> {
            CacheSet set = new CacheSet(numWays);
            cacheSets.put(setIndex, set);
            return set;
        });

        cacheSet.insertInSet(tag, this.replacementPolicy, command, writeAlgorithm);
    }

    public void printCache() {
        out.println("******************** Cache Start **************************");
        for(Map.Entry<Integer, CacheSet> cacheSet : cacheSets.entrySet()) {
            out.println("Set " + cacheSet.getKey() + " - " + cacheSet.getValue());
        }
        out.println("******************** Cache End **************************");
    }

    /**
     * Generate tag method.
     * Method accepts hex address as a String. Converts it to the binary.
     * Calculates offset, setIndex and tag bits
     * offset bits are calculated by last (lineSize/log2) bits
     * setBits are calculated by gathering last (numSets/log2) bits after offset bits
     * Example:
     * lineSize = 4
     * numSets = 4
     * Hex Address = bfedde0c
     * Hex Address as Binary = 10111111111011011101111000001100
     * SetIndex bits size => log(numSets)/log(2) = 2
     * Offset bits => log(lineSize)/log(2) = 2 => last 2 bits = 00
     * Set Index bits = 11
     * Tag bits = 1011111111101101110111100000
     * Set index = 3
     * Generated Tag = 201252320
     * and the remain bits are tag bits
     * We put setIndex parameter to the CacheParameters enum so we can use them later
     * @param hexAddress Hex address as an input
     * @return tag Return's tag bit of cache index
     */
    private long generateTag(String hexAddress) {
//        out.println("************************ Generating Tag ******************************");
//        out.println("Hex Address = " + hexAddress);
        String binary = new BigInteger(hexAddress, 16).toString(2);;
//        out.println("Binary of hex address " + binary);
        int bits = (int) Math.round((Math.log(numSets) / Math.log(2)));

 //       out.println("Bits of binary address " + bits);

        int binaryLength = binary.length();
        if(bits > binaryLength) {
            binaryLength = bits;
        }

        long offsetSize = getOffsetSize();

        int startIndexOffset = (int) (binaryLength - offsetSize);

        String offsetBits = binary.substring(startIndexOffset);
 //       out.println("Offset bits " + offsetBits);
        String setBits = binary.substring(startIndexOffset - bits, startIndexOffset);
 //       out.println("Set Index bits " + setBits);
        String tagBits = binary.substring(0, startIndexOffset - bits);
 //       out.println("Tag bits " + tagBits);
        if(offsetBits.length() != 0) {
            int offSet = Integer.parseInt(offsetBits, 2);
            CacheParameters.INSTANCE.setOffset(offSet);
        }
        int setIndex = Integer.parseInt(setBits, 2);
        CacheParameters.INSTANCE.setSetIndex(setIndex);
  //      out.println("Set index = " + setIndex);
        long tag = Long.parseLong(tagBits, 2);
 //       out.println("Generated Tag = " + tag);

        return tag;
    }

    public long getOffsetSize() {
        return Math.round(Math.log(lineSize) / Math.log(2));
    }
}
