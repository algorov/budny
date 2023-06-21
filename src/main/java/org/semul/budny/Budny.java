package org.semul.budny;

public class Budny {
    public static void main(String[] args) {
        Manager manager = new Manager();

        manager.addAccount(addAccount("", ""));
        manager.printAccounts();
    }

    public static Account addAccount(String username, String password) {
        Account account = new Account(username, password);
        account.start();

        return account;
    }
}
