package org.example.game.Persons;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component

public class AdminPerson {
    private StartPerson admin;

    @Autowired
    public AdminPerson(StartPerson adminPerson) {
        this.admin = adminPerson;
    }

    public int givePoint(StartPerson person, int point) {
        return person.point += point;
    }

}
