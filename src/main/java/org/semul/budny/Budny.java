package org.semul.budny;


public class Budny {
    public static void main(String[] args) {
        Manager manager = new Manager();

        manager.addAccount(new Account("", ""));
        manager.printAccounts();
    }
}
