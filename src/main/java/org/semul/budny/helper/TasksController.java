package org.semul.budny.helper;

import java.util.LinkedList;
import java.util.List;

public class TasksController extends Thread {
    public static List<Task> tasks = new LinkedList<>();
    private volatile boolean alive;

    public static TasksController getInstance() {
        TasksController controller = new TasksController();
        controller.start();

        return controller;
    }

    public TasksController() {
        this.alive = true;
    }

    @Override
    public void run() {
        while (alive) {
            tasks.removeIf(task -> task.getState() == State.TERMINATED);

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        for (Task task : tasks) {
            task.interrupt();
        }
    }

    public void halt() {
        this.alive = false;
    }
}
