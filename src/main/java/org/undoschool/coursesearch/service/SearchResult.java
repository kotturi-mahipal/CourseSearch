package org.undoschool.coursesearch.service;

import org.undoschool.coursesearch.document.CourseDocument;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Search response - what we return to the API caller.
 */
@Data
@Builder
public class SearchResult {
    private long total;                    // Total number of matching documents
    private List<CourseDocument> courses;  // Current page of results
}