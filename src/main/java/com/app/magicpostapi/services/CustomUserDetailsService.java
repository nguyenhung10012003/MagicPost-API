package com.app.magicpostapi.services;

import com.app.magicpostapi.models.CustomUserDetails;
import com.app.magicpostapi.models.User;
import com.app.magicpostapi.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findAccountByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException(username));
        if (user == null) throw new UsernameNotFoundException(username);
        return new CustomUserDetails(user);
    }
}
