package org.semul.budny.captcha;

import com.twocaptcha.TwoCaptcha;
import com.twocaptcha.captcha.Normal;

public class CaptchaSolution {
    public static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(CaptchaSolution.class);
    public static String API_KEY = "df1293f1a441c150294d5838fd0b1cb7";

    public static String solution(String captchaPath) {
        logger.info("Captcha solution...");

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
            logger.info("Captcha solved.");
        } catch (Exception e) {
            logger.error(e);
        }

        return captcha.getCode();
    }
}
