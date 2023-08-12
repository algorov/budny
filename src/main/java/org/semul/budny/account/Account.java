package org.semul.budny.account;

import org.semul.budny.connection.Session;
import org.semul.budny.exception.FailEmployException;
import org.semul.budny.exception.StartSessionException;
import org.semul.budny.manager.Manager;

import java.util.LinkedList;
import java.util.Queue;

public class Account extends Thread {
    public static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Account.class);
    private Manager manager;
    private final String username;
    private final String password;
    private volatile AccountInfo info;
    private volatile boolean status;
    private volatile boolean completionStatus;
    private Session session;
    private final Queue<Intention> taskQueue;

    public enum Intention {
        DISABLE, GET_INFO, EMPLOY
    }

    public static synchronized Account getInstance(Manager manager, String username, String password) {
        return new Account(manager, username, password);
    }

    private Account(Manager manager, String username, String password) {
        logger.info("Initialization...");

        this.manager = manager;
        this.username = username;
        this.password = password;
        this.status = false;
        this.taskQueue = new LinkedList<>();
        this.completionStatus = false;
        this.session = null;

        logger.info("Done.");
    }

    @Override
    public void run() {
        launch();

        while (this.status) {
            System.out.println(this.taskQueue.size());
            if (this.taskQueue.size() > 0) {
                postRequest(this.taskQueue.poll());
            }

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void launch() {
        logger.info("Launch...");

        this.session = Session.getInstance(this, this.username, this.password);

        try {
            this.session.start();
            this.status = true;

            logger.info("Successfully");
        } catch (StartSessionException e) {
            logger.error(e);
            disable();
        } finally {
            this.completionStatus = true;
        }
    }

    private void disable() {
        logger.info("Disable...");

        if (this.session != null) {
            this.session.interrupt();
            this.session = null;
        }

        this.manager = null;
        this.status = false;
        this.completionStatus = true;

        logger.info("Successfully");
    }

    public void addTask(Intention intent) {
        logger.info("Adding a task: " + intent);
        this.taskQueue.add(intent);
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
    }

    public boolean isLive() {
        logger.info("Alive: " + this.status);
        return this.status;
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

    public boolean isCompletion(boolean autochange) {
        logger.info("Completion status: " + this.completionStatus);

        if (this.completionStatus) {
            if (autochange) {
                this.completionStatus = false;
            }

            return true;
        } else return false;
    }

    @Override
    public String toString() {
        logger.info("toString.");
        return "\n● Account:\n" +
                "▬▬ username: " + this.username + ";\n" +
                "▬▬ password: " + this.password + ";\n" +
                "▬▬ status: " + (this.status ? "launched" : "stopped") + ";\n";
    }
}
