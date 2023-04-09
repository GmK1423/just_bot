package org.example.bot.database.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
@Entity
public class Person {
    @Id
    @GeneratedValue
    private int id;
    @NotEmpty(message = "Name should not be empty")
    @Size(min = 3, max = 15, message = "Name should be between 3 and 15 characters")
    private String nickname;
    //private String race;
    @Min(value = 0, message = "Number of points should be greater than 0")
    @Column(name = "numberofpoints")
    private int numberOfPoints;
    private String rang;

    public Person(int id, String nickname, int numberOfPoints, String rang) {
        this.id = id;
        this.nickname = nickname;
        this.numberOfPoints = numberOfPoints;
        this.rang = rang;
    }

    public Person() {
    }

    public int getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public int getNumberOfPoints() {
        return numberOfPoints;
    }

    public String getRang() {
        return rang;
    }


    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setNumberOfPoints(int numberOfPoints) {
        this.numberOfPoints = numberOfPoints;
    }

    public void setRang(String rang) {
        this.rang = rang;
    }
//    public void setNickName(String nickName) {
//        this.nickName = nickName;
//    }
}
