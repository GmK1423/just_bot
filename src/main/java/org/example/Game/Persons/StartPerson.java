package org.example.Game.Persons;

import org.example.Game.Ranks.Ranks;

public class StartPerson extends Ranks {
    private int id;
    private static String userName;
    static{
        userName = StartPerson.getName();
    }
    private static int score;
    static{
        score = StartPerson.getScore();
    }
    protected int point;

    public StartPerson(int id) {
        super(score);
        this.id = id;
    }

    public static String getName() {
        return "NotImplement";
    }

    public static int getScore() {
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
