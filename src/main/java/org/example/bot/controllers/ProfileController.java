package org.example.bot.controllers;


import org.example.bot.database.models.Person;
import org.example.bot.database.repository.PersonRepository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProfileController {
    private final PersonRepository personRepository;

    public ProfileController(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @GetMapping("1")
    public String index(Model model) {
        //Получим всех людей из DAO и передадим на отображение и представление
        return null;
    }

    @GetMapping("user/{id}")
    public Person getUserById(@PathVariable int id) {
        return personRepository.findById(id).orElseThrow();
    }

    @GetMapping("getUsers")
    public List<Person> getUsers() {
        return personRepository.findAll();
    }

    @DeleteMapping("user/{id}")
    public void deleteUserById(@PathVariable int id) {
        personRepository.deleteById(id);
    }

    @GetMapping("user")
    public void createUserByMame(@RequestParam String name) {
        Person person = new Person();
        person.setNickname(name);
        person.setRang("huy");
        person.setNumberOfPoints(0);
        personRepository.save(person);
    }

    @GetMapping("update/user/{id}")
    public void updateUserById(@PathVariable int id,
                               @RequestParam String name) {

        Person person = personRepository.findById(id).orElseThrow();
        person.setNickname(name);
        personRepository.save(person);
    }
}
