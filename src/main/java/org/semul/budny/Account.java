package org.semul.budny;

import org.semul.budny.exception.StartSessionException;
import org.semul.budny.helper.Session;

public class Account extends Thread {
    private final String username;
    private final String password;
    private boolean status;
    private Session session;
    private volatile boolean completionStatus;

    // Command queue.


    Account(String username, String password) {
        this.username = username;
        this.password = password;
        this.status = false;
        this.session = null;
        this.completionStatus = false;
    }

    @Override
    public void run() {

    }

    public void launch() {
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

    public void disable() {
        if (this.session != null) {
            this.session.interrupt();
            this.session = null;
        }

        this.status = false;
        this.completionStatus = true;
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

    public boolean getСompletionStatus() {
        return this.completionStatus;
    }

    public void setCompletionStatus(boolean value) {
        this.completionStatus = value;
    }
}
