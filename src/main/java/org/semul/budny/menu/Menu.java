package org.semul.budny.menu;

import org.semul.budny.Budny;
import org.semul.budny.controller.ThreadsController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Menu extends Thread {
    public static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Menu.class);
    private Budny app;
    private BufferedReader reader;
    private boolean flag;

    public static Menu getInstance(Budny app) {
        Menu menu = new Menu(app);
        menu.start();
        ThreadsController.pool.add(menu);

        return menu;
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
                System.out.print(">>> ");
                switch (reader.readLine()) {
                    case "1" -> {
                        if (!flag) {
                            addAccount();
                        }
                    }
                    case "2" -> quit();
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
            System.out.println(Thread.currentThread().getName() + " is interrupted!");
            Thread.currentThread().interrupt();
        }

        try {
            this.reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void greetings() {
        System.out.println("HI!");
    }

    private void printMenu() {
        System.out.println("Список команд:");
        if (!flag) {
            System.out.println("1 - начать");
        }

        System.out.println("2 - выйти");
    }

    private void addAccount() {
        try {
            logger.info("Input data.");
            System.out.print("Enter the login >>> ");
            String login = this.reader.readLine();
            System.out.print("Enter the password >>> ");
            String password = this.reader.readLine();
            this.app.signIn(login, password);

            this.flag = true;
            System.out.println("Всё начнется или нет");
        } catch (IOException e) {
            logger.error(e);
            throw new RuntimeException(e);
        }
    }

    private void quit() throws InterruptedException {
        System.out.println("Bye");
        this.app.complete();
        throw new InterruptedException();
    }
}
