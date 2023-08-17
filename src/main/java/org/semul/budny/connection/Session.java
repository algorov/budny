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
    private EventDriver driver;

    public static synchronized Session getInstance(Account account, EventDriver driver) {
        return new Session(account, driver);
    }

    private Session(Account account, EventDriver eventDriver) {
        logger.info("Initialization");

        this.account = account;
        this.driver = eventDriver;
    }

    /**
     * Connects to account. If it fails, pushes for an exception.
     * @throws StartSessionException failed connection.
     */
    public void start() throws StartSessionException {
        logger.info("Start");

        try {
            this.driver.signIn();
        } catch (FailAuthorizationException e) {
            logger.error(e);
            throw new StartSessionException(e.getMessage());
        }
    }

    // Closes account's connection.
    public void close() {
        logger.info("Close");

        this.driver.quit();
        this.driver = null;
        this.account = null;
    }

    /**
     * Reconnect to account. If it fails, pushes for an exception.
     * @throws StartSessionException failed connection.
     */
    private void restore() throws StartSessionException {
        logger.info("Restore");
        start();
    }

    // Check session status.
    private boolean isConnect() {
        boolean connect = this.driver.checkConnection();
        logger.info("Connect status: " + connect);
        return connect;
    }

    /**
     * <h1>Intents</h1>
     */
    public void getAccountInfo() {
        logger.info("Get account info");
        this.account.setInfo(new AccountInfo(this.driver.getWorkEndCountdown()));
    }

    public void getEmploy() throws FailEmployException, StartSessionException {
        if (!isConnect()) {
            logger.warn("Connection broken");
            restore();
        }

        this.driver.employ();
    }
}
