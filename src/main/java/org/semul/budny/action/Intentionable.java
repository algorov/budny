package org.semul.budny.action;

import org.openqa.selenium.chrome.ChromeDriver;

public abstract class Intentionable {
    protected ChromeDriver driver;
    protected String username;
    protected String password;

    protected Intentionable(ChromeDriver driver, String username, String password) {
        this.driver = driver;
        this.username = username;
        this.password = password;
    }

    public abstract boolean status();

    public void interrupt() {
        this.driver = null;
        this.username = null;
        this.password = null;
    }
}
