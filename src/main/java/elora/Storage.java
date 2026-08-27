package elora;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

import elora.task.Deadline;
import elora.task.Event;
import elora.task.Task;
import elora.task.Todo;

/**
 * Deals with loading tasks from the save file and writing tasks back to
 * it. This is the only class that touches the filesystem.
 */
public class Storage {
    private String filePath;

    /**
     * Creates a Storage that reads from and writes to the given file path.
     *
     * @param filePath Relative path to the save file, e.g. "data/elora.txt".
     */
    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads tasks from the save file. Returns an empty list if the file
     * doesn't exist yet, e.g. on a fresh install. Lines that can't be
     * understood are skipped with a warning rather than aborting the
     * whole load.
     *
     * @return The tasks read from the save file, possibly empty.
     */
    public ArrayList<Task> load() {
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

    /**
     * Writes the given tasks to the save file, overwriting any previous
     * content. Creates the parent folder first if it doesn't exist yet.
     *
     * @param tasks The current tasks to persist.
     */
    public void save(ArrayList<Task> tasks) {
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

    /**
     * Parses a single save-file line into the matching Task subclass.
     *
     * @param fileLine One line read from the save file.
     * @return The Task represented by that line.
     * @throws EloraException If the line is malformed or has an
     *     unrecognized type.
     */
    private Task parseTaskFromFileLine(String fileLine) throws EloraException {
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
}
