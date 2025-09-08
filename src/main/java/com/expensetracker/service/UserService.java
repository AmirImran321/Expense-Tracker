package com.expensetracker.service;

import com.expensetracker.model.User;
import com.expensetracker.model.User.Role;
import com.expensetracker.repository.UserRepository;
import com.expensetracker.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
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

        if (user.getUserRole() == null) {
        		  System.err.println("Null userRole for user: " + user.getUsername());
        		  user.setUserRole(Role.USER); 
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully!");
    }

    public ResponseEntity<?> login(User user) {
        return userRepository.findByUsername(user.getUsername())
            .filter(u -> passwordEncoder.matches(user.getPassword(), u.getPassword()))
            .map(u -> ResponseEntity.ok(Map.of(
                "accessToken", jwtUtil.generateToken(u),
                "refreshToken", jwtUtil.generateRefreshToken(u)
            )))
            .orElse(ResponseEntity.status(401).body(
                Map.of("error", "Invalid credentials")
            ));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public boolean isAdmin(User user) {
        return user.getUserRole() == Role.ADMIN;
    }

}
