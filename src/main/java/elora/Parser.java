package elora;

/**
 * Deals with making sense of the user's raw input: splitting it into a
 * command word and its arguments, and mapping the command word to a
 * CommandType.
 */
public class Parser {
    public static String getCommandWord(String input) {
        int spaceIndex = input.indexOf(' ');
        return (spaceIndex == -1) ? input : input.substring(0, spaceIndex);
    }

    public static String getArguments(String input) {
        int spaceIndex = input.indexOf(' ');
        return (spaceIndex == -1) ? "" : input.substring(spaceIndex + 1).trim();
    }

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
