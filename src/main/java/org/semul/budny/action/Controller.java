package org.semul.budny.action;

import org.openqa.selenium.chrome.ChromeDriver;
import org.semul.budny.exception.FailAuthorizationException;
import org.semul.budny.exception.FailEmployException;

public class Controller {
    public static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Controller.class);
    private Connection connect;
    private Employ employ;

    public Controller(ChromeDriver driver, String username, String password) {
        logger.info("Initialization...");
        this.connect = new Connection(driver, username, password);
        this.employ = new Employ(driver, username, password);
        logger.info("Done.");
    }

    public void interrupt() {
        logger.info("Interrupt...");
        this.connect.interrupt();
        this.connect = null;

        this.employ.interrupt();
        this.employ = null;
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

    public int getEmploymentCountdown() {
        logger.info("Get employment countdown.");
        return this.employ.getEmploymentCountdown();
    }

    public void employ() throws FailEmployException {
        logger.info("Employ...");
        this.employ.execute();
    }

    public boolean checkEmploymentState() {
        logger.info("Check employment state.");
        return this.employ.status();
    }
}
