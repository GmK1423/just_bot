package org.example.config.controllers;


import org.example.config.dao.ReadDAOTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class Profile {

    private final ReadDAOTest personDAO;

    @Autowired
    public Profile(ReadDAOTest personDAO) {
        this.personDAO = personDAO;
    }

    @GetMapping()
    public String index(Model model) {
        //Получим всех людей из DAO и передадим на отображение и представление
        return null;
    }

    @GetMapping()
    public String show(int id, Model model) {
        //Получим одного человека из DAO и передадим на отображение и представление
        return null;
    }
}
