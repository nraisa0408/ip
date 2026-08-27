package elora;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

import elora.task.Task;
import elora.task.TaskList;

/**
 * Deals with all interaction with the user: reading input and printing
 * output. This is the only class that touches System.in/System.out
 * directly.
 */
public class Ui {
    private static final String LOGO = " _____ _\n"
            + "| ____| | ___  _ __ __ _\n"
            + "|  _| | |/ _ \\| '__/ _` |\n"
            + "| |___| | (_) | | | (_| |\n"
            + "|_____|_|\\___/|_|  \\__,_|\n";
    private static final String LINE = "____________________________________________________________";
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy");

    private Scanner scanner;

    /**
     * Creates a Ui that reads user input from standard input.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Prints the startup banner and greeting.
     */
    public void showWelcome() {
        System.out.println(LINE);
        System.out.println(LOGO);
        System.out.println("Hello! I'm Elora - part friend, part philosopher, part guide.");
        System.out.println("What can I do for you?");
        System.out.println(LINE);
    }

    /**
     * Prints the horizontal divider line used to frame each response.
     */
    public void showLine() {
        System.out.println(LINE);
    }

    /**
     * Reads and returns the next full line of user input.
     *
     * @return The line of input entered by the user.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Prints the farewell message shown when the user exits.
     */
    public void showGoodbye() {
        System.out.println("Bye for now, friend. Until our paths cross again!");
    }

    /**
     * Prints an error message to the user.
     *
     * @param message The error message to display.
     */
    public void showError(String message) {
        System.out.println(message);
    }

    /**
     * Prints confirmation that a task was added.
     *
     * @param task The task that was added.
     * @param totalCount The total number of tasks now in the list.
     */
    public void showTaskAdded(Task task, int totalCount) {
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + totalCount + " tasks in the list.");
    }

    /**
     * Prints confirmation that a task was marked as done.
     *
     * @param task The task that was marked.
     */
    public void showTaskMarked(Task task) {
        System.out.println("Nice! I've marked this task as done:");
        System.out.println("  " + task);
    }

    /**
     * Prints confirmation that a task was marked as not done.
     *
     * @param task The task that was unmarked.
     */
    public void showTaskUnmarked(Task task) {
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println("  " + task);
    }

    /**
     * Prints confirmation that a task was deleted.
     *
     * @param task The task that was removed.
     * @param totalCount The total number of tasks remaining in the list.
     */
    public void showTaskDeleted(Task task, int totalCount) {
        System.out.println("Noted. I've removed this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + totalCount + " tasks in the list.");
    }

    /**
     * Prints every task in the given list, numbered from 1.
     *
     * @param tasks The task list to display.
     */
    public void showTaskList(TaskList tasks) {
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Prints the tasks occurring on a given date, or a message saying
     * there are none.
     *
     * @param date The date being queried.
     * @param matches The tasks that occur on that date.
     */
    public void showTasksOnDate(LocalDate date, List<Task> matches) {
        System.out.println("Here's what's happening on " + date.format(DISPLAY_DATE_FORMAT) + ":");
        if (matches.isEmpty()) {
            System.out.println("  Nothing on your list for that day.");
        } else {
            for (Task task : matches) {
                System.out.println("  " + task);
            }
        }
    }

    /**
     * Closes the scanner reading from standard input. Should be called
     * once, when the application is exiting.
     */
    public void closeScanner() {
        scanner.close();
    }
}
