package projectfolder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Runs the DINO chatbot and manages the user's tasks.
 */
public class DINO {
    private static final String TODO_PREFIX = "todo ";
    private static final String DEADLINE_PREFIX = "deadline ";
    private static final String EVENT_PREFIX = "event ";
    private static final String MARK_PREFIX = "mark ";
    private static final String UNMARK_PREFIX = "unmark ";
    private static final String DELETE_PREFIX = "delete ";

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
        ArrayList<Task> tasks = loadTasks();

        printGreeting();

        while (true) {
            String input = scanner.nextLine();

            if (input.equals("bye")) {
                printGoodbye();
                break;
            }

            try {
                processCommand(input, tasks);
            } catch (DinoException e) {
                System.out.println(e.getMessage());
            }
        }

        scanner.close();
    }

    private static void processCommand(String input, ArrayList<Task> tasks)
            throws DinoException {
        if (input.equals("list")) {
            listTasks(tasks);
            return;
        }

        if (input.equals("mark") || input.startsWith(MARK_PREFIX)) {
            markTask(tasks, input);
            saveTasks(tasks);
            return;
        }

        if (input.equals("unmark") || input.startsWith(UNMARK_PREFIX)) {
            unmarkTask(tasks, input);
            saveTasks(tasks);
            return;
        }

        if (input.equals("delete") || input.startsWith(DELETE_PREFIX)) {
            deleteTask(tasks, input);
            saveTasks(tasks);
            return;
        }

        if (input.equals("todo") || input.startsWith(TODO_PREFIX)) {
            addTask(tasks, createTodo(input));
            saveTasks(tasks);
            return;
        }

        if (input.equals("deadline") || input.startsWith(DEADLINE_PREFIX)) {
            addTask(tasks, createDeadline(input));
            saveTasks(tasks);
            return;
        }

        if (input.equals("event") || input.startsWith(EVENT_PREFIX)) {
            addTask(tasks, createEvent(input));
            saveTasks(tasks);
            return;
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

    private static void markTask(ArrayList<Task> tasks, String input)
            throws DinoException {
        int taskIndex = getTaskIndex(
                input, MARK_PREFIX, tasks.size(), "mark");

        tasks.get(taskIndex).markAsDone();

        System.out.println("Nice! I've marked this task as done:");
        System.out.println(tasks.get(taskIndex));
    }

    private static void unmarkTask(ArrayList<Task> tasks, String input)
            throws DinoException {
        int taskIndex = getTaskIndex(
                input, UNMARK_PREFIX, tasks.size(), "unmark");

        tasks.get(taskIndex).markAsNotDone();

        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println(tasks.get(taskIndex));
    }

    private static void deleteTask(ArrayList<Task> tasks, String input)
            throws DinoException {
        int taskIndex = getTaskIndex(
                input, DELETE_PREFIX, tasks.size(), "delete");

        Task removedTask = tasks.remove(taskIndex);

        System.out.println("Noted. I've removed this task:");
        System.out.println("  " + removedTask);
        System.out.println(
                "Now you have " + tasks.size() + " tasks in the list."
        );
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

    private static void addTask(ArrayList<Task> tasks, Task task) {
        tasks.add(task);

        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println(
                "Now you have " + tasks.size() + " tasks in the list."
        );
    }

    private static void listTasks(ArrayList<Task> tasks) {
        System.out.println("Here are the tasks in your list:");

        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + "." + tasks.get(i));
        }
    }

    private static void saveTasks(ArrayList<Task> tasks)
            throws DinoException {
        File directory = new File(DATA_DIRECTORY);

        if (!directory.exists() && !directory.mkdirs()) {
            throw new DinoException(
                    "OOPS!!! I could not create the data folder."
            );
        }

        File dataFile = new File(directory, DATA_FILE);

        try (FileWriter writer = new FileWriter(dataFile)) {
            for (Task task : tasks) {
                writer.write(convertTaskToData(task));
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

    private static ArrayList<Task> loadTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        File dataFile = new File(
                new File(DATA_DIRECTORY), DATA_FILE);

        if (!dataFile.exists()) {
            return tasks;
        }

        try (Scanner fileScanner = new Scanner(dataFile)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                Task task = convertDataToTask(line);

                if (task != null) {
                    tasks.add(task);
                }
            }
        } catch (FileNotFoundException e) {
            return tasks;
        }

        return tasks;
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