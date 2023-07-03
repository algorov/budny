package org.semul.budny.action;

import org.openqa.selenium.chrome.ChromeDriver;
import org.semul.budny.exception.FailAuthorizationException;
import org.semul.budny.exception.FailEmployException;

public class IntentController {
    private final ChromeDriver driver;
    private Connection connect;
    private Employ employ;

    public IntentController(ChromeDriver driver, String username, String password) {
        this.driver = driver;
        this.connect = new Connection(driver, username, password);
        this.employ = new Employ(driver, username, password);
    }

    public void interrupt() {
        this.connect.interrupt();
        this.connect = null;

        this.employ.interrupt();
        this.employ = null;
    }

    public void signIn() throws FailAuthorizationException {
        this.connect.execute();
    }

    public boolean checkConnection() {
        if (this.driver != null) {
            return connect.status();
        }

        return false;
    }

    public void employ() throws FailEmployException {
        this.employ.execute();
    }

    public boolean checkEmploymentState() {
        return this.employ.status();
    }
}
