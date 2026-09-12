package elora;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import elora.task.Deadline;
import elora.task.Event;
import elora.task.Task;
import elora.task.TaskList;
import elora.task.Todo;

/**
 * Entry point and main command loop for the Elora chatbot: a personal
 * assistant that tracks todos, deadlines, and events entered via a
 * simple text command language. Command execution is exposed both as a
 * blocking console loop ({@link #run()}) and as a single-shot
 * {@link #getResponse(String)} call, so the same logic can back either a
 * CLI or a GUI front end.
 */
public class Elora {
    private static final String DATA_FILE_PATH = "data" + File.separator + "elora.txt";
    private static final String BY_DELIMITER = " /by ";
    private static final String FROM_DELIMITER = " /from ";
    private static final String TO_DELIMITER = " /to ";
    private static final String RESERVED_CHARACTER = "|";

    private Storage storage;
    private TaskList tasks;
    private Ui ui;

    /**
     * Creates an Elora instance, loading any previously saved tasks from
     * the given file path.
     *
     * @param filePath Relative path to the save file.
     */
    public Elora(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        tasks = new TaskList(storage.load());
    }

    /**
     * Creates an Elora instance using the default save file location.
     */
    public Elora() {
        this(DATA_FILE_PATH);
    }

    /**
     * Runs the main command loop: greets the user, then repeatedly reads
     * a command, executes it, and prints the result, until the user
     * types "bye".
     */
    public void run() {
        System.out.println(ui.welcomeMessage());
        if (!storage.getLoadWarnings().isEmpty()) {
            System.out.println(ui.corruptedLinesWarning(storage.getLoadWarnings()));
        }
        boolean isExit = false;
        while (!isExit) {
            String input = ui.readCommand();
            ui.showLine();

            try {
                System.out.println(executeCommand(input));
                isExit = isExitCommand(input);
            } catch (EloraException e) {
                System.out.println(e.getMessage());
            }

            ui.showLine();
        }
        ui.closeScanner();
    }

    /**
     * Returns Elora's initial greeting, for a GUI to display as its
     * first message. If the save file had lines that couldn't be
     * understood, a warning listing them is appended so the user knows
     * that data wasn't silently lost.
     *
     * @return The welcome message.
     */
    public String getWelcomeMessage() {
        String welcome = ui.chatWelcomeMessage();
        if (storage.getLoadWarnings().isEmpty()) {
            return welcome;
        }
        return welcome + "\n\n" + ui.corruptedLinesWarning(storage.getLoadWarnings());
    }

    /**
     * Executes a single line of user input and returns Elora's reply,
     * for a GUI to display. Errors are reported as an error
     * {@link Response} rather than thrown, so the GUI can style them
     * differently and catch the user's eye.
     *
     * @param input The full line of user input.
     * @return Elora's reply to that input, marked as an error if one occurred.
     */
    public Response getResponse(String input) {
        try {
            return Response.of(executeCommand(input));
        } catch (EloraException e) {
            return Response.error(e.getMessage());
        }
    }

    /**
     * Returns whether the given input is the command that ends the
     * session, so a GUI knows when to close.
     *
     * @param input The full line of user input.
     * @return true if the input is the "bye" command.
     */
    public boolean isExitCommand(String input) {
        return Parser.parseCommandType(Parser.getCommandWord(input)) == CommandType.BYE;
    }

    /**
     * Parses and executes a single line of user input against the task
     * list, returning Elora's reply.
     *
     * @param input The full line of user input.
     * @return Elora's reply to that input.
     * @throws EloraException If the input can't be understood or acted on.
     */
    private String executeCommand(String input) throws EloraException {
        String commandWord = Parser.getCommandWord(input);
        String arguments = Parser.getArguments(input);
        CommandType commandType = Parser.parseCommandType(commandWord);

        switch (commandType) {
            case BYE:
                return ui.goodbyeMessage();
            case LIST:
                return ui.taskListMessage(tasks);
            case MARK:
                return handleMark(arguments);
            case UNMARK:
                return handleUnmark(arguments);
            case DELETE:
                return handleDelete(arguments);
            case TODO:
                return handleTodo(arguments);
            case DEADLINE:
                return handleDeadline(arguments);
            case EVENT:
                return handleEvent(arguments);
            case ON:
                return handleOn(arguments);
            case FIND:
                return handleFind(arguments);
            case SORT:
                return handleSort();
            default:
                throw new EloraException(
                        "Hold on - I don't recognize that one yet. Could you rephrase it?");
        }
    }

