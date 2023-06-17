package org.semul.budny;

import java.util.HashMap;


public class Manager {
    private HashMap<String, Account> accounts = null;


    public Manager() {
        accounts = new HashMap<>();
    }

    public void addAccount(Account account) {
        accounts.put(account.getUsername(), account);
    }

    public void printAccounts() {
        for (Account account : accounts.values()) {
            System.out.println("~ " + account.getUsername() + ": " + account.getPassword() + ";");
        }
    }

    // Planning.
}
