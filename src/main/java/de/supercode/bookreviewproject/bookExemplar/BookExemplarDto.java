package de.supercode.bookreviewproject.bookExemplar;

public record BookExemplarDto(
        Long id,
        Long bookId,
        BookExemplarStatus status
) {
}

