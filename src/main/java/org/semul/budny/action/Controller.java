package org.semul.budny.action;

import org.openqa.selenium.chrome.ChromeDriver;
import org.semul.budny.exception.FailAuthorizationException;
import org.semul.budny.exception.FailEmployException;

public class Controller {
    private Connection connect;
    private Employ employ;

    public Controller(ChromeDriver driver, String username, String password) {
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
        return connect.status();
    }

    public void employ() throws FailEmployException {
        this.employ.execute();
    }

    public boolean checkEmploymentState() {
        return this.employ.status();
    }
}
