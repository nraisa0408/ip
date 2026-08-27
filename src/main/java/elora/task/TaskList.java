package elora.task;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Contains the task list and the operations that can be performed on it,
 * such as adding, removing, and querying tasks.
 */
public class TaskList {
    private ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list backed by the given tasks, typically ones
     * loaded from the save file.
     *
     * @param tasks The initial tasks.
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task The task to add.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes and returns the task at the given index.
     *
     * @param index Zero-based index of the task to remove.
     * @return The task that was removed.
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns the task at the given index.
     *
     * @param index Zero-based index of the task.
     * @return The task at that index.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Returns the number of tasks currently in the list.
     *
     * @return The current task count.
     */
    public int size() {
        return tasks.size();
    }

    public ArrayList<Task> getAll() {
        return tasks;
    }

    /**
     * Returns the tasks that occur on the given date.
     *
     * @param date The date to filter by.
     * @return A new list containing only the matching tasks, possibly empty.
     */
    public ArrayList<Task> getTasksOnDate(LocalDate date) {
        ArrayList<Task> matches = new ArrayList<>();
        for (Task task : tasks) {
            if (task.isOccurringOn(date)) {
                matches.add(task);
            }
        }
        return matches;
    }
}
