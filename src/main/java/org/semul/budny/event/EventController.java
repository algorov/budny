package org.semul.budny.event;

import org.openqa.selenium.chrome.ChromeDriver;
import org.semul.budny.exception.FailAuthorizationException;
import org.semul.budny.exception.FailEmployException;

public class EventController {
    public static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(EventController.class);
    private Connection connect;
    private Employ employ;

    public EventController(ChromeDriver driver, String username, String password) {
        logger.info("Initialization...");

        this.connect = new Connection(driver, username, password);
        this.employ = new Employ(driver, username, password);

        logger.info("Done.");
    }

    public void signIn() throws FailAuthorizationException {
        logger.info("Sign in.");
        this.connect.execute();
    }

    public boolean checkConnection() {
        logger.info("Check connection.");
        return connect.status();
    }

    public int getWorkEndCountdown() {
        logger.info("Get employment countdown.");
        return this.employ.getWorkEndCountdown();
    }

    public boolean checkWorkState() {
        logger.info("Check work state.");
        return this.employ.status();
    }

    public void employ() throws FailEmployException {
        logger.info("Employ.");
        this.employ.execute();
    }

    public void interrupt() {
        logger.info("Interrupt...");
        this.connect.cleanup();
        this.connect = null;

        this.employ.cleanup();
        this.employ = null;
        logger.info("Done.");
    }
}
