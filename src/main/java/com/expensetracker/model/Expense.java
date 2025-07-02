package com.expensetracker.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Expense {
    @Id @GeneratedValue
    private Long id;

    private String title;
    private double amount;
    private String category;
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
