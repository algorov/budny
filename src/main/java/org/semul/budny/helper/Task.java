package org.semul.budny.helper;

import org.semul.budny.account.Account;
import org.semul.budny.event.Intention;
import org.semul.budny.manager.Manager;

public class Task extends Thread {
    public static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Task.class);
    public static volatile int taskCount = 0;
    private Manager manager;
    private Account account;
    private Intention intent;
    private int time;

    public static Task getInstance(Manager manager, Account account, Intention intent, int countdown) {
        Task task = new Task(manager, account, intent, countdown);
        task.start();
        TasksController.tasks.add(task);

        return task;
    }

    private Task(Manager manager, Account account, Intention intent, int countdown) {
        logger.info("Initialization...");

        this.manager = manager;
        this.account = account;
        this.intent = intent;
        this.time = countdown;
    }

    @Override
    public void run() {
        try {
            this.time = Countdown.getCorrectTime(this.time, 0.05F);
            logger.info("[WAIT] " + intent + " after " + this.time / 1000 + " sec");
            taskCount++;

            Thread.sleep(this.time);

            if (account.isLive()) {
                logger.info("[RUN] " + intent);

                if (intent == Intention.EMPLOY) {
                    manager.getJob(account);
                }
            }

            taskCount--;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            quit();
        }
    }

    private void quit() {
        logger.info("Quit task");

        this.manager = null;
        this.account = null;
        this.intent = null;
    }
}
