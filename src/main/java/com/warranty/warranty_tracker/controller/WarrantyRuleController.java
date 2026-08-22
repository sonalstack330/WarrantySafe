package com.warranty.warranty_tracker.controller;

import com.warranty.warranty_tracker.entity.WarrantyRule;
import com.warranty.warranty_tracker.service.WarrantyRuleEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/warranty-rules")
@RequiredArgsConstructor
@Slf4j
public class WarrantyRuleController {

    private final WarrantyRuleEngine warrantyRuleEngine;

    /**
     * GET /api/warranty-rules/period?brand=Apple&category=Electronics
     * Returns the warranty period in months (defaults to 12 if no rule matches).
     */
    @GetMapping("/period")
    public ResponseEntity<WarrantyPeriodResponse> getWarrantyPeriod(
            @RequestParam String brand,
            @RequestParam String category) {

        Integer months = warrantyRuleEngine.getWarrantyPeriodMonths(brand, category);
        return ResponseEntity.ok(new WarrantyPeriodResponse(brand, category, months));
    }

    /**
     * GET /api/warranty-rules/exists?brand=Apple&category=Electronics
     * Returns true/false whether a specific rule exists.
     */
    @GetMapping("/exists")
    public ResponseEntity<Boolean> ruleExists(
            @RequestParam String brand,
            @RequestParam String category) {

        boolean exists = warrantyRuleEngine.hasWarrantyRule(brand, category);
        return ResponseEntity.ok(exists);
    }

    /**
     * GET /api/warranty-rules?brand=Apple&category=Electronics
     * Returns the full WarrantyRule object, or 404 if not found.
     */
    @GetMapping
    public ResponseEntity<WarrantyRule> getWarrantyRule(
            @RequestParam String brand,
            @RequestParam String category) {

        Optional<WarrantyRule> rule = warrantyRuleEngine.getWarrantyRule(brand, category);

        return rule
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * Simple response DTO — never expose JPA entities directly in API responses.
     * Keeps the API contract stable even if the entity changes later.
     */
    record WarrantyPeriodResponse(String brand, String category, Integer warrantyMonths) {}
}