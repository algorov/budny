package org.semul.budny.event;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.semul.budny.exception.FailAuthorizationException;
import org.semul.budny.exception.FailEmployException;

import java.time.Duration;

public class EventDriver {
    public static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(EventDriver.class);

    private ChromeDriver driver;
    private Connection connect;
    private Employ employ;

    public EventDriver(ChromeDriver driver, String username, String password) {
        logger.info("Initialization...");

        this.driver = driver;
        this.connect = new Connection(driver, username, password);
        this.employ = new Employ(driver, username, password);

        logger.info("Done");
    }

    public static ChromeDriver getDriver() {
        logger.info("Init driver...");

        String path = System.getProperty("user.dir") + "/driver/chromedriver";
        System.setProperty("webdriver.chrome.driver", path);

        ChromeOptions option = new ChromeOptions();
        String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) " + "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36";
        option.addArguments("user-agent=" + userAgent);
        option.addArguments("--disable-blink-features=AutomationControlled");
        option.addArguments("--remote-allow-origins=*");

        ChromeDriver driver = new ChromeDriver(option);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofMillis(60000));
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));
        driver.manage().timeouts().scriptTimeout(Duration.ofMillis(5000));

        logger.info("Successfully.");

        return driver;
    }

    private void quitDriver() {
        this.driver.close();
        this.driver.quit();
        this.driver = null;
    }

    public void signIn() throws FailAuthorizationException {
        logger.info("Sign in");
        this.connect.run();
    }

    public boolean checkConnection() {
        logger.info("Check connection");
        return connect.getStatus();
    }

    public int getWorkEndCountdown() {
        logger.info("Get employment countdown");
        return this.employ.detWorkEndCountdown();
    }

    public boolean checkWorkState() {
        logger.info("Check work state");
        return this.employ.getStatus();
    }

    public void employ() throws FailEmployException {
        logger.info("Employ");
        this.employ.run();
    }

    public void quit() {
        logger.info("Quit");

        quitDriver();
        this.connect.cleanup();
        this.employ.cleanup();
        this.connect = null;
        this.employ = null;
    }
}
