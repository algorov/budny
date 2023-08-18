package org.semul.budny.controller;

import org.semul.budny.helper.Task;

import java.util.ArrayList;
import java.util.List;

public class TasksController extends Thread implements Controller<Task> {
    public static List<Task> tasks = new ArrayList<>();

    public static void startThread() {
        TasksController controller = new TasksController();
        controller.start();
    }

    public static void add(Task task) {
        tasks.add(task);
    }

    public static int size() {
        return tasks.size();
    }

    @Override
    public void run() {
        ThreadsController.add(this);

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
    public boolean isLive() {
        return !this.isInterrupted();
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
