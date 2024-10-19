package de.supercode.bookreviewproject.book;

import java.util.Set;

public record BookResponseDto(
        Long id,
        String title,
        String author,
        String description,
        Set<Genre> genres)
{
}