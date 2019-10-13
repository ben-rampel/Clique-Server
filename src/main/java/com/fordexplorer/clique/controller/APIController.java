package com.fordexplorer.clique.controller;

import com.fordexplorer.clique.data.Group;
import com.fordexplorer.clique.data.Location;
import com.fordexplorer.clique.data.Message;
import com.fordexplorer.clique.data.Person;
import com.fordexplorer.clique.db.GroupRepository;
import com.fordexplorer.clique.db.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class APIController {

    private Logger logger = LoggerFactory.getLogger(APIController.class);
    private GroupRepository groupRepository;
    private PersonRepository personRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    @Autowired
    public APIController(GroupRepository groupRepository, PersonRepository personRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.groupRepository = groupRepository;
        this.personRepository = personRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    /* Account management */

    //Create user
    @PostMapping("/registerUser")
    public void registerUser(@RequestBody Person person) {
        logger.info("Registering user {}", person.getUsername());
        person.setPassword(bCryptPasswordEncoder.encode(person.getPassword()));
        this.personRepository.save(person);
    }

    //Get user profile
    @GetMapping("/users/{username}")
    public Person getUser(@PathVariable String username) {
        logger.info("Getting User Profile for {}", username);
        return personRepository.findPersonByUsername(username);

        //maybe only allow access if the person you're looking up is trying to join your group?
    }

    /* Group management */

    //Create group
    @PostMapping("/createGroup")
    public void createGroup(@RequestBody Group toAdd) {
        String username = SecureContextUtils.getCurrentUserName();
        logger.info("Got UserDetails {}", username);
        logger.info("{} is trying to create group {}", username, toAdd.getName());
        Person owner = personRepository.findPersonByUsername(username);
        groupRepository.save(toAdd);
        owner.setCurrentGroup(toAdd);
        personRepository.save(owner);
        logger.info("group {} is created", toAdd.getName());
    }

    //Get groups near user
    @GetMapping("/getGroups")
    public Map<String, List<Group>> getGroups(@RequestParam Double longitude, @RequestParam Double latitude) {
        logger.info("Finding group near location {} {}", latitude, longitude);
        Location location = new Location(latitude, longitude);
        List<Group> result = new ArrayList<>();
        for (Group g : groupRepository.findAll()) {
            logger.info("Examining group {} at location {}, {}", g.getName(), g.getLocation().getLatitude(), g.getLocation().getLongitude());
            //if group is within 1 mile of specified location
            double distance = g.getLocation().distanceTo(location);
            logger.info("Group {} distance {} miles", g.getName(), distance);
            if (distance < 10) {
                // break out of json loop
                sanitizeGroup(g);
                logger.info("Found Group {} with members {}", g.getName(), g.getMembers());
                result.add(g);
            }
        }
        logger.info("Found {} groups near location {} {}", result.size(), latitude, longitude);
        Map<String, List<Group>> response = new HashMap<>();
        response.put("groups", result);
        return response;
    }

    //Join group
    @PostMapping("/groups/{id}")
    public ResponseEntity<String> joinGroup(@PathVariable Long id) {
        String username = SecureContextUtils.getCurrentUserName();
        Person person = personRepository.findPersonByUsername(username);
        logger.info("Adding {} to group {}", person.getUsername(), id);
        if (groupRepository.findById(id).isPresent()) {
            Group foundGroup = groupRepository.findById(id).get();
            logger.info("Added {} to the group {}", person.getUsername(), id);
            person.setCurrentGroup(foundGroup);
            personRepository.save(person);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    //Leave group
    @DeleteMapping("/groups/{id}/me")
    public ResponseEntity<String> leaveGroup(@PathVariable Long id) {
        String username = SecureContextUtils.getCurrentUserName();
        logger.info("{} is trying to leave group {}", username, id);
        Person person = personRepository.findPersonByUsername(username);
        if (groupRepository.findById(id).isPresent()) {
            groupRepository.findById(id).get().removeMember(person);
            logger.info("{} has left group {}", person.getUsername(), id);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    //Get group info, including people wanting to join group
    @GetMapping("/groups/{id}")
    public Group getGroup(@PathVariable Long id) {
        logger.info("Getting group {}", id);
        Optional<Group> found = groupRepository.findById(id);
        if (!found.isPresent()) return null;

        Group g = found.get();
        sanitizeGroup(g);
        return g;
    }

    @GetMapping("/chat/{id}/messages")
    public List<Message> getMessages(@PathVariable Long id){
        Optional<Group> group = groupRepository.findById(id);
        return group.map(Group::getGroupMessages).orElse(null);
    }

    @PostMapping("/chat/{id}}/sendMessage")
    public ResponseEntity<String> sendMessage(@PathVariable Long id, @RequestBody Message message){
        if(groupRepository.findById(id).isPresent()){
            groupRepository.findById(id).get().addMessage(message);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    private void sanitizeGroup(Group g) {
        // break out of json loop
        for (Person p : g.getMembers()) {
            p.setCurrentGroup(null);
        }
        for (Person p : g.getWannabeMembers()) {
            p.setCurrentGroup(null);
        }
    }

}
