package projectfolder;

/**
 * Represents a task that occurs between a start and end time.
 */
public class Event extends Task {
    protected String from;
    protected String to;

    /**
     * Creates an event with the given description, start time, and end time.
     *
     * @param description description of the event
     * @param from start time of the event
     * @param to end time of the event
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + from + " to: " + to + ")";
    }
}