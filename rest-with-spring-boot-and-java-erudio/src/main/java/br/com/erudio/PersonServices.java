package br.com.erudio;

import br.com.erudio.model.Person;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

@Service
public class PersonServices {

    private final AtomicLong counter = new AtomicLong();
    private Logger logger = Logger.getLogger(PersonServices.class.getName());

    public ArrayList<Person> findAll() {
        logger.info("Finding all People!");

        ArrayList<Person> persons = new ArrayList<Person>();

        for (int i = 1; i < 9; i++) {
            Person person = mockPerson(i);
            persons.add(person);
        }

        return persons;
    }

    public Person findById(String id) {
        logger.info("Finding one Person!");

        Person person = new Person();

        person.setId(counter.incrementAndGet());
        person.setFirstName("Levi");
        person.setLastName("Marques");
        person.setAdress("Mogi Guaçu - SP - Brasil");
        person.setGender("Male");

        return person;
    }

    public Person create(Person person) {
        logger.info("Creating one Person!");

        return person;
    }

    public Person update(Person person) {
        logger.info("Updating one Person!");

        return person;
    }

    public void delete (String id) {
        logger.info("Deleting one Person!");

    }

    private Person mockPerson(int i) {
        Person person = new Person();

        person.setId(counter.incrementAndGet());
        person.setFirstName("Firstname " + i);
        person.setLastName("Lastname " + i);
        person.setAdress("Some address in Brasil");
        person.setGender("Male");

        return person;
    }
}
