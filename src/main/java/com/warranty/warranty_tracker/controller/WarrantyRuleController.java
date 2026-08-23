package com.warranty.warranty_tracker.controller;

import com.warranty.warranty_tracker.service.WarrantyRuleEngine;
import com.warranty.warranty_tracker.dto.WarrantyRuleRequest;
import com.warranty.warranty_tracker.dto.WarrantyRuleResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;
import java.net.URI;

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
    @PostMapping
    public ResponseEntity<WarrantyRuleResponse> createRule(@Valid @RequestBody WarrantyRuleRequest request) {
        WarrantyRule created = warrantyRuleEngine.createRule(request);
        return ResponseEntity
                .created(URI.create("/api/warranty-rules/" + created.getId()))
                .body(toResponse(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WarrantyRuleResponse> updateRule(
            @PathVariable Long id,
            @Valid @RequestBody WarrantyRuleRequest request) {

        try {
            WarrantyRule updated = warrantyRuleEngine.updateRule(id, request);
            return ResponseEntity.ok(toResponse(updated));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRule(@PathVariable Long id) {
        try {
            warrantyRuleEngine.deleteRule(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<WarrantyRuleResponse> getRuleById(@PathVariable Long id) {
        try {
            WarrantyRule rule = warrantyRuleEngine.getRuleById(id);
            return ResponseEntity.ok(toResponse(rule));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<WarrantyRuleResponse>> getAllRules() {
        List<WarrantyRuleResponse> rules = warrantyRuleEngine.getAllRules()
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(rules);
    }

    private WarrantyRuleResponse toResponse(WarrantyRule rule) {
        return new WarrantyRuleResponse(
                rule.getId(),
                rule.getBrand(),
                rule.getCategory(),
                rule.getWarrantyMonths(),
                rule.getWarrantyType(),
                rule.getIsActive(),
                rule.getCreatedAt(),
                rule.getUpdatedAt()
        );
    }
}