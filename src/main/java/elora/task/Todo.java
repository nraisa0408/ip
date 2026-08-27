package elora.task;

/**
 * Represents a task with no associated date or time.
 */
public class Todo extends Task {

    /**
     * Creates a new todo with the given description.
     *
     * @param description What needs to be done.
     */
    public Todo(String description) {
        super(description);
    }

    @Override
    public String toSaveFormat() {
        return "T | " + super.toSaveFormat();
    }

    /**
     * Returns this todo as "[T]&lt;base task format&gt;".
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
