package org.semul.budny.menu;

import org.semul.budny.Budny;
import org.semul.budny.controller.ThreadsController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Menu extends Thread {
    public static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Menu.class);
    private final Budny app;
    private final BufferedReader reader;
    private boolean flag;

    public static void getInstance(Budny app) {
        Menu menu = new Menu(app);
        menu.start();
        ThreadsController.pool.add(menu);
    }

    public Menu(Budny app) {
        this.app = app;
        this.reader = new BufferedReader(new InputStreamReader(System.in));
        this.flag = false;
    }

    @Override
    public void run() {
        greetings();

        try {
            while (!Thread.currentThread().isInterrupted()) {
                printMenu();

                switch (reader.readLine()) {
                    case "1" -> {
                        if (!flag) {
                            addAccount();
                        } else {
                            quit();
                        }
                    }
                    case "2" -> {
                        if (!flag) {
                            quit();
                        }
                    }
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (InterruptedException ex) {
            try {
                this.reader.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            Thread.currentThread().interrupt();
        }

        try {
            this.reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void greetings() {
        System.out.println("▬▬▬▬▬▬ WELCOME! ▬▬▬▬▬▬");
    }

    private void printMenu() {
        System.out.println("\nCommand List:");
        System.out.print("[1] ");
        if (!flag) {
            System.out.println("- sign in.");
        } else {
            System.out.println("- exit.");
        }

        if (!flag) {
            System.out.println("[2] - exit.");
        }

        System.out.print("\n[●] -> ");
    }

    private void addAccount() {
        try {
            logger.info("Input data");

            System.out.print("\n▬ Enter the login [●] -> ");
            String login = this.reader.readLine();
            System.out.print("▬ Enter the password [●] -> ");
            String password = this.reader.readLine();
            System.out.println();

            this.app.signIn(login, password);
            this.flag = true;
        } catch (IOException e) {
            logger.error(e);
            throw new RuntimeException(e);
        }
    }

    private void quit() throws InterruptedException {
        logger.info("Exit");
        this.app.complete();
        throw new InterruptedException();
    }
}
