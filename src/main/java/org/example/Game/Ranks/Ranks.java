package org.example.Game.Ranks;

import org.example.Game.Persons.StartPerson;

public class Ranks {
    private int progress;
    private String personStatus;
    private String levelBonus;

    public Ranks(StartPerson person) {

        this.progress = person.getProgress();
        this.personStatus = getPersonStatus();
        this.levelBonus = getLevelBonus();
//        System.out.println(personStatus);
    }


    private String getPersonStatus() {
        if ((progress >= 0) && (progress <= 1000)) {
            if (progress < 150) {
                return PersonStatus.PEASANT.toString();
            } else if (progress < 350) {
                return PersonStatus.KNIGHT.toString();
            } else if (progress < 500) {
                return PersonStatus.BARON.toString();
            } else if (progress < 750) {
                return PersonStatus.HERCEG.toString();
            } else if (progress < 950) {
                return PersonStatus.KING.toString();
            } else {
                return PersonStatus.GOD.toString();
            }
        } else {
            return "ErrorStatus";
        }
    }

    public String getLevelBonus(){
        switch (personStatus){
            case "Peasent":
                return "What are the bonuses? Go to work";
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
