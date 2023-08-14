package org.semul.budny;


import org.semul.budny.exception.ExeptionCount;
import org.semul.budny.helper.Controller;
import org.semul.budny.helper.ThreadsController;
import org.semul.budny.manager.Manager;
import org.semul.budny.menu.Menu;




public class Budny {
    public static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Budny.class);
    private volatile boolean isAlive;
    private final Manager manager;
    private final Menu menu;
    private final Controller controller;


    public Budny() {
        this.isAlive = true;
        this.manager = Manager.getInstance();
        this.manager.start();
        this.menu = Menu.getInstance(this);
        this.controller = ThreadsController.getInstance();
    }

    public static void main(String[] args) {
        logger.info("Initialization");

        Budny app = new Budny();

        while (app.isAlive && ExeptionCount.count < 5) {
        }

        app.controller.halt();
    }

    public void signIn(String username, String password) {
        logger.info("Sign in account");
        manager.addAccount(username, password);
    }

    public void signOut(int accountId) {
        logger.info("Sign out account");
        this.manager.delAccount(this.manager.getAccount(accountId));
    }

    public void complete() {
        this.isAlive = false;
    }
}
