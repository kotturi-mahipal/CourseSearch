package org.undoschool.coursesearch.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.undoschool.coursesearch.document.CourseDocument;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseSearchService {

    private final ElasticsearchOperations elasticsearchOperations;

    public SearchResult searchCourses(SearchCriteria criteria) {
        log.debug("Searching courses with criteria: {}", criteria);

        Pageable pageable = createPageable(criteria);

        org.springframework.data.elasticsearch.core.query.Query searchQuery = buildSpringDataQuery(criteria, pageable);

        SearchHits<CourseDocument> searchHits =
                elasticsearchOperations.search(searchQuery, CourseDocument.class);

        List<CourseDocument> courses = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());

        // If fuzzy is requested and there are no hits, retry with fuzziness, then a contains fallback
        if (criteria.isFuzzy() && StringUtils.hasText(criteria.getQuery()) && searchHits.getTotalHits() == 0) {
            log.debug("Fuzzy fallback engaged for query: {}", criteria.getQuery());
            org.springframework.data.elasticsearch.core.query.Query fuzzyNative = buildFuzzyNativeTextQuery(criteria, pageable);
            SearchHits<CourseDocument> fuzzyHits = elasticsearchOperations.search(fuzzyNative, CourseDocument.class);
            if (fuzzyHits.getTotalHits() > 0) {
                courses = fuzzyHits.getSearchHits().stream().map(SearchHit::getContent).collect(Collectors.toList());
                return SearchResult.builder().total(fuzzyHits.getTotalHits()).courses(courses).build();
            }

            org.springframework.data.elasticsearch.core.query.Query containsFallback = buildContainsFallbackQuery(criteria, pageable);
            SearchHits<CourseDocument> fallbackHits = elasticsearchOperations.search(containsFallback, CourseDocument.class);
            if (fallbackHits.getTotalHits() > 0) {
                courses = fallbackHits.getSearchHits().stream().map(SearchHit::getContent).collect(Collectors.toList());
                return SearchResult.builder().total(fallbackHits.getTotalHits()).courses(courses).build();
            }
        }

        return SearchResult.builder()
                .total(searchHits.getTotalHits())
                .courses(courses)
                .build();
    }

    private org.springframework.data.elasticsearch.core.query.Query buildSpringDataQuery(SearchCriteria criteria, Pageable pageable) {
        Criteria root = new Criteria();

        boolean any = false;

        if (StringUtils.hasText(criteria.getQuery())) {
            Criteria title = new Criteria("title").matches(criteria.getQuery());
            Criteria description = new Criteria("description").matches(criteria.getQuery());
            Criteria text = title.or(description);
            root = root.and(text);
            any = true;
        }

        if (StringUtils.hasText(criteria.getCategory())) {
            root = root.and(new Criteria("category").is(criteria.getCategory()));
            any = true;
        }

        if (criteria.getType() != null) {
            root = root.and(new Criteria("type").is(criteria.getType().name()));
            any = true;
        }

        if (criteria.getMinPrice() != null) {
            root = root.and(new Criteria("price").greaterThanEqual(criteria.getMinPrice()));
            any = true;
        }
        if (criteria.getMaxPrice() != null) {
            root = root.and(new Criteria("price").lessThanEqual(criteria.getMaxPrice()));
            any = true;
        }

        if (criteria.getMinAge() != null) {
            root = root.and(new Criteria("maxAge").greaterThanEqual(criteria.getMinAge()));
            any = true;
        }
        if (criteria.getMaxAge() != null) {
            root = root.and(new Criteria("minAge").lessThanEqual(criteria.getMaxAge()));
            any = true;
        }

        if (criteria.getStartDate() != null) {
            root = root.and(new Criteria("nextSessionDate").greaterThanEqual(criteria.getStartDate()));
            any = true;
        }

        if (!any) {
            return NativeQuery.builder()
                    .withQuery(q -> q.matchAll(m -> m))
                    .withTrackTotalHits(true)
                    .withPageable(pageable)
                    .build();
        }

        CriteriaQuery criteriaQuery = new CriteriaQuery(root);
        criteriaQuery.setPageable(pageable);
        return criteriaQuery;
    }

    private org.springframework.data.elasticsearch.core.query.Query buildContainsFallbackQuery(SearchCriteria criteria, Pageable pageable) {
        Criteria root = new Criteria();

        boolean any = false;

        if (StringUtils.hasText(criteria.getQuery())) {
            Criteria title = new Criteria("title").contains(criteria.getQuery());
            Criteria description = new Criteria("description").contains(criteria.getQuery());
            Criteria text = title.or(description);
            root = root.and(text);
            any = true;
        }

        if (StringUtils.hasText(criteria.getCategory())) {
            root = root.and(new Criteria("category").is(criteria.getCategory()));
            any = true;
        }

        if (criteria.getType() != null) {
            root = root.and(new Criteria("type").is(criteria.getType().name()));
            any = true;
        }

        if (criteria.getMinPrice() != null) {
            root = root.and(new Criteria("price").greaterThanEqual(criteria.getMinPrice()));
            any = true;
        }
        if (criteria.getMaxPrice() != null) {
            root = root.and(new Criteria("price").lessThanEqual(criteria.getMaxPrice()));
            any = true;
        }

        if (criteria.getMinAge() != null) {
            root = root.and(new Criteria("maxAge").greaterThanEqual(criteria.getMinAge()));
            any = true;
        }
        if (criteria.getMaxAge() != null) {
            root = root.and(new Criteria("minAge").lessThanEqual(criteria.getMaxAge()));
            any = true;
        }

        if (criteria.getStartDate() != null) {
            root = root.and(new Criteria("nextSessionDate").greaterThanEqual(criteria.getStartDate()));
            any = true;
        }

        if (!any) {
            return NativeQuery.builder()
                    .withQuery(q -> q.matchAll(m -> m))
                    .withTrackTotalHits(true)
                    .withPageable(pageable)
                    .build();
        }

        CriteriaQuery criteriaQuery = new CriteriaQuery(root);
        criteriaQuery.setPageable(pageable);
        return criteriaQuery;
    }

    private org.springframework.data.elasticsearch.core.query.Query buildFuzzyNativeTextQuery(SearchCriteria criteria, Pageable pageable) {
        // Only apply multi_match fuzzy on text fields; keep pagination & total hits tracking
        return NativeQuery.builder()
                .withQuery(q -> q.multiMatch(mm -> mm
                        .query(criteria.getQuery())
                        .fields("title", "description")
                        .fuzziness("AUTO")
                ))
                .withTrackTotalHits(true)
                .withPageable(pageable)
                .build();
    }

    private Pageable createPageable(SearchCriteria criteria) {
        int size = Math.min(Math.max(criteria.getSize(), 1), 100);
        int page = Math.max(criteria.getPage(), 0);
        Sort sort = createSort(criteria.getSort());
        return PageRequest.of(page, size, sort);
    }

    private Sort createSort(String sortParam) {
        String key = sortParam != null ? sortParam.toLowerCase() : "upcoming";
        return switch (key) {
            case "priceasc" -> Sort.by(Sort.Direction.ASC, "price");
            case "pricedesc" -> Sort.by(Sort.Direction.DESC, "price");
            default -> Sort.by(Sort.Direction.ASC, "nextSessionDate");
        };
    }
}
