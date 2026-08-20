package com.warranty.warranty_tracker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "warranty_rules")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarrantyRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "brand", nullable = false)
    private String brand; // e.g., "Apple", "Samsung", "Dell"

    @Column(name = "category", nullable = false)
    private String category; // e.g., "Electronics", "Appliances", "Furniture"

    @Column(name = "warranty_months", nullable = false)
    private Integer warrantyMonths; // e.g., 12 = 1 year

    @Column(name = "warranty_type")
    private String warrantyType; // e.g., "Manufacturer"

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
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