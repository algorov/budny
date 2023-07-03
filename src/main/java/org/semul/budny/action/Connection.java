package org.semul.budny.action;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.semul.budny.captcha.Captcha;
import org.semul.budny.captcha.CaptchaSolution;
import org.semul.budny.exception.FailAuthorizationException;
import org.semul.budny.heroeswm.Paths;

public class Connection extends Intentionable {
    Connection(ChromeDriver driver, String username, String password) {
        super(driver, username, password);
    }

    public void execute() throws FailAuthorizationException {
        signIn();

        if (!status()) {
            String captchaUrl = getCaptchaUrl();
            if (captchaUrl != null) {
                this.signIn(captchaUrl);

                if (!status()) {
                    throw new FailAuthorizationException(">>> [Error] - Not valid data or captcha!");
                }
            } else {
                throw new FailAuthorizationException(">>> [Error] - Not valid data!");
            }
        }
    }

    @Override
    public boolean status() {
        return !((Paths.URL).equals(this.driver.getCurrentUrl()) ||
                (Paths.URL + Paths.PagePath.LOGIN.getValue()).equals(this.driver.getCurrentUrl()));
    }

    private void signIn() {
        this.driver.get(Paths.URL);

        try {
            WebElement loginField = driver.findElement(new By.ByClassName(Paths.LoginPageElement.EFP01_LOGIN.getValue()));
            loginField.clear();
            loginField.sendKeys(username);

            WebElement passwordField = driver.findElement(new By.ByClassName(Paths.LoginPageElement.EFP01_PASSWORD.getValue()));
            passwordField.clear();
            passwordField.sendKeys(password);

            WebElement authButton = driver.findElement(new By.ByClassName(Paths.LoginPageElement.BTNP01_AUTH.getValue()));
            authButton.click();
        } catch (NoSuchElementException e) {
        }
    }

    private void signIn(String captchaUrl) {
        try {
            WebElement loginField = driver.findElement(new By.ByXPath(Paths.LoginPageElement.EFP02_LOGIN.getValue()));
            loginField.clear();
            loginField.sendKeys(username);

            WebElement passwordField = driver.findElement(new By.ByXPath(Paths.LoginPageElement.EFP02_PASSWORD.getValue()));
            passwordField.clear();
            passwordField.sendKeys(password);

            String captchaPath = Captcha.save(captchaUrl);
            String code = CaptchaSolution.solution(captchaPath);
            Captcha.delete(captchaPath);

            if (code != null) {
                WebElement captchaCodeField = driver.findElement(new By.ByXPath(Paths.LoginPageElement.EFP01_CAPTCHA.getValue()));
                captchaCodeField.clear();
                captchaCodeField.sendKeys(code);
            }

            WebElement authButton = driver.findElement(new By.ByXPath(Paths.LoginPageElement.BTNP02_AUTH.getValue()));
            authButton.click();
        } catch (NoSuchElementException e) {
        }
    }

    private String getCaptchaUrl() {
        WebElement captchaField = null;
        try {
            captchaField = driver.findElement(new By.ByXPath(Paths.LoginPageElement.FP01_CAPTCHA.getValue()));
        } catch (NoSuchElementException e) {
        }

        return captchaField != null ? captchaField.getAttribute("src") : null;
    }
}
