package org.semul.budny.heroeswm;

import java.util.Map;

import static java.util.Map.entry;

public class Paths {
    public static final String URL = "https://www.heroeswm.ru/";

    public enum PagePath {
        LOGIN("login.php"),
        HOME("home.php"),
        MAP("map.php"),
        OI("object-info.php");

        private final String value;

        PagePath(String path) {
            this.value = path;
        }

        public String getValue() {
            return this.value;
        }
    }

    public enum LoginPageElement {
        // Paths for web elements (ByClassName).
        EFP01_LOGIN("inp_login"),
        EFP01_PASSWORD("inp_pass"),
        BTNP01_AUTH("entergame"),

        // Paths for web elements (ByXPath) in case of incorrect data entry or if captcha detection is needed.
        EFP02_LOGIN("/html/body/center/table/tbody/tr/td/table/tbody/tr/td/form/table/tbody/tr[1]/td[2]/input"),
        EFP02_PASSWORD("/html/body/center/table/tbody/tr/td/table/tbody/tr/td/form/table/tbody/tr[2]/td[2]/input"),
        FP01_CAPTCHA("/html/body/center/table/tbody/tr/td/table/tbody/tr/td/form/table/tbody/tr[4]/td/table/tbody/tr/td[1]/img"),
        EFP01_CAPTCHA("/html/body/center/table/tbody/tr/td/table/tbody/tr/td/form/table/tbody/tr[4]/td/table/tbody/tr/td[2]/input"),
        BTNP02_AUTH("/html/body/center/table/tbody/tr/td/table/tbody/tr/td/form/table/tbody/tr[5]/td/input[1]");

        private final String value;

        LoginPageElement(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }

    public static final Map<String, String> MAP_SECTOR = Map.ofEntries(
            entry("Ungovernable Steppe", "cx=48&cy=48"),
            entry("Eagle Nest", "cx=49&cy=48"),
            entry("Peaceful Camp", "cx=50&cy=48"),
            entry("Crystal Garden", "cx=51&cy=48"),
            entry("Fairy Trees", "cx=52&cy=48"),
            entry("Sunny City", "cx=48&cy=49"),
            entry("Shining Spring", "cx=49&cy=49"),
            entry("Tiger Lake", "cx=50&cy=49"),
            entry("Rogues' Wood", "cx=51&cy=49"),
            entry("Bear Mountain", "cx=52&cy=49"),
            entry("Mithril Coast", "cx=53&cy=49"),
            entry("Sublime Arbor", "cx=48&cy=50"),
            entry("Green Wood", "cx=49&cy=50"),
            entry("Empire Capital", "cx=50&cy=50"),
            entry("East River", "cx=51&cy=50"),
            entry("Magma Mines", "cx=52&cy=50"),
            entry("Harbour City", "cx=53&cy=50"),
            entry("Lizard Lowland", "cx=49&cy=51"),
            entry("Wolf Dale", "cx=50&cy=51"),
            entry("Dragons' Caves", "cx=51&cy=51"),
            entry("The Wilderness", "cx=49&cy=52"),
            entry("Portal Ruins", "cx=50&cy=52"),
            entry("Great Wall", "cx=51&cy=52"),
            entry("Titans' Valley", "cx=51&cy=53"),
            entry("Fishing Village", "cx=52&cy=53"),
            entry("Kingdom Castle", "cx=52&cy=54")
    );

    public enum MapPageElement {
        // Paths for web elements (ByXPath).
        FP01_LABEL("//*[@id=\"set_mobile_max_width\"]/div[1]/b");

        private final String value;

        MapPageElement(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }

    public enum WorkType {
        MINING("sh"),
        PROCESSING("fc"),
        PRODUCTION("mn");

        private final String value;

        WorkType(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }

    public enum OIPageElement {
        // Paths for web elements (ByXPath).
        FP01_CAPTCHA("//*[@id=\"getjob_form\"]/img[1]"),
        EFP01_CAPTCHA("//*[@id=\"code\"]"),
        BTNP01_EMPLOY("//*[@id=\"wbtn\"]"),
        FP01_EMPLOY_STATUS("/html/body/center/table/tbody/tr/td/table/tbody/tr/td/table[1]/tbody/tr/td/b"),
        EA_SUCCESS("Вы устроены на работу!");

        private final String value;

        OIPageElement(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }

    public enum HomePageElement {
        // Paths for web elements (ByXPath).
        FP01_EMPLOY_STATUS("//*[@id=\"set_mobile_max_width\"]/div[2]/div[2]/div[2]/span"),
        EA_FREE("Вы нигде не работаете."),
        EA_FREE_SOON("Последнее место работы:");

        private final String value;

        HomePageElement(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }
}
