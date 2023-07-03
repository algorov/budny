package org.semul.budny.action;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.semul.budny.captcha.Captcha;
import org.semul.budny.captcha.CaptchaSolution;
import org.semul.budny.exception.FailEmployException;
import org.semul.budny.heroeswm.Paths;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;

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
            WebElement loginField = driver.findElement(new By.ByClassName(Paths.L_EFP01_LOGIN));
            loginField.clear();
            loginField.sendKeys(this.username);

            WebElement passwordField = driver.findElement(new By.ByClassName(Paths.L_EFP01_PASSWORD));
            passwordField.clear();
            passwordField.sendKeys(this.password);

            WebElement authButton = driver.findElement(new By.ByClassName(Paths.L_BTNP01_AUTH));
            authButton.click();
        } catch (NoSuchElementException e) {
        }
    }

    public void signIn(String captchaUrl) {
        if ((Paths.URL + Paths.LOGIN_PATH).equals(this.driver.getCurrentUrl())) {
            try {
                WebElement loginField = driver.findElement(new By.ByXPath(Paths.L_EFP02_LOGIN));
                loginField.clear();
                loginField.sendKeys(this.username);

                WebElement passwordField = driver.findElement(new By.ByXPath(Paths.L_EFP02_PASSWORD));
                passwordField.clear();
                passwordField.sendKeys(this.password);

                String captchaPath = Captcha.save(captchaUrl);
                String code = CaptchaSolution.solution(captchaPath);
                Captcha.delete(captchaPath);

                if (code != null) {
                    WebElement captchaCodeField = driver.findElement(new By.ByXPath(Paths.L_EFP01_CAPTCHA));
                    captchaCodeField.clear();
                    captchaCodeField.sendKeys(code);
                }

                WebElement authButton = driver.findElement(new By.ByXPath(Paths.L_BTNP02_AUTH));
                authButton.click();
            } catch (NoSuchElementException e) {
            }
        }
    }

    public String getCaptchaUrl() {
        WebElement captchaField = null;
        try {
            captchaField = driver.findElement(new By.ByXPath(Paths.OI_FP01_CAPTCHA));
        } catch (NoSuchElementException e) {
            try {
                captchaField = driver.findElement(new By.ByXPath(Paths.L_FP01_CAPTCHA));
            } catch (NoSuchElementException q) {
            }
        }

        return captchaField != null ? captchaField.getAttribute("src") : null;
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
    public void employ() throws FailEmployException {
        this.driver.get(Paths.URL + Paths.MAP_PATH);

        String vacancyUrl;
        String sectorPath = defSector();
        if (sectorPath != null) {
            vacancyUrl = defJobPath(sectorPath);
        } else {
            throw new FailEmployException(">>> [Error] - Sector not defined!");
        }

        if (vacancyUrl != null) {
            driver.get(vacancyUrl);

            String quessCaptchaUrl = getCaptchaUrl();
            if (quessCaptchaUrl != null) {
                String localPath = Captcha.save(quessCaptchaUrl);
                String solution = CaptchaSolution.solution(localPath);
                Captcha.delete(localPath);

                if (solution != null) {
                    WebElement captchaEnterField = driver.findElement(new By.ByXPath(Paths.OI_EFP01_CAPTCHA));
                    captchaEnterField.click();
                    captchaEnterField.sendKeys(solution);
                } else {
                    throw new FailEmployException(">>> [Error] - No solving captcha!");
                }
            }

            try {
                WebElement employButton = driver.findElement(new By.ByXPath(Paths.OI_BTNP01_EMPLOY));
                employButton.click();
            } catch (NoSuchElementException e) {
                throw new FailEmployException(">>> [Error] - NoSuchElementException!");
            }
        } else {
            throw new FailEmployException(">>> [Error] - No vacancies!");
        }
    }

    public boolean checkEmploymentState() {
        if ((Paths.URL + Paths.OI_PATH).equals(this.driver.getCurrentUrl().split("\\?")[0])) {
            WebElement employStatusField = null;
            try {
                employStatusField = this.driver.findElement(new By.ByXPath(Paths.OI_FP01_EMPLOY_STATUS));
            } catch (NoSuchElementException e) {
            }

            if (employStatusField != null) {
                return Paths.OI_EA_SUCCESS.equals(employStatusField.getText());
            } else {
                return false;
            }
        } else {
            this.driver.get(Paths.URL + Paths.HOME_PATH);

            return getEmploymentCountdown() != 0;
        }
    }

    // If successful, it will return a positive number, otherwise - -1.
    private int getEmploymentCountdown() {
        int countdown = 0;

        try {
            String assertion = driver.findElement(new By.ByXPath(Paths.H_FP01_EMPLOY_STATUS)).getText();
            if (!assertion.contains(Paths.H_EA_FREE)) {
                if (assertion.contains(Paths.H_EA_FREE_SOON)) {
                    ArrayList<String> assertionParts = new ArrayList<>(Arrays.asList(assertion.split(" ")));
                    assertionParts.removeIf(part -> !part.matches("\\d+"));
                    countdown = Integer.parseInt(assertionParts.get(0)) * 60;
                } else {
                    String[] assertionParts = assertion.split(" ");
                    LocalTime timeEmployment = LocalTime.parse(assertionParts[assertionParts.length - 1]);
                    LocalTime currentTime = LocalTime.now();

                    int difference = (int) ChronoUnit.MINUTES.between(timeEmployment, currentTime);
                    if (difference < 0) {
                        difference += 1440;
                    }

                    countdown = (60 - difference) * 60;
                }
            }
        } catch (NoSuchElementException e) {
            return -1;
        }

        return countdown;
    }

    private String defSector() {
        WebElement labelField = null;
        try {
            labelField = driver.findElement(new By.ByXPath(Paths.M_FP01_LABEL));
        } catch (NoSuchElementException e) {
        }

        return labelField != null ? Paths.MAP_SECTOR.get(labelField.getText()) : null;
    }

    private String defJobPath(String sectorPath) {
        for (Paths.WorkType item : Paths.WorkType.values()) {
            String url = Paths.URL + Paths.MAP_PATH + "?" + sectorPath + "&st=" + item.getValue();
            this.driver.get(url);

            WebElement vacancyField = null;
            try {
                vacancyField = this.driver.findElement(new By.ByLinkText("»»»"));
            } catch (NoSuchElementException e) {
            }

            if (vacancyField != null) {
                return vacancyField.getAttribute("href");
            }
        }

        return null;
    }
}