    /**
     * Handles "mark INDEX": marks the given task as done.
     *
     * @param arguments The text after the command word.
     * @return The confirmation message.
     * @throws EloraException If the index is missing, invalid, or out of range.
     */
    private String handleMark(String arguments) throws EloraException {
        int index = parseTaskIndex(arguments,
                "Hold on - which task should I mark done? Give me a number, like mark 2.");
        Task task = tasks.get(index);
        task.markAsDone();
        storage.save(tasks.getAll());
        return ui.taskMarkedMessage(task);
    }

    /**
     * Handles "unmark INDEX": marks the given task as not done.
     *
     * @param arguments The text after the command word.
     * @return The confirmation message.
     * @throws EloraException If the index is missing, invalid, or out of range.
     */
    private String handleUnmark(String arguments) throws EloraException {
        int index = parseTaskIndex(arguments,
                "Hold on - which task should I unmark? Give me a number, like unmark 2.");
        Task task = tasks.get(index);
        task.markAsNotDone();
        storage.save(tasks.getAll());
        return ui.taskUnmarkedMessage(task);
    }

    /**
     * Handles "delete INDEX": removes the given task from the list.
     *
     * @param arguments The text after the command word.
     * @return The confirmation message.
     * @throws EloraException If the index is missing, invalid, or out of range.
     */
    private String handleDelete(String arguments) throws EloraException {
        int index = parseTaskIndex(arguments,
                "Hold on - which task should I delete? Give me a number, like delete 2.");
        Task removed = tasks.remove(index);
        storage.save(tasks.getAll());
        return ui.taskDeletedMessage(removed, tasks.size());
    }

    /**
     * Handles "todo DESCRIPTION": adds a description-only task.
     *
     * @param arguments The text after the command word.
     * @return The confirmation message.
     * @throws EloraException If the description is missing, invalid, or a duplicate.
     */
    private String handleTodo(String arguments) throws EloraException {
        if (arguments.isEmpty()) {
            throw new EloraException(
                    "Hold on - a todo needs a description. What would you like to remember?");
        }
        validateNoReservedCharacter(arguments);
        Task task = new Todo(arguments);
        addTaskIfNotDuplicate(task);
        storage.save(tasks.getAll());
        return ui.taskAddedMessage(task, tasks.size());
    }

    /**
     * Handles "deadline DESCRIPTION /by DATE": adds a task due by a date.
     *
     * @param arguments The text after the command word.
     * @return The confirmation message.
     * @throws EloraException If the description, /by, or date is missing or invalid,
     *     or the resulting task is a duplicate.
     */
    private String handleDeadline(String arguments) throws EloraException {
        if (arguments.isEmpty()) {
            throw new EloraException("Hold on - a deadline needs a description too. What's due?");
        }
        if (!arguments.contains(BY_DELIMITER)) {
            throw new EloraException("Hold on - I'll need a /by time to know when this"
                    + " is due. Try: deadline return book /by Sunday");
        }
        if (countOccurrences(arguments, BY_DELIMITER) > 1) {
            throw new EloraException(
                    "Hold on - I see more than one /by. Please give just one due date.");
        }
        String[] parts = arguments.split(BY_DELIMITER, 2);
        String description = parts[0].trim();
        String byString = parts[1].trim();
        if (description.isEmpty()) {
            throw new EloraException("Hold on - a deadline needs a description too. What's due?");
        }
        if (byString.isEmpty()) {
            throw new EloraException(
                    "Hold on - you've given me a /by, but no actual date. When's this due?");
        }
        validateNoReservedCharacter(description);
        LocalDate by;
        try {
            by = LocalDate.parse(byString);
        } catch (DateTimeParseException e) {
            throw new EloraException(
                    "Hold on - I don't understand that date. Please use yyyy-mm-dd, like 2019-10-15.");
        }
        Task task = new Deadline(description, by);
        addTaskIfNotDuplicate(task);
        storage.save(tasks.getAll());
        return ui.taskAddedMessage(task, tasks.size());
    }

