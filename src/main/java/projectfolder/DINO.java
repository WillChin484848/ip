package projectfolder;

import java.util.Scanner;

/**
 * Runs the DINO chatbot and manages the my tasks.
 */
public class DINO {
    private static final int MAX_TASKS = 100;

    /**
     * Starts DINO and processes commands entered by me.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Task[] tasks = new Task[MAX_TASKS];
        int taskCount = 0;

        printGreeting();

        while (true) {
            String input = scanner.nextLine();

            if (input.equals("bye")) {
                printGoodbye();
                break;
            }

            if (input.equals("list")) {
                listTasks(tasks, taskCount);
                continue;
            }

            if (input.startsWith("mark ")) {
                markTask(tasks, input);
                continue;
            }

            if (input.startsWith("unmark ")) {
                unmarkTask(tasks, input);
                continue;
            }

            if (input.startsWith("todo ")) {
                tasks[taskCount] = createTodo(input);
                taskCount++;
                printAddedTask(tasks[taskCount - 1], taskCount);
                continue;
            }

            if (input.startsWith("deadline ")) {
                tasks[taskCount] = createDeadline(input);
                taskCount++;
                printAddedTask(tasks[taskCount - 1], taskCount);
                continue;
            }

            if (input.startsWith("event ")) {
                tasks[taskCount] = createEvent(input);
                taskCount++;
                printAddedTask(tasks[taskCount - 1], taskCount);
            }
        }

        scanner.close();
    }

    /**
     * Prints DINO's greeting.
     */
    private static void printGreeting() {
        System.out.println(" ____    ___   _   _    ___  ");
        System.out.println("|  _ \\  |_ _| | \\ | |  / _ \\ ");
        System.out.println("| | | |  | |  |  \\| | | | | |");
        System.out.println("| |_| |  | |  | |\\  | | |_| |");
        System.out.println("|____/  |___| |_| \\_|  \\___/ ");
        System.out.println();
        System.out.println("Hello! I'm DINO");
        System.out.println("What can I do for you?");
        System.out.println();
    }

    /**
     * Prints DINO's goodbye message.
     */
    private static void printGoodbye() {
        System.out.println("Bye. Hope to see you again soon!");
    }

    /**
     * Displays all tasks currently stored.
     *
     * @param tasks array containing the tasks
     * @param taskCount number of stored tasks
     */
    private static void listTasks(Task[] tasks, int taskCount) {
        System.out.println("Here are the tasks in your list:");

        for (int i = 0; i < taskCount; i++) {
            System.out.println((i + 1) + "." + tasks[i]);
        }
    }

    /**
     * Marks the specified task as done.
     *
     * @param tasks array containing the tasks
     * @param input mark command entered by the user
     */
    private static void markTask(Task[] tasks, String input) {
        int taskIndex = getTaskIndex(input, "mark ");

        tasks[taskIndex].markAsDone();

        System.out.println("Nice! I've marked this task as done:");
        System.out.println(tasks[taskIndex]);
    }

    /**
     * Marks the specified task as not done.
     *
     * @param tasks array containing the tasks
     * @param input unmark command entered by the user
     */
    private static void unmarkTask(Task[] tasks, String input) {
        int taskIndex = getTaskIndex(input, "unmark ");

        tasks[taskIndex].markAsNotDone();

        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println(tasks[taskIndex]);
    }

    /**
     * Gets the array index of a task from a command.
     *
     * @param input command entered by the user
     * @param commandPrefix command prefix to remove
     * @return array index of the task
     */
    private static int getTaskIndex(String input, String commandPrefix) {
        String taskNumberText = input.substring(commandPrefix.length());
        int taskNumber = Integer.parseInt(taskNumberText);
        return taskNumber - 1;
    }

    /**
     * Creates a todo from the user's command.
     *
     * @param input todo command entered by the user
     * @return created todo
     */
    private static Todo createTodo(String input) {
        String description = input.substring("todo ".length());
        return new Todo(description);
    }

    /**
     * Creates a deadline from the user's command.
     *
     * @param input deadline command entered by the user
     * @return created deadline
     */
    private static Deadline createDeadline(String input) {
        String taskDetails = input.substring("deadline ".length());
        int byIndex = taskDetails.indexOf(" /by ");

        String description = taskDetails.substring(0, byIndex);
        String by = taskDetails.substring(byIndex + " /by ".length());

        return new Deadline(description, by);
    }

    /**
     * Creates an event from the user's command.
     *
     * @param input event command entered by the user
     * @return created event
     */
    private static Event createEvent(String input) {
        String taskDetails = input.substring("event ".length());

        int fromIndex = taskDetails.indexOf(" /from ");
        int toIndex = taskDetails.indexOf(" /to ");

        String description = taskDetails.substring(0, fromIndex);
        String from = taskDetails.substring(
                fromIndex + " /from ".length(), toIndex);
        String to = taskDetails.substring(toIndex + " /to ".length());

        return new Event(description, from, to);
    }

    /**
     * Prints information about a newly added task.
     *
     * @param task task that was added
     * @param taskCount total number of tasks
     */
    private static void printAddedTask(Task task, int taskCount) {
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + taskCount + " tasks in the list.");
    }
}