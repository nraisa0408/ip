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
     * first message.
     *
     * @return The welcome message.
     */
    public String getWelcomeMessage() {
        return ui.welcomeMessage();
    }

    /**
     * Executes a single line of user input and returns Elora's reply,
     * for a GUI to display. Errors are reported as the returned text
     * rather than thrown.
     *
     * @param input The full line of user input.
     * @return Elora's reply to that input.
     */
    public String getResponse(String input) {
        try {
            return executeCommand(input);
        } catch (EloraException e) {
            return e.getMessage();
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
            case BYE: {
                return ui.goodbyeMessage();
            }
            case LIST: {
                return ui.taskListMessage(tasks);
            }
            case MARK: {
                int index = parseTaskIndex(arguments,
                        "Hold on - which task should I mark done? Give me a number, like mark 2.");
                Task task = tasks.get(index);
                task.markAsDone();
                storage.save(tasks.getAll());
                return ui.taskMarkedMessage(task);
            }
            case UNMARK: {
                int index = parseTaskIndex(arguments,
                        "Hold on - which task should I unmark? Give me a number, like unmark 2.");
                Task task = tasks.get(index);
                task.markAsNotDone();
                storage.save(tasks.getAll());
                return ui.taskUnmarkedMessage(task);
            }
            case DELETE: {
                int index = parseTaskIndex(arguments,
                        "Hold on - which task should I delete? Give me a number, like delete 2.");
                Task removed = tasks.remove(index);
                storage.save(tasks.getAll());
                return ui.taskDeletedMessage(removed, tasks.size());
            }
            case TODO: {
                if (arguments.isEmpty()) {
                    throw new EloraException(
                            "Hold on - a todo needs a description. What would you like to remember?");
                }
                Task task = new Todo(arguments);
                tasks.add(task);
                storage.save(tasks.getAll());
                return ui.taskAddedMessage(task, tasks.size());
            }
            case DEADLINE: {
                if (arguments.isEmpty()) {
                    throw new EloraException(
                            "Hold on - a deadline needs a description too. What's due?");
                }
                if (!arguments.contains(BY_DELIMITER)) {
                    throw new EloraException("Hold on - I'll need a /by time to know when this"
                            + " is due. Try: deadline return book /by Sunday");
                }
                String[] parts = arguments.split(BY_DELIMITER, 2);
                String description = parts[0].trim();
                String byString = parts[1].trim();
                if (description.isEmpty()) {
                    throw new EloraException(
                            "Hold on - a deadline needs a description too. What's due?");
                }
                if (byString.isEmpty()) {
                    throw new EloraException(
                            "Hold on - you've given me a /by, but no actual date. When's this due?");
                }
                LocalDate by;
                try {
                    by = LocalDate.parse(byString);
                } catch (DateTimeParseException e) {
                    throw new EloraException(
                            "Hold on - I don't understand that date. Please use yyyy-mm-dd, like 2019-10-15.");
                }
                Task task = new Deadline(description, by);
                tasks.add(task);
                storage.save(tasks.getAll());
                return ui.taskAddedMessage(task, tasks.size());
            }
            case EVENT: {
                if (arguments.isEmpty()) {
                    throw new EloraException("Hold on - an event needs a description. What's happening?");
                }
                if (!arguments.contains(FROM_DELIMITER)) {
                    throw new EloraException("Hold on - I'll need a /from time to know when this"
                            + " starts. Try: event meeting /from Mon 2pm /to 4pm");
                }
                String[] fromParts = arguments.split(FROM_DELIMITER, 2);
                String description = fromParts[0].trim();
                if (description.isEmpty()) {
                    throw new EloraException("Hold on - an event needs a description. What's happening?");
                }
                if (!fromParts[1].contains(TO_DELIMITER)) {
                    throw new EloraException("Hold on - I still need a /to time to know when this ends.");
                }
                String[] toParts = fromParts[1].split(TO_DELIMITER, 2);
                String from = toParts[0].trim();
                String to = toParts[1].trim();
                if (from.isEmpty()) {
                    throw new EloraException(
                            "Hold on - when does this begin? I'm missing the /from time.");
                }
                if (to.isEmpty()) {
                    throw new EloraException("Hold on - and when does it end? I'm missing the /to time.");
                }
                Task task = new Event(description, from, to);
                tasks.add(task);
                storage.save(tasks.getAll());
                return ui.taskAddedMessage(task, tasks.size());
            }
            case ON: {
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
            case FIND: {
                if (arguments.isEmpty()) {
                    throw new EloraException("Hold on - what should I search for? Try: find book");
                }
                return ui.matchingTasksMessage(tasks.findTasks(arguments));
            }
            case SORT: {
                tasks.sortByDate();
                storage.save(tasks.getAll());
                return ui.taskListSortedMessage(tasks);
            }
            default:
                throw new EloraException(
                        "Hold on - I don't recognize that one yet. Could you rephrase it?");
        }
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
     * Launches the console version of the application.
     *
     * @param args Not used.
     */
    public static void main(String[] args) {
        new Elora(DATA_FILE_PATH).run();
    }
}
