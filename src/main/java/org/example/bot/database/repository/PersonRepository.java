package org.example.bot.database.repository;

import org.example.bot.database.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person,Integer>{

}
