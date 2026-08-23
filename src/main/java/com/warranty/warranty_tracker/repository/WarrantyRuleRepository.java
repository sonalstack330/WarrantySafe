package com.warranty.warranty_tracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface WarrantyRuleRepository extends JpaRepository<WarrantyRule, Long> {

    /**
     * Find warranty rule by brand and category
     * Used by WarrantyRuleEngine to determine warranty period
     * @param brand - product brand
     * @param category - product category
     * @return Optional containing rule if found
     */
    Optional<WarrantyRule> findByBrandAndCategoryAndIsActiveTrue(String brand, String category);

    /**
     * Find all rules for a specific brand
     * @param brand - brand name
     * @return List of warranty rules for this brand
     */
    List<WarrantyRule> findByBrandAndIsActiveTrue(String brand);

    /**
     * Find all rules for a specific category
     * @param category - product category
     * @return List of warranty rules for this category
     */
    List<WarrantyRule> findByCategoryAndIsActiveTrue(String category);

    /**
     * Find all active warranty rules
     * @return List of all active rules
     */
    List<WarrantyRule> findByIsActiveTrue();

    /**
     * Get default warranty rule (fallback if no specific rule found)
     * Example: brand="Unknown", category="General"
     * @return Default warranty rule
     */
    Optional<WarrantyRule> findByBrandAndCategoryAndIsActiveTrueOrderByIdAsc(String brand, String category);

    /**
     * Find all inactive rules (for admin)
     * @return List of disabled warranty rules
     */
    List<WarrantyRule> findByIsActiveFalse();

    /**
     * Check if a rule exists for brand+category
     * @param brand - brand name
     * @param category - category name
     * @return true if rule exists
     */
    boolean existsByBrandAndCategory(String brand, String category);
}