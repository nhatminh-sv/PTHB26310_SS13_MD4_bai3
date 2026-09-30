package com.example.demo2.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;    // ⬅️ sửa thành chữ thường
import lombok.Data;                // ⬅️ sửa thành chữ thường
import lombok.NoArgsConstructor;   // ⬅️ sửa thành chữ thường

import java.time.LocalDateTime;

@Entity
@Table(name = "supplies")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Supply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 500)
    private String specification;

    @Column(length = 200)
    private String provider;

    @Column(length = 50)
    private String unit;

    @Column(nullable = false)
    private Integer quantity = 0;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}