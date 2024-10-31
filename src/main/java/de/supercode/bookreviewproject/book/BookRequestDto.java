package de.supercode.bookreviewproject.book;

import java.util.Set;

public record BookRequestDto(
        String title,
        String author,
        String description,
        Set<Genre> genres
) {
}

