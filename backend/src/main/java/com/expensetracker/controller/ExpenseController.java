package com.expensetracker.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/api/expenses")
public class ExpenseController {

    @Autowired private ExpenseService expenseService;
    

    @PostMapping
    public Expense createExpense(@RequestBody Expense expense, Principal principal) {
        return expenseService.createExpense(expense, principal.getName());
    }

    @GetMapping
    public ResponseEntity<List<Expense>> getAllExpenses(Principal principal) {
        return ResponseEntity.ok(expenseService.getExpensesForUser(principal.getName()));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    public ResponseEntity<Expense> getExpenseById(@PathVariable Long id, Principal principal) {
        Expense expense = expenseService.getExpensesByUserId(id, principal.getName());
        return ResponseEntity.ok(expense);
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<Expense> updateExpense(@PathVariable Long id, @RequestBody Expense updatedExpense, Principal principal) {
        Expense saved = expenseService.updateExpense(id, updatedExpense, principal.getName());
        return ResponseEntity.ok(saved);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    public ResponseEntity<String> deleteExpense(@PathVariable Long id, Principal principal) {
        expenseService.deleteExpense(id, principal.getName());
        return ResponseEntity.ok("Expense deleted successfully!");
    }
}
