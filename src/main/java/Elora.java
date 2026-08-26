import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

public class Elora {
    private static final String DATA_FILE_PATH = "data" + File.separator + "elora.txt";
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy");

    public static void main(String[] args) {
        String logo = " _____ _\n"
                + "| ____| | ___  _ __ __ _\n"
                + "|  _| | |/ _ \\| '__/ _` |\n"
                + "| |___| | (_) | | | (_| |\n"
                + "|_____|_|\\___/|_|  \\__,_|\n";
        String line = "____________________________________________________________";

        System.out.println(line);
        System.out.println(logo);
        System.out.println("Hello! I'm Elora - part friend, part philosopher, part guide.");
        System.out.println("What can I do for you?");
        System.out.println(line);

        ArrayList<Task> tasks = loadTasks(DATA_FILE_PATH);
        Scanner scanner = new Scanner(System.in);
        boolean isExit = false;

        while (!isExit) {
            String input = scanner.nextLine();
            System.out.println(line);

            try {
                String commandWord;
                String arguments;
                int spaceIndex = input.indexOf(' ');
                if (spaceIndex == -1) {
                    commandWord = input;
                    arguments = "";
                } else {
                    commandWord = input.substring(0, spaceIndex);
                    arguments = input.substring(spaceIndex + 1).trim();
                }
                CommandType commandType = parseCommandType(commandWord);

                switch (commandType) {
                case BYE: {
                    System.out.println("Bye for now, friend. Until our paths cross again!");
                    isExit = true;
                    break;
                }
                case LIST: {
                    System.out.println("Here are the tasks in your list:");
                    for (int i = 0; i < tasks.size(); i++) {
                        System.out.println((i + 1) + "." + tasks.get(i));
                    }
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
                    tasks.get(index).markAsDone();
                    saveTasks(tasks, DATA_FILE_PATH);
                    System.out.println("Nice! I've marked this task as done:");
                    System.out.println("  " + tasks.get(index));
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
                    tasks.get(index).markAsNotDone();
                    saveTasks(tasks, DATA_FILE_PATH);
                    System.out.println("OK, I've marked this task as not done yet:");
                    System.out.println("  " + tasks.get(index));
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
                    saveTasks(tasks, DATA_FILE_PATH);
                    System.out.println("Noted. I've removed this task:");
                    System.out.println("  " + removed);
                    System.out.println("Now you have " + tasks.size() + " tasks in the list.");
                    break;
                }
                case TODO: {
                    if (arguments.isEmpty()) {
                        throw new EloraException("Hold on - a todo needs a description. What would you like to remember?");
                    }
                    tasks.add(new Todo(arguments));
                    saveTasks(tasks, DATA_FILE_PATH);
                    System.out.println("Got it. I've added this task:");
                    System.out.println("  " + tasks.get(tasks.size() - 1));
                    System.out.println("Now you have " + tasks.size() + " tasks in the list.");
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
                    tasks.add(new Deadline(description, by));
                    saveTasks(tasks, DATA_FILE_PATH);
                    System.out.println("Got it. I've added this task:");
                    System.out.println("  " + tasks.get(tasks.size() - 1));
                    System.out.println("Now you have " + tasks.size() + " tasks in the list.");
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
                    tasks.add(new Event(description, from, to));
                    saveTasks(tasks, DATA_FILE_PATH);
                    System.out.println("Got it. I've added this task:");
                    System.out.println("  " + tasks.get(tasks.size() - 1));
                    System.out.println("Now you have " + tasks.size() + " tasks in the list.");
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
                    System.out.println("Here's what's happening on " + targetDate.format(DISPLAY_DATE_FORMAT) + ":");
                    boolean foundAny = false;
                    for (Task task : tasks) {
                        if (task.isOccurringOn(targetDate)) {
                            System.out.println("  " + task);
                            foundAny = true;
                        }
                    }
                    if (!foundAny) {
                        System.out.println("  Nothing on your list for that day.");
                    }
                    break;
                }
                default:
                    throw new EloraException("Hold on - I don't recognize that one yet. Could you rephrase it?");
                }
            } catch (EloraException e) {
                System.out.println(e.getMessage());
            }

            System.out.println(line);
        }
        scanner.close();
    }

    private static CommandType parseCommandType(String commandWord) {
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

    private static ArrayList<Task> loadTasks(String filePath) {
        ArrayList<Task> tasks = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            return tasks;
        }
        try {
            Scanner fileScanner = new Scanner(file);
            while (fileScanner.hasNextLine()) {
                String fileLine = fileScanner.nextLine();
                if (fileLine.trim().isEmpty()) {
                    continue;
                }
                try {
                    tasks.add(parseTaskFromFileLine(fileLine));
                } catch (EloraException e) {
                    System.out.println("Hold on - I found a save file line I couldn't understand, so I'm skipping it: " + fileLine);
                }
            }
            fileScanner.close();
        } catch (FileNotFoundException e) {
            // File existed a moment ago (just checked) but is gone now; treat as no data yet.
        }
        return tasks;
    }

    private static Task parseTaskFromFileLine(String fileLine) throws EloraException {
        String[] parts = fileLine.split(" \\| ");
        if (parts.length < 3) {
            throw new EloraException("Line has too few fields: " + fileLine);
        }
        String type = parts[0];
        boolean isDone = parts[1].equals("1");
        String description = parts[2];

        Task task;
        switch (type) {
        case "T":
            task = new Todo(description);
            break;
        case "D":
            if (parts.length < 4) {
                throw new EloraException("Deadline line is missing its date: " + fileLine);
            }
            try {
                task = new Deadline(description, LocalDate.parse(parts[3]));
            } catch (DateTimeParseException e) {
                throw new EloraException("Deadline line has an unreadable date: " + fileLine);
            }
            break;
        case "E":
            if (parts.length < 5) {
                throw new EloraException("Event line is missing its from/to times: " + fileLine);
            }
            task = new Event(description, parts[3], parts[4]);
            break;
        default:
            throw new EloraException("Unrecognized task type \"" + type + "\": " + fileLine);
        }
        if (isDone) {
            task.markAsDone();
        }
        return task;
    }

    private static void saveTasks(ArrayList<Task> tasks, String filePath) {
        try {
            File file = new File(filePath);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            FileWriter writer = new FileWriter(file);
            for (Task task : tasks) {
                writer.write(task.toSaveFormat() + System.lineSeparator());
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Hold on - I couldn't save your tasks to disk: " + e.getMessage());
        }
    }
}
