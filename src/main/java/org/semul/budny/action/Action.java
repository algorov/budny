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
        this.driver.get(Paths.URL + Paths.MAP_PATH);

        String sectorPath = defSector();
        String vacancyUrl = null;

        if (sectorPath != null) {
            vacancyUrl = defJobPath(sectorPath);
        } else {
            System.out.println("Бабубэ0");
        }

        if (vacancyUrl != null) {
            driver.get(vacancyUrl);

            String quessCaptchaUrl = getCaptchaUrl();

            if (quessCaptchaUrl != null) {
                String localPath = Captcha.save(quessCaptchaUrl);
                String solution = CaptchaSolution.solution(localPath);
                Captcha.delete(localPath);

                if (solution != null) {
                    WebElement captchaEnterField = driver.findElement(new By.ByXPath("//*[@id=\"code\"]"));
                    captchaEnterField.click();
                    captchaEnterField.sendKeys(solution);
                } else {
                    System.out.println("Бабубэ");
                }
            }

            try {
                WebElement employButton = driver.findElement(new By.ByXPath("//*[@id=\"wbtn\"]"));
                employButton.click();
            } catch (NoSuchElementException e) {
                System.out.println("Бабубэ2");
            }
        } else {
            System.out.println("Бабубэ3");
        }
    }

    private String defSector() {
        System.out.print(">>> Area definition -> ");

        WebElement labelField = null;

        try {
            labelField = driver.findElement(new By.ByXPath("//*[@id=\"set_mobile_max_width\"]/div[1]/b"));
        } catch (NoSuchElementException e) {
        }

        String label = labelField != null ? Paths.MAP_SECTOR.get(labelField.getText()) : null;
        System.out.println(label);

        return label;
    }

    public String defJobPath(String sectorPath) {
        System.out.print(">>> Get job -> ");

        for (Paths.WorkType item : Paths.WorkType.values()) {
            WebElement vacancyField = null;

            String url = Paths.URL + Paths.MAP_PATH + "?" + sectorPath + "&st=" + item.getValue();
            this.driver.get(url);

            try {
                vacancyField = this.driver.findElement(new By.ByLinkText("»»»"));
            } catch (NoSuchElementException e) {
            }

            if (vacancyField != null) {
                return vacancyField.getAttribute("href");
            }
        }

        System.out.println("empty.");

        return null;
    }
}
