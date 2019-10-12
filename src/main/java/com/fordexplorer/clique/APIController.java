package com.fordexplorer.clique;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class APIController {
    /* Account management */

    //Create user
    @PostMapping("/registerUser")
    public void registerUser(@RequestBody Person person){

    }
    //Login
    //login(credentials) -> JWT Token

    //Get user profile
    @GetMapping("/getProfile/{username}")
    public Person getProfile(@PathVariable String username){
        return null;
    }

    /* Group management */

    //Create group
    @PostMapping("/createGroup")
    public void createGroup(@RequestBody String body){
        Group group = new Group();
        //Add the creater of the group
        //group.addMember();
    }
    //Get groups near user
    @GetMapping("/getGroups")
    public List<Group> getGroups(@RequestBody Location location){
        return null;
    }
    //Get group info, including people wanting to join group
    @GetMapping("/getGroup/{id}")
    public Group getGroup(@RequestParam String id){
        return null;
    }
    /* Group interaction */
    //Chat
}
