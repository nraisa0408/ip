public class Task {
    protected String description;
    protected boolean isDone;
    protected char type;
    protected String by;
    protected String from;
    protected String to;

    public Task(String description, char type) {
        this.description = description;
        this.isDone = false;
        this.type = type;
    }

    public void markAsDone() {
        this.isDone = true;
    }

    public void markAsNotDone() {
        this.isDone = false;
    }

    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    @Override
    public String toString() {
        String base = "[" + type + "][" + getStatusIcon() + "] " + description;
        if (type == 'D') {
            base += " (by: " + by + ")";
        } else if (type == 'E') {
            base += " (from: " + from + " to: " + to + ")";
        }
        return base;
    }
}
