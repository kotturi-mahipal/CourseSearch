package org.undoschool.coursesearch;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.undoschool.coursesearch.document.CourseDocument;
import org.undoschool.coursesearch.service.CourseSearchService;
import org.undoschool.coursesearch.service.SearchCriteria;
import org.undoschool.coursesearch.service.SearchResult;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourseSearchServiceTest {

    @Mock
    private ElasticsearchOperations elasticsearchOperations;

    @InjectMocks
    private CourseSearchService courseSearchService;

    @Test
    void searchCourses_shouldReturnResults_whenMatchesFound() {
        // Arrange
        SearchCriteria criteria = SearchCriteria.builder()
                .query("Java")
                .page(0)
                .size(10)
                .build();

        CourseDocument doc = CourseDocument.builder().id("1").title("Java 101").build();

        // 1. Create the SearchHit (This constructor is stable enough to use directly)
        // Arguments: index, id, routing, score, sortValues, highlightFields, innerHits, nestedMetaData, explanation, matchedQueries, content
        SearchHit<CourseDocument> hit = new SearchHit<>(
                "courses", "1", null, 1.0f, null, null, null, null, null, null, doc);

        // 2. Mock the SearchHits interface instead of instantiating SearchHitsImpl
        SearchHits<CourseDocument> searchHits = mock(SearchHits.class);
        when(searchHits.getTotalHits()).thenReturn(1L);
        when(searchHits.getSearchHits()).thenReturn(Collections.singletonList(hit));

        // 3. Mock the ES operations to return our mocked SearchHits
        when(elasticsearchOperations.search(any(Query.class), eq(CourseDocument.class)))
                .thenReturn(searchHits);

        // Act
        SearchResult result = courseSearchService.searchCourses(criteria);

        // Assert
        assertThat(result.getTotal()).isEqualTo(1);
        assertThat(result.getCourses()).hasSize(1);
        assertThat(result.getCourses().get(0).getTitle()).isEqualTo("Java 101");
    }
}