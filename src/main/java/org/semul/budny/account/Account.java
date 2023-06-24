package org.semul.budny.account;

import org.semul.budny.exception.StartSessionException;
import org.semul.budny.connection.Session;

import java.util.LinkedList;
import java.util.Queue;

public class Account extends Thread {
    private final String username;
    private final String password;
    private boolean status;
    private volatile boolean completionStatus;
    private Session session;

    private final Queue taskQueue;


    public Account(String username, String password) {
        this.username = username;
        this.password = password;
        this.status = true;
        this.taskQueue = new LinkedList<>();
        this.completionStatus = false;
        this.session = null;
    }

    public enum Intention {
        LAUNCH, DISABLE, EMPLOY
    }

    public void addTask(Intention intention) {
        this.taskQueue.add(intention);
    }

    @Override
    public void run() {
        while (this.status) {
            System.out.println("Ждем-с...");

            if (this.taskQueue.size() > 0) {
                Intention intent = (Intention) this.taskQueue.poll();

                switch (intent) {
                    case LAUNCH:
                        launch();
                        break;
                    case DISABLE:
                        disable();
                        break;
                    case EMPLOY:
                        employ();
                        break;
                }
            }
        }
    }

    // *** Intentions ***
    private void launch() {
        this.session = new Session(this.username, this.password);

        try {
            this.session.start();
        } catch (StartSessionException e) {
            System.out.println(e.getMessage());
            disable();
        } finally {
            this.completionStatus = true;
        }
    }

    private void disable() {
        if (this.session != null) {
            this.session.interrupt();
            this.session = null;
        }

        this.status = false;
    }

    private void employ() {
        System.out.println("~ It's work!");
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public boolean getStatus() {
        return this.status;
    }

    public boolean getCompletionStatus() {
        return this.completionStatus;
    }

    public void changeCompletionStatus() {
        this.completionStatus = !this.completionStatus;
    }

    @Override
    public String toString() {
        return "~ Account {\n▬ username: " + this.username + ";\n" + "▬ password: " + this.password + ";\n"
                + "▬ status: " + this.status + ";}";
    }
}
