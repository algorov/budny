package org.semul.budny.connection;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.semul.budny.action.Controller;
import org.semul.budny.exception.FailAuthorizationException;
import org.semul.budny.exception.FailEmployException;
import org.semul.budny.exception.StartSessionException;

import java.time.Duration;

public class Session {
    private final String username;
    private final String password;
    private ChromeDriver driver;
    private Controller exec;

    public Session(String username, String password) {
        this.username = username;
        this.password = password;
        this.driver = null;
        this.exec = null;
    }

    // Connect to account. If it fails, pushes for an exception.
    public void start() throws StartSessionException {
        if (this.driver == null) {
            this.driver = initDriver();
            this.exec = new Controller(this.driver, this.username, this.password);
        }

        try {
            this.exec.signIn();
        } catch (FailAuthorizationException e) {
            throw new StartSessionException(e.getMessage());
        }
    }

    // Disconnecting account's connection.
    public void interrupt() {
        if (this.driver != null) {
            this.driver.close();
            this.driver = null;
        }

        if (this.exec != null) {
            this.exec.interrupt();
            this.exec = null;
        }
    }

    // Reconnect to account. If it fails, pushes for an exception.
    private void restore() throws StartSessionException {
        start();
    }

    private ChromeDriver initDriver() {
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

        return driver;
    }

    // Account connection check.
    private boolean status() {
        if (this.exec != null) {
            return this.exec.checkConnection();
        }

        return false;
    }

    public void employ() throws FailEmployException {
        if (!this.exec.checkEmploymentState()) {
            System.out.println("ща устроимся");
            try {
                this.exec.employ();
            } catch (FailEmployException e) {
                System.out.println(e.getMessage());
            }

        } else {
            System.out.println("Ты уже устроился");
        }
    }
}
