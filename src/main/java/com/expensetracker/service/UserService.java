package com.expensetracker.service;
import com.expensetracker.model.User;
import com.expensetracker.repository.UserRepository;
import com.expensetracker.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public ResponseEntity<String> register(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists!");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully!");
    }

    public ResponseEntity<?> login(User user) {
        return userRepository.findByUsername(user.getUsername())
            .filter(u -> passwordEncoder.matches(user.getPassword(), u.getPassword()))
            .map(u -> ResponseEntity.ok(Map.of(
                "accessToken", jwtUtil.generateToken(u.getUsername()),
                "refreshToken", jwtUtil.generateRefreshToken(u.getUsername())
            )))
            .orElse(ResponseEntity.status(401).body(
            	    Map.of("error", "Invalid credentials")
            		));
    }
}
