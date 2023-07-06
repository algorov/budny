package org.semul.budny.manager;

import org.semul.budny.account.Account;
import org.semul.budny.account.AccountInfo;
import org.semul.budny.helper.Wave;

import java.util.ArrayList;

public class Manager {
    public static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Manager.class);
    private final ArrayList<Account> accounts;

    public Manager() {
        logger.info("Initialization...");
        this.accounts = new ArrayList<>();
        logger.info("Done.");
    }

    public void enableAccount(String username, String password) {
        logger.info("Enable account...");

        Account account = new Account(this, username, password);
        account.start();
        synchronization(account);

        if (account.getStatus()) {
            accounts.add(account);
            logger.info("Account has been added.");
            planning(account);
        } else {
            logger.info("Account has not been added.");
        }
    }

    public void disableAccount(Account account) {
        logger.info("Disable account...");
        account.addTask(Account.Intention.DISABLE);
        synchronization(account);

        if (!account.getStatus()) {
            logger.info("Account has been disabled.");
            accounts.remove(account);
        } else {
            logger.info("Account has not been disabled.");
        }
    }

    private void planning(Account account) {
        logger.info("Planning...");
        AccountInfo accountInfo = getAccountInfo(account);
        createWave(account, Account.Intention.EMPLOY, accountInfo.employmentCountdown()).start();
        logger.info("Done.");
    }

    public Wave createWave(Account account, Account.Intention intention, int countdown) {
        logger.info("Create wave.");
        return new Wave(this, account, intention, countdown);
    }

    private AccountInfo getAccountInfo(Account account) {
        logger.info("Get account info...");
        account.addTask(Account.Intention.GET_INFO);
        synchronization(account);
        logger.info("Done.");
        logger.info("Account info: " + account.getInfo());

        return account.getInfo();
    }

    // *** Intents. ***
    public synchronized void getJob(Account account) {
        logger.info("Signal to employ.");
        account.addTask(Account.Intention.EMPLOY);
    }

    // Waiting for a response from another (account) thread about the completion of the process.
    private void synchronization(Account account) {
        logger.info("Synchronization...");

        while (!account.getCompletionStatus()) {
            try {
                Thread.sleep(2333);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        account.changeCompletionStatus();
        logger.info("Done.");
    }

    public Account getAccount(int index) {
        logger.info("Get account.");

        if (accounts.size() != 0) {
            return this.accounts.get(index);
        }

        return null;
    }

    @Override
    public String toString() {
        logger.info("toString.");
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

