package org.semul.budny;


import org.semul.budny.manager.Manager;

public class Budny {
    public static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Budny.class);
    private final Manager manager;

    public Budny() {
        this.manager = new Manager();
    }

    public static void main(String[] args) {
        logger.info("Initialization.");

        Budny app = new Budny();
        app.signIn(args[0], args[1]);

        System.out.println(app.manager);
        while (true) {

        }
    }

    public void signIn(String username, String password) {
        logger.info("Sign in account.");
        manager.enableAccount(username, password);
    }

    public void signOut(int accountId) {
        logger.info("Sign out account.");
        this.manager.disableAccount(this.manager.getAccount(accountId));
    }
}
