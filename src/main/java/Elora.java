import java.util.ArrayList;
import java.util.Scanner;

public class Elora {
    public static void main(String[] args) {
        String logo = " _____ _\n"
                + "| ____| | ___  _ __ __ _\n"
                + "|  _| | |/ _ \\| '__/ _` |\n"
                + "| |___| | (_) | | | (_| |\n"
                + "|_____|_|\\___/|_|  \\__,_|\n";
        String line = "____________________________________________________________";

        System.out.println(line);
        System.out.println(logo);
        System.out.println("Hello! I'm Elora - part friend, part philosopher, part guide.");
        System.out.println("What can I do for you?");
        System.out.println(line);

        ArrayList<Task> tasks = new ArrayList<>();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine();
            System.out.println(line);

            try {
                if (input.equals("bye")) {
                    System.out.println("Bye for now, friend. Until our paths cross again!");
                    System.out.println(line);
                    break;
                } else if (input.equals("list")) {
                    System.out.println("Here are the tasks in your list:");
                    for (int i = 0; i < tasks.size(); i++) {
                        System.out.println((i + 1) + "." + tasks.get(i));
                    }
                } else if (input.equals("mark") || input.startsWith("mark ")) {
                    String arg = input.equals("mark") ? "" : input.substring(5).trim();
                    if (arg.isEmpty()) {
                        throw new EloraException("Hold on - which task should I mark done? Give me a number, like mark 2.");
                    }
                    int index;
                    try {
                        index = Integer.parseInt(arg) - 1;
                    } catch (NumberFormatException e) {
                        throw new EloraException("Hold on - \"" + arg + "\" doesn't look like a task number to me.");
                    }
                    if (index < 0 || index >= tasks.size()) {
                        throw new EloraException("Hold on - I don't see a task numbered " + arg + ". Take another look at your list?");
                    }
                    tasks.get(index).markAsDone();
                    System.out.println("Nice! I've marked this task as done:");
                    System.out.println("  " + tasks.get(index));
                } else if (input.equals("unmark") || input.startsWith("unmark ")) {
                    String arg = input.equals("unmark") ? "" : input.substring(7).trim();
                    if (arg.isEmpty()) {
                        throw new EloraException("Hold on - which task should I unmark? Give me a number, like unmark 2.");
                    }
                    int index;
                    try {
                        index = Integer.parseInt(arg) - 1;
                    } catch (NumberFormatException e) {
                        throw new EloraException("Hold on - \"" + arg + "\" doesn't look like a task number to me.");
                    }
                    if (index < 0 || index >= tasks.size()) {
                        throw new EloraException("Hold on - I don't see a task numbered " + arg + ". Take another look at your list?");
                    }
                    tasks.get(index).markAsNotDone();
                    System.out.println("OK, I've marked this task as not done yet:");
                    System.out.println("  " + tasks.get(index));
                } else if (input.equals("delete") || input.startsWith("delete ")) {
                    String arg = input.equals("delete") ? "" : input.substring(7).trim();
                    if (arg.isEmpty()) {
                        throw new EloraException("Hold on - which task should I delete? Give me a number, like delete 2.");
                    }
                    int index;
                    try {
                        index = Integer.parseInt(arg) - 1;
                    } catch (NumberFormatException e) {
                        throw new EloraException("Hold on - \"" + arg + "\" doesn't look like a task number to me.");
                    }
                    if (index < 0 || index >= tasks.size()) {
                        throw new EloraException("Hold on - I don't see a task numbered " + arg + ". Take another look at your list?");
                    }
                    Task removed = tasks.remove(index);
                    System.out.println("Noted. I've removed this task:");
                    System.out.println("  " + removed);
                    System.out.println("Now you have " + tasks.size() + " tasks in the list.");
                } else if (input.equals("todo") || input.startsWith("todo ")) {
                    String description = input.equals("todo") ? "" : input.substring(5).trim();
                    if (description.isEmpty()) {
                        throw new EloraException("Hold on - a todo needs a description. What would you like to remember?");
                    }
                    tasks.add(new Todo(description));
                    System.out.println("Got it. I've added this task:");
                    System.out.println("  " + tasks.get(tasks.size() - 1));
                    System.out.println("Now you have " + tasks.size() + " tasks in the list.");
                } else if (input.equals("deadline") || input.startsWith("deadline ")) {
                    String remainder = input.equals("deadline") ? "" : input.substring(9).trim();
                    if (remainder.isEmpty()) {
                        throw new EloraException("Hold on - a deadline needs a description too. What's due?");
                    }
                    if (!remainder.contains(" /by ")) {
                        throw new EloraException("Hold on - I'll need a /by time to know when this is due. Try: deadline return book /by Sunday");
                    }
                    String[] parts = remainder.split(" /by ", 2);
                    String description = parts[0].trim();
                    String by = parts[1].trim();
                    if (description.isEmpty()) {
                        throw new EloraException("Hold on - a deadline needs a description too. What's due?");
                    }
                    if (by.isEmpty()) {
                        throw new EloraException("Hold on - you've given me a /by, but no actual time. When's this due?");
                    }
                    tasks.add(new Deadline(description, by));
                    System.out.println("Got it. I've added this task:");
                    System.out.println("  " + tasks.get(tasks.size() - 1));
                    System.out.println("Now you have " + tasks.size() + " tasks in the list.");
                } else if (input.equals("event") || input.startsWith("event ")) {
                    String remainder = input.equals("event") ? "" : input.substring(6).trim();
                    if (remainder.isEmpty()) {
                        throw new EloraException("Hold on - an event needs a description. What's happening?");
                    }
                    if (!remainder.contains(" /from ")) {
                        throw new EloraException("Hold on - I'll need a /from time to know when this starts. Try: event meeting /from Mon 2pm /to 4pm");
                    }
                    String[] fromParts = remainder.split(" /from ", 2);
                    String description = fromParts[0].trim();
                    if (description.isEmpty()) {
                        throw new EloraException("Hold on - an event needs a description. What's happening?");
                    }
                    if (!fromParts[1].contains(" /to ")) {
                        throw new EloraException("Hold on - I still need a /to time to know when this ends.");
                    }
                    String[] toParts = fromParts[1].split(" /to ", 2);
                    String from = toParts[0].trim();
                    String to = toParts[1].trim();
                    if (from.isEmpty()) {
                        throw new EloraException("Hold on - when does this begin? I'm missing the /from time.");
                    }
                    if (to.isEmpty()) {
                        throw new EloraException("Hold on - and when does it end? I'm missing the /to time.");
                    }
                    tasks.add(new Event(description, from, to));
                    System.out.println("Got it. I've added this task:");
                    System.out.println("  " + tasks.get(tasks.size() - 1));
                    System.out.println("Now you have " + tasks.size() + " tasks in the list.");
                } else {
                    throw new EloraException("Hold on - I don't recognize that one yet. Could you rephrase it?");
                }
            } catch (EloraException e) {
                System.out.println(e.getMessage());
            }

            System.out.println(line);
        }
        scanner.close();
    }
}
