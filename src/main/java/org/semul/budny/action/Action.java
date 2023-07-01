package org.semul.budny.action;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.semul.budny.captcha.Captcha;
import org.semul.budny.captcha.CaptchaSolution;
import org.semul.budny.heroeswm.Paths;

public class Action {
    private final ChromeDriver driver;
    private final String username;
    private final String password;

    public Action(ChromeDriver driver, String username, String password) {
        this.driver = driver;
        this.username = username;
        this.password = password;
    }

    public void signIn() {
        this.driver.get(Paths.URL);

        try {
            WebElement loginField = driver.findElement(new By.ByClassName(Paths.LOGIN_FIELD_PATH));
            loginField.clear();
            loginField.sendKeys(this.username);

            WebElement passwordField = driver.findElement(new By.ByClassName(Paths.PASSWORD_FIELD_PATH));
            passwordField.clear();
            passwordField.sendKeys(this.password);

            WebElement authButton = driver.findElement(new By.ByClassName(Paths.AUTH_BTN_PATH));
            authButton.click();
        } catch (NoSuchElementException e) {}
    }

    public void signIn(String captchaUrl) {
        try {
            WebElement loginField = driver.findElement(new By.ByXPath(Paths.LOGIN_FIELD_PATH_2));
            loginField.clear();
            loginField.sendKeys(this.username);

            WebElement passwordField = driver.findElement(new By.ByXPath(Paths.PASSWORD_FIELD_PATH_2));
            passwordField.clear();
            passwordField.sendKeys(this.password);

            String captchaPath = Captcha.save(captchaUrl);
            String code = CaptchaSolution.solution(captchaPath);
            Captcha.delete(captchaPath);

            if (code != null) {
                WebElement captchaCodeField = driver.findElement(new By.ByXPath(Paths.CAPTCHA_ENTER_FIELD_PATH));
                captchaCodeField.clear();
                captchaCodeField.sendKeys(code);
            }

            WebElement authButton = driver.findElement(new By.ByXPath(Paths.AUTH_BTN_PATH_2));
            authButton.click();
        } catch (NoSuchElementException e) {}
    }

    public String getCaptchaUrl() {
        System.out.print(">>> Get captcha URL -> ");

        WebElement captchaField = null;

        try {
            captchaField = driver.findElement(new By.ByXPath("//*[@id=\"getjob_form\"]/img[1]"));
        } catch (NoSuchElementException e) {
            try {
                captchaField = driver.findElement(new By.ByXPath(Paths.CAPTCHA_FIELD_PATH));
            } catch (NoSuchElementException q) {
            }
        }

        String captchaUrl = captchaField != null ? captchaField.getAttribute("src") : null;
        System.out.println(captchaUrl);

        return captchaUrl;
    }

    public boolean checkConnection() {
        if (this.driver != null) {
            this.driver.navigate().refresh();

            return !((Paths.URL).equals(this.driver.getCurrentUrl()) ||
                    (Paths.URL + Paths.LOGIN_PATH).equals(this.driver.getCurrentUrl()));
        }

        return false;
    }

    // Apparatus employed.
    public void employ() {
    }
}
