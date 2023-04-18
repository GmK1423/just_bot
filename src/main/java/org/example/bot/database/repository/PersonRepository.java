package org.example.bot.database.repository;

import org.example.bot.database.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface PersonRepository extends JpaRepository<Person,Long>{

}
