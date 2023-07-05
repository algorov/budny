package org.semul.budny.helper;

import org.semul.budny.account.Account;
import org.semul.budny.manager.Manager;

import java.util.Objects;

public class Wave extends Thread {
    private Manager manager;
    private Account account;
    private Account.Intention intent;
    private final int countdown;

    public Wave(Manager manager, Account account, Account.Intention intent, int countdown) {
        this.manager = manager;
        this.account = account;
        this.intent = intent;
        this.countdown = countdown;
    }

    @Override
    public void run() {
        System.out.println(">>> [WAVE] Signal to the manager about '" + intent + "' in " + this.countdown + " seconds.");

        try {
            Thread.sleep(countdown * 1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println(">>> [WAVE] Signal to the manager about '" + intent + '.');

        if (Objects.requireNonNull(intent) == Account.Intention.EMPLOY) {
            manager.getJob(account);
        }

        clear();
    }

    private void clear() {
        this.manager = null;
        this.account = null;
        this.intent = null;
    }
}
