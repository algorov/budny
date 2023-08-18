package org.semul.budny.captcha;

import com.twocaptcha.TwoCaptcha;
import com.twocaptcha.captcha.Normal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class CaptchaSolution {
    public static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(CaptchaSolution.class);
    public static String API_KEY = setApiKey();

    public static String setApiKey() {
        StringBuilder builder = new StringBuilder();

        try (FileReader reader = new FileReader(System.getProperty("user.dir") + "/config/secret.txt")) {
            int symb;
            int cursor = 0;
            while ((symb = reader.read()) != -1) {
                if (cursor > 7) {
                    builder.append((char) symb);
                }

                cursor++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return builder.toString();
    }

    public static String solution(String captchaPath) {
        logger.info("Captcha solution");

        TwoCaptcha twoCaptcha = new TwoCaptcha(API_KEY);
        twoCaptcha.setDefaultTimeout(60);

        Normal captcha = new Normal();
        captcha.setFile(captchaPath);
        captcha.setMinLen(0);
        captcha.setMaxLen(6);
        captcha.setCaseSensitive(true);
        captcha.setLang("en");

        try {
            twoCaptcha.solve(captcha);
            logger.info("Captcha solved");
        } catch (Exception e) {
            logger.error(e);
        }

        return captcha.getCode();
    }
}
