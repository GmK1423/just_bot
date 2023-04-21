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
    private long id;
    private String nickname;
    //private String race;
    private int numberOfPoints;
    private String rang;

    private boolean admin;

    public void setId(long id) {
        this.id = id;
    }

    public void setNumberOfPoints(int numberOfPoints) {
        this.numberOfPoints = numberOfPoints;
    }

    public void setRang(String rang) {
        this.rang = rang;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public Person() {
    }


    public long getId() {
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

//    public void setNumberOfPoints(int numberOfPoints) {
//        this.numberOfPoints = numberOfPoints;
//    }

//    public void setRang(String rang) {
//        this.rang = rang;
//    }
}
