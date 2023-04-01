package org.example.Game.Ranks;

import org.apache.commons.lang3.NotImplementedException;

public class Ranks {
    private int gamePoints;
    private String personStatus;

    public Ranks(int gamePoints) {
        this.gamePoints = gamePoints;
        this.personStatus = setPersonStatus();
        System.out.println(personStatus);
    }


    private String setPersonStatus() {
        if ((gamePoints >= 0) && (gamePoints <= 1000)) {
            if (gamePoints < 150) {
                return PersonStatus.PEASANT.getValue();
            } else if (gamePoints < 350) {
                return PersonStatus.KNIGHT.getValue();
            } else if (gamePoints < 500) {
                return PersonStatus.BARON.getValue();
            } else if (gamePoints < 750) {
                return PersonStatus.HERCEG.getValue();
            } else if (gamePoints < 950) {
                return PersonStatus.KING.getValue();
            } else {
                return PersonStatus.GOD.getValue();
            }
        } else {
            return "ErrorStatus";
        }
    }

    public String getLevelBonus(){
        switch (personStatus){
            case "Peasent":
                return "";
            case "Knight":
                return "";
            case "Herceg":
                return "";
            case "King":
                return "";
            case "God":
                return "";
            default:
                return "Who are you, soldier?";
        }
    }



}
