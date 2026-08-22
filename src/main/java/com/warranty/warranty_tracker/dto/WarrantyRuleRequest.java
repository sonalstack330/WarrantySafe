package com.warranty.warranty_tracker.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record WarrantyRuleRequest(
        @NotBlank(message = "Brand is required")
        String brand,

        @NotBlank(message = "Category is required")
        String category,

        @NotNull(message = "Warranty months is required")
        @Min(value = 1, message = "Warranty months must be at least 1")
        Integer warrantyMonths,

        String warrantyType,

        Boolean isActive
) {}