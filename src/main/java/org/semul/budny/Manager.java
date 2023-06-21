package org.semul.budny;

import java.util.HashMap;

public class Manager {
    private HashMap<String, Account> accounts;

    public Manager() {
        this.accounts = new HashMap<>();
    }

    public void addAccount(Account account) {
        synchronization(account);

        if (account.getStatus()) {
            accounts.put(account.getUsername(), account);
        }
    }

    private void synchronization(Account account) {
        while (!account.getСompletionStatus()) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        account.setCompletionStatus(false);
    }

    public void printAccounts() {
        for (Account account : accounts.values()) {
            System.out.println("~ " + account.getUsername() + ": " + account.getPassword() + ";");
        }
    }

    // Planning.
}

