package org.semul.budny;

public class Account extends Thread {
    private final String USERNAME;
    private final String PASSWORD;
    private Session session = null;
    private boolean processingFlag = false;
    private boolean signInStatus;

    // Command queue.


    public Account(String username, String password) {
        this.USERNAME = username;
        this.PASSWORD = password;
    }

    @Override
    public void run() {
        System.out.println("Account {" + this.USERNAME + "} will start working soon.");

        this.session = new Session(this.USERNAME, this.PASSWORD);
        if (this.session.startSession()) {
            this.signInStatus = true;
            System.out.println("Successful login!");
        } else {
            this.signInStatus = false;
            System.out.println("Failed to login!");
        }

        processingFlag = true;

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        this.session.interruptSession();
    }

    public String getUsername() {
        return USERNAME;
    }

    public String getPassword() {
        return PASSWORD;
    }

    public boolean getProcessingFlag() {
        return processingFlag;
    }

    public boolean getSignInStatus() {
        return signInStatus;
    }
}
