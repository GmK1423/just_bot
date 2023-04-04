package org.example.Game.Persons;

public class StartPerson{
    private int id;
    private String userName;
    private int progress;
    protected int point = 0;

    public StartPerson(int id, String userName, int progress) {
        this.id = id;
        this.userName = userName;
        this.progress = progress;
    }

    public String getName() {
        return userName;
    }

    public int getProgress() {
        return progress;
    }

    public String getInfo() {
        return "ID- " + id + " Name- " + userName + " number of points" + progress;
    }

    public void getPoints() {
        progress += point;
    }

//    public static void getUsersTable(){
//
//    }

}
