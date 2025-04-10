package com.example.bookinghotelproject.controller;

import com.example.bookinghotelproject.entity.User;
import com.example.bookinghotelproject.security.JwtTokenProvider;
import org.springframework.security.core.AuthenticationException;
import com.example.bookinghotelproject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        userService.register(user.getUsername(), user.getPassword());
        return ResponseEntity.ok("Registration successful");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
        String token = jwtTokenProvider.createToken(user.getUsername());
        return ResponseEntity.ok(Map.of("username", user.getUsername(), "token", token));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication auth) {
        return ResponseEntity.ok(Map.of("username", auth.getName()));
    }
}
