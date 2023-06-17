package org.semul.budny;

import org.openqa.selenium.chrome.ChromeDriver;


public class Action {
    ChromeDriver driver;


    public Action(ChromeDriver driver) {
        this.driver = driver;
    }

    // Login.
    public void signIn(String username, String password) {
        System.out.println("There should be authorization.");
    }

    // Apparatus employed.
    public void employ() {
    }
}
