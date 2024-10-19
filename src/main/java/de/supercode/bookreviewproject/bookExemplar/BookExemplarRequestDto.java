package de.supercode.bookreviewproject.bookExemplar;

public record BookExemplarRequestDto(
        Long bookId,
        BookExemplarStatus status
) {
}