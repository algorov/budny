package org.semul.budny.menu;

import org.semul.budny.Budny;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Menu extends Thread{
    public static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Menu.class);
    private Budny app;
    private BufferedReader reader;
    private boolean alive;
    private boolean flag;

    public Menu(Budny app) {
        this.app = app;
        this.reader = new BufferedReader(new InputStreamReader(System.in));
        this.alive = true;
        this.flag = false;
    }

    @Override
    public void run() {
        greetings();

        while (alive) {
            printMenu();

            try {
                System.out.print(">>> ");
                switch (reader.readLine()) {
                    case "1" -> {
                        if (!flag) {
                            addAccount();
                        }
                    }
                    case "2" -> complete();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void greetings() {
        System.out.println("HI!");
    }
    public void printMenu() {
        System.out.println("Список команд:");
        if (!flag) {
            System.out.println("1 - начать");
        }

        System.out.println("2 - выйти");
    }

    public void addAccount() {
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

    private void complete() {
        System.out.println("Bye");
        this.app.complete();
        this.alive = false;
    }
}
