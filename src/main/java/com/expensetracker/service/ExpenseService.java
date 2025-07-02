package com.expensetracker.service;
import com.expensetracker.model.Expense;
import com.expensetracker.model.User;
import com.expensetracker.repository.ExpenseRepository;
import com.expensetracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public ResponseEntity<Expense> createExpense(Expense expense, String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        expense.setUser(user);
        return ResponseEntity.ok(expenseRepository.save(expense));
    }

    public List<Expense> getExpensesForUser(String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        return expenseRepository.findByUser(user);
    }

    public Expense updateExpense(Long id, Expense updatedExpense, String username) {
        Expense existing = expenseRepository.findById(id).orElseThrow();
        if (!existing.getUser().getUsername().equals(username)) throw new SecurityException("Unauthorized");

        existing.setTitle(updatedExpense.getTitle());
        existing.setAmount(updatedExpense.getAmount());
        existing.setDate(updatedExpense.getDate());
        return expenseRepository.save(existing);
    }

    public void deleteExpense(Long id, String username) {
        Expense expense = expenseRepository.findById(id).orElseThrow();
        if (!expense.getUser().getUsername().equals(username)) throw new SecurityException("Unauthorized");
        expenseRepository.delete(expense);
    }
}
