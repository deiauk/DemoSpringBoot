package com.example.lalala.demo;

import com.example.lalala.demo.model.User;
import com.example.lalala.demo.respository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class DetailService implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(s);
        if (user == null) throw new UsernameNotFoundException(s + " was not found");
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getToken(),
                AuthorityUtils.createAuthorityList()
        );
    }
}
