package com.fordexplorer.clique.db;

import com.fordexplorer.clique.data.Person;
import org.springframework.data.repository.CrudRepository;

public interface PersonRepository extends CrudRepository<Person, Long> {

    Person findPersonByUsername(String username);

}
