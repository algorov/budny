package org.semul.budny.helper;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.semul.budny.exception.StartSessionException;

import java.time.Duration;

public class Session {
    private final String username;
    private final String password;
    private ChromeDriver driver;
    private Action exec;

    public Session(String username, String password) {
        this.username = username;
        this.password = password;
        this.driver = null;
        this.exec = null;
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

    // Connect to account. Returns true on connection, false otherwise.
    public void start() throws StartSessionException {
        if (this.driver == null) {
            this.driver = initDriver();
            this.exec = new Action(driver);
        }

        this.exec.signIn(this.username, this.password);

        // Check for a connection to the account.
        if (!status()) {
            String captchaUrl = this.exec.getCaptchaUrl();

            if (captchaUrl != null) {
                this.exec.signIn(username, password, captchaUrl);

                if (!status()) {
                    throw new StartSessionException();
                }
            } else {
                throw new StartSessionException();
            }
        }

        System.out.println("~ {Successful login!}");
    }

    private void restore() throws StartSessionException {
        start();
    }

    // Disconnecting a account's connection.
    public void interrupt() {
        if (this.driver != null) {
            this.driver.close();
            this.driver = null;
        }

        this.exec = null;
    }

    // Account connection check.
    private boolean status() {
        if (this.exec != null)
            return this.exec.checkConnection();

        return false;
    }
}
