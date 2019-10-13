package com.fordexplorer.clique.auth;

import com.fordexplorer.clique.db.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailService implements UserDetailsService {

    @Autowired
    private PersonRepository db;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails result = db.findPersonByUsername(username);
        if (result == null) {
            logger.info("User {} not found", username);
            throw new UsernameNotFoundException(username);
        }
        return result;
    }
}
