package de.supercode.bookreviewproject.review;

public record ReviewResponseDto(
        Long id,
        Long userId,
        Long bookId,
        int stars,
        String content
){
}
