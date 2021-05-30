package simulator.util;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import simulator.model.Statistics;

import static java.lang.System.out;

/**
 * @since 5/27/2021
 * @author kcakmak
 */
public class FileHandler {

    private FileHandler() {

    }

    /**
     * Reads file from src/main/resources folder
     * @param filePath Desired file's path
     * @return BufferedReader as file
     */
    public static BufferedReader readResourceFile(String filePath) {
        InputStream is = FileHandler.class.getClassLoader().getResourceAsStream(filePath);

        return new BufferedReader(new InputStreamReader(is));  //creates a buffering character input stream
    }

    /**
     * Reads trace file from src/main/resources folder
     * @return BufferedReader
     */
    public static BufferedReader readTraceFile() {
        //This should be "aligned.trace" but size became too long so I created new short file with first 5000 lines of aligned.trace
        return readResourceFile("alignedshort.trace");
    }

    /**
     * Reads parameters under src/main/resources/test1(2)/params.txt
     * @param folderName Folder name that includes params.txt, Can be test1 or test2
     * @return parameters as Map.class
     */
    public static Map<String, Object> readParams(String folderName) throws IOException {
        BufferedReader br =  readResourceFile(folderName + "/params.txt");  //creates a buffering character input stream
        String line;
        Map<String, Object> paramsMap = new HashMap<>();
        while ((line = br.readLine()) != null) {
            String[] lineSplitted = line.split("\\s+");
            out.println(Arrays.toString(lineSplitted));
            String param = lineSplitted[0];
            String value = lineSplitted[lineSplitted.length - 1];
            paramsMap.put(param, value);
        }
        br.close();

        return paramsMap;
    }

    public static void generateWarmupResults(String folderName, Statistics statistics) throws IOException {
        writeResultsToFile(folderName + "-Warmup", statistics);
    }

    public static void generateFinalResults(String folderName, Statistics statistics) throws IOException {
        writeResultsToFile(folderName + "-Final", statistics);
    }

    public static void writeResultsToFile(String header, Statistics statistics) throws IOException {
        try (FileWriter fileWriter = new FileWriter(header + "-results.txt")) {
            String results = generateResultString(header, statistics);
            fileWriter.write(results);
        }
    }

    private static String generateResultString(String header, Statistics statistics) {
        return header + " Stats\n" +
                "--------------------------------\n" +
                "Num Accesses: " + statistics.getNumAccesses() + "\n" +
                "Num Hits: " + statistics.getNumHits() + "\n" +
                "Num Misses: " + statistics.getNumMisses() + "\n" +
                "Cache Hit Rate: %" + statistics.getCacheHitRate() + "\n" +
                "Num writes to Main Memory: " + statistics.getWritesCountToTheMainMemory() + "\n";
    }
}
