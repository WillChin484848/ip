import java.util.Scanner;

public class DINO {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        String[] tasks = new String[100];
        boolean[] isDone = new boolean[100];
        int taskCount = 0;

        System.out.println(" ____    ___   _   _    ___  ");
        System.out.println("|  _ \\  |_ _| | \\ | |  / _ \\ ");
        System.out.println("| | | |  | |  |  \\| | | | | |");
        System.out.println("| |_| |  | |  | |\\  | | |_| |");
        System.out.println("|____/  |___| |_| \\_|  \\___/ ");

        System.out.println();
        System.out.println("Hello! I'm DINO");
        System.out.println("What can I do for you?");
        System.out.println();

        while (true) {
            String input = scanner.nextLine();

            if (input.equals("bye")) {
                System.out.println("Bye. Hope to see you again soon!");
                break;

            } else if (input.equals("list")) {
                System.out.println("Here are the tasks in your list:");

                for (int i = 0; i < taskCount; i++) {
                    String status = isDone[i] ? "X" : " ";
                    System.out.println((i + 1) + ".[" + status + "] " + tasks[i]);
                }

            } else if (input.startsWith("mark ")) {
                int taskNumber = Integer.parseInt(input.substring(5));
                int index = taskNumber - 1;

                isDone[index] = true;

                System.out.println("Nice! I've marked this task as done:");
                System.out.println("[X] " + tasks[index]);

            } else {
                tasks[taskCount] = input;
                isDone[taskCount] = false;
                taskCount++;

                System.out.println("added: " + input);
            }
        }

        scanner.close();
    }
}