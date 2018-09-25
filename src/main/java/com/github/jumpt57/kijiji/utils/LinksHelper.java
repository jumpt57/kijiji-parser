package com.github.jumpt57.kijiji.utils;

public enum LinksHelper {
    BASE_URL("https://www.kijiji.ca"),
    MAGIC_PAGE("https://www.kijiji.ca/b-appartement-condo-studio-2-1-2/grand-montreal/page-9999999/c212l80002?ad=offering&meuble=1"),
    TEMPLATE_PAGE("https://www.kijiji.ca/b-appartement-condo-studio-2-1-2/grand-montreal/page-%s/c212l80002?ad=offering&meuble=1");

    private String value;

    LinksHelper(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
