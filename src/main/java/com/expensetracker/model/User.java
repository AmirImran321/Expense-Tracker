package com.expensetracker.model;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;

    public enum Role {
        USER,
        ADMIN
    }
    
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role userRole = Role.USER;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<Expense> expenses = new ArrayList<>();

    public String getRoleAsString() {
    	return userRole != null ? userRole.name() : "USER"; 
    }
}
