package com.fordexplorer.clique;

import com.fordexplorer.clique.controller.APIController;
import com.fordexplorer.clique.data.Person;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.PostConstruct;

@RunWith(SpringRunner.class)
@SpringBootTest
public class APITest {

    @Autowired
    private APIController controller;
    private Person testPerson;

    @PostConstruct
    public void setup() {
        testPerson = new Person();
    }


}
