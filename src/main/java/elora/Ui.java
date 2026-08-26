package elora;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

import elora.task.Task;
import elora.task.TaskList;

/**
 * Deals with all interaction with the user: reading input and printing output.
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

    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    public void showWelcome() {
        System.out.println(LINE);
        System.out.println(LOGO);
        System.out.println("Hello! I'm Elora - part friend, part philosopher, part guide.");
        System.out.println("What can I do for you?");
        System.out.println(LINE);
    }

    public void showLine() {
        System.out.println(LINE);
    }

    public String readCommand() {
        return scanner.nextLine();
    }

    public void showGoodbye() {
        System.out.println("Bye for now, friend. Until our paths cross again!");
    }

    public void showError(String message) {
        System.out.println(message);
    }

    public void showTaskAdded(Task task, int totalCount) {
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + totalCount + " tasks in the list.");
    }

    public void showTaskMarked(Task task) {
        System.out.println("Nice! I've marked this task as done:");
        System.out.println("  " + task);
    }

    public void showTaskUnmarked(Task task) {
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println("  " + task);
    }

    public void showTaskDeleted(Task task, int totalCount) {
        System.out.println("Noted. I've removed this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + totalCount + " tasks in the list.");
    }

    public void showTaskList(TaskList tasks) {
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + "." + tasks.get(i));
        }
    }

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

    public void closeScanner() {
        scanner.close();
    }
}
