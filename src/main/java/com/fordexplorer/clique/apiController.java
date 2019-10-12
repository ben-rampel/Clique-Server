package com.fordexplorer.clique;

import com.fordexplorer.clique.data.Group;
import com.fordexplorer.clique.data.Person;
import org.springframework.web.bind.annotation.*;

@RestController
public class apiController {
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
    //Get group info, including people wanting to join group

    /* Group interaction */
    //Chat
}
