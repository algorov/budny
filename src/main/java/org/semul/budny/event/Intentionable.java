package org.semul.budny.event;

import org.openqa.selenium.chrome.ChromeDriver;

public abstract class Intentionable {
    public static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Intentionable.class);
    protected ChromeDriver driver;
    protected String username;
    protected String password;

    protected Intentionable(ChromeDriver driver, String username, String password) {
        this.driver = driver;
        this.username = username;
        this.password = password;
    }

    public void cleanup() {
        logger.info("Interrupt...");

        this.driver = null;
        this.username = null;
        this.password = null;

        logger.info("Done");
    }
}
