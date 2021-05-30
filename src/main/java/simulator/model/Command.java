package simulator.model;

import java.util.Arrays;

import simulator.exception.IncorrectNameException;

/**
 * @since 5/25/2021
 * @author kcakmak
 */
public enum Command {
    LOAD("L"),
    STORE("S");

    private final String name;

    Command(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Command fromName(String text) {
        for (Command c : Command.values()) {
            if (c.name.equalsIgnoreCase(text)) {
                return c;
            }
        }
        throw new IncorrectNameException("Command",Arrays.toString(Command.values()), text);
    }
}
