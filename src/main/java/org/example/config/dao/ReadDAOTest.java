package org.example.config.dao;

import org.example.config.models.Person;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ReadDAOTest {
    private static int PEOPLE_COUNT;
    private List<Person> people;

    {
        people = new ArrayList<>();

        people.add(new Person(++PEOPLE_COUNT, "Nagibator228", 150, "chepuh"));
        people.add(new Person(++PEOPLE_COUNT, "grandMaster3000", 150, "solidniyDyadya"));
        people.add(new Person(++PEOPLE_COUNT, "Echkere123", 150, "normChel"));
        people.add(new Person(++PEOPLE_COUNT, "Biba", 150, "ogo"));
        people.add(new Person(++PEOPLE_COUNT, "Boba", 150, "ogo"));
    }

    public List<Person> index() {
        return people;
    }

    public Person getPerson(int id) {
        return people.stream().filter(person -> person.getId() == id).findAny().orElse(null);
    }
}
