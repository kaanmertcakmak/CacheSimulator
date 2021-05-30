//
//                               ASSIA, Inc.
//                     Confidential and Proprietary
//                         ALL RIGHTS RESERVED.
//
//      This software is provided under license and may be used
//      or distributed only in accordance with the terms of
//      such license.
//
package cachesimulator;

import java.io.IOException;
import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import simulator.cacheoperations.Cache;
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

        long expectedTag = 201252386;
        long generatedTag = cache.generateTag(HEX_ADDRESS);

        Assert.assertEquals(expectedTag, generatedTag);
    }
}