    /**
     * Handles "event DESCRIPTION /from START /to END": adds a task spanning
     * a start and end time.
     *
     * @param arguments The text after the command word.
     * @return The confirmation message.
     * @throws EloraException If the description, /from, or /to is missing or
     *     invalid, the event ends before it starts, or it's a duplicate.
     */
    private String handleEvent(String arguments) throws EloraException {
        if (arguments.isEmpty()) {
            throw new EloraException("Hold on - an event needs a description. What's happening?");
        }
        if (!arguments.contains(FROM_DELIMITER)) {
            throw new EloraException("Hold on - I'll need a /from time to know when this"
                    + " starts. Try: event meeting /from Mon 2pm /to 4pm");
        }
        if (countOccurrences(arguments, FROM_DELIMITER) > 1) {
            throw new EloraException(
                    "Hold on - I see more than one /from. Please give just one start time.");
        }
        String[] fromParts = arguments.split(FROM_DELIMITER, 2);
        String description = fromParts[0].trim();
        if (description.isEmpty()) {
            throw new EloraException("Hold on - an event needs a description. What's happening?");
        }
        if (!fromParts[1].contains(TO_DELIMITER)) {
            throw new EloraException("Hold on - I still need a /to time to know when this ends.");
        }
        if (countOccurrences(fromParts[1], TO_DELIMITER) > 1) {
            throw new EloraException(
                    "Hold on - I see more than one /to. Please give just one end time.");
        }
        String[] toParts = fromParts[1].split(TO_DELIMITER, 2);
        String from = toParts[0].trim();
        String to = toParts[1].trim();
        if (from.isEmpty()) {
            throw new EloraException("Hold on - when does this begin? I'm missing the /from time.");
        }
        if (to.isEmpty()) {
            throw new EloraException("Hold on - and when does it end? I'm missing the /to time.");
        }
        validateNoReservedCharacter(description);
        validateNoReservedCharacter(from);
        validateNoReservedCharacter(to);
        validateChronologicalOrder(from, to);
        Task task = new Event(description, from, to);
        addTaskIfNotDuplicate(task);
        storage.save(tasks.getAll());
        return ui.taskAddedMessage(task, tasks.size());
    }

    /**
     * Handles "on DATE": lists tasks occurring on the given date.
     *
     * @param arguments The text after the command word.
     * @return The formatted tasks-on-date message.
     * @throws EloraException If the date is missing or invalid.
     */
    private String handleOn(String arguments) throws EloraException {
        if (arguments.isEmpty()) {
            throw new EloraException("Hold on - which date? Try: on 2019-10-15");
        }
        LocalDate targetDate;
        try {
            targetDate = LocalDate.parse(arguments);
        } catch (DateTimeParseException e) {
            throw new EloraException(
                    "Hold on - I don't understand that date. Please use yyyy-mm-dd, like 2019-10-15.");
        }
        return ui.tasksOnDateMessage(targetDate, tasks.getTasksOnDate(targetDate));
    }

    /**
     * Handles "find KEYWORD": lists tasks whose description matches the keyword.
     *
     * @param arguments The text after the command word.
     * @return The formatted matching-tasks message.
     * @throws EloraException If the keyword is missing.
     */
    private String handleFind(String arguments) throws EloraException {
        if (arguments.isEmpty()) {
            throw new EloraException("Hold on - what should I search for? Try: find book");
        }
        return ui.matchingTasksMessage(tasks.findTasks(arguments));
    }

    /**
     * Handles "sort": sorts the task list by date and persists the new order.
     *
     * @return The formatted, sorted task list message.
     * @throws EloraException If the sorted list couldn't be saved.
     */
    private String handleSort() throws EloraException {
        tasks.sortByDate();
        storage.save(tasks.getAll());
        return ui.taskListSortedMessage(tasks);
    }

