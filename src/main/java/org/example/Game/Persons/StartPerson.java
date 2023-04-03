package org.example.Game.Persons;

import org.example.Game.Ranks.Ranks;

public class StartPerson extends Ranks{
    private int id;
    private String userName;

    {
        userName = getName();
    }

    private static int score;

    static {
        score = getScore();
    }

    protected int point = 0;

    public StartPerson(int id) {
        super(score);
        this.id = id;
    }

    private String getName() {
        return "NotImplement";
    }

    private static int getScore() {
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
