package elora.task;

import java.time.LocalDate;

/**
 * Represents a task with a description and a done/not-done status.
 * Serves as the base class for Todo, Deadline, and Event.
 */
public class Task {
    protected String description;
    protected boolean isDone;

    /**
     * Creates a new, not-done task with the given description.
     *
     * @param description What the task is about.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Marks this task as done.
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Marks this task as not done.
     */
    public void markAsNotDone() {
        this.isDone = false;
    }

    /**
     * Returns the single-character icon representing this task's status.
     *
     * @return "X" if the task is done, or a single space otherwise.
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    public String getDescription() {
        return description;
    }

    /**
     * Returns whether this task occurs on the given date. A plain Task
     * has no associated date, so this always returns false; subclasses
     * with a meaningful date (e.g. Deadline) override this.
     *
     * @param date The date to check against.
     * @return false, always, for a plain Task.
     */
    public boolean isOccurringOn(LocalDate date) {
        return false;
    }

    /**
     * Returns this task's representation for the save file, as
     * "&lt;0 or 1&gt; | &lt;description&gt;". Subclasses prepend their
     * type letter and append any extra fields of their own.
     *
     * @return The save-file line for this task, without its type prefix.
     */
    public String toSaveFormat() {
        return (isDone ? "1" : "0") + " | " + description;
    }

    /**
     * Returns this task as "[&lt;status icon&gt;] &lt;description&gt;".
     * Subclasses prepend their own type marker to this.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
