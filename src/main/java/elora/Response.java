package elora;

/**
 * A reply from Elora to a single line of user input, paired with whether
 * it was an error. The GUI uses {@link #isError()} to style error replies
 * differently from normal ones, so mistakes catch the user's eye instead
 * of blending into the conversation.
 */
public final class Response {
    private final String text;
    private final boolean error;

    private Response(String text, boolean error) {
        this.text = text;
        this.error = error;
    }

    /**
     * Creates a normal, successful response.
     *
     * @param text The reply text to show the user.
     * @return The response, marked as not an error.
     */
    public static Response of(String text) {
        return new Response(text, false);
    }

    /**
     * Creates a response reporting that something went wrong.
     *
     * @param text The error message to show the user.
     * @return The response, marked as an error.
     */
    public static Response error(String text) {
        return new Response(text, true);
    }

    public String getText() {
        return text;
    }

    public boolean isError() {
        return error;
    }
}
