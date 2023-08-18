package org.semul.budny;

import org.semul.budny.controller.Controller;
import org.semul.budny.controller.TasksController;
import org.semul.budny.controller.ThreadsController;
import org.semul.budny.exception.ExeptionCount;
import org.semul.budny.manager.Manager;
import org.semul.budny.menu.Menu;

import static org.semul.budny.controller.ThreadsController.controller;

public class Budny {
    public static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Budny.class);
    private final Manager manager;
    private volatile boolean isAlive;


    public Budny(Controller controller) {
        TasksController.startThread();
        Menu.getInstance(this);
        this.manager = Manager.getInstance();
        this.isAlive = true;
    }

    public static void main(String[] args) {
        logger.info("Initialization");
        Budny app = new Budny(ThreadsController.getInstance());

        while (app.isAlive && ExeptionCount.count <= 5 && controller.isLive()) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        controller.close();
    }

    public void signIn(String username, String password) {
        logger.info("Sign in account");
        manager.addAccount(username, password);
    }

    public void complete() {
        this.isAlive = false;
    }
}
