package com.fordexplorer.clique.controller;

import com.fordexplorer.clique.auth.JwtTokenManager;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class APIController {

    private Logger logger = LoggerFactory.getLogger(APIController.class);
    private GroupRepository groupRepository;
    private PersonRepository personRepository;
    private JwtTokenManager jwtTokenManager;

    @Autowired
    public APIController(GroupRepository groupRepository, PersonRepository personRepository, JwtTokenManager jwtTokenManager) {
        this.groupRepository = groupRepository;
        this.personRepository = personRepository;
        this.jwtTokenManager = jwtTokenManager;
    }

    /* Account management */

    //Create user
    @PostMapping("/registerUser")
    public ResponseEntity<String> registerUser(@RequestBody Person person) {
        logger.info("Registering user {}", person.getUsername());
        personRepository.save(person);
        String token = String.format("{\n\"token\": \"%s\"\n}", jwtTokenManager.createToken(person.getUsername()));
        logger.info("Returning Token {}", token);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }

    //Login
    //login(credentials) -> JWT Token
    @PostMapping("/login")
    public ResponseEntity<String> login(String username, String password) {
        logger.info("Authentication user {}", username);
        Person person = personRepository.findPersonByUsername(username);
        if (person == null) {
            logger.info("User {} Not Found", username);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (person.getPassword().equals(password)) {
            logger.info("User {} logged in", username);
            String token = String.format("{token: %s}", jwtTokenManager.createToken(username));
            return new ResponseEntity<>(token, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    //Get user profile
    @GetMapping("/getUser/{username}")
    public Person getUser(@PathVariable String username) {
        logger.info("Getting User Profile for {}", username);
        return personRepository.findPersonByUsername(username);

        //maybe only allow access if the person you're looking up is trying to join your group?
    }

    /* Group management */

    //Create group
    @PostMapping("/createGroup")
    public void createGroup(@AuthenticationPrincipal UserDetails person, @RequestBody Group toAdd) {
        logger.info("{} is trying to create group {}", person.getUsername(), toAdd.getName());
        Person owner = personRepository.findPersonByUsername(person.getUsername());
        toAdd.addMember(owner);

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
            if (distance < 1) {
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
    @PostMapping("/joinGroup/{id}")
    public ResponseEntity<String> joinGroup(@PathVariable Long id, @AuthenticationPrincipal UserDetails authInfo) {
        Person person = personRepository.findPersonByUsername(authInfo.getUsername());
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
    @PostMapping("/leaveGroup/{id}")
    public ResponseEntity<String> leaveGroup(@PathVariable Long id, @AuthenticationPrincipal Person person) {
        logger.info("{} is trying to leave group {}", person.getUsername(), id);
        if (groupRepository.findById(id).isPresent()) {
            groupRepository.findById(id).get().removeMember(person);
            logger.info("{} has left group {}", person.getUsername(), id);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    //Get group info, including people wanting to join group
    @GetMapping("/getGroup/{id}")
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
