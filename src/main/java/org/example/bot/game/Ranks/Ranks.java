package org.example.bot.game.Ranks;

import org.example.bot.database.models.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Ranks {

    public String getPersonStatus(int progress) {
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
            return PersonStatus.PEASANT.toString();
        }
    }

//    public String getLevelBonus() {
//        switch (personStatus) {
//            case "Peasent":
//                return "What are the bonuses? Go to work";
//            case "Knight":
//                return "";
//            case "Herceg":
//                return "";
//            case "King":
//                return "";
//            case "God":
//                return "";
//            default:
//                return "Who are you, soldier?";
//        }
}

