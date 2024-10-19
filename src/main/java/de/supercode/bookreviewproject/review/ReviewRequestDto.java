package de.supercode.bookreviewproject.review;

public record ReviewRequestDto(
    String content,
    Long bookId,
    int stars
){}
