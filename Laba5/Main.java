import java.io.*;
import java.time.LocalDateTime;

public class Main {
    private static volatile int counter = 0;
    private static final int MAX_COUNTER = 240;

    public static void main(String[] args) throws InterruptedException {
        Thread task1 = createTaskThread("Thread1", 250);
        Thread task2 = createTaskThread("Thread2", 500);
        Thread task3 = createTaskThread("Thread3", 1000);

        task1.start();
        task2.start();
        task3.start();

        while (counter <= MAX_COUNTER) {
            Thread.sleep(1000);
        }

        interruptTask(task1);
        interruptTask(task2);
        interruptTask(task3);
    }

    private static Thread createTaskThread(String threadName, int delay) {
        return new Thread(() -> {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("D:\\POE\\Lab5_Changed\\file.txt", true))) {
                while (!Thread.interrupted() && counter <= MAX_COUNTER) {
                    LocalDateTime now = LocalDateTime.now();
                    String line = String.format("%s - %s - %d\n", threadName, now.toString(), counter);
                    synchronized (Main.class) {
                        writer.write(line);
                        writer.flush();
                        counter++;
                    }
                    Thread.sleep(delay);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private static void interruptTask(Thread task) {
        if (task != null) {
            task.interrupt();
        }
    }
}
