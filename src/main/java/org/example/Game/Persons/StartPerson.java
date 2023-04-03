package org.example.Game.Persons;

import org.example.Game.Ranks.Ranks;

public class StartPerson {
    private int id;
    private String userName;

    {
        userName = getName();
    }

    private int score;

    {
        score = getScore();
    }

    protected int point = 0;

    public StartPerson(int id) {
        this.id = id;
    }

    private String getName() {
        return "NotImplement";
    }

    private int getScore() {
        return -1;
    }

    public String getInfo() {
        return "ID- " + id + " Name- " + userName + " number of points" + score;
    }

    public void getPoints() {
        score += point;
    }

//    public static void getUsersTable(){
//
//    }

}
