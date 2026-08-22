package com.warranty.warranty_tracker.dto;

import java.time.LocalDateTime;

public record WarrantyRuleResponse(
        Long id,
        String brand,
        String category,
        Integer warrantyMonths,
        String warrantyType,
        Boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}