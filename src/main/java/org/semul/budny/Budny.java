package org.semul.budny;

import org.semul.budny.controller.Controller;
import org.semul.budny.controller.TasksController;
import org.semul.budny.controller.ThreadsController;
import org.semul.budny.exception.ExeptionCount;
import org.semul.budny.manager.Manager;
import org.semul.budny.menu.Menu;

public class Budny {
    public static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Budny.class);
    private final Manager manager;
    private final Controller<Thread> threadsController;
    private volatile boolean isAlive;


    public Budny() {
        this.isAlive = true;
        this.threadsController = ThreadsController.getInstance();
        TasksController.startThread();
        Menu.getInstance(this);
        this.manager = Manager.getInstance();
    }

    public static void main(String[] args) {
        logger.info("Initialization");
        Budny app = new Budny();

        while (app.isAlive && ExeptionCount.count < 5 && app.threadsController.isLive()) {
        }

        if (app.threadsController.isLive()) {
            app.threadsController.close();
        }
    }

    public void signIn(String username, String password) {
        logger.info("Sign in account");
        manager.addAccount(username, password);
    }

    public void complete() {
        this.isAlive = false;
    }
}
