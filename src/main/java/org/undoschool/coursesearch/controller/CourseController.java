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
import java.util.List;

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
    public ResponseEntity<SearchResult> searchCourses(@ModelAttribute SearchCriteria criteria) {
        SearchResult result = searchService.searchCourses(criteria);
        return ResponseEntity.ok(result);
    }

    /**
     * Assignment B: Autocomplete Suggestion Endpoint
     * GET /api/search/suggest?q={partialTitle}
     */
    @GetMapping("/search/suggest")
    public ResponseEntity<List<String>> suggestCourses(@RequestParam String q) {
        log.info("Suggestion request - q: {}", q);
        List<String> suggestions = searchService.suggestCourses(q);
        return ResponseEntity.ok(suggestions);
    }

    /**
     * Health check endpoint to verify the API is running.
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Course Search API is running!");
    }
}