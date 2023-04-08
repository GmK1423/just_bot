package org.example.config.models;

public class Person {
    private int id;
    private String nickName;
    //private String race;
    private int numberOfPoints;
    private String rang;

    public Person(int id, String nickName, int numberOfPoints, String rang) {
        this.id = id;
        this.nickName = nickName;
        this.numberOfPoints = numberOfPoints;
        this.rang = rang;
    }

    public int getId() {
        return id;
    }

    public String getNickName() {
        return nickName;
    }

    public int getNumberOfPoints() {
        return numberOfPoints;
    }

    public String getRang() {
        return rang;
    }

//    public void setNickName(String nickName) {
//        this.nickName = nickName;
//    }
}
