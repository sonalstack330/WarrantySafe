package com.warranty.warranty_tracker.service;
import com.warranty.warranty_tracker.dto.WarrantyRuleRequest;

import com.warranty.warranty_tracker.repository.WarrantyRuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class WarrantyRuleEngine {

    private final WarrantyRuleRepository warrantyRuleRepository;

    public Integer getWarrantyPeriodMonths(String brand, String category) {
        log.debug("Fetching warranty rule for brand: {}, category: {}", brand, category);

        String normalizedBrand = normalizeBrand(brand);
        String normalizedCategory = normalizeCategory(category);

        Optional<WarrantyRule> rule = warrantyRuleRepository
                .findByBrandAndCategoryAndIsActiveTrue(normalizedBrand, normalizedCategory);

        if (rule.isPresent()) {
            Integer warrantyMonths = rule.get().getWarrantyMonths();
            log.info("Found warranty rule: {} {} = {} months",
                    normalizedBrand, normalizedCategory, warrantyMonths);
            return warrantyMonths;
        }

        log.warn("No warranty rule found for {} {}. Using default: 12 months",
                normalizedBrand, normalizedCategory);

        return 12;
    }

    public boolean hasWarrantyRule(String brand, String category) {
        return warrantyRuleRepository.existsByBrandAndCategory(brand, category);
    }

    public Optional<WarrantyRule> getWarrantyRule(String brand, String category) {
        String normalizedBrand = normalizeBrand(brand);
        String normalizedCategory = normalizeCategory(category);

        return warrantyRuleRepository
                .findByBrandAndCategoryAndIsActiveTrue(normalizedBrand, normalizedCategory);
    }

    private String normalizeBrand(String brand) {
        if (brand == null || brand.isBlank()) {
            return "Unknown";
        }
        String cleaned = brand.trim().replaceAll("\\s+", " ").toLowerCase();
        return Character.toUpperCase(cleaned.charAt(0)) + cleaned.substring(1);
    }

    private String normalizeCategory(String category) {
        if (category == null || category.isBlank()) {
            return "General";
        }
        String cleaned = category.trim().replaceAll("\\s+", " ").toLowerCase();
        return Character.toUpperCase(cleaned.charAt(0)) + cleaned.substring(1);
    }
    public WarrantyRule createRule(WarrantyRuleRequest request) {
        WarrantyRule rule = new WarrantyRule();
        rule.setBrand(normalizeBrand(request.brand()));
        rule.setCategory(normalizeCategory(request.category()));
        rule.setWarrantyMonths(request.warrantyMonths());
        rule.setWarrantyType(request.warrantyType());
        rule.setIsActive(request.isActive() != null ? request.isActive() : true);

        WarrantyRule saved = warrantyRuleRepository.save(rule);
        log.info("Created warranty rule: id={}, brand={}, category={}",
                saved.getId(), saved.getBrand(), saved.getCategory());
        return saved;
    }
    public WarrantyRule updateRule(Long id, WarrantyRuleRequest request) {
        WarrantyRule existing = warrantyRuleRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Warranty rule not found with id: " + id));

        existing.setBrand(normalizeBrand(request.brand()));
        existing.setCategory(normalizeCategory(request.category()));
        existing.setWarrantyMonths(request.warrantyMonths());
        existing.setWarrantyType(request.warrantyType());
        if (request.isActive() != null) {
            existing.setIsActive(request.isActive());
        }

        WarrantyRule updated = warrantyRuleRepository.save(existing);
        log.info("Updated warranty rule: id={}", updated.getId());
        return updated;
    }
    public void deleteRule(Long id) {
        if (!warrantyRuleRepository.existsById(id)) {
            throw new NoSuchElementException("Warranty rule not found with id: " + id);
        }
        warrantyRuleRepository.deleteById(id);
        log.info("Deleted warranty rule: id={}", id);
    }

    public List<WarrantyRule> getAllRules() {
        return warrantyRuleRepository.findAll();
    }

    public WarrantyRule getRuleById(Long id) {
        return warrantyRuleRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Warranty rule not found with id: " + id));
    }
}