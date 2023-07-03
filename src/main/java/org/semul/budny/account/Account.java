package org.semul.budny.account;

import org.semul.budny.connection.Session;
import org.semul.budny.exception.FailEmployException;
import org.semul.budny.exception.StartSessionException;

import java.util.LinkedList;
import java.util.Queue;

public class Account extends Thread {
    private final String username;
    private final String password;
    private volatile boolean status;
    private volatile boolean completionStatus;
    private Session session;
    private final Queue<Intention> taskQueue;

    public enum Intention {
        DISABLE, EMPLOY
    }

    public Account(String username, String password) {
        this.username = username;
        this.password = password;
        this.status = false;
        this.taskQueue = new LinkedList<>();
        this.completionStatus = false;
        this.session = null;
    }

    @Override
    public void run() {
        launch();

        while (this.status) {
            if (this.taskQueue.size() > 0) {
                switch (this.taskQueue.poll()) {
                    case DISABLE -> disable();
                    case EMPLOY -> employ();
                }
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void addTask(Intention intention) {
        this.taskQueue.add(intention);
    }

    // *** Intentions ***
    private void launch() {
        this.session = new Session(this.username, this.password);

        try {
            this.session.start();
            this.status = true;
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
        this.completionStatus = true;
    }

    private void employ() {
        try {
            this.session.employ();
        } catch (FailEmployException e) {
            System.out.println(e.getMessage());
        }
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
        return "\n● Account:\n▬▬ username: " + this.username + ";\n" + "▬▬ password: " + this.password + ";\n"
                + "▬▬ status: " + (this.status ? "launched" : "stopped") + ";\n";
    }
}
