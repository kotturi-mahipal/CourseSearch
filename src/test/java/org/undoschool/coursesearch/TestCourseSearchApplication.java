package org.undoschool.coursesearch;

import org.springframework.boot.SpringApplication;

public class TestCourseSearchApplication {

    public static void main(String[] args) {
        SpringApplication.from(CourseSearchApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
