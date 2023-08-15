package org.semul.budny.helper;

import java.util.ArrayList;
import java.util.List;

public class TasksController extends Thread implements Controller<Task>{
    public static List<Task> tasks = new ArrayList<>();

    public static void startThread() {
        TasksController controller = new TasksController();
        controller.start();
        ThreadsController.pool.add(controller);
    }

    public static void add(Task task) {
        tasks.add(task);
    }

    public static int size() {
        return tasks.size();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            tasks.removeIf(task -> task.getState() == State.TERMINATED);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        quit();
    }

    @Override
    public void close() {
        this.interrupt();
    }

    private void quit() {
        for (Task task : tasks) {
            task.interrupt();
        }
    }
}
