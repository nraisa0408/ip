package elora.task;

import java.util.Objects;

/**
 * Represents a task that spans from a start time to an end time.
 */
public class Event extends Task {
    protected String from;
    protected String to;

    /**
     * Creates a new event with the given description, start, and end.
     *
     * @param description What the event is.
     * @param from When the event starts.
     * @param to When the event ends.
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns this event's representation for the save file, as
     * "E | &lt;base task format&gt; | &lt;from&gt; | &lt;to&gt;".
     *
     * @return The save-file line for this event.
     */
    @Override
    public String toSaveFormat() {
        return "E | " + super.toSaveFormat() + " | " + from + " | " + to;
    }

    /**
     * Returns this event as "[E]&lt;base task format&gt; (from:
     * &lt;from&gt; to: &lt;to&gt;)".
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }

    /**
     * Returns true if {@code other} is also an Event with the same
     * description, start, and end, so that adding a second event with
     * identical details can be flagged as a duplicate.
     */
    @Override
    public boolean equals(Object other) {
        if (!super.equals(other)) {
            return false;
        }
        Event that = (Event) other;
        return from.equals(that.from) && to.equals(that.to);
    }

    /**
     * Returns a hash code consistent with {@link #equals(Object)}: equal
     * events (same description, from, and to) always hash the same.
     *
     * @return This event's hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), from, to);
    }
}
