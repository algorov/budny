package org.semul.budny.connection;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.semul.budny.account.Account;
import org.semul.budny.account.AccountInfo;
import org.semul.budny.event.EventController;
import org.semul.budny.exception.FailAuthorizationException;
import org.semul.budny.exception.FailEmployException;
import org.semul.budny.exception.StartSessionException;

import java.time.Duration;

public class Session {
    public static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Session.class);
    private Account account;
    private final String username;
    private final String password;
    private ChromeDriver driver;
    private EventController exec;

    public static Session getInstance(Account account, String username, String password) {
        return new Session(account, username, password);
    }

    private Session(Account account, String username, String password) {
        logger.info("Initialization");

        this.account = account;
        this.username = username;
        this.password = password;
        this.driver = null;
        this.exec = null;

        logger.info("Done");
    }

    private ChromeDriver initDriver() {
        logger.info("Init driver...");

        String driverPath = System.getProperty("user.dir") + "/driver/chromedriver";
        System.setProperty("webdriver.chrome.driver", driverPath);

        ChromeOptions chromeOptions = new ChromeOptions();
        String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) " + "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36";
        chromeOptions.addArguments("user-agent=" + userAgent);
        chromeOptions.addArguments("--disable-blink-features=AutomationControlled");
        chromeOptions.addArguments("--remote-allow-origins=*");

        driver = new ChromeDriver(chromeOptions);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofMillis(60000));
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));
        driver.manage().timeouts().scriptTimeout(Duration.ofMillis(5000));

        logger.info("Successfully.");

        return driver;
    }

    public void getRequest(Account.Intention intent) throws StartSessionException, FailEmployException {
        switch (intent) {
            case GET_INFO -> getAccountInfo();
            case EMPLOY -> getEmploy();
            case DISABLE -> interrupt();
        }
    }

    // Connects to account. If it fails, pushes for an exception.
    public void start() throws StartSessionException {
        if (this.driver == null) {
            logger.info("Start...");
            this.driver = initDriver();
            this.exec = new EventController(this.driver, this.username, this.password);
        } else {
            logger.info("Restart...");
        }

        try {
            this.exec.signIn();
            logger.info("Successfully");
        } catch (FailAuthorizationException e) {
            logger.error(e);
            throw new StartSessionException(e.getMessage());
        }
    }

    // Disconnects account's connection.
    public void interrupt() {
        logger.info("Interrupt...");

        if (this.driver != null) {
            this.driver.close();
            this.driver = null;
        }

        if (this.exec != null) {
            this.exec.interrupt();
            this.exec = null;
        }

        this.account = null;

        logger.info("Done");
    }

    // Reconnect to account. If it fails, pushes for an exception.
    private void restore() throws StartSessionException {
        logger.info("Restore");
        start();
    }

    // Account connection check.
    private boolean isConnect() {
        logger.info("Check connect");
        boolean connect = false;

        if (this.exec != null) {
            connect = this.exec.checkConnection();
        }

        logger.info("Connect status: " + connect);
        return connect;
    }

    public void getAccountInfo() {
        logger.info("Get account info");
        this.account.setInfo(new AccountInfo(this.exec.getWorkEndCountdown()));
    }

    public void getEmploy() throws FailEmployException, StartSessionException {
        if (isConnect()) {
            employAction();
        } else {
            logger.warn("Connection broken");
            restore();
            employAction();
        }

    }

    private void employAction() throws FailEmployException {
        logger.info("Employ");

        if (!this.exec.checkWorkState()) {
            logger.info("Necessary");
            this.exec.employ();
        } else {
            logger.info("Not necessary");
        }
    }
}
