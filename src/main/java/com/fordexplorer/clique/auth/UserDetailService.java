package com.fordexplorer.clique.auth;

import com.fordexplorer.clique.db.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailService implements UserDetailsService {

    @Autowired
    private PersonRepository db;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return db.findPersonByUsername(username);
    }
}
