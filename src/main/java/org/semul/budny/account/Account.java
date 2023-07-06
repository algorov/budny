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

    public Account(Manager manager, String username, String password) {
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
            if (this.taskQueue.size() > 0) {
                switch (this.taskQueue.poll()) {
                    case DISABLE -> disable();
                    case GET_INFO -> requestForInfo();
                    case EMPLOY -> employ();
                }
            }

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void addTask(Intention intention) {
        logger.info("Adding a task '" + intention + "'...");
        this.taskQueue.add(intention);
        logger.info("Done.");
    }

    // *** Intentions ***
    private void launch() {
        logger.info("Launch...");
        this.session = new Session(this.username, this.password);

        try {
            this.session.start();
            this.status = true;
            logger.info("Successfully.");
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

        logger.info("Successfully.");
    }

    private void requestForInfo() {
        logger.info("Request for information...");

        this.info = this.session.getAccountInfo();
        this.completionStatus = true;

        logger.info("Successfully.");
    }

    private void employ() {
        logger.info("Employ...");

        try {
            this.session.employ();
            logger.info("Successfully.");
            this.manager.createWave(this, Intention.EMPLOY, 60 * 60).start();
        } catch (FailEmployException e) {
            logger.warn(e);
            this.manager.createWave(this, Intention.EMPLOY, 5).start();
        } catch (StartSessionException ex) {
            logger.warn(ex);
            this.manager.disableAccount(this);
        }
    }

    public String getUsername() {
        logger.info("Get username.");
        return this.username;
    }

    public String getPassword() {
        logger.info("Get password.");
        return this.password;
    }

    public AccountInfo getInfo() {
        logger.info("Get info.");
        return this.info;
    }

    public boolean getStatus() {
        logger.info("Get status.");
        logger.info("Status: " + this.status + ".");
        return this.status;
    }

    public boolean getCompletionStatus() {
        logger.info("Get completion status.");
        logger.info("Completion status: " + this.completionStatus + ".");
        return this.completionStatus;
    }

    public void changeCompletionStatus() {
        logger.info("Change completion status.");
        this.completionStatus = !this.completionStatus;
    }

    @Override
    public String toString() {
        logger.info("toString.");
        return "\n● Account:\n▬▬ username: " + this.username + ";\n" + "▬▬ password: " + this.password + ";\n"
                + "▬▬ status: " + (this.status ? "launched" : "stopped") + ";\n";
    }
}
