package org.semul.budny.connection;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.semul.budny.account.AccountInfo;
import org.semul.budny.event.EventController;
import org.semul.budny.exception.FailAuthorizationException;
import org.semul.budny.exception.FailEmployException;
import org.semul.budny.exception.StartSessionException;

import java.time.Duration;

public class Session {
    public static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Session.class);
    private final String username;
    private final String password;
    private ChromeDriver driver;
    private EventController exec;

    public static Session getInstance(String username, String password) {
        return new Session(username, password);
    }

    private Session(String username, String password) {
        logger.info("Initialization.");

        this.username = username;
        this.password = password;
        this.driver = null;
        this.exec = null;

        logger.info("Done.");
    }

    // Connect to account. If it fails, pushes for an exception.
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
            logger.info("Successfully.");
        } catch (FailAuthorizationException e) {
            logger.error(e);
            throw new StartSessionException(e.getMessage());
        }
    }

    // Disconnecting account's connection.
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

        logger.info("Done.");
    }

    // Reconnect to account. If it fails, pushes for an exception.
    private void restore() throws StartSessionException {
        logger.info("Restore.");
        start();
    }

    private ChromeDriver initDriver() {
        logger.info("Init driver...");

        String driverPath = System.getProperty("user.dir") + "/driver/chromedriver";
        System.setProperty("webdriver.chrome.driver", driverPath);

        String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36";

        ChromeOptions chromeOptions = new ChromeOptions();
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

    // Account connection check.
    private boolean status() {
        logger.info("Check status.");
        boolean connectionStatus = false;

        if (this.exec != null) {
            connectionStatus = this.exec.checkConnection();
        }

        logger.info("Connection status: " + connectionStatus + ".");
        return connectionStatus;
    }

    public AccountInfo getAccountInfo() {
        logger.info("Get account info.");
        return new AccountInfo(this.exec.getWorkEndCountdown());
    }

    public void employ() throws FailEmployException, StartSessionException {
        if (status()) {
            employAction();
        } else {
            logger.warn("Connection broken.");
            restore();
            employAction();
        }

    }

    private void employAction() throws FailEmployException {
        logger.info("Employ.");

        if (!this.exec.checkWorkState()) {
            logger.info("Necessary.");
            this.exec.employ();
        } else {
            logger.info("Not necessary.");
        }
    }
}
