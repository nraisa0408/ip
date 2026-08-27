package elora;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import elora.task.Deadline;
import elora.task.Event;
import elora.task.Task;
import elora.task.TaskList;
import elora.task.Todo;

public class Elora {
    private static final String DATA_FILE_PATH = "data" + File.separator + "elora.txt";

    private Storage storage;
    private TaskList tasks;
    private Ui ui;

    public Elora(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        tasks = new TaskList(storage.load());
    }

    public void run() {
        ui.showWelcome();
        boolean isExit = false;
        while (!isExit) {
            String input = ui.readCommand();
            ui.showLine();

            try {
                String commandWord = Parser.getCommandWord(input);
                String arguments = Parser.getArguments(input);
                CommandType commandType = Parser.parseCommandType(commandWord);

                switch (commandType) {
                case BYE: {
                    ui.showGoodbye();
                    isExit = true;
                    break;
                }
                case LIST: {
                    ui.showTaskList(tasks);
                    break;
                }
                case MARK: {
                    if (arguments.isEmpty()) {
                        throw new EloraException("Hold on - which task should I mark done? Give me a number, like mark 2.");
                    }
                    int index;
                    try {
                        index = Integer.parseInt(arguments) - 1;
                    } catch (NumberFormatException e) {
                        throw new EloraException("Hold on - \"" + arguments + "\" doesn't look like a task number to me.");
                    }
                    if (index < 0 || index >= tasks.size()) {
                        throw new EloraException("Hold on - I don't see a task numbered " + arguments + ". Take another look at your list?");
                    }
                    Task task = tasks.get(index);
                    task.markAsDone();
                    storage.save(tasks.getAll());
                    ui.showTaskMarked(task);
                    break;
                }
                case UNMARK: {
                    if (arguments.isEmpty()) {
                        throw new EloraException("Hold on - which task should I unmark? Give me a number, like unmark 2.");
                    }
                    int index;
                    try {
                        index = Integer.parseInt(arguments) - 1;
                    } catch (NumberFormatException e) {
                        throw new EloraException("Hold on - \"" + arguments + "\" doesn't look like a task number to me.");
                    }
                    if (index < 0 || index >= tasks.size()) {
                        throw new EloraException("Hold on - I don't see a task numbered " + arguments + ". Take another look at your list?");
                    }
                    Task task = tasks.get(index);
                    task.markAsNotDone();
                    storage.save(tasks.getAll());
                    ui.showTaskUnmarked(task);
                    break;
                }
                case DELETE: {
                    if (arguments.isEmpty()) {
                        throw new EloraException("Hold on - which task should I delete? Give me a number, like delete 2.");
                    }
                    int index;
                    try {
                        index = Integer.parseInt(arguments) - 1;
                    } catch (NumberFormatException e) {
                        throw new EloraException("Hold on - \"" + arguments + "\" doesn't look like a task number to me.");
                    }
                    if (index < 0 || index >= tasks.size()) {
                        throw new EloraException("Hold on - I don't see a task numbered " + arguments + ". Take another look at your list?");
                    }
                    Task removed = tasks.remove(index);
                    storage.save(tasks.getAll());
                    ui.showTaskDeleted(removed, tasks.size());
                    break;
                }
                case TODO: {
                    if (arguments.isEmpty()) {
                        throw new EloraException("Hold on - a todo needs a description. What would you like to remember?");
                    }
                    Task task = new Todo(arguments);
                    tasks.add(task);
                    storage.save(tasks.getAll());
                    ui.showTaskAdded(task, tasks.size());
                    break;
                }
                case DEADLINE: {
                    if (arguments.isEmpty()) {
                        throw new EloraException("Hold on - a deadline needs a description too. What's due?");
                    }
                    if (!arguments.contains(" /by ")) {
                        throw new EloraException("Hold on - I'll need a /by time to know when this is due. Try: deadline return book /by Sunday");
                    }
                    String[] parts = arguments.split(" /by ", 2);
                    String description = parts[0].trim();
                    String byString = parts[1].trim();
                    if (description.isEmpty()) {
                        throw new EloraException("Hold on - a deadline needs a description too. What's due?");
                    }
                    if (byString.isEmpty()) {
                        throw new EloraException("Hold on - you've given me a /by, but no actual date. When's this due?");
                    }
                    LocalDate by;
                    try {
                        by = LocalDate.parse(byString);
                    } catch (DateTimeParseException e) {
                        throw new EloraException("Hold on - I don't understand that date. Please use yyyy-mm-dd, like 2019-10-15.");
                    }
                    Task task = new Deadline(description, by);
                    tasks.add(task);
                    storage.save(tasks.getAll());
                    ui.showTaskAdded(task, tasks.size());
                    break;
                }
                case EVENT: {
                    if (arguments.isEmpty()) {
                        throw new EloraException("Hold on - an event needs a description. What's happening?");
                    }
                    if (!arguments.contains(" /from ")) {
                        throw new EloraException("Hold on - I'll need a /from time to know when this starts. Try: event meeting /from Mon 2pm /to 4pm");
                    }
                    String[] fromParts = arguments.split(" /from ", 2);
                    String description = fromParts[0].trim();
                    if (description.isEmpty()) {
                        throw new EloraException("Hold on - an event needs a description. What's happening?");
                    }
                    if (!fromParts[1].contains(" /to ")) {
                        throw new EloraException("Hold on - I still need a /to time to know when this ends.");
                    }
                    String[] toParts = fromParts[1].split(" /to ", 2);
                    String from = toParts[0].trim();
                    String to = toParts[1].trim();
                    if (from.isEmpty()) {
                        throw new EloraException("Hold on - when does this begin? I'm missing the /from time.");
                    }
                    if (to.isEmpty()) {
                        throw new EloraException("Hold on - and when does it end? I'm missing the /to time.");
                    }
                    Task task = new Event(description, from, to);
                    tasks.add(task);
                    storage.save(tasks.getAll());
                    ui.showTaskAdded(task, tasks.size());
                    break;
                }
                case ON: {
                    if (arguments.isEmpty()) {
                        throw new EloraException("Hold on - which date? Try: on 2019-10-15");
                    }
                    LocalDate targetDate;
                    try {
                        targetDate = LocalDate.parse(arguments);
                    } catch (DateTimeParseException e) {
                        throw new EloraException("Hold on - I don't understand that date. Please use yyyy-mm-dd, like 2019-10-15.");
                    }
                    ui.showTasksOnDate(targetDate, tasks.getTasksOnDate(targetDate));
                    break;
                }
                case FIND: {
                    ui.showMatchingTasks(tasks.findTasks(arguments));
                    break;
                }
                default:
                    throw new EloraException("Hold on - I don't recognize that one yet. Could you rephrase it?");
                }
            } catch (EloraException e) {
                ui.showError(e.getMessage());
            }

            ui.showLine();
        }
        ui.closeScanner();
    }

    public static void main(String[] args) {
        new Elora(DATA_FILE_PATH).run();
    }
}
