package elora;

/**
 * Deals with making sense of the user's raw input: splitting it into a
 * command word and its arguments, and mapping the command word to a
 * CommandType.
 */
public class Parser {

    /**
     * Returns the first word of the given input, treated as the command
     * word.
     *
     * @param input Full line of raw user input.
     * @return The command word, or the entire input if it contains no space.
     */
    public static String getCommandWord(String input) {
        int spaceIndex = input.indexOf(' ');
        return (spaceIndex == -1) ? input : input.substring(0, spaceIndex);
    }

    /**
     * Returns everything after the command word, with leading and
     * trailing whitespace removed.
     *
     * @param input Full line of raw user input.
     * @return The arguments following the command word, or an empty
     *     string if the input contains no space.
     */
    public static String getArguments(String input) {
        int spaceIndex = input.indexOf(' ');
        return (spaceIndex == -1) ? "" : input.substring(spaceIndex + 1).trim();
    }

    /**
     * Returns the CommandType matching the given command word.
     *
     * @param commandWord The first word of the user's input.
     * @return The matching CommandType, or CommandType.UNKNOWN if the
     *     word isn't recognized.
     */
    public static CommandType parseCommandType(String commandWord) {
        switch (commandWord) {
        case "bye":
            return CommandType.BYE;
        case "list":
            return CommandType.LIST;
        case "mark":
            return CommandType.MARK;
        case "unmark":
            return CommandType.UNMARK;
        case "delete":
            return CommandType.DELETE;
        case "todo":
            return CommandType.TODO;
        case "deadline":
            return CommandType.DEADLINE;
        case "event":
            return CommandType.EVENT;
        case "on":
            return CommandType.ON;
        default:
            return CommandType.UNKNOWN;
        }
    }
}
