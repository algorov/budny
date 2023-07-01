package org.semul.budny.heroeswm;

import java.util.Map;

import static java.util.Map.entry;

public class Paths {
    /* *** Page paths *** */
    public static final String URL = "https://www.heroeswm.ru/";
    public static final String LOGIN_PATH = "login.php";
    public static final String HOME_PATH = "home.php";
    public static final String MAP_PATH = "map.php";


    /* *** Login page *** */
    // Paths for web elements (ByClassName).
    public static final String LOGIN_FIELD_PATH = "inp_login";
    public static final String PASSWORD_FIELD_PATH = "inp_pass";
    public static final String AUTH_BTN_PATH = "entergame";

    // Paths for web elements (ByXPath) in case of incorrect data entry or if captcha detection is needed.
    public static final String LOGIN_FIELD_PATH_2 = "/html/body/center/table/tbody/tr/td/table/" +
            "tbody/tr/td/form/table/tbody/tr[1]/td[2]/input";
    public static final String PASSWORD_FIELD_PATH_2 = "/html/body/center/table/tbody/tr/td/table/tbody/" +
            "tr/td/form/table/tbody/tr[2]/td[2]/input";
    public static final String CAPTCHA_FIELD_PATH = "/html/body/center/table/tbody/tr/td/table/tbody/tr/td/" +
            "form/table/tbody/tr[4]/td/table/tbody/tr/td[1]/img";
    public static final String CAPTCHA_ENTER_FIELD_PATH = "/html/body/center/table/tbody/tr/td/table/tbody/" +
            "tr/td/form/table/tbody/tr[4]/td/table/tbody/tr/td[2]/input";
    public static final String AUTH_BTN_PATH_2 = "/html/body/center/table/tbody/tr/td/table/tbody/" +
            "tr/td/form/table/tbody/tr[5]/td/input[1]";


    /* *** Map *** */
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

    enum WorkType {
        MINING("sh"),
        PROCESSING("fc"),
        PRODUCTION("mn");

        private final String value;

        public String getValue() {
            return this.value;
        }

        WorkType(String value) {
            this.value = value;
        }
    }
}
