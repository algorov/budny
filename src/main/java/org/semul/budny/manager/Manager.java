package org.semul.budny.manager;

import org.semul.budny.account.Account;
import org.semul.budny.account.AccountInfo;
import org.semul.budny.helper.Task;
import org.semul.budny.helper.TasksController;

import java.util.ArrayList;

public class Manager extends Thread {
    public static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Manager.class);
    private TasksController tasksController;
    private ArrayList<Account> accounts;
    private volatile boolean status;

    public static Manager getInstance() {
        return new Manager();
    }

    private Manager() {
        logger.info("Initialization...");
        this.tasksController = TasksController.getInstance();
        this.accounts = new ArrayList<>();
        this.status = true;
        logger.info("Done");
    }

    @Override
    public void run() {
        while (this.status) {
            if (Task.taskCount == 0) {
                planning();
            }

            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        cleanup();
    }

    public synchronized void addAccount(String username, String password) {
        logger.info("Enable account...");

        Account account = Account.getInstance(this, username, password);
        account.start();
        synch(account);

        if (account.isLive()) {
            accounts.add(account);
            logger.info("Account has been added");
        } else {
            logger.info("Account hasn't been added");
        }
    }

    public synchronized void delAccount(Account account) {
        logger.info("Disable account...");

        account.addTask(Account.Intention.DISABLE);
        synch(account);

        if (!account.isLive()) {
            logger.info("Account has been disabled");
            accounts.remove(account);
        } else {
            logger.info("Account hasn't been disabled");
        }
    }

    private void planning() {
        logger.info("Planning...");

        for (Account account : this.accounts) {
            if (account.isCompletion(true)) {
                AccountInfo accountInfo = getAccountInfo(account);
                Task.getInstance(this, account, Account.Intention.EMPLOY, accountInfo.workEndCountdown()).start();
            }
        }

        logger.info("Done");
    }

    private AccountInfo getAccountInfo(Account account) {
        logger.info("Get account info...");

        account.addTask(Account.Intention.GET_INFO);
        synch(account);

        AccountInfo accountInfo = account.getInfo();
        logger.info("Account info: " + accountInfo);

        return accountInfo;
    }

    public synchronized void getJob(Account account) {
        logger.info("Signal to employ");
        account.addTask(Account.Intention.EMPLOY);
    }

    // Waits for a response from another (account) thread about the completion of the process.
    private void synch(Account account) {
        logger.info("Synchronization...");

        while (!account.isCompletion(false)) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        logger.info("Done");
    }

    public Account getAccount(int index) {
        logger.info("Get account");

        if (accounts.size() != 0) {
            return this.accounts.get(index);
        }

        return null;
    }

    public int getActiveAccountsCount() {
        return this.accounts.size();
    }

    public void halt() {
        this.status = false;
    }

    private void cleanup() {
        if (this.accounts.size() > 0) {
            for (Account account : accounts) {
                account.interrupt();
            }
        }

        this.tasksController.halt();
        this.tasksController = null;
        this.accounts = null;
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

