package org.semul.budny.connection;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.semul.budny.captcha.Captcha;
import org.semul.budny.captcha.CaptchaSolution;
import org.semul.budny.heroeswm.HeroesWMStructure;

public class Action {
    private final ChromeDriver driver;
    private final String username;
    private final String password;

    Action(ChromeDriver driver, String username, String password) {
        this.driver = driver;
        this.username = username;
        this.password = password;
    }

    public void signIn() {
        this.driver.get(HeroesWMStructure.URL);

        try {
            WebElement loginField = driver.findElement(new By.ByClassName("inp_login"));
            loginField.clear();
            loginField.sendKeys(this.username);

            WebElement passwordField = driver.findElement(new By.ByClassName("inp_pass"));
            passwordField.clear();
            passwordField.sendKeys(this.password);

            WebElement authButton = driver.findElement(new By.ByClassName("entergame"));
            authButton.click();
        } catch (NoSuchElementException e) {}
    }

    public void signIn(String captchaUrl) {
        try {
            WebElement loginField = driver.findElement(
                    new By.ByXPath("/html/body/center/table/tbody/tr/td/table/" +
                            "tbody/tr/td/form/table/tbody/tr[1]/td[2]/input"));
            loginField.clear();
            loginField.sendKeys(this.username);

            WebElement passwordField = driver.findElement(
                    new By.ByXPath("/html/body/center/table/tbody/tr/td/table/tbody/" +
                            "tr/td/form/table/tbody/tr[2]/td[2]/input"));
            passwordField.clear();
            passwordField.sendKeys(this.password);

            String captchaPath = Captcha.save(captchaUrl);
            String code = CaptchaSolution.solution(captchaPath);
            Captcha.delete(captchaPath);

            if (code != null) {
                WebElement captchaCodeField = driver.findElement(
                        new By.ByXPath("/html/body/center/table/tbody/tr/td/table/tbody/" +
                                "tr/td/form/table/tbody/tr[4]/td/table/tbody/tr/td[2]/input"));
                captchaCodeField.clear();
                captchaCodeField.sendKeys(code);
            }

            WebElement authButton = driver.findElement(
                    new By.ByXPath("/html/body/center/table/tbody/tr/td/table/tbody/" +
                            "tr/td/form/table/tbody/tr[5]/td/input[1]"));
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
                captchaField = driver.findElement(
                        new By.ByXPath("/html/body/center/table/tbody/tr/td/table/tbody/tr/td/"
                                + "form/table/tbody/tr[4]/td/table/tbody/tr/td[1]/img"));
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

            return !((HeroesWMStructure.URL).equals(this.driver.getCurrentUrl()) ||
                    (HeroesWMStructure.URL + HeroesWMStructure.LOGIN_PATH).equals(this.driver.getCurrentUrl()));
        }

        return false;
    }

    // Apparatus employed.
    public void employ() {
    }
}
