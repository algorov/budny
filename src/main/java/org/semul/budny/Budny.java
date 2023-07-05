package org.semul.budny;

import org.semul.budny.account.Account;
import org.semul.budny.manager.Manager;

public class Budny {
    private final Manager manager;

    public Budny() {
        this.manager = new Manager();
    }

    public static void main(String[] args) {
        Budny app = new Budny();
        app.signIn(args[0], args[1]);

        System.out.println(app.manager);

    }

    public void signIn(String username, String password) {
        manager.enableAccount(username, password);
    }

    public void signOut(int accountId) {
        this.manager.disableAccount(this.manager.getAccount(accountId));
    }
}
