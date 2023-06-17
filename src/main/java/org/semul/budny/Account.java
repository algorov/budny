package org.semul.budny;


public class Account extends Thread{
    private final String USERNAME;
    private final String PASSWORD;

    private Session session = null;

    // Command queue.


    public Account(String username, String password) {
        this.USERNAME = username;
        this.PASSWORD = password;
    }

    @Override
    public void run() {
        System.out.println("Account {" + this.USERNAME + "} will start working soon.");

        this.session = new Session(this.USERNAME, this.PASSWORD);
        this.session.startSession();

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
}
