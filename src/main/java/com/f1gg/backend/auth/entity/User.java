package com.f1gg.backend.auth.entity;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class User {
    private Long id;
    private String email;
    private String password;
    private String nickname;
    private String role; // USER, ADMIN
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
