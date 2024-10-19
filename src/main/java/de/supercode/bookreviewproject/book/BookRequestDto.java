package de.supercode.bookreviewproject.book;

import java.util.Set;

// REQUEST OHNE ID
public record BookRequestDto(
        String title,
        String author,
        String description,
        Set<Genre> genres
) {
}

