package org.example.bot.config.bot.Ranks;

public enum PersonStatus {
    PEASANT("Peasant"),
    KNIGHT("Knight"),
    BARON("Baron"),
    HERCEG("Herceg"),
    KING("King"),
    GOD("God");

    private String str;

    PersonStatus(String str) {
        this.str = str;
    }
    @Override
    public String toString(){
        return str;
    }
}
