package com.example.bookinghotelproject.service;

import com.example.bookinghotelproject.entity.User;
import com.example.bookinghotelproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public User register(String username, String password) {
        if(userRepository.existsByUsername(username)){
            throw new IllegalArgumentException("User already exists");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(encoder.encode(password));
        log.info("New user registered: {}", username);
        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), Collections.emptyList());
    }
}
