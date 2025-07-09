package com.expensetracker.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.expensetracker.service.UserService;
import com.expensetracker.model.RevokedToken;
import com.expensetracker.model.User;
import com.expensetracker.repository.RevokedTokenRepository;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final RevokedTokenRepository revokedTokenRepository;
    
    public AuthController(UserService userService, RevokedTokenRepository revokedTokenRepository) {
        this.userService = userService;
        this.revokedTokenRepository = revokedTokenRepository;
    }
  

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        return userService.register(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User user) {
        return userService.login(user);
    }
    
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader,
                                         @RequestBody Map<String, String> request) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Missing or invalid Authorization header");
        }

        String accessToken = authHeader.substring(7);
        revokedTokenRepository.save(new RevokedToken(accessToken)); // revoke access token

        String refreshToken = request.get("refreshToken");
        if (refreshToken != null && !refreshToken.isBlank()) {
            revokedTokenRepository.save(new RevokedToken(refreshToken)); // revoke refresh token
        }

        return ResponseEntity.ok("Logged out successfully!");
    }

}
