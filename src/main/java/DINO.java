import java.util.Scanner;

public class DINO {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

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
            }

            System.out.println(input);
        }

        scanner.close();
    }
}