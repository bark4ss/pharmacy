package com.example.pharmacy.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserWithoutPassword implements Entity {
    private Long userId;
    private String login;
    private UserRole role;
}
