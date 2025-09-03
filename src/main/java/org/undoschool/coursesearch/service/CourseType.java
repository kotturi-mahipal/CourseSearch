package org.undoschool.coursesearch.service;

/**
 * Enum for course types.
 * ES will store this as a string, but Java gives us type safety.
 */
public enum CourseType {
    ONE_TIME,
    COURSE,
    CLUB
}