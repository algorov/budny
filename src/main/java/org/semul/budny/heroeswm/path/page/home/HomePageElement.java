package org.semul.budny.heroeswm.path.page.home;

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
