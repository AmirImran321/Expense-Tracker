package com.expensetracker.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.expensetracker.model.Expense;
import com.expensetracker.service.ExpenseService;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {

    @Autowired private ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<Expense> createExpense(@RequestBody Expense expense, Principal principal) {
        return expenseService.createExpense(expense, principal.getName());
    }

    @GetMapping
    public ResponseEntity<List<Expense>> getAllExpenses(Principal principal) {
        return ResponseEntity.ok(expenseService.getExpensesForUser(principal.getName()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Expense> updateExpense(@PathVariable Long id, @RequestBody Expense expense, Principal principal) {
        return ResponseEntity.ok(expenseService.updateExpense(id, expense, principal.getName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteExpense(@PathVariable Long id, Principal principal) {
        expenseService.deleteExpense(id, principal.getName());
        return ResponseEntity.ok("Expense deleted successfully!");
    }
}
