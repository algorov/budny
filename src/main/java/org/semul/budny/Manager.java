package org.semul.budny;

import java.util.HashMap;

public class Manager {
    private HashMap<String, Account> accounts = null;

    public Manager() {
        accounts = new HashMap<>();
    }

    // TODO: wrap in decorator.
    public void addAccount(Account account) {
        while (!account.getProcessingFlag()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        if (account.getSignInStatus()) {
            accounts.put(account.getUsername(), account);
        }
    }

    public void printAccounts() {
        for (Account account : accounts.values()) {
            System.out.println("~ " + account.getUsername() + ": " + account.getPassword() + ";");
        }
    }

    // Planning.
}
