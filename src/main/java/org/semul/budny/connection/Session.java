package org.semul.budny.connection;

import org.semul.budny.account.Account;
import org.semul.budny.account.AccountInfo;
import org.semul.budny.event.EventDriver;
import org.semul.budny.exception.FailAuthorizationException;
import org.semul.budny.exception.FailEmployException;
import org.semul.budny.exception.StartSessionException;

public class Session {
    public static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Session.class);
    private Account account;
    private EventDriver exec;

    public static synchronized Session getInstance(Account account, EventDriver driver) {
        return new Session(account, driver);
    }

    private Session(Account account, EventDriver eventDriver) {
        logger.info("Initialization");

        this.account = account;
        this.exec = eventDriver;
    }

    // Connects to account. If it fails, pushes for an exception.
    public void start() throws StartSessionException {
        logger.info("Start");

        try {
            this.exec.signIn();
        } catch (FailAuthorizationException e) {
            logger.error(e);
            throw new StartSessionException(e.getMessage());
        }
    }

    // Disconnects account's connection.
    public void close() {
        logger.info("Close");

        this.exec.quit();
        this.exec = null;
        this.account = null;
    }

    // Reconnect to account. If it fails, pushes for an exception.
    private void restore() throws StartSessionException {
        logger.info("Restore");
        start();
    }

    // Account connection check.
    private boolean isConnect() {
        boolean connect = this.exec.checkConnection();
        logger.info("Connect status: " + connect);
        return connect;
    }

    /**
     * <h1>Intents</h1>
     */
    public void getAccountInfo() {
        logger.info("Get account info");
        this.account.setInfo(new AccountInfo(this.exec.getWorkEndCountdown()));
    }

    public void getEmploy() throws FailEmployException, StartSessionException {
        if (!isConnect()) {
            logger.warn("Connection broken");
            restore();
        }

        boolean needFlag = !this.exec.checkWorkState();
        logger.info(needFlag ? "Necessary" : "Not necessary");
        if (needFlag) {
            this.exec.employ();
        }
    }
}
