package org.semul.budny.heroeswm.path.page.map;

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
