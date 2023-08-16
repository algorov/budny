package org.semul.budny.helper;

import org.semul.budny.account.Account;
import org.semul.budny.controller.TasksController;
import org.semul.budny.action.Intention;
import org.semul.budny.manager.Manager;

public class Task extends Thread {
    public static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Task.class);
    private Manager manager;
    private Account account;
    private Intention intent;
    private int time;

    public static void startThread(Manager manager, Account account, Intention intent, int countdown) {
        Task task = new Task(manager, account, intent, countdown);
        task.start();
    }

    private Task(Manager manager, Account account, Intention intent, int countdown) {
        logger.info("Initialization...");

        this.manager = manager;
        this.account = account;
        this.intent = intent;
        this.time = countdown > 0 ? countdown + 60 : 0;
    }

    @Override
    public void run() {
        TasksController.tasks.add(this);

        this.time = Countdown.getCorrectTime(this.time, 0.05F);
        logger.info("[WAIT] " + intent + " after " + this.time / 1000 + " sec");

        try {
            Thread.sleep(this.time);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }

        if (account.isLive()) {
            logger.info("[RUN] " + intent);
            if (intent == Intention.EMPLOY) {
                manager.getJob(account);
            }
        }

        quit();
    }

    private void quit() {
        logger.info("Quit task");

        this.manager = null;
        this.account = null;
        this.intent = null;
    }
}
