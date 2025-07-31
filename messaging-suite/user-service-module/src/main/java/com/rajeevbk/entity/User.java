package com.rajeevbk.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // In User.java
    @Column(name = "username", nullable = false, unique = true)
    private String username; // Will store the preferred_username

    @Column(nullable = false)
    private String kcUserId;

    @Column(nullable = true)
    private String email;

    @Column(nullable = true)
    private String role; // e.g., 'patient', 'doctor', 'caregiver'

    @Column(name = "first_name", nullable = true)
    private String firstName;

    @Column(name = "last_name", nullable = true)
    private String lastName;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Version // ➕ Added for optimistic locking support
    private Long version;
}
