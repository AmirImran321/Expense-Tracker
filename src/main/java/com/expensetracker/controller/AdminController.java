package com.expensetracker.controller;

import com.expensetracker.model.Expense;
import com.expensetracker.model.User;
import com.expensetracker.repository.ExpenseRepository;
import com.expensetracker.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
   
    @GetMapping("/expenses/byId")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Expense> getExpenseById(@RequestParam Long id) {
        Expense expense = expenseRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense not found"));
        return ResponseEntity.ok(expense);
    }
   
    @GetMapping("/expenses")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<Expense>> getExpensesByUserId(@RequestParam Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<Expense> expenses = expenseRepository.findByUser(userOpt.get());
        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers(Authentication auth) {
        return ResponseEntity.ok(userRepository.findAll());
    }

}
