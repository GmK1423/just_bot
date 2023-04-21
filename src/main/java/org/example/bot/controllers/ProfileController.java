package org.example.bot.controllers;


import org.example.bot.database.models.Person;
import org.example.bot.database.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProfileController {
    @Autowired
    private final PersonRepository personRepository;

    public ProfileController(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public PersonRepository getPersonRepository() {
        return personRepository;
    }

//    @GetMapping("1")
//    public String index(Model model) {
//        //Получим всех людей из DAO и передадим на отображение и представление
//        return null;
//    }

    @GetMapping("user/{id}")
    public Person getUserById(@PathVariable Long id) {
        return personRepository.findById(id).orElseThrow();
    }

    @GetMapping("getUsers")
    public List<Person> getUsers() {
        return personRepository.findAll();
    }

    @DeleteMapping("user/{id}")
    public void deleteUserById(@PathVariable Long id) {
        personRepository.deleteById(id);
    }

    @GetMapping("user")
    public void createUserByName(@RequestParam String name) {
        Person person = new Person();
        person.setNickname(name);
        personRepository.save(person);
    }

    @GetMapping("update/user/{id}")
    public void updateUserById(@PathVariable Long id,
                               @RequestParam String name) {

        Person person = personRepository.findById(id).orElseThrow();
        person.setNickname(name);
        personRepository.save(person);
    }

    public void giveAdminStatus(@PathVariable Long id){
        Person person = personRepository.findById(id).orElseThrow();
        person.setAdmin(true);
        personRepository.save(person);
    }
}
