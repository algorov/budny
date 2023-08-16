package org.semul.budny.heroeswm.path.page.login;

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
