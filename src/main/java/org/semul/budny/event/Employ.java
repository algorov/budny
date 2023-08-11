package org.semul.budny.event;

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

public class Employ extends Intentionable {
    public static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Employ.class);

    Employ(ChromeDriver driver, String username, String password) {
        super(driver, username, password);
        logger.info("Initialization.");
    }

    public void execute() throws FailEmployException {
        logger.info("Execute.");
        this.driver.get(Paths.URL + Paths.PagePath.MAP.getValue());

        String vacancyUrl;
        String sectorPath = defSector();
        logger.info("Sector path: " + sectorPath + ".");

        if (sectorPath != null) {
            vacancyUrl = defJobPath(sectorPath);
        } else {
            String message = "Sector not defined!";
            logger.warn(message);
            throw new FailEmployException(message);
        }

        logger.info("Vacancy URL: " + vacancyUrl + ".");

        if (vacancyUrl != null) {
            driver.get(vacancyUrl);

            String quessCaptchaUrl = getCaptchaUrl();
            logger.info("Quess captcha URL: " + quessCaptchaUrl + ".");

            if (quessCaptchaUrl != null) {
                String localPath = Captcha.save(quessCaptchaUrl);
                logger.info("Captcha local PATH: " + localPath + ".");

                String solution = CaptchaSolution.solution(localPath);
                logger.info("Captcha colution: " + solution + ".");
                Captcha.delete(localPath);

                if (solution != null) {
                    WebElement captchaEnterField = driver.findElement(new By.ByXPath(Paths.OIPageElement.EFP01_CAPTCHA.getValue()));
                    captchaEnterField.click();
                    captchaEnterField.sendKeys(solution);
                } else {
                    String message = "No solving captcha!";
                    logger.warn(message);
                    throw new FailEmployException(message);
                }
            }

            try {
                WebElement employButton = driver.findElement(new By.ByXPath(Paths.OIPageElement.BTNP01_EMPLOY.getValue()));
                employButton.click();
            } catch (NoSuchElementException e) {
                String message = "NoSuchElementException!";
                logger.warn(message);
                throw new FailEmployException(message);
            }

            boolean status = status();
            logger.info("Employ status: " + status + ".");

            if (!status) {
                String message = "Captcha solved incorrectly!";
                logger.warn(message);
                throw new FailEmployException(message);
            }
        } else {
            String message = "No vacancies!";
            logger.warn(message);
            throw new FailEmployException(message);
        }
    }

    public boolean status() {
        logger.info("Get status.");

        if ((Paths.URL + Paths.PagePath.OI.getValue()).equals(this.driver.getCurrentUrl().split("\\?")[0])) {
            WebElement employStatusField = null;
            try {
                employStatusField = this.driver.findElement(new By.ByXPath(Paths.OIPageElement.FP01_EMPLOY_STATUS.getValue()));
            } catch (NoSuchElementException ignored) {
            }

            if (employStatusField != null) {
                return Paths.OIPageElement.EA_SUCCESS.getValue().equals(employStatusField.getText());
            } else {
                return false;
            }
        } else {
            this.driver.get(Paths.URL + Paths.PagePath.HOME.getValue());
            int workEndCountdown = getWorkEndCountdown();

            logger.info("Work end countdown: " + workEndCountdown + " sec.");

            return workEndCountdown != 0;
        }
    }

    // Предзназначен для подсчета обратного времени, чтобы закончить работу. Если не удалось определить - то возвращает -1.
    public int getWorkEndCountdown() {
        logger.info("Get work end countdown.");

        int countdown = 0;

        try {
            this.driver.get(Paths.URL + Paths.PagePath.HOME.getValue());

            String assertion = driver.findElement(new By.ByXPath(Paths.HomePageElement.FP01_EMPLOY_STATUS.getValue())).getText();
            if (!assertion.contains(Paths.HomePageElement.EA_FREE.getValue())) {
                if (assertion.contains(Paths.HomePageElement.EA_FREE_SOON.getValue())) {
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
            countdown = -1;
        }

        System.out.println("Countdown = " + countdown);

        return countdown;
    }

    // Возвращет сеткор или null.
    private String defSector() {
        logger.info("Def sector.");

        WebElement labelField = null;
        try {
            labelField = driver.findElement(new By.ByXPath(Paths.MapPageElement.FP01_LABEL.getValue()));
        } catch (NoSuchElementException e) {
            logger.warn(e);
        }

        return labelField != null ? Paths.MAP_SECTOR.get(labelField.getText()) : null;
    }

    // Возвращет path или null.
    private String defJobPath(String sectorPath) {
        logger.info("Def job PATH.");

        for (Paths.WorkType item : Paths.WorkType.values()) {
            String url = Paths.URL + Paths.PagePath.MAP.getValue() + "?" + sectorPath + "&st=" + item.getValue();
            this.driver.get(url);

            WebElement vacancyField = null;
            try {
                vacancyField = this.driver.findElement(new By.ByLinkText("»»»"));
            } catch (NoSuchElementException e) {
                logger.warn(e);
            }

            if (vacancyField != null) {
                return vacancyField.getAttribute("href");
            }
        }

        return null;
    }

    // Возвращет url или null.
    private String getCaptchaUrl() {
        logger.info("Get captcha URL.");

        WebElement captchaField = null;
        try {
            captchaField = driver.findElement(new By.ByXPath(Paths.OIPageElement.FP01_CAPTCHA.getValue()));
        } catch (NoSuchElementException e) {
            logger.warn(e);
        }

        return captchaField != null ? captchaField.getAttribute("src") : null;
    }
}
