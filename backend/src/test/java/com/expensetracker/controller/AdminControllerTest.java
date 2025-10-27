package com.expensetracker.controller;

import com.expensetracker.config.SecurityConfig;
import com.expensetracker.model.Expense;
import com.expensetracker.model.User;
import com.expensetracker.model.User.Role;
import com.expensetracker.service.ExpenseService;
import com.expensetracker.service.UserService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@Import({SecurityConfig.class, AdminControllerTest.MockServiceConfig.class})
@WebMvcTest(AdminController.class)
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private UserService userService;

    @TestConfiguration
    static class MockServiceConfig {
        @Bean
        public ExpenseService expenseService() {
            return Mockito.mock(ExpenseService.class);
        }

        @Bean
        public UserService userService() {
            return Mockito.mock(UserService.class);
        }
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetAllUsers_AsAdmin() throws Exception {
        List<User> mockUsers = List.of(
            User.builder().id(1L).username("john").password("pass").expenses(new ArrayList<>()).userRole(Role.USER).build(),
            User.builder().id(2L).username("admin").password("pass").expenses(new ArrayList<>()).userRole(Role.ADMIN).build()
        );

        when(userService.getAllUsers()).thenReturn(mockUsers);

        mockMvc.perform(get("/api/admin/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].username").value("john"))
            .andExpect(jsonPath("$[1].username").value("admin"));
    }

    @Test
    void testGetAllUsers_WithoutToken() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetExpensesByUserId_AsAdmin() throws Exception {
        List<Expense> mockExpenses = List.of(
            Expense.builder()
                .id(1L)
                .title("Lunch")
                .category("Food")
                .amount(20.0)
                .date(LocalDate.of(2025, 8, 9))
                .build(),
            Expense.builder()
                .id(2L)
                .title("Taxi")
                .category("Transportation")
                .amount(35.0)
                .date(LocalDate.of(2025, 8, 8))
                .build()
        );

        when(expenseService.getExpensesForUser("admin")).thenReturn(mockExpenses);

        mockMvc.perform(get("/api/admin/expenses").param("userId", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].title").value("Lunch"))
            .andExpect(jsonPath("$[0].category").value("Food"))
            .andExpect(jsonPath("$[0].amount").value(20.0))
            .andExpect(jsonPath("$[0].date").value("2025-08-09"))
            .andExpect(jsonPath("$[1].title").value("Taxi"))
            .andExpect(jsonPath("$[1].category").value("Transportation"))
            .andExpect(jsonPath("$[1].amount").value(35.0))
            .andExpect(jsonPath("$[1].date").value("2025-08-08"));
    }


    @Test
    void testGetExpensesByUserId_WithoutToken() throws Exception {
        mockMvc.perform(get("/api/admin/expenses").param("userId", "1"))
            .andExpect(status().isUnauthorized());
    }
}
