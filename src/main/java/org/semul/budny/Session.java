package org.semul.budny;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.semul.budny.heroeswm.HeroesWMStructure;

import java.time.Duration;

public class Session {
    private final String username;
    private final String password;
    private ChromeDriver driver;
    private Action exec;

    Session(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Connect to account.
    public boolean startSession() {
        this.driver = initDriver();
        this.exec = new Action(driver);
        exec.signIn(this.username, this.password);

        // Check for a connection to the account.
        if (!sessionState()) {
            String captchaUrl = exec.getCaptchaUrl();

            if (captchaUrl != null) {
                exec.signIn(username, password, captchaUrl);

                if (!sessionState()) {
                    System.out.println("Invalid data or incorrectly solved captcha!");
                    interruptSession();

                    return false;
                }
            } else {
                System.out.println("Invalid data!");
                interruptSession();

                return false;
            }
        }

        return true;
    }

    public void interruptSession() {
        if (driver != null) {
            driver.close();
            driver = null;
        }

        exec = null;
    }

    public boolean sessionState() {
        driver.navigate().refresh();

        return !((HeroesWMStructure.URL).equals(driver.getCurrentUrl()) ||
                (HeroesWMStructure.URL + HeroesWMStructure.LOGIN_PATH).equals(driver.getCurrentUrl()));
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
}
