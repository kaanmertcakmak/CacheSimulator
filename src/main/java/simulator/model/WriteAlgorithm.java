package simulator.model;

import java.util.Arrays;

import simulator.exception.IncorrectNameException;

/**
 * @since 5/25/2021
 * @author kcakmak
 */
public enum WriteAlgorithm {
    WRITE_BACK("write_back"),
    WRITE_THROUGH("write_through");

    private final String name;


    WriteAlgorithm(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static WriteAlgorithm fromName(String text) {
        for (WriteAlgorithm algorithm : WriteAlgorithm.values()) {
            if (algorithm.name.equalsIgnoreCase(text)) {
                return algorithm;
            }
        }
        throw new IncorrectNameException("Write Algorithm", Arrays.toString(Command.values()), text);
    }

    public boolean isEqualTo(WriteAlgorithm algorithm) {
        return name.equals(algorithm.getName());
    }
}
