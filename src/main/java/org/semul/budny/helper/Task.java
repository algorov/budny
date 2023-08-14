package org.semul.budny.helper;

import org.semul.budny.account.Account;
import org.semul.budny.event.Intention;
import org.semul.budny.manager.Manager;

import java.util.Objects;

public class Task extends Thread {
    public static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Task.class);
    public static volatile int taskCount = 0;
    private Manager manager;
    private Account account;
    private Intention intent;
    private int countdown;

    public static Task getInstance(Manager manager, Account account, Intention intent, int countdown) {
        Task task = new Task(manager, account, intent, countdown);
        TasksController.tasks.add(task);

        return task;
    }

    private Task(Manager manager, Account account, Intention intent, int countdown) {
        logger.info("Initialization...");

        this.manager = manager;
        this.account = account;
        this.intent = intent;
        this.countdown = countdown;

        logger.info("Done.");
    }

    @Override
    public void run() {
        try {
            this.countdown = Countdown.getCorrectTime(this.countdown, 0.05F);
            logger.info("Signal to the manager about '" + intent + "' in " + this.countdown / 1000 + " seconds");
            taskCount++;

            Thread.sleep(this.countdown);

            if (account.isLive()) {
                logger.info("Signal to the manager about '" + intent + '.');

                if (Objects.requireNonNull(intent) == Intention.EMPLOY) {
                    manager.getJob(account);
                }
            }

            taskCount--;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            clear();
        }
    }

    private void clear() {
        logger.info("Interrupt task...");

        this.manager = null;
        this.account = null;
        this.intent = null;

        logger.info("Done");
    }
}
