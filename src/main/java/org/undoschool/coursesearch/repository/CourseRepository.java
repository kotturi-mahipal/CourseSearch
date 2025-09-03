package org.undoschool.coursesearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import org.undoschool.coursesearch.document.CourseDocument;

@Repository
public interface CourseRepository extends ElasticsearchRepository<CourseDocument, String> {
}
