package org.example.bot.game.Persons;

public class AdminPerson {
    private StartPerson admin;

    public AdminPerson(StartPerson adminPerson) {
        this.admin = adminPerson;
    }

    public int givePoint(StartPerson person, int point) {
        return person.point += point;
    }

}
