package org.semul.budny.heroeswm.path.page.oi;

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