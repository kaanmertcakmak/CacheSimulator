package simulator.model;

import java.util.Arrays;

import simulator.exception.IncorrectNameException;

/**
 * @since 5/25/2021
 * @author kcakmak
 */
public enum ReplacementPolicy {
    LRU("LRU"),
    LFU("LFU");

    private final String name;

    ReplacementPolicy(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static ReplacementPolicy fromName(String text) {
        for (ReplacementPolicy replacementPolicy : ReplacementPolicy.values()) {
            if (replacementPolicy.name.equalsIgnoreCase(text)) {
                return replacementPolicy;
            }
        }
        throw new IncorrectNameException("Replacement Policy", Arrays.toString(Command.values()), text);
    }
}
