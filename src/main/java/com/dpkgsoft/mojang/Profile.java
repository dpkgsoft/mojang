package com.dpkgsoft.mojang;

public class Profile {
    private final String uuid;
    private final String playerName;
    private boolean legacy = false;
    private boolean demo = false;

    public Profile(String uuid, String playerName) {
        this.uuid = uuid;
        this.playerName = playerName;
    }

    public Profile(String uuid, String playerName, boolean demo, boolean legacy) {
        this.uuid = uuid;
        this.playerName = playerName;
        this.demo = demo;
        this.legacy = legacy;
    }

    public String getUuid() {
        return uuid;
    }

    public String getPlayerName() {
        return playerName;
    }

    public boolean isLegacy() {
        return legacy;
    }

    public boolean isDemo() {
        return demo;
    }
}
