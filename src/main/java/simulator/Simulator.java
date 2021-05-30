package simulator;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

import simulator.cacheoperations.Cache;
import simulator.model.CacheParameters;
import simulator.model.Command;
import simulator.model.ReplacementPolicy;
import simulator.model.Statistics;
import simulator.model.WriteAlgorithm;
import simulator.util.FileHandler;

import static java.lang.System.out;

public class Simulator {

    public static void main(String[] args) throws IOException {
        String folderName = "test1";
        if(args.length > 0) {
            folderName = args[0];
            out.println("Simulator will run with the params.txt under " + folderName + " folder");
        } else {
            out.println("No folder is selected so the params under default folder " + folderName + " will be used");
        }
        runSimulatorAndGenerateResults(folderName);
    }

    private static void runSimulatorAndGenerateResults(String inputFolderName) throws IOException {
        Map<String, Object> params = FileHandler.readParams(inputFolderName);
        int numSets = Integer.parseInt(String.valueOf(params.get("num_sets")));
        int lineSize = Integer.parseInt(String.valueOf(params.get("line_size")));
        ReplacementPolicy replacementPolicy = ReplacementPolicy.fromName(String.valueOf(params.get("replacement_policy")));
        WriteAlgorithm writeAlgorithm = WriteAlgorithm.fromName(String.valueOf(params.get("write_policy")));
        int numWarmUp = Integer.parseInt(String.valueOf(params.get("num_warmup")));
        int numAccess = Integer.parseInt(String.valueOf(params.get("num_access")));
        int numWays = Integer.parseInt(String.valueOf(params.get("num_ways")));
        Statistics statistics = Statistics.INSTANCE;
        Cache cache = new Cache(numSets, lineSize, replacementPolicy, writeAlgorithm);
        BufferedReader br = FileHandler.readTraceFile();  //creates a buffering character input stream
        String line;
        int runCount = 0;
        while ((line = br.readLine()) != null) {
            if(runCount == numWarmUp) {
                statistics.setNumAccesses(runCount);
                out.println(statistics.toString());
                FileHandler.generateWarmupResults(inputFolderName, statistics);
                statistics.reset();
            }

            if(runCount == numWarmUp + numAccess) {
                statistics.setNumAccesses(numAccess);
                out.println(statistics.toString());
                FileHandler.generateFinalResults(inputFolderName, statistics);
                break;
            }

            String[] lineSplitted = line.split(" ");
            String hexAddress = lineSplitted[0];
            String command = lineSplitted[lineSplitted.length - 1];
            Command com = Command.fromName(command);
            cache.insertInCache(hexAddress, numWays, com);
            runCount++;
            CacheParameters.INSTANCE.setRecency(runCount);

        }
        br.close();
    }
}
