import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Contains the task list and the operations that can be performed on it,
 * such as adding, removing, and querying tasks.
 */
public class TaskList {
    private ArrayList<Task> tasks;

    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    public void add(Task task) {
        tasks.add(task);
    }

    public Task remove(int index) {
        return tasks.remove(index);
    }

    public Task get(int index) {
        return tasks.get(index);
    }

    public int size() {
        return tasks.size();
    }

    public ArrayList<Task> getAll() {
        return tasks;
    }

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