    /**
     * Parses a 1-based task number argument (as used by mark/unmark/delete)
     * into a validated 0-based index into the current task list.
     *
     * @param arguments The raw argument text following the command word.
     * @param missingIndexMessage The error to report if arguments is empty,
     *     since that message differs by command (e.g. "which task should I
     *     mark done?" vs "...delete?").
     * @return The validated, 0-based task index.
     * @throws EloraException If the argument is missing, not a number, or
     *     out of range for the current task list.
     */
    private int parseTaskIndex(String arguments, String missingIndexMessage) throws EloraException {
        if (arguments.isEmpty()) {
            throw new EloraException(missingIndexMessage);
        }
        int index;
        try {
            index = Integer.parseInt(arguments) - 1;
        } catch (NumberFormatException e) {
            throw new EloraException(
                    "Hold on - \"" + arguments + "\" doesn't look like a task number to me.");
        }
        if (index < 0 || index >= tasks.size()) {
            throw new EloraException(
                    "Hold on - I don't see a task numbered " + arguments
                    + ". Take another look at your list?");
        }
        return index;
    }

    /**
     * Rejects text containing the '|' character, since the save file
     * uses " | " to separate fields; letting it through would corrupt
     * the save file and shift fields when the file is next loaded.
     *
     * @param text The user-supplied text to check (a description,
     *     or an event's from/to time).
     * @throws EloraException If the text contains '|'.
     */
    private void validateNoReservedCharacter(String text) throws EloraException {
        if (text.contains(RESERVED_CHARACTER)) {
            throw new EloraException(
                    "Hold on - task details can't contain the '|' character; "
                    + "I use that internally to save your tasks.");
        }
    }

    /**
     * Counts non-overlapping occurrences of a delimiter in text, used to
     * detect a parameter (like /by) given more than once before it's
     * split on.
     *
     * @param text The text to search.
     * @param delimiter The delimiter to count.
     * @return The number of times delimiter occurs in text.
     */
    private static int countOccurrences(String text, String delimiter) {
        return (text.length() - text.replace(delimiter, "").length()) / delimiter.length();
    }

    /**
     * Rejects an event whose start and end are both parseable as plain
     * ISO dates (yyyy-mm-dd) and out of order, i.e. the end date is the
     * same as or before the start date. Event times are otherwise free
     * text (e.g. "Mon 2pm"), so this check only fires when both sides
     * happen to be dates; a free-text time is left unvalidated.
     *
     * @param from The event's start time, as typed by the user.
     * @param to The event's end time, as typed by the user.
     * @throws EloraException If both parse as dates and to isn't after from.
     */
    private void validateChronologicalOrder(String from, String to) throws EloraException {
        LocalDate fromDate = tryParseDate(from);
        LocalDate toDate = tryParseDate(to);
        if (fromDate != null && toDate != null && !toDate.isAfter(fromDate)) {
            throw new EloraException(
                    "Hold on - an event's end date can't be the same as or before its start date.");
        }
    }

    /**
     * Parses text as an ISO date (yyyy-mm-dd), returning null instead of
     * throwing if it isn't one, since event times are usually free text.
     *
     * @param text The text to try to parse.
     * @return The parsed date, or null if text isn't a valid ISO date.
     */
    private static LocalDate tryParseDate(String text) {
        try {
            return LocalDate.parse(text);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Adds a task to the list, unless an equal task (same type,
     * description, and any type-specific fields like a due date) is
     * already present.
     *
     * @param task The task to add.
     * @throws EloraException If an equal task is already in the list.
     */
    private void addTaskIfNotDuplicate(Task task) throws EloraException {
        if (tasks.getAll().contains(task)) {
            throw new EloraException(
                    "Hold on - that exact task is already on your list. No need to add it twice.");
        }
        tasks.add(task);
    }

    /**
     * Launches the console version of the application.
     *
     * @param args Not used.
     */
    public static void main(String[] args) {
        new Elora(DATA_FILE_PATH).run();
    }
}
