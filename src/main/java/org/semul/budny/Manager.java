package org.semul.budny;

import java.util.HashMap;

public class Manager {
    private HashMap<String, Account> accounts;

    public Manager() {
        accounts = new HashMap<>();
    }

    private void synchronization(Account account) {
        while (!account.getProcessingFlag()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        account.toggleProcessingFlag();
    }

    public void addAccount(Account account) {
        synchronization(account);

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
