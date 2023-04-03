package org.example.Game.Persons;

import org.example.Game.Ranks.Ranks;

public class StartPerson extends Ranks {
    private int id;
    private String userName;
    private int score;
    protected int point;

    public StartPerson(int id, String userName, int score) {
        super(score);
        this.id = id;
        this.userName = userName;
        this.score = score;
    }

    public String getInfo() {
        return "ID- " + id + " Name- " + userName + " number of points" + score;
    }

    public void getPoints(){
        score += point;
    }

//    public static void getUsersTable(){
//
//    }

}
