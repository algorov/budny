package org.semul.budny;


import org.semul.budny.exception.ExeptionCount;
import org.semul.budny.manager.Manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Budny {
    public static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Budny.class);
    private final Manager manager;

    public Budny() {
        this.manager = Manager.getInstance();
        this.manager.start();
    }

    public static void main(String[] args) {
        logger.info("Initialization");

        Budny app = new Budny();
//        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//
//        try {
//            logger.info("Input data.");
//            System.out.print("Enter the login >>> ");
//            String login = reader.readLine();
//            System.out.print("Enter the password >>> ");
//            String password = reader.readLine();
//            app.signIn(login, password);
//        } catch (IOException e) {
//            logger.error(e);
//            throw new RuntimeException(e);
//        }
        app.signIn(args[0], args[1]);

        while (app.manager.getActiveAccountsCount() > 0 && ExeptionCount.count <= 5) {
        }

        app.manager.halt();
    }

    public void signIn(String username, String password) {
        logger.info("Sign in account");
        manager.addAccount(username, password);
    }

    public void signOut(int accountId) {
        logger.info("Sign out account");
        this.manager.delAccount(this.manager.getAccount(accountId));
    }
}
