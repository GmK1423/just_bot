package org.example.config.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;

@Controller
public class FirstController {

    @GetMapping("/bot_open")
    public String botMain(Model model){
        model.addAttribute("message",
                "hello user \n" +
                        "\n" +
                        "1. Profile \n" +
                        "2. Shop \n" +
                        "3. Exit \n");
//        main бота и переход в следующий контроллер
//        1. Профиль
//        2. Магазин
//         ..............
//         ..  Выход
        return "something";
    }

}
