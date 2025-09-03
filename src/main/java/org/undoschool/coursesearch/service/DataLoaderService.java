package org.undoschool.coursesearch.service;

import org.undoschool.coursesearch.document.CourseDocument;
import org.undoschool.coursesearch.repository.CourseRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.document.Document;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataLoaderService {

    private final CourseRepository courseRepository;
    private final ObjectMapper objectMapper;
    private final ElasticsearchOperations elasticsearchOperations;

    @Bean
    public ApplicationRunner loadSampleData() {
        return args -> {
            log.info("Starting sample data loading...");

            try {
                // Ensure index exists with mappings derived from entity
                IndexOperations indexOps = elasticsearchOperations.indexOps(org.undoschool.coursesearch.document.CourseDocument.class);
                if (!indexOps.exists()) {
                    boolean created = indexOps.create();
                    if (created) {
                        Document mapping = indexOps.createMapping(org.undoschool.coursesearch.document.CourseDocument.class);
                        indexOps.putMapping(mapping);
                        log.info("Created Elasticsearch index 'courses' with mappings");
                    } else {
                        log.warn("Failed to create index 'courses' or it already exists");
                    }
                }

                // Check if data already exists
                long existingCount = courseRepository.count();
                if (existingCount > 0) {
                    log.info("Found {} existing courses, skipping data load", existingCount);
                    return;
                }

                // Load JSON file from classpath
                ClassPathResource resource = new ClassPathResource("sample_courses.json");

                // Parse JSON into Java objects
                List<CourseDocument> courses = objectMapper.readValue(
                        resource.getInputStream(),
                        new TypeReference<List<CourseDocument>>() {}
                );

                // Bulk save to Elasticsearch
                List<CourseDocument> savedCourses = (List<CourseDocument>) courseRepository.saveAll(courses);

                log.info("Successfully indexed {} courses into Elasticsearch", savedCourses.size());

            } catch (IOException e) {
                log.error("Failed to load sample data: {}", e.getMessage(), e);
                throw new RuntimeException("Could not initialize sample data", e);
            }
        };
    }
}