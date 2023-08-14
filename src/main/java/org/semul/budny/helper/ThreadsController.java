package org.semul.budny.helper;

import java.util.LinkedList;
import java.util.List;

public class ThreadsController extends Thread implements Controller {
    public static List<Thread> threads = new LinkedList<>();

    public static Controller getInstance() {
        ThreadsController controller = new ThreadsController();
        controller.start();

        return controller;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                threads.removeIf(thread -> thread.getState() == State.TERMINATED);
                Thread.sleep(500);
            }
        } catch (InterruptedException ignored) {
            for (Thread thread : threads) {
                thread.interrupt();
            }
            System.out.println("Я тут");
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void halt() {
        this.interrupt();
    }
}
