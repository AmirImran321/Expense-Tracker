package com.expensetracker.service;

import com.expensetracker.model.Expense;
import com.expensetracker.model.User;
import com.expensetracker.repository.ExpenseRepository;
import com.expensetracker.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public ResponseEntity<Expense> createExpense(Expense expense, String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow();
        expense.setUser(user);
        return ResponseEntity.ok(expenseRepository.save(expense));
    }

    public List<Expense> getExpensesForUser(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(()-> new RuntimeException("User not found!"));
        System.out.println("Fetching expenses for user: " + username);
        List<Expense> expenses = expenseRepository.findByUser(user);
        System.out.println("Found " + expenses.size() + " expenses.");
        return expenses;

      
    }
    
    public Expense getExpenseById(Long id, String username) {
        Expense expense = expenseRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Expense not found"));

        if (!expense.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("You are not authorized to view this expense.");
        }

        return expense;
    }


    public Expense updateExpense(Long id, Expense updatedExpense, String username) {
        Expense existing = expenseRepository.findById(id)
            .orElseThrow(()-> new RuntimeException("Expense not found!"));

        if (!existing.getUser().getUsername().equals(username)) {
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

        if (!expense.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("You are not authorized to delete this expense.");
        }

        expenseRepository.delete(expense);
    }

}
