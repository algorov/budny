package org.semul.budny.event;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.semul.budny.captcha.Captcha;
import org.semul.budny.captcha.CaptchaSolution;
import org.semul.budny.exception.FailAuthorizationException;
import org.semul.budny.heroeswm.Paths;

public class Connection extends Intentionable {
    public static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Connection.class);

    Connection(ChromeDriver driver, String username, String password) {
        super(driver, username, password);
        logger.info("Initialization.");
    }

    public void run() throws FailAuthorizationException {
        logger.info("Execute.");
        signIn();

        boolean connectionStatus = getStatus();
        logger.info("Connection status: " + connectionStatus);

        if (!connectionStatus) {
            String captchaUrl = detCaptchaUrl();
            logger.info("Captcha URL: " + captchaUrl);

            if (captchaUrl != null) {
                this.signIn(captchaUrl);

                if (!getStatus()) {
                    String message = "Not valid data or captcha!";
                    logger.warn(message);
                    throw new FailAuthorizationException(message);
                }
            } else {
                String message = "Not valid data!";
                logger.warn(message);
                throw new FailAuthorizationException(message);
            }
        }
    }

    public boolean getStatus() {
        logger.info("Def status...");

        this.driver.navigate().refresh();
        boolean status = !((Paths.URL).equals(this.driver.getCurrentUrl()) ||
                (Paths.URL + Paths.PagePath.LOGIN.getValue()).equals(this.driver.getCurrentUrl()));

        logger.info("Status: " + status);

        return status;
    }

    private void signIn() {
        logger.info("Sign in");
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
            logger.warn(e);
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
            logger.info("Captcha PATH: " + captchaPath);
            String code = CaptchaSolution.solution(captchaPath);
            logger.info("Captcha solution: " + code);
            Captcha.delete(captchaPath);

            if (code != null) {
                WebElement captchaCodeField = driver.findElement(new By.ByXPath(Paths.LoginPageElement.EFP01_CAPTCHA.getValue()));
                captchaCodeField.click();
                captchaCodeField.clear();
                captchaCodeField.sendKeys(code);
            }

            WebElement authButton = driver.findElement(new By.ByXPath(Paths.LoginPageElement.BTNP02_AUTH.getValue()));
            authButton.click();
        } catch (NoSuchElementException e) {
            logger.warn(e);
        }
    }

    private String detCaptchaUrl() {
        logger.info("Determines captcha URL");

        WebElement captchaField = null;
        try {
            captchaField = driver.findElement(new By.ByXPath(Paths.LoginPageElement.FP01_CAPTCHA.getValue()));
        } catch (NoSuchElementException e) {
            logger.warn(e);
        }

        return captchaField != null ? captchaField.getAttribute("src") : null;
    }
}
