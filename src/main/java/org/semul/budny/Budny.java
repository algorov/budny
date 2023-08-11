package org.semul.budny;


import org.semul.budny.exception.ExeptionCount;
import org.semul.budny.manager.Manager;

public class Budny {
    public static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Budny.class);
    private final Manager manager;

    public Budny() {
        this.manager = Manager.getInstance();
        this.manager.start();
    }

    public static void main(String[] args) {
        logger.info("Initialization.");

        Budny app = new Budny();
        app.signIn(args[0], args[1]);

        while (app.manager.getActiveAccountsCount() > 0 && ExeptionCount.count <= 5) {
        }

        app.manager.halt();
    }

    public void signIn(String username, String password) {
        logger.info("Sign in account.");
        manager.addAccount(username, password);
    }

    public void signOut(int accountId) {
        logger.info("Sign out account.");
        this.manager.delAccount(this.manager.getAccount(accountId));
    }
}
