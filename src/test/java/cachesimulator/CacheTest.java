package cachesimulator;

import java.io.IOException;
import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import simulator.cacheoperations.Cache;
import simulator.cacheoperations.CacheSet;
import simulator.exception.IncorrectInputException;
import simulator.model.CacheParameters;
import simulator.model.Command;
import simulator.model.ReplacementPolicy;
import simulator.model.WriteAlgorithm;
import simulator.util.FileHandler;

/**
 * @since 5/30/2021
 */
public class CacheTest {

    private static int numSets;
    private static int lineSize;
    private static int numWarmUp;
    private static int numAccess;
    private static int numWays;
    private static ReplacementPolicy replacementPolicy;
    private static WriteAlgorithm writeAlgorithm;
    private final String HEX_ADDRESS = "bfede22c";
    private final Long EXPECTED_TAG = 201252386L;

    @BeforeClass
    public static void initParams() throws IOException {
        Map<String, Object> params = FileHandler.readParams("test1");
        numSets = Integer.parseInt(String.valueOf(params.get("num_sets")));
        lineSize = Integer.parseInt(String.valueOf(params.get("line_size")));
        replacementPolicy = ReplacementPolicy.fromName(String.valueOf(params.get("replacement_policy")));
        writeAlgorithm = WriteAlgorithm.fromName(String.valueOf(params.get("write_policy")));
        numWarmUp = Integer.parseInt(String.valueOf(params.get("num_warmup")));
        numAccess = Integer.parseInt(String.valueOf(params.get("num_access")));
        numWays = Integer.parseInt(String.valueOf(params.get("num_ways")));
    }

    @Test
    public void testGenerateTag() {
        Cache cache = new Cache(numSets, lineSize, replacementPolicy, writeAlgorithm);

        long generatedTag = cache.generateTag(HEX_ADDRESS);

        Assert.assertEquals(EXPECTED_TAG.longValue(), generatedTag);
    }

    @Test
    public void testGenerateTagNullHexAddress() {
        Cache cache = new Cache(numSets, lineSize, replacementPolicy, writeAlgorithm);

        IncorrectInputException incorrectInputException = Assert.assertThrows(IncorrectInputException.class, () -> cache.generateTag(null));

        Assert.assertEquals("Hex address null is incorrect. Please provide 16 bit hex address",
                incorrectInputException.getMessage());
    }

    @Test
    public void testGenerateTagEmptyHexAddress() {
        Cache cache = new Cache(numSets, lineSize, replacementPolicy, writeAlgorithm);

        IncorrectInputException incorrectInputException = Assert.assertThrows(IncorrectInputException.class, () -> cache.generateTag(""));

        Assert.assertEquals("Hex address is incorrect. Please provide 16 bit hex address",
                incorrectInputException.getMessage());
    }

    @Test
    public void testInsertInCache() {
        Cache cache = new Cache(numSets, lineSize, replacementPolicy, writeAlgorithm);

        cache.insertInCache(HEX_ADDRESS, numWays, Command.LOAD);

        int setIndex = CacheParameters.INSTANCE.getSetIndex();
        CacheSet insertedSet = cache.getCacheSets().get(setIndex);

        Assert.assertNotNull(insertedSet);
    }

}
