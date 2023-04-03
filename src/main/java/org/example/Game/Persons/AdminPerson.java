package org.example.Game.Persons;

public class AdminPerson extends StartPerson {

    public AdminPerson(int id) {
        super(id);
    }

    public int givePoint(StartPerson person, int point) {
        return person.point += point;
    }

}
