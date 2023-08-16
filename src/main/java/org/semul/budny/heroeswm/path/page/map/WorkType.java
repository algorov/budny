package org.semul.budny.heroeswm.path.page.map;

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
