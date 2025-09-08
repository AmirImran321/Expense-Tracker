package com.expensetracker.service;

import com.expensetracker.model.Expense;
import com.expensetracker.model.User;
import com.expensetracker.repository.ExpenseRepository;
import com.expensetracker.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    
    private boolean canAccess(Expense expense, User requester) {
        return expense.getUser().equals(requester) || requester.getUserRole() == User.Role.ADMIN;
    }

    public Expense createExpense(Expense expense, String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        expense.setUser(user);
        return expenseRepository.save(expense);
    }

    public List<Expense> getExpensesForUser(String username) {
        User requester = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (requester.getUserRole() == User.Role.ADMIN) {
            return expenseRepository.findAll(); 
        }

        return expenseRepository.findByUser(requester);
    }

    public Expense getExpensesByUserId(Long id, String username) {
        Expense expense = expenseRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Expense not found"));

        User requester = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));


        boolean isOwner = expense.getUser().getUsername().equals(username);
        boolean isAdmin = requester.getUserRole() == User.Role.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("You are not authorized to view this expense.");
        }
        return expense;
    }

    public Expense updateExpense(Long id, Expense updatedExpense, String username) {
        Expense existing = expenseRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Expense not found"));

        User requester = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (!canAccess(existing, requester)) {
            throw new AccessDeniedException("You are not authorized to update this expense.");
        }

        existing.setTitle(updatedExpense.getTitle());
        existing.setCategory(updatedExpense.getCategory());
        existing.setAmount(updatedExpense.getAmount());
        existing.setDate(updatedExpense.getDate());

        return expenseRepository.save(existing);
    }

    public void deleteExpense(Long id, String username) {
        Expense expense = expenseRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Expense not found"));

        User requester = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (!canAccess(expense, requester)) {
            throw new AccessDeniedException("You are not authorized to delete this expense.");
        }

        expenseRepository.delete(expense);
    }

}
