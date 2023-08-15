package org.semul.budny.account;

import org.semul.budny.connection.Session;
import org.semul.budny.event.EventDriver;
import org.semul.budny.event.Intention;
import org.semul.budny.exception.FailEmployException;
import org.semul.budny.exception.StartSessionException;
import org.semul.budny.helper.ThreadsController;

import java.util.LinkedList;
import java.util.Queue;

public class Account extends Thread {
    public static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Account.class);
    private final String username;
    private final String password;
    private volatile AccountInfo info;
    private Session session;
    private final Queue<Intention> taskQueue;
    private volatile boolean blockPlanning;
    private volatile boolean complete;

    public static synchronized Account getInstance(String username, String password) {
        Account account = new Account(username, password);
        account.start();
        ThreadsController.threads.add(account);
        return account;
    }

    private Account(String username, String password) {
        logger.info("Initialization.");

        this.username = username;
        this.password = password;
        this.session = Session.getInstance(this, new EventDriver(EventDriver.getDriver(), username, password));
        this.taskQueue = new LinkedList<>();
        this.blockPlanning = false;
        this.complete = false;
    }

    @Override
    public void run() {
        try {
            launch();

            while (!Thread.currentThread().isInterrupted()) {
                if (this.taskQueue.size() > 0) {
                    runIntent(this.taskQueue.poll());
                }

                Thread.sleep(2000);
            }
        } catch (InterruptedException ignoredOne) {
            try {
                quit(false);
            } catch (InterruptedException ignoredTwo) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void launch() throws InterruptedException {
        logger.info("Launch");

        try {
            this.session.start();
            this.complete = true;
        } catch (StartSessionException e) {
            logger.error(e);
            throw new InterruptedException();
        }
    }

    // Если это действие от намерения менеджера то true если же при неудачном входе в аккаунт false (для синхронизации)
    private void quit(boolean flag) throws InterruptedException {
        logger.info("Disable");

        if (this.session != null) {
            this.session.close();
            this.session = null;
        }

        if (flag) {
            throw new InterruptedException();
        }

        this.complete = true;
        Thread.currentThread().interrupt();
    }

    public boolean isLive() {
        boolean status = !this.isInterrupted();
        logger.info("Alive: " + status);
        return status;
    }

    public void addTask(Intention intent) {
        logger.info("Add a task: " + intent);
        this.taskQueue.add(intent);
        setBlockPlanningStatus(intent);
    }

    private void runIntent(Intention intent) throws InterruptedException {
        logger.info("POST: " + intent);

        try {
            switch (intent) {
                case GET_INFO -> session.getAccountInfo();
                case EMPLOY -> session.getEmploy();
                case DISABLE -> quit(true);
            }
        } catch (FailEmployException employEx) {
            logger.warn(employEx);
        } catch (StartSessionException sessionEx) {
            logger.error(sessionEx);
        } finally {
            setBlockPlanningStatus(intent);
            this.complete = true;
        }
    }

    public void setInfo(AccountInfo info) {
        logger.info("Set info");
        this.info = info;
    }

    public AccountInfo getInfo() {
        logger.info("Get info");
        return this.info;
    }

    private void setBlockPlanningStatus(Intention intent) {
        this.blockPlanning = Intention.GET_INFO == intent;
    }

    public boolean getBlockPlanningStatus() {
        return this.blockPlanning;
    }

    /**
     * Статус звершения задачи.
     * Если при чтении <b>completionStatus == true</b>, то происходит изменение состояния.
     */
    public boolean isComplete() {
        if (this.complete) {
            this.complete = false;
            return true;
        } else return false;
    }

    @Override
    public String toString() {
        return "\n● Account:\n" +
                "▬▬ username: " + this.username + ";\n" +
                "▬▬ password: " + this.password + ";\n" +
                "▬▬ status: " + (!this.isInterrupted() ? "started" : "stopped") + ";\n";
    }
}
