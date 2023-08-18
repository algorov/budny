package org.semul.budny.event;

import org.openqa.selenium.chrome.ChromeDriver;

public abstract class EventAbstract implements Eventable {
    public static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(EventAbstract.class);
    protected ChromeDriver driver;
    protected String username;
    protected String password;

    protected EventAbstract(ChromeDriver driver, String username, String password) {
        this.driver = driver;
        this.username = username;
        this.password = password;
    }

    // Derived classes must override the method!
    @Override
    public void run() throws Exception {
    }

    @Override
    public void quit() {
        logger.info("Interrupt");

        this.driver = null;
        this.username = null;
        this.password = null;
    }
}
