package org.semul.budny.manager;

import org.semul.budny.account.Account;
import org.semul.budny.account.AccountInfo;
import org.semul.budny.event.Intention;
import org.semul.budny.helper.Task;
import org.semul.budny.helper.TasksController;
import org.semul.budny.helper.ThreadsController;

import java.util.ArrayList;

public class Manager extends Thread {
    public static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Manager.class);
    private final ArrayList<Account> accounts;

    public static Manager getInstance() {
        Manager manager = new Manager();
        manager.start();

        return manager;
    }

    private Manager() {
        logger.info("Initialization");
        this.accounts = new ArrayList<>();
    }

    @Override
    public void run() {
        ThreadsController.pool.add(this);

        while (!Thread.currentThread().isInterrupted()) {
            if (TasksController.size() == 0) {
                planning();
            }

            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        quit();
    }

    /**
     * <h1>Intents</h1>
     */
    public synchronized void addAccount(String username, String password) {
        logger.info("Enables account");

        Account account = Account.getInstance(username, password);
        sync(account);

        if (account.isLive()) {
            accounts.add(account);
        }
    }

    public synchronized void delAccount(Account account) {
        logger.info("Disables account");

        account.addTask(Intention.DISABLE);
        sync(account);

        if (!account.isLive()) {
            accounts.remove(account);
        }
    }

    public synchronized void getJob(Account account) {
        logger.info("Signal to employ");
        account.addTask(Intention.EMPLOY);
        sync(account);
    }

    private void planning() {
        logger.info("Planning");

        for (Account account : this.accounts) {
            if (!account.getBlockPlanningStatus()) {
                AccountInfo accountInfo = getAccountInfo(account);
                Task.startThread(this, account, Intention.EMPLOY, accountInfo.workEndCountdown());
            }
        }
    }

    private AccountInfo getAccountInfo(Account account) {
        logger.info("Get account info");

        account.addTask(Intention.GET_INFO);
        sync(account);

        AccountInfo accountInfo = account.getInfo();
        logger.info("Account info: " + accountInfo);

        return accountInfo;
    }

    // Waits for a response from another (account) thread about the completion of the process.
    private void sync(Account account) {
        logger.info("Synchronization");

        while (!account.isComplete()) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void quit() {
        for (Account account : this.accounts) {
            account.interrupt();
        }
    }

    @Override
    public String toString() {
        logger.info("toString.");
        StringBuilder stringBuilder = new StringBuilder("\n~~~ [Manager] ~~~\n");

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

