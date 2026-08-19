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

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine();
            System.out.println(line);
            if (input.equals("bye")) {
                System.out.println("Bye for now, friend. Until our paths cross again!");
                System.out.println(line);
                break;
            }
            System.out.println("I hear you: " + input);
            System.out.println(line);
        }
        scanner.close();
    }
}
