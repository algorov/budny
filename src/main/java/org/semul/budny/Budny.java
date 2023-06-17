package org.semul.budny;


public class Budny {
    public static void main(String[] args) {
        Manager manager = new Manager();

        manager.addAccount(enableNewAccount("AHsgdajsd", " asdasdasd"));
        manager.printAccounts();
    }

    public static Account enableNewAccount(String username, String password) {
        Account account = new Account(username, password);
        account.start();

        return account;
    }
}
