package org.undoschool.coursesearch.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "org.undoschool.coursesearch.repository")
public class ElasticSearchConfig {
}
