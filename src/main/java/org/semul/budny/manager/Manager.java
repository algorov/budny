package org.semul.budny.manager;

import org.semul.budny.account.Account;
import org.semul.budny.account.AccountInfo;
import org.semul.budny.helper.Wave;

import java.util.ArrayList;

public class Manager {
    private final ArrayList<Account> accounts;

    public Manager() {
        this.accounts = new ArrayList<>();
    }

    public void enableAccount(String username, String password) {
        System.out.println(">>> [MANAGER] Enable account...");
        Account account = new Account(this, username, password);
        account.start();
        synchronization(account);

        if (account.getStatus()) {
            accounts.add(account);
            System.out.println(">>> [MANAGER] Account has been added.");
            planning(account);
        } else {
            System.out.println(">>> [MANAGER] Account has not been added.");
        }
    }

    public void disableAccount(Account account) {
        System.out.println(">>> [MANAGER] Disable account...");
        account.addTask(Account.Intention.DISABLE);
        synchronization(account);

        if (!account.getStatus()) {
            System.out.println(">>> [MANAGER] Account has been disabled.");
            accounts.remove(account);
        } else {
            System.out.println(">>> [MANAGER] Account has not been disabled.");
        }
    }

    private void planning(Account account) {
        System.out.println(">>> [MANAGER] Planning...");
        AccountInfo accountInfo = getAccountInfo(account);
        createWave(account, Account.Intention.EMPLOY, accountInfo.employmentCountdown()).start();
    }

    public Wave createWave(Account account, Account.Intention intention, int countdown) {
        return new Wave(this, account, intention, countdown);
    }

    private AccountInfo getAccountInfo(Account account) {
        System.out.println(">>> [MANAGER] Get account info...");
        account.addTask(Account.Intention.GET_INFO);
        synchronization(account);

        return account.getInfo();
    }

    // *** Intents. ***
    public synchronized void getJob(Account account) {
        System.out.println(">>> [MANAGER] Signal to employ.");
        account.addTask(Account.Intention.EMPLOY);
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
        System.out.println(">>> [MANAGER] Get account.");
        if (accounts.size() != 0) {
            return this.accounts.get(index);
        }

        return null;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("\n~~~ Manager ~~~\n");

        if (accounts.size() != 0) {
            for (Account account : accounts) {
                stringBuilder.append(account.toString());
            }
        } else {
            stringBuilder.append(">>> [Empty] <<<");
        }

        return stringBuilder.toString();
    }
}

