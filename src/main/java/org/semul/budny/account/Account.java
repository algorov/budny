package org.semul.budny.account;

import org.semul.budny.connection.Session;
import org.semul.budny.exception.FailEmployException;
import org.semul.budny.exception.StartSessionException;
import org.semul.budny.helper.ThreadsController;
import org.semul.budny.manager.Manager;

import java.util.LinkedList;
import java.util.Queue;

public class Account extends Thread {
    public static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Account.class);
    private Manager manager;
    private final String username;
    private final String password;
    private volatile AccountInfo info;
    private volatile boolean blockPlanning;
    private volatile boolean completionStatus;
    private Session session;
    private final Queue<Intention> taskQueue;

    public enum Intention {
        DISABLE, GET_INFO, EMPLOY
    }

    public static synchronized Account getInstance(Manager manager, String username, String password) {
        Account account = new Account(manager, username, password);
        ThreadsController.threads.add(account);

        return account;
    }

    private Account(Manager manager, String username, String password) {
        logger.info("Initialization...");

        this.manager = manager;
        this.username = username;
        this.password = password;
        this.taskQueue = new LinkedList<>();
        setBlockPlanningStatus(false);
        this.completionStatus = false;
        this.session = null;

        logger.info("Done.");
    }

    @Override
    public void run() {
        try {
            launch();

            while (!Thread.currentThread().isInterrupted()) {
                if (this.taskQueue.size() > 0) {
                    postRequest(this.taskQueue.poll());
                }

                Thread.sleep(5000);
            }
        } catch (InterruptedException ignored) {
            disable();
        }

    }

    private void launch() throws InterruptedException {
        logger.info("Launch...");

        this.session = Session.getInstance(this, this.username, this.password);

        try {
            this.session.start();
            this.completionStatus = true;
            logger.info("Successfully");
        } catch (StartSessionException e) {
            logger.error(e);
            System.out.println("Чтоооооо");
            throw new InterruptedException();
        }
    }

    private void disable() {
        logger.info("Disable...");

        quitSession();

        this.manager = null;
        this.completionStatus = true;

        logger.info("Successfully");

        Thread.currentThread().interrupt();
    }

    private void quitSession() {
        if (this.session != null) {
            this.session.close();
            this.session = null;
        }
    }

    public void addTask(Intention intent) {
        logger.info("Adding a task: " + intent);
        this.taskQueue.add(intent);

        if (intent == Intention.GET_INFO) {
            setBlockPlanningStatus(true);
        }
        logger.info("Done");
    }

    private void postRequest(Intention intent) {
        logger.info("POST: " + intent);

        try {
            this.session.getRequest(intent);
        } catch (FailEmployException employEx) {
            logger.warn(employEx);
        } catch (StartSessionException sessionEx) {
            logger.error(sessionEx);
            this.manager.delAccount(this);
        }

        this.completionStatus = true;

        if (intent != Intention.GET_INFO) {
            setBlockPlanningStatus(false);
        }
    }

    public boolean isLive() {
        boolean status = !this.isInterrupted();
        logger.info("Alive: " + status);
        return status;
    }

    public AccountInfo getInfo() {
        logger.info("Get info");
        return this.info;
    }

    public void setInfo(AccountInfo info) {
        this.info = info;
    }

    public String getUsername() {
        logger.info("Get username");
        return this.username;
    }

    public String getPassword() {
        logger.info("Get password");
        return this.password;
    }

    public void setBlockPlanningStatus(boolean status) {
        this.blockPlanning = status;
    }

    public boolean getBlockPlanningStatus() {
        return this.blockPlanning;
    }

    /**
     * Статус звершения задачи.
     * Если при чтении <b>completionStatus == true</b>, то происходит изменение состояния.
     */
    public boolean isCompletion() {
        logger.info("Completion status: " + this.completionStatus);

        if (this.completionStatus) {
            this.completionStatus = false;
            return true;
        } else return false;
    }

    @Override
    public String toString() {
        logger.info("toString.");
        return "\n● Account:\n" +
                "▬▬ username: " + this.username + ";\n" +
                "▬▬ password: " + this.password + ";\n" +
                "▬▬ status: " + (!this.isInterrupted() ? "launched" : "stopped") + ";\n";
    }
}
