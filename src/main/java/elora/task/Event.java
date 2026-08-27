package elora.task;

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
}
