package com.expensetracker.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RevokedToken {
    @Id @GeneratedValue
    private Long id;

    private String token;
    //revoked token
    public RevokedToken(String token) {
        this.token = token;
    }
}
