package org.semul.budny.event;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.semul.budny.captcha.Captcha;
import org.semul.budny.captcha.CaptchaSolution;
import org.semul.budny.exception.FailAuthorizationException;
import org.semul.budny.heroeswm.path.page.PagePath;
import org.semul.budny.heroeswm.path.page.login.LoginPageElement;

import static org.semul.budny.heroeswm.path.page.PagePath.URL;

public class Connect extends EventAbstract {
    public static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Connect.class);

    Connect(ChromeDriver driver, String username, String password) {
        super(driver, username, password);
        logger.info("Initialization");
    }

    @Override
    public void run() throws FailAuthorizationException {
        logger.info("Execute");
        signIn();

        boolean connectionStatus = getStatus();
        logger.info("Connection status: " + connectionStatus);

        if (!connectionStatus) {
            String captchaUrl = detCaptchaUrl();
            logger.info("CAPTCHA URL: " + captchaUrl);

            if (captchaUrl != null) {
                this.signIn(captchaUrl);

                if (!getStatus()) {
                    String message = "Not valid data or CAPTCHA!";
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
        logger.info("Determines status");

        this.driver.navigate().refresh();
        boolean status = !((URL).equals(this.driver.getCurrentUrl()) ||
                (URL + PagePath.LOGIN.getValue()).equals(this.driver.getCurrentUrl()));
        logger.info("Status: " + status);

        return status;
    }

    private void signIn() {
        logger.info("Sign in");
        this.driver.get(URL);

        try {
            WebElement loginField = driver.findElement(new By.ByClassName(LoginPageElement.EFP01_LOGIN.getValue()));
            loginField.clear();
            loginField.sendKeys(username);

            WebElement passwordField = driver.findElement(new By.ByClassName(LoginPageElement.EFP01_PASSWORD.getValue()));
            passwordField.clear();
            passwordField.sendKeys(password);

            WebElement authButton = driver.findElement(new By.ByClassName(LoginPageElement.BTNP01_AUTH.getValue()));
            authButton.click();
        } catch (NoSuchElementException e) {
            logger.warn(e);
        }
    }

    private void signIn(String captchaUrl) {
        try {
            WebElement loginField = driver.findElement(new By.ByXPath(LoginPageElement.EFP02_LOGIN.getValue()));
            loginField.clear();
            loginField.sendKeys(username);

            WebElement passwordField = driver.findElement(new By.ByXPath(LoginPageElement.EFP02_PASSWORD.getValue()));
            passwordField.clear();
            passwordField.sendKeys(password);

            String captchaPath = Captcha.save(captchaUrl);
            logger.info("CAPTCHA PATH: " + captchaPath);
            String code = CaptchaSolution.solution(captchaPath);
            logger.info("CAPTCHA solution: " + code);
            Captcha.delete(captchaPath);

            if (code != null) {
                WebElement captchaCodeField = driver.findElement(new By.ByXPath(LoginPageElement.EFP01_CAPTCHA.getValue()));
                captchaCodeField.click();
                captchaCodeField.clear();
                captchaCodeField.sendKeys(code);
            }

            WebElement authButton = driver.findElement(new By.ByXPath(LoginPageElement.BTNP02_AUTH.getValue()));
            authButton.click();
        } catch (NoSuchElementException e) {
            logger.warn(e);
        }
    }

    private String detCaptchaUrl() {
        logger.info("Determines CAPTCHA URL");

        WebElement captchaField = null;
        try {
            captchaField = driver.findElement(new By.ByXPath(LoginPageElement.FP01_CAPTCHA.getValue()));
        } catch (NoSuchElementException e) {
            logger.warn(e);
        }

        return captchaField != null ? captchaField.getAttribute("src") : null;
    }
}
