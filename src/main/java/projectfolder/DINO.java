package projectfolder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * Runs the DINO chatbot and manages the user's tasks.
 */
public class DINO {
    private static final int MAX_TASKS = 100;

    private static final String TODO_PREFIX = "todo ";
    private static final String DEADLINE_PREFIX = "deadline ";
    private static final String EVENT_PREFIX = "event ";
    private static final String MARK_PREFIX = "mark ";
    private static final String UNMARK_PREFIX = "unmark ";

    private static final String DATA_DIRECTORY = "data";
    private static final String DATA_FILE = "dino.txt";
    private static final String SEPARATOR = " | ";

    /**
     * Starts DINO and processes commands entered by the user.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Task[] tasks = new Task[MAX_TASKS];
        int taskCount = loadTasks(tasks);

        printGreeting();

        while (true) {
            String input = scanner.nextLine();

            if (input.equals("bye")) {
                printGoodbye();
                break;
            }

            try {
                taskCount = processCommand(input, tasks, taskCount);
            } catch (DinoException e) {
                System.out.println(e.getMessage());
            }
        }

        scanner.close();
    }

    private static int processCommand(String input, Task[] tasks, int taskCount)
            throws DinoException {
        if (input.equals("list")) {
            listTasks(tasks, taskCount);
            return taskCount;
        }

        if (input.equals("mark") || input.startsWith(MARK_PREFIX)) {
            markTask(tasks, taskCount, input);
            saveTasks(tasks, taskCount);
            return taskCount;
        }

        if (input.equals("unmark") || input.startsWith(UNMARK_PREFIX)) {
            unmarkTask(tasks, taskCount, input);
            saveTasks(tasks, taskCount);
            return taskCount;
        }

        if (input.equals("todo") || input.startsWith(TODO_PREFIX)) {
            checkTaskCapacity(taskCount);
            Task task = createTodo(input);
            taskCount = addTask(tasks, taskCount, task);
            saveTasks(tasks, taskCount);
            return taskCount;
        }

        if (input.equals("deadline") || input.startsWith(DEADLINE_PREFIX)) {
            checkTaskCapacity(taskCount);
            Task task = createDeadline(input);
            taskCount = addTask(tasks, taskCount, task);
            saveTasks(tasks, taskCount);
            return taskCount;
        }

        if (input.equals("event") || input.startsWith(EVENT_PREFIX)) {
            checkTaskCapacity(taskCount);
            Task task = createEvent(input);
            taskCount = addTask(tasks, taskCount, task);
            saveTasks(tasks, taskCount);
            return taskCount;
        }

        throw new DinoException(
                "OOPS!!! I don't know what that command means."
        );
    }

    private static Todo createTodo(String input) throws DinoException {
        if (input.equals("todo")) {
            throw new DinoException(
                    "OOPS!!! Please give your todo a description."
            );
        }

        String description = input.substring(TODO_PREFIX.length()).trim();

        if (description.isEmpty()) {
            throw new DinoException(
                    "OOPS!!! Please give your todo a description."
            );
        }

        return new Todo(description);
    }

    private static Deadline createDeadline(String input)
            throws DinoException {
        if (input.equals("deadline")) {
            throw new DinoException(
                    "OOPS!!! A deadline needs a description and /by time."
            );
        }

        String taskDetails = input.substring(DEADLINE_PREFIX.length());
        int byIndex = taskDetails.indexOf(" /by ");

        if (byIndex < 0) {
            throw new DinoException(
                    "OOPS!!! A deadline must contain /by."
            );
        }

        String description = taskDetails.substring(0, byIndex).trim();
        String by = taskDetails.substring(
                byIndex + " /by ".length()).trim();

        if (description.isEmpty()) {
            throw new DinoException(
                    "OOPS!!! Please give your deadline a description."
            );
        }

        if (by.isEmpty()) {
            throw new DinoException(
                    "OOPS!!! Please give your deadline a /by value."
            );
        }

        return new Deadline(description, by);
    }

    private static Event createEvent(String input) throws DinoException {
        if (input.equals("event")) {
            throw new DinoException(
                    "OOPS!!! An event needs a description, /from, and /to."
            );
        }

        String taskDetails = input.substring(EVENT_PREFIX.length());
        int fromIndex = taskDetails.indexOf(" /from ");
        int toIndex = taskDetails.indexOf(" /to ");

        if (fromIndex < 0 || toIndex < 0 || toIndex <= fromIndex) {
            throw new DinoException(
                    "OOPS!!! An event must contain /from followed by /to."
            );
        }

        String description = taskDetails.substring(0, fromIndex).trim();
        String from = taskDetails.substring(
                fromIndex + " /from ".length(), toIndex).trim();
        String to = taskDetails.substring(
                toIndex + " /to ".length()).trim();

        if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
            throw new DinoException(
                    "OOPS!!! Please complete all parts of your event."
            );
        }

        return new Event(description, from, to);
    }

    private static void markTask(Task[] tasks, int taskCount, String input)
            throws DinoException {
        int taskIndex = getTaskIndex(
                input, MARK_PREFIX, taskCount, "mark");

        tasks[taskIndex].markAsDone();

        System.out.println("Nice! I've marked this task as done:");
        System.out.println(tasks[taskIndex]);
    }

    private static void unmarkTask(Task[] tasks, int taskCount, String input)
            throws DinoException {
        int taskIndex = getTaskIndex(
                input, UNMARK_PREFIX, taskCount, "unmark");

        tasks[taskIndex].markAsNotDone();

        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println(tasks[taskIndex]);
    }

    private static int getTaskIndex(String input, String prefix,
                                    int taskCount, String command)
            throws DinoException {
        if (input.equals(command)) {
            throw new DinoException(
                    "OOPS!!! Please specify a task number."
            );
        }

        String taskNumberText = input.substring(prefix.length()).trim();

        try {
            int taskNumber = Integer.parseInt(taskNumberText);

            if (taskNumber < 1 || taskNumber > taskCount) {
                throw new DinoException(
                        "OOPS!!! That task number does not exist."
                );
            }

            return taskNumber - 1;
        } catch (NumberFormatException e) {
            throw new DinoException(
                    "OOPS!!! The task number must be a number."
            );
        }
    }

    private static int addTask(Task[] tasks, int taskCount, Task task) {
        tasks[taskCount] = task;
        taskCount++;

        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println(
                "Now you have " + taskCount + " tasks in the list."
        );

        return taskCount;
    }

    private static void checkTaskCapacity(int taskCount)
            throws DinoException {
        if (taskCount >= MAX_TASKS) {
            throw new DinoException(
                    "OOPS!!! Your task list is full."
            );
        }
    }

    private static void listTasks(Task[] tasks, int taskCount) {
        System.out.println("Here are the tasks in your list:");

        for (int i = 0; i < taskCount; i++) {
            System.out.println((i + 1) + "." + tasks[i]);
        }
    }

    private static void saveTasks(Task[] tasks, int taskCount)
            throws DinoException {
        File directory = new File(DATA_DIRECTORY);

        if (!directory.exists() && !directory.mkdirs()) {
            throw new DinoException(
                    "OOPS!!! I could not create the data folder."
            );
        }

        File dataFile = new File(directory, DATA_FILE);

        try (FileWriter writer = new FileWriter(dataFile)) {
            for (int i = 0; i < taskCount; i++) {
                writer.write(convertTaskToData(tasks[i]));
                writer.write(System.lineSeparator());
            }
        } catch (IOException e) {
            throw new DinoException(
                    "OOPS!!! I could not save your tasks."
            );
        }
    }

    private static String convertTaskToData(Task task) {
        String status = task.isDone ? "1" : "0";

        if (task instanceof Todo) {
            return "T" + SEPARATOR
                    + status + SEPARATOR
                    + task.description;
        }

        if (task instanceof Deadline) {
            Deadline deadline = (Deadline) task;
            return "D" + SEPARATOR
                    + status + SEPARATOR
                    + deadline.description + SEPARATOR
                    + deadline.by;
        }

        Event event = (Event) task;
        return "E" + SEPARATOR
                + status + SEPARATOR
                + event.description + SEPARATOR
                + event.from + SEPARATOR
                + event.to;
    }

    private static int loadTasks(Task[] tasks) {
        File dataFile = new File(
                new File(DATA_DIRECTORY), DATA_FILE);

        if (!dataFile.exists()) {
            return 0;
        }

        int taskCount = 0;

        try (Scanner fileScanner = new Scanner(dataFile)) {
            while (fileScanner.hasNextLine() && taskCount < MAX_TASKS) {
                String line = fileScanner.nextLine();
                Task task = convertDataToTask(line);

                if (task != null) {
                    tasks[taskCount] = task;
                    taskCount++;
                }
            }
        } catch (FileNotFoundException e) {
            return 0;
        }

        return taskCount;
    }

    private static Task convertDataToTask(String line) {
        String[] taskData = line.split("\\s\\|\\s");

        if (taskData.length < 3) {
            return null;
        }

        String taskType = taskData[0];
        boolean isDone = taskData[1].equals("1");
        Task task;

        switch (taskType) {
            case "T":
                task = new Todo(taskData[2]);
                break;
            case "D":
                if (taskData.length < 4) {
                    return null;
                }
                task = new Deadline(taskData[2], taskData[3]);
                break;
            case "E":
                if (taskData.length < 5) {
                    return null;
                }
                task = new Event(taskData[2], taskData[3], taskData[4]);
                break;
            default:
                return null;
        }

        if (isDone) {
            task.markAsDone();
        }

        return task;
    }

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

    private static void printGoodbye() {
        System.out.println("Bye. Hope to see you again soon!");
    }
}