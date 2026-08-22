package com.warranty.warranty_tracker.service;

import com.warranty.warranty_tracker.entity.WarrantyRule;
import com.warranty.warranty_tracker.repository.WarrantyRuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Optional;

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
}