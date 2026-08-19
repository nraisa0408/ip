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
            } else {
                tasks[taskCount] = new Task(input);
                taskCount++;
                System.out.println("added: " + input);
            }

            System.out.println(line);
        }
        scanner.close();
    }
}
