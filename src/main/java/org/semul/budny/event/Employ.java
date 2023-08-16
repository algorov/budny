package org.semul.budny.event;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.semul.budny.captcha.Captcha;
import org.semul.budny.captcha.CaptchaSolution;
import org.semul.budny.exception.FailEmployException;
import org.semul.budny.heroeswm.path.page.PagePath;
import org.semul.budny.heroeswm.path.page.home.HomePageElement;
import org.semul.budny.heroeswm.path.page.map.MapPageElement;
import org.semul.budny.heroeswm.path.page.map.WorkType;
import org.semul.budny.heroeswm.path.page.oi.OIPageElement;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;

import static org.semul.budny.heroeswm.path.page.PagePath.URL;
import static org.semul.budny.heroeswm.path.page.map.MapSector.MAP_SECTOR;

public class Employ extends EventAbstract {
    public static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Employ.class);

    Employ(ChromeDriver driver, String username, String password) {
        super(driver, username, password);
        logger.info("Initialization");
    }

    @Override
    public void run() throws FailEmployException {
        logger.info("Execute");
        this.driver.get(URL + PagePath.MAP.getValue());

        String sectorPath = detSector();
        logger.info("Sector PATH: " + sectorPath);

        String vacancyUrl;
        if (sectorPath != null) {
            vacancyUrl = detJobPath(sectorPath);
        } else {
            String message = "Sector not determined!";
            logger.warn(message);
            throw new FailEmployException(message);
        }
        logger.info("Vacancy URL: " + vacancyUrl);

        if (vacancyUrl != null) {
            driver.get(vacancyUrl);

            String quessCaptchaUrl = getCaptchaUrl();
            logger.info("Quess CAPTCHA URL: " + quessCaptchaUrl);
            if (quessCaptchaUrl != null) {
                String localPath = Captcha.save(quessCaptchaUrl);
                logger.info("CAPTCHA PATH: " + localPath);

                String solution = CaptchaSolution.solution(localPath);
                logger.info("CAPTCHA solution: " + solution);
                Captcha.delete(localPath);

                if (solution != null) {
                    WebElement captchaEnterField = driver.findElement(new By.ByXPath(OIPageElement.EFP01_CAPTCHA.getValue()));
                    captchaEnterField.click();
                    captchaEnterField.sendKeys(solution);
                } else {
                    String message = "No solving CAPTCHA!";
                    logger.warn(message);
                    throw new FailEmployException(message);
                }
            }

            try {
                WebElement employButton = driver.findElement(new By.ByXPath(OIPageElement.BTNP01_EMPLOY.getValue()));
                employButton.click();
            } catch (NoSuchElementException e) {
                String message = "NoSuchElementException!";
                logger.warn(message);
                throw new FailEmployException(message);
            }

            boolean status = getStatus();
            logger.info("Employ status: " + status);
            if (!status) {
                String message = "CAPTCHA solved incorrectly!";
                logger.warn(message);
                throw new FailEmployException(message);
            }
        } else {
            String message = "No vacancies!";
            logger.warn(message);
            throw new FailEmployException(message);
        }
    }

    private boolean getStatus() {
        logger.info("Get status");

        if ((URL + PagePath.OI.getValue()).equals(this.driver.getCurrentUrl().split("\\?")[0])) {
            WebElement statusField = null;
            try {
                statusField = this.driver.findElement(new By.ByXPath(OIPageElement.FP01_EMPLOY_STATUS.getValue()));
            } catch (NoSuchElementException ignored) {
            }

            if (statusField != null) {
                return OIPageElement.EA_SUCCESS.getValue().equals(statusField.getText());
            } else {
                return false;
            }
        } else {
            this.driver.get(URL + PagePath.HOME.getValue());
            int countdown = detWorkEndCountdown();
            logger.info("Work end countdown: " + countdown + " sec");

            return countdown != 0;
        }
    }

    public int detWorkEndCountdown() {
        logger.info("Determines work end countdown");

        int countdown = 0;
        try {
            this.driver.get(URL + PagePath.HOME.getValue());

            String assertion = driver.findElement(new By.ByXPath(HomePageElement.FP01_EMPLOY_STATUS.getValue())).getText();
            if (!assertion.contains(HomePageElement.EA_FREE.getValue())) {
                if (assertion.contains(HomePageElement.EA_FREE_SOON.getValue())) {
                    ArrayList<String> assertionParts = new ArrayList<>(Arrays.asList(assertion.split(" ")));
                    assertionParts.removeIf(part -> !part.matches("\\d+"));
                    countdown = Integer.parseInt(assertionParts.get(0)) * 60;
                } else {
                    String[] assertionParts = assertion.split(" ");
                    LocalTime timeEmpl = LocalTime.parse(assertionParts[assertionParts.length - 1]);
                    LocalTime curTime = LocalTime.now();

                    int diff = (int) ChronoUnit.MINUTES.between(timeEmpl, curTime);
                    if (diff < 0) {
                        diff += 1440;
                    }

                    countdown = (60 - diff) * 60;
                }
            }
        } catch (NoSuchElementException e) {
            countdown = -1;
        }

        return countdown;
    }

    private String detSector() {
        logger.info("Determines sector");

        try {
            WebElement labelField = driver.findElement(new By.ByXPath(MapPageElement.FP01_LABEL.getValue()));
            return MAP_SECTOR.get(labelField.getText());
        } catch (NoSuchElementException e) {
            logger.warn(e);
        }

        return null;
    }

    private String detJobPath(String sectorPath) {
        logger.info("Def job PATH");

        for (WorkType item : WorkType.values()) {
            String url = URL + PagePath.MAP.getValue() + "?" + sectorPath + "&st=" + item.getValue();
            this.driver.get(url);

            try {
                WebElement vacField = this.driver.findElement(new By.ByLinkText("»»»"));
                return vacField.getAttribute("href");
            } catch (NoSuchElementException e) {
                logger.warn(e);
            }
        }

        return null;
    }

    private String getCaptchaUrl() {
        logger.info("Get CAPTCHA URL");

        try {
            WebElement field = driver.findElement(new By.ByXPath(OIPageElement.FP01_CAPTCHA.getValue()));
            return field.getAttribute("src");
        } catch (NoSuchElementException e) {
            logger.warn(e);
        }

        return null;
    }
}
