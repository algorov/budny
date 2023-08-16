package org.semul.budny.heroeswm.path.page;


public enum PagePath {
    LOGIN("login.php"),
    HOME("home.php"),
    MAP("map.php"),
    OI("object-info.php");

    public static final String URL = "https://www.heroeswm.ru/";
    private final String value;

    PagePath(String path) {
        this.value = path;
    }

    public String getValue() {
        return this.value;
    }
}
