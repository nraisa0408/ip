package elora.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Represents a task that needs to be done by a specific date.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy");

    protected LocalDate by;

    /**
     * Creates a new deadline with the given description and due date.
     *
     * @param description What needs to be done.
     * @param by The date it's due by.
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.by = by;
    }

    public LocalDate getBy() {
        return by;
    }

    /**
     * Returns true if this deadline's due date matches the given date.
     *
     * @param date The date to check against.
     * @return true if this deadline is due on that date.
     */
    @Override
    public boolean isOccurringOn(LocalDate date) {
        return by.equals(date);
    }

    @Override
    public LocalDate getSortDate() {
        return by;
    }

    @Override
    public String toSaveFormat() {
        return "D | " + super.toSaveFormat() + " | " + by;
    }

    /**
     * Returns this deadline as "[D]&lt;base task format&gt; (by:
     * &lt;formatted date&gt;)".
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by.format(DISPLAY_FORMAT) + ")";
    }

    /**
     * Returns true if {@code other} is also a Deadline with the same
     * description and due date, so that adding a second deadline with
     * identical details can be flagged as a duplicate.
     */
    @Override
    public boolean equals(Object other) {
        if (!super.equals(other)) {
            return false;
        }
        Deadline that = (Deadline) other;
        return by.equals(that.by);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), by);
    }
}
