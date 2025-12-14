package org.undoschool.coursesearch.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Search criteria - captures all possible search parameters.
 *
 * Why separate DTOs?
 * - Clean separation between API layer and service layer
 * - Easy to validate and transform request parameters
 * - Can evolve independently from ES document structure
 */
@Data
@Builder
@NoArgsConstructor  // FIX: Required for Spring MVC binding
@AllArgsConstructor // FIX: Required for @Builder to work with @NoArgsConstructor
public class SearchCriteria {
    private String query;           // Full-text search term
    private String category;        // Exact category filter
    private CourseType type;        // Exact type filter
    private Integer minAge;         // Age range filters
    private Integer maxAge;
    private Double minPrice;        // Price range filters
    private Double maxPrice;
    private LocalDateTime startDate;    // Show courses on/after this date
    private String sort;            // Sort option: upcoming, priceAsc, priceDesc
    private int page;               // Pagination
    private int size;
    private boolean fuzzy;          // Enable fuzzy matching for typos

    // Default values helper method
    public static SearchCriteria withDefaults() {
        return SearchCriteria.builder()
                .page(0)
                .size(10)
                .sort("upcoming")
                .fuzzy(false)
                .build();
    }
}