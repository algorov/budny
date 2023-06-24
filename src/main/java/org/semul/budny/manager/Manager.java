package org.semul.budny.manager;

import org.semul.budny.account.Account;

import java.util.ArrayList;

public class Manager {
    private final ArrayList<Account> accounts;

    public Manager() {
        this.accounts = new ArrayList<>();
    }

    public Account createAccount(String username, String password) {
        Account account = new Account(username, password);
        account.start();

        return account;
    }

    public void enableAccount(Account account) {
        account.addTask(Account.Intention.LAUNCH);
        synchronization(account);

        if (account.getStatus())
            accounts.add(account);
    }

    public void disableAccount(Account account) {
        account.addTask(Account.Intention.DISABLE);
        synchronization(account);
        accounts.remove(account);
    }

    // *** Intents. ***
    public void getJob(Account account) {
        account.addTask(Account.Intention.EMPLOY);
    }

    public void getInfo(Account account) {
    }

    // Waiting for a response from another (account) thread about the completion of the process.
    private void synchronization(Account account) {
        while (!account.getCompletionStatus()) {
            try {
                Thread.sleep(2333);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        account.changeCompletionStatus();
    }

    public Account getAccount(int index) {
        return this.accounts.get(index);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("\n~~~ Manager ~~~\n");

        for (Account account : accounts) {
            stringBuilder.append(account.toString());
        }

        return stringBuilder.toString();
    }
}

