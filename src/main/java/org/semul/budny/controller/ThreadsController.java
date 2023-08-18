package org.semul.budny.controller;

import java.util.ArrayList;
import java.util.List;

public class ThreadsController extends Thread implements Controller<Thread> {
    public static Controller controller = null;
    public static List<Thread> pool = new ArrayList<>();

    public static Controller getInstance() {
        ThreadsController temp = new ThreadsController();
        temp.start();
        controller = temp;

        return temp;
    }

    public static void add(Thread thread) {
        pool.add(thread);
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            pool.removeIf(thread -> thread.getState() == State.TERMINATED);
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
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
        for (Thread thread : pool) {
            if (!thread.isInterrupted()) {
                thread.interrupt();
            }
        }
    }
}
