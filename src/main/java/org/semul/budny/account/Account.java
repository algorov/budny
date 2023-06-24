package org.semul.budny.account;

import org.semul.budny.exception.StartSessionException;
import org.semul.budny.connection.Session;

public class Account extends Thread {
    private final String username;
    private final String password;
    private boolean status;
    private volatile boolean completionStatus;
    private Session session;

    // Command queue.


    public Account(String username, String password) {
        this.username = username;
        this.password = password;
        this.status = true;
        this.completionStatus = false;
        this.session = null;
    }

    @Override
    public void run() {
        while (this.status) {

        }
    }

    public void launch() {
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

    public void disable() {
        if (this.session != null) {
            this.session.interrupt();
            this.session = null;
        }

        this.status = false;
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
