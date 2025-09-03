package org.undoschool.coursesearch.controller;

import org.undoschool.coursesearch.service.CourseSearchService;
import org.undoschool.coursesearch.service.CourseType;
import org.undoschool.coursesearch.service.SearchCriteria;
import org.undoschool.coursesearch.service.SearchResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CourseController {

    private final CourseSearchService searchService;

    /**
     * Main search endpoint: GET /api/search
     */
    @GetMapping("/search")
    public ResponseEntity<SearchResult> searchCourses(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) CourseType type,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(defaultValue = "upcoming") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "false") boolean fuzzy
    ) {

        log.info("Search request - q: {}, category: {}, type: {}, page: {}, size: {}",
                q, category, type, page, size);

        // Build search criteria from request parameters
        SearchCriteria criteria = SearchCriteria.builder()
                .query(q)
                .category(category)
                .type(type)
                .minAge(minAge)
                .maxAge(maxAge)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .startDate(startDate)
                .sort(sort)
                .page(page)
                .size(size)
                .fuzzy(fuzzy)
                .build();

        // Execute search
        SearchResult result = searchService.searchCourses(criteria);

        log.info("Search completed - found {} total courses, returning {} on page {}",
                result.getTotal(), result.getCourses().size(), page);

        return ResponseEntity.ok(result);
    }

    /**
     * Health check endpoint to verify the API is running.
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Course Search API is running!");
    }
}