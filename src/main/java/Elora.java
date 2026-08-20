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

        Task[] tasks = new Task[100];
        int taskCount = 0;

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine();
            System.out.println(line);

            if (input.equals("bye")) {
                System.out.println("Bye for now, friend. Until our paths cross again!");
                System.out.println(line);
                break;
            } else if (input.equals("list")) {
                System.out.println("Here are the tasks in your list:");
                for (int i = 0; i < taskCount; i++) {
                    System.out.println((i + 1) + "." + tasks[i]);
                }
            } else if (input.startsWith("mark ")) {
                int index = Integer.parseInt(input.substring(5).trim()) - 1;
                tasks[index].markAsDone();
                System.out.println("Nice! I've marked this task as done:");
                System.out.println("  " + tasks[index]);
            } else if (input.startsWith("unmark ")) {
                int index = Integer.parseInt(input.substring(7).trim()) - 1;
                tasks[index].markAsNotDone();
                System.out.println("OK, I've marked this task as not done yet:");
                System.out.println("  " + tasks[index]);
            } else if (input.startsWith("todo ")) {
                String description = input.substring(5);
                tasks[taskCount] = new Todo(description);
                taskCount++;
                System.out.println("Got it. I've added this task:");
                System.out.println("  " + tasks[taskCount - 1]);
                System.out.println("Now you have " + taskCount + " tasks in the list.");
            } else if (input.startsWith("deadline ")) {
                String remainder = input.substring(9);
                String[] parts = remainder.split(" /by ", 2);
                tasks[taskCount] = new Deadline(parts[0], parts[1]);
                taskCount++;
                System.out.println("Got it. I've added this task:");
                System.out.println("  " + tasks[taskCount - 1]);
                System.out.println("Now you have " + taskCount + " tasks in the list.");
            } else if (input.startsWith("event ")) {
                String remainder = input.substring(6);
                String[] fromParts = remainder.split(" /from ", 2);
                String[] toParts = fromParts[1].split(" /to ", 2);
                tasks[taskCount] = new Event(fromParts[0], toParts[0], toParts[1]);
                taskCount++;
                System.out.println("Got it. I've added this task:");
                System.out.println("  " + tasks[taskCount - 1]);
                System.out.println("Now you have " + taskCount + " tasks in the list.");
            } else {
                System.out.println("I don't recognize that command yet.");
            }

            System.out.println(line);
        }
        scanner.close();
    }
}
