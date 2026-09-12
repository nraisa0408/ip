package elora;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    private final List<String> loadWarnings = new ArrayList<>();

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
     * doesn't exist yet (e.g. on a fresh install) or can't be read (e.g.
     * permission denied). Lines that can't be understood are skipped
     * rather than aborting the whole load; call {@link #getLoadWarnings()}
     * afterwards to see which lines, if any, were skipped.
     *
     * @return The tasks read from the save file, possibly empty.
     */
    public ArrayList<Task> load() {
        loadWarnings.clear();
        ArrayList<Task> tasks = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists() || !file.canRead()) {
            return tasks;
        }
        try {
            Scanner fileScanner = new Scanner(file);
            while (fileScanner.hasNextLine()) {
                String fileLine = fileScanner.nextLine();
                if (fileLine.isBlank()) {
                    continue;
                }
                try {
                    tasks.add(parseTaskFromFileLine(fileLine));
                } catch (EloraException e) {
                    loadWarnings.add(fileLine);
                }
            }
            fileScanner.close();
        } catch (FileNotFoundException e) {
            // File existed a moment ago (just checked) but is gone now; treat as no data yet.
        }
        return tasks;
    }

    /**
     * Returns the save-file lines skipped by the most recent {@link #load()}
     * call because they couldn't be understood, so the caller can warn the
     * user instead of silently losing that data.
     *
     * @return The skipped lines, in file order, possibly empty.
     */
    public List<String> getLoadWarnings() {
        return Collections.unmodifiableList(loadWarnings);
    }

    /**
     * Writes the given tasks to the save file, overwriting any previous
     * content. Creates the parent folder first if it doesn't exist yet.
     *
     * @param tasks The current tasks to persist.
     * @throws EloraException If the file can't be written to, e.g.
     *     because its folder couldn't be created or permission was denied.
     */
    public void save(ArrayList<Task> tasks) throws EloraException {
        try {
            File file = new File(filePath);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists() && !parentDir.mkdirs()) {
                throw new EloraException(
                        "Hold on - I couldn't create the folder to save your tasks in: " + parentDir);
            }
            FileWriter writer = new FileWriter(file);
            for (Task task : tasks) {
                writer.write(task.toSaveFormat() + System.lineSeparator());
            }
            writer.close();
        } catch (IOException e) {
            throw new EloraException("Hold on - I couldn't save your tasks to disk: " + e.getMessage());
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
