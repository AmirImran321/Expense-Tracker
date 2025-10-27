package com.expensetracker.controller;

import com.expensetracker.model.Expense;
import com.expensetracker.security.JwtUtil;
import com.expensetracker.service.ExpenseService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExpenseController.class)
class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExpenseService expenseService;

    @MockBean
    private JwtUtil jwtUtil;

    private final String jwt = "valid.jwt.token";

    @BeforeEach
    void setup() {
        when(jwtUtil.validateToken(jwt)).thenReturn(true);
        when(jwtUtil.getUsername(jwt)).thenReturn("user");
        when(jwtUtil.getRole(jwt)).thenReturn("ROLE_USER");

    }

    @Test
    void shouldCreateExpense_whenValidRequest() throws Exception {
    	Expense newExpense = Expense.builder()
    		    .title("Lunch")
    		    .amount(15.0)
    		    .category("Food")
    		    .date(LocalDate.now())
    		    .build();

    	Expense savedExpense = Expense.builder()
    		    .title("Lunch")
    		    .amount(15.0)
    		    .category("Food")
    		    .date(LocalDate.now())
    		    .build();


        when(expenseService.createExpense(any(Expense.class), eq("user"))).thenReturn(savedExpense);

        mockMvc.perform(post("/api/expenses")
                .header("Authorization", "Bearer " + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(newExpense)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(savedExpense.getId()))
            .andExpect(jsonPath("$.title").value("Lunch"));
    }

    @Test
    void shouldReturnExpenseById_whenAuthorized() throws Exception {
    	Expense newExpense = Expense.builder()
    		    .title("Lunch")
    		    .amount(15.0)
    		    .category("Food")
    		    .date(LocalDate.now())
    		    .build();
        when(expenseService.getExpensesByUserId(1L, "user")).thenReturn(newExpense);

        mockMvc.perform(get("/api/expenses/1")
                .header("Authorization", "Bearer " + jwt))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Lunch"));
    }

    @Test
    void shouldUpdateExpense_whenValidRequest() throws Exception {
    	Expense updatedExpense = Expense.builder()
    		    .title("Lunch")
    		    .amount(15.0)
    		    .category("Food")
    		    .date(LocalDate.now())
    		    .build();
    	
    	Expense result = Expense.builder()
    		    .title("Lunch")
    		    .amount(15.0)
    		    .category("Food")
    		    .date(LocalDate.now())
    		    .build();

        when(expenseService.updateExpense(eq(1L), any(Expense.class), eq("user"))).thenReturn(result);

        mockMvc.perform(put("/api/expenses/1")
                .header("Authorization", "Bearer " + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(updatedExpense)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Dinner"));
    }

    @Test
    void shouldDeleteExpense_whenAuthorized() throws Exception {
        doNothing().when(expenseService).deleteExpense(1L, "user");

        mockMvc.perform(delete("/api/expenses/1")
                .header("Authorization", "Bearer " + jwt))
            .andExpect(status().isNoContent());
    }

    @Test
    void shouldRejectAccessWithoutJwt() throws Exception {
        mockMvc.perform(get("/api/expenses/1"))
            .andExpect(status().isForbidden());
    }

    @Test
    void shouldRejectAccessWithInvalidJwt() throws Exception {
        String invalidJwt = "invalid.jwt.token";
        when(jwtUtil.validateToken(invalidJwt)).thenReturn(false);

        mockMvc.perform(get("/api/expenses/1")
                .header("Authorization", "Bearer " + invalidJwt))
            .andExpect(status().isForbidden());
    }
}
